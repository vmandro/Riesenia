package com.example.sos

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var smsManager : SmsManager
    private lateinit var gsmloc : GsmCellLocation
    private lateinit var lm : LocationManager
    private lateinit var tm : TelephonyManager
    private lateinit var provider : String
    private var cislo1 : String? = null
    private var cislo2 : String? = null
    private var cislo3 : String? = null
    private var number : String? = null
    private var message : String? = null

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        addPermissions()
        managers()
    }

    override fun onResume() {
        super.onResume()
        textViewContact1.text = sharedPreferences.getString("username1", "First Contact")
        textViewContact2.text = sharedPreferences.getString("username2", "Second Contact")
        textViewContact3.text = sharedPreferences.getString("username3", "Third Contact")
        cislo1 = sharedPreferences.getString("phonenumber1", "0902804593")
        cislo2 = sharedPreferences.getString("phonenumber2", "0902804593")
        cislo3 = sharedPreferences.getString("phonenumber3", "0902804593")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                if (applicationContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    val i = Intent(this, SettingActivity::class.java)
                    startActivity(i)
                } else {
                    addPermissions()
                }
            }
        }
        return true
    }

    override fun onLocationChanged(p0: Location) {}

    fun contactClick(v : View){
        when (v.id) {
            textViewContact1.id -> {
                number = cislo1
                imageViewCircle1.visibility = View.VISIBLE
                imageViewCircle2.visibility = View.INVISIBLE
                imageViewCircle3.visibility = View.INVISIBLE
            }
            textViewContact2.id -> {
                number = cislo2
                imageViewCircle1.visibility = View.INVISIBLE
                imageViewCircle2.visibility = View.VISIBLE
                imageViewCircle3.visibility = View.INVISIBLE
            }
            textViewContact3.id -> {
                number = cislo3
                imageViewCircle1.visibility = View.INVISIBLE
                imageViewCircle2.visibility = View.INVISIBLE
                imageViewCircle3.visibility = View.VISIBLE
            }
        }
    }

    fun messageClick(v : View){
        when (v.id) {
            textViewWarning.id -> {
                message = sharedPreferences.getString("warning", "warning")
                imageViewCircle4.visibility = View.VISIBLE
                imageViewCircle5.visibility = View.INVISIBLE
                imageViewCircle6.visibility = View.INVISIBLE
            }
            textViewAlarm.id -> {
                message = sharedPreferences.getString("alarm", "alarm")
                imageViewCircle4.visibility = View.INVISIBLE
                imageViewCircle5.visibility = View.VISIBLE
                imageViewCircle6.visibility = View.INVISIBLE
            }
            textViewUrgent.id -> {
                message = sharedPreferences.getString("urgent", "urgent")
                imageViewCircle4.visibility = View.INVISIBLE
                imageViewCircle5.visibility = View.INVISIBLE
                imageViewCircle6.visibility = View.VISIBLE
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun sendClick(v : View){
        if (number == null){
            showMessageOK("choose contact")
        } else if (message == null){
            showMessageOK( "choose emergency level")
        } else {
            if (applicationContext.checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                managers()
                val location = lm.getLastKnownLocation(provider)
                val mess = message +
                        " lat: ${"%.4f".format(location?.latitude)}, long: ${"%.4f".format(location?.longitude)}, " +
                        "mmc: ${tm.networkOperator.substring(0, 3)}, mnc: ${tm.networkOperator.substring(3)}, " +
                        "cid: ${gsmloc.cid}, lac: ${gsmloc.lac}"
                smsManager.sendTextMessage(number, null, mess, null, null)
                showMessageOK( "sms was sent")
                imageViewCircle1.visibility = View.INVISIBLE
                imageViewCircle2.visibility = View.INVISIBLE
                imageViewCircle3.visibility = View.INVISIBLE
                imageViewCircle4.visibility = View.INVISIBLE
                imageViewCircle5.visibility = View.INVISIBLE
                imageViewCircle6.visibility = View.INVISIBLE
                number = null
                message = null
            } else {
                addPermissions()
            }
        }
    }

    private fun addPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissionsNeeded = mutableListOf<String>()
        val permissionsList = mutableListOf<String>()
        if (addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("TELE")
        if (addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("GPS")
        if (addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SMS")
        if (addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("CONTACTS")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                showMessageOKCancel(
                    "Grant permissions for: " + permissionsNeeded.joinToString(separator = ", "),
                    DialogInterface.OnClickListener { _, _ ->
                        requestPermissions(permissionsList.toTypedArray(), 999)
                    })
                return
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addPermission(permissionsList: MutableList<String>,
                              permission: String): Boolean {
        if (applicationContext.checkSelfPermission(permission)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            return true
        }
        return false
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showMessageOK(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()
            .show()
    }

    @Suppress("DEPRECATION")
    private fun managers(){
        lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        provider = LocationManager.GPS_PROVIDER
        if (!lm.isProviderEnabled(provider)) {
            val gpsSettingIntent =
                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(gpsSettingIntent)
        }

        tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gsmloc = tm.cellLocation as GsmCellLocation
        }

        smsManager = SmsManager.getDefault()
    }
}