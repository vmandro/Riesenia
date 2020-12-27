package com.example.weather

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.httpupload.RetrofitClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import info.androidhive.fontawesome.FontDrawable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
//    testovaci jsonString
//    private var jsonString = "{\"coord\":{\"lon\":18.17,\"lat\":48.56},\"weather\":[{\"id\":701,\"main\":\"Mist\",\"description\":\"mist\",\"icon\":\"01d\"}],\"base\":\"stations\",\"main\":{\"temp\":2.85,\"feels_like\":-1.72,\"temp_min\":2.22,\"temp_max\":3.33,\"pressure\":1009,\"humidity\":93},\"visibility\":5000,\"wind\":{\"speed\":4.1,\"deg\":333},\"clouds\":{\"all\":100},\"dt\":1607645477,\"sys\":{\"type\":1,\"id\":7051,\"country\":\"SK\",\"sunrise\":1607668187,\"sunset\":1607698312},\"timezone\":3600,\"id\":3057174,\"name\":\"Topoľčany\",\"cod\":200}"
    private val CITY = "topolcany"
    private val APP_ID = "4c144439e1df35dc2459a3867236b22a"

    private var jsonString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val screen = findViewById(R.id.screen) as ConstraintLayout
        screen.setBackgroundColor(Color.parseColor("#349eeb"))


        //update kazdych 30 minut automaticky
        object : CountDownTimer(60000 * 30, 60000) {
            override fun onFinish() {
                updateWeather()
                this.start()
            }

            override fun onTick(millisUntilFinished: Long) {}
        }.start()

        val fab = findViewById(R.id.floatingActionButton) as FloatingActionButton
        val drawable = FontDrawable(this, R.string.fa_redo_solid, true, false)
        drawable.setTextColor(ContextCompat.getColor(this, R.color.white))
        fab.setImageDrawable(drawable)
        fab.setOnClickListener { updateWeather() }

        updateWeather()
    }

    fun updateWeather() {
        RetrofitClient.instance.getJson("weather?q=${CITY}&appid=${APP_ID}&units=metric")
            .enqueue(object: Callback<ResponseBody?> {
                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    jsonString = response.body()!!.string()
                    updateTextViews()
                }
            })
    }

    fun updateTextViews() {
        val weatherImageView = findViewById(R.id.weatherImageView) as ImageView
        val temperatureTextView = findViewById(R.id.temperatureTextView) as TextView
        val maxMinTempTextView = findViewById(R.id.maxMinTempTextView) as TextView
        val pressureTextView = findViewById(R.id.pressureTextView) as TextView
        val humidityTextView = findViewById(R.id.humidityTextView) as TextView
        val visibilityTextView = findViewById(R.id.visibilityTextView) as TextView
        val windTextView = findViewById(R.id.windTextView) as TextView
        val sunriseTextView = findViewById(R.id.sunriseTextView) as TextView
        val sunsetTextView = findViewById(R.id.sunsetTextView) as TextView
        val locationTextView = findViewById(R.id.locationTextView) as TextView
        val updatedTextView = findViewById(R.id.updatedTextView) as TextView
        val feelsLikeTempTextView = findViewById(R.id.feelsLikeTempTextView) as TextView
        val weatherTextView = findViewById(R.id.weatherTextView) as TextView


        val gson = Gson()
        val weatherApiResp = gson.fromJson(jsonString, ApiResponse::class.java)

        val weather = weatherApiResp.weather

        val main = weatherApiResp.main
        val temperature = main.temp
        val feelsLikeTemperature = main.feels_like
        val maxTemperature = main.temp_max
        val minTemperature = main.temp_min
        val pressure = main.pressure
        val humidity = main.humidity

        val visibility = weatherApiResp.visibility

        val wind = weatherApiResp.wind
        val speed = wind.speed
        val windAngle = wind.deg

        val nowdt = weatherApiResp.dt

        val sys = weatherApiResp.sys
        val country = sys.country
        val sunrisedt = sys.sunrise
        val sunsetdt = sys.sunset

        val cityName = weatherApiResp.name

        val maxMinTempString = "${maxTemperature} °C/${minTemperature} °C"
        maxMinTempTextView.text = maxMinTempString

        pressureTextView.text = "$pressure mb"
        humidityTextView.text = "$humidity %"
        visibilityTextView.text = "${visibility/1000f} km"
        val speedRounded = String.format("%.2f", speed*3.6)
        val dirList = listOf(
            "N",
            "NE",
            "E",
            "SE",
            "S",
            "SW",
            "W",
            "NW",
            "N"
        )
        val dirIndex = ((windAngle + 22.5) / 45).toInt()

        windTextView.text = "$speedRounded km/h, ${dirList[dirIndex]}"

        val updatedDF = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
        val updated = java.util.Date(nowdt * 1000)

        val sunDF = java.text.SimpleDateFormat("HH:mm")
        val sunriseTime = java.util.Date(sunrisedt * 1000)
        val sunsetTime = java.util.Date(sunsetdt * 1000)

        sunriseTextView.text = "${sunDF.format(sunriseTime)}"
        sunsetTextView.text = "${sunDF.format(sunsetTime)}"

        val location = "$cityName, $country"
        locationTextView.text = location

        updatedTextView.text = "${updatedDF.format(updated)}"

        val feelsLikeTemp = String.format("%.1f", feelsLikeTemperature)
        feelsLikeTempTextView.text = "$feelsLikeTemp °C"

        val weatherTemperature = String.format("%.1f", temperature)
        temperatureTextView.text = "$weatherTemperature °C"

        val weatherDescription = weather.joinToString(", ") { it.description }
        weatherTextView.text = weatherDescription

        //vybera sa len ikona pre 1. pocasie v liste(moze ich byt viac)
        val weatherIcon = "icon_${weatherApiResp.weather.get(0).icon}"
        val resID: Int = resources.getIdentifier(weatherIcon, "drawable", packageName)
        weatherImageView.setImageResource(resID)
    }
}
