package com.toma6.geocacher


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), LocationListener {
    val TAG = "CACHE"
    var latitude : Double = 0.0
    var longitude : Double = 0.0

    internal val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 777

    lateinit var lm : LocationManager
    lateinit var provider : String
    lateinit var lastLocation : Location

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ----- Preferences & Settings
        val sharedPrefs = getDefaultSharedPreferences(this)

        addPermissions()

        // GPS
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.accuracy = Criteria.ACCURACY_COARSE
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isBearingRequired = false
        criteria.isSpeedRequired = false
        if (sharedPrefs.getBoolean("useGPS", true))
            provider = LocationManager.GPS_PROVIDER
        else
            provider = lm.getBestProvider(criteria, false)?:LocationManager.GPS_PROVIDER
        if (!lm.isProviderEnabled(provider)) {
            val gpsSettingIntent =
                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(gpsSettingIntent)
        }
        try {
            val loc =  lm.getLastKnownLocation(provider)
            if (loc != null)
                lastLocation = loc
            Log.d(TAG, "$lastLocation")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }

        navigateButton.setOnClickListener{
            if(checkInputs()) goToMapsActivity(latitude, longitude)
        }
    }

    private fun goToMapsActivity(targetLat: Double, targetLong: Double){
        val intent = Intent(this, MapsActivity::class.java)
        val targetAddress : String = getAdress(targetLat,targetLong)
        intent.putExtra("START_LATITUDE", latitude)
        intent.putExtra("START_LONGITUDE", longitude)
        intent.putExtra("TARGET_LATITUDE", targetLat)
        intent.putExtra("TARGET_LONGITUDE", targetLong)
        intent.putExtra("ADDRESS",targetAddress)
        startActivity(intent)
    }

    private fun getAdress(latitude: Double, longitude : Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        return addresses[0].getAddressLine(0)
    }

    private fun checkInputs(): Boolean {
        val latitude : String = targetLat.text.toString()
        val longitude : String = targetLong.text.toString()
        if (latitude == "" || longitude == "" || latitude == null || longitude == null){
            val toast = Toast.makeText(this, "LAT AND LONG VALUES MISSING", Toast.LENGTH_SHORT)
            toast.show()
            return false
        }
        try {
            this.latitude = latitude.toDouble()
            this.longitude = longitude.toDouble()
        }
        catch (ex: java.lang.Exception) {
            Log.e(TAG, "INSERT DOUBLE VALUES")
            ex.printStackTrace()
            return false
        }
        if ((this.latitude < -90 || this.latitude > 90) || (this.longitude < -180  || this.longitude > 180)){
            val toast = Toast.makeText(this, "VALUES ARE OUT OF RANGE", Toast.LENGTH_SHORT)
            toast.show()
            return false
        }
        return true
    }

    //permissions
    private fun addPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissionsNeeded = mutableListOf<String>()
        val permissionsList = mutableListOf<String>()
        if (addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS-FINE")
        if (addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("GPS-COARSE")
        if (addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                var message = "You need to grant access to " + permissionsNeeded[0]
                showMessageOKCancel(
                    permissionsNeeded.joinToString(separator = ", "),
                    DialogInterface.OnClickListener { dialog, which ->
                        //@Override
                        requestPermissions(
                            permissionsList.toTypedArray<String>(),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
                        )
                    })
                return
            }
            requestPermissions(
                permissionsList.toTypedArray<String>(),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
            )
        }
    }
    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addPermission(
        permissionsList: MutableList<String>,
        permission: String
    ): Boolean {
        if (applicationContext.checkSelfPermission(permission)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!shouldShowRequestPermissionRationale(permission)) return true
        }
        return false
    }

    override fun onProviderDisabled(arg0: String) {
        Log.d(TAG, "onProviderDisabled:$arg0")
    }

    override fun onProviderEnabled(arg0: String) {
        Log.d(TAG, "onProviderEnabled:$arg0")
    }

    override fun onStatusChanged(arg0: String, arg1: Int, arg2: Bundle) {
        Log.d(TAG, "onStatusChanged")
    }
    override fun onLocationChanged(loc: Location) {
        lastLocation = loc
        Log.d(TAG, "onLocationChanged: lat:${loc.latitude}, long: ${loc.longitude}")
        displayLoc(lastLocation)
    }
    override fun onResume() {
        super.onResume()
        try {
            lm.requestLocationUpdates(provider, 2000, 10f, this)
        } catch (e: SecurityException) {
            Log.e(TAG, "security exception: " + e.message)
        }
    }
    override fun onPause() {
        super.onPause()
        lm.removeUpdates(this)
    }

    //GPS
    fun displayLoc(loc: Location) {
        posLat.text = "LAT   ${"%.8f".format(loc.latitude)}"
        posLong.text = "LONG   ${"%.8f".format(loc.longitude)}"
    }
}