package com.olsinoo.simpleweather

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.floor
import kotlin.math.roundToInt

// Weather Icons made by https://www.flaticon.com/authors/linector

class MainActivity : AppCompatActivity() {
    private val tag = "weatherTag"
    private lateinit var locationManager: LocationManager
    private lateinit var currentLocation : LatLong
    private val permsCode = 715
    private val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
    )
    private lateinit var requestTimer: TimerTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        findViewById<ConstraintLayout>(R.id.bottomCardLayout).clipToOutline = true
        findViewById<LinearLayout>(R.id.innerLayoutFeelsLike).clipToOutline = true
        findViewById<Button>(R.id.reloadButton).setOnClickListener {
            requestWeatherUpdate()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            while (ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(
                            this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, perms, permsCode)
            }
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0F,
                    locListener
            )
        }
    }

    data class LatLong (
            val latitude : Double,
            val longitude : Double)

    // GPS turned off
    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    // No Internet connection
    private fun buildAlertMessageNoInternet() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your Wifi and Mobile Data are disabled. In order to use this app you need to enable one of them.\nEnable?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_WIFI_SETTINGS)) }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private var requestRunning = false

    private fun requestWeatherUpdate() {
        if (requestRunning) return
        if (!isDeviceConnectedToNetwork()) {
            buildAlertMessageNoInternet()
            return
        }
        requestRunning = true
        Log.d(tag,"REQUEST")
        if (this::currentLocation.isInitialized) {
            receiveWeatherUpdate(currentLocation.latitude, currentLocation.longitude)
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (lastLocation != null) {
                    receiveWeatherUpdate(lastLocation.latitude, lastLocation.longitude)
                } else {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(10000)
                        requestWeatherUpdate()
                    }
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            delay(10000)
            requestRunning = false
        }
    }

    private fun receiveWeatherUpdate(lat: Double, long: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(tag,"RECEIVED")
            val weather = RetroService.get().get(lat, long)
            Log.d(tag,"RECEIVED: ${weather.name}, ${weather.weatherState?.get(0)?.weatherCondition}, $weather")
            withContext(Dispatchers.Main) {
                findViewById<TextView>(R.id.textWeatherCondition).text =
                    weather.weatherState?.get(0)?.weatherCondition ?: "Unavailable"
                findViewById<TextView>(R.id.textWeatherConditionExact).text =
                    weather.weatherState?.get(0)?.description ?: "Unavailable"
                findViewById<TextView>(R.id.textCelsius).text =
                    "${weather.weatherDetail.temperature?.roundToInt()?: "???"}°C"
                findViewById<TextView>(R.id.textViewLocation).text =
                    weather.name?: "Unavailable"
                findViewById<TextView>(R.id.textViewFeelsLikeTemp).text =
                    "${weather.weatherDetail.feeling?.roundToInt()?: "???"}°C"
                findViewById<TextView>(R.id.textViewHumidity).text =
                    "${weather.weatherDetail.humidity?: "???"}%"
                findViewById<TextView>(R.id.textViewMaxTemp).text =
                    "${weather.weatherDetail.maxTemperature?: "???"}°C"
                findViewById<TextView>(R.id.textViewMinTemp).text =
                    "${weather.weatherDetail.minTemperature?: "???"}°C"
                findViewById<TextView>(R.id.textViewSunrise).text =
                    getTime(weather.sunDetail.sunrise)
                findViewById<TextView>(R.id.textViewSunset).text =
                    getTime(weather.sunDetail.sunset)
                findViewById<TextView>(R.id.textViewWindDegree).text =
                    getDirectionFromDegree(weather.windDetail.degree?: 0)
                findViewById<TextView>(R.id.textViewWindSpeed).text = "${weather.windDetail.speed ?: ""} m/s"
                    if (weather.weatherState?.get(0)?.weatherCondition != null) {
                    changeImage(weather.weatherState[0].weatherCondition!!)
                }
            }
        }
    }

// OpenWeather main weather types:
// Thunderstorm, Drizzle, Rain, Snow, Clear, Clouds, Squall, Tornado
// Mist, Smoke, Haze, Dust, Fog, Sand, Dust, Ash - Lower Visibility Family, a.k.a. mist

    private fun changeImage(weather: String) {
        findViewById<ImageView>(R.id.fabWeatherImage).setImageDrawable(
            when (weather) {
                "Thunderstorm" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_storm, theme)
                "Drizzle" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_rain, theme)
                "Rain" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_rain, theme)
                "Snow" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_snowing, theme)
                "Clear" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_sun, theme)
                "Clouds" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_partly_cloudy, theme)
                "Tornado" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tornado, theme)
                "Squall" -> ResourcesCompat.getDrawable(resources, R.drawable.ic_tornado, theme)
                else -> ResourcesCompat.getDrawable(resources, R.drawable.ic_mist, theme)
            }
        )
    }

    private fun getTime(unixTime: Long?) : String {
        if (unixTime == null) {
            return ""
        }
        val cal = Calendar.getInstance()
        cal.timeInMillis = unixTime * 1000
        return "${cal.get(Calendar.HOUR_OF_DAY)}:${cal.get(Calendar.MINUTE)}"
    }

    private fun getDirectionFromDegree(deg : Int) : String {
        val direction = (floor(deg/45.0)).toInt()
        val directions = listOf("North","NorthEast","East","SouthEast","South","SouthWest","West","NorthWest")
        return directions[direction]
    }

    private fun isDeviceConnectedToNetwork(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        } else {
            if (connectivityManager.activeNetworkInfo?.type != 0) return true
        }
        return false
    }

    private val locListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            currentLocation = LatLong(location.latitude, location.longitude)
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    override fun onResume() {
        super.onResume()
        // Check if device has GPS
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        } else {
            checkPermissions()
            requestWeatherUpdate()
            requestTimer = Timer("weatherRequests").schedule(0,300000) {
                requestWeatherUpdate()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        locationManager.removeUpdates(locListener)
    }

}
