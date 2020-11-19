package com.olsinoo.sosplease

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // ---------------------- Values ----------------------
    private val tag = "SOSAPP"
    private val permissionsCode = 456
    private val defaultName = "clovek"
    private val defaultNumber = "0917302746"
    private val settingsResult = 715
    private val locResult = 646
    private var selectedNumber = ""
    private var selectedName = ""

    private lateinit var locationManagerGPS : LocationManager
    private lateinit var telephonyManagerGSM : TelephonyManager
    lateinit var lastLocation : Location
    private var gsmLocation : GsmCellLocation? = null

    // ---------------------- Array of Permissions ----------------------
    private val perms = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_CONTACTS
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permissionsCode)
        }
        val preferences = getDefaultSharedPreferences(this)
        preferences.getString("user", defaultName)
        preferences.getString("phone", defaultNumber)

        // ------------------- bottom row of buttons - showing chosen contacts -------------------
        buttonContact1.setOnClickListener {
            selectedName = preferences.getString("contact_1_name", defaultName) ?: defaultName
            selectedNumber = preferences.getString("contact_1_num", defaultNumber) ?: defaultNumber
            textViewCurrentContactSelected.text = "$selectedName $selectedNumber"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonContact1.setTextColor(getColor(R.color.colorAccent))
            } else {
                buttonContact1.setTextColor(Color.GREEN)
            }
            buttonContact2.setTextColor(Color.BLACK)
            buttonContact3.setTextColor(Color.BLACK)
        }
        buttonContact2.setOnClickListener {
            selectedName = preferences.getString("contact_2_name", defaultName) ?: defaultName
            selectedNumber = preferences.getString("contact_2_num", defaultNumber) ?: defaultNumber
            textViewCurrentContactSelected.text = "$selectedName $selectedNumber"
            buttonContact1.setTextColor(Color.BLACK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonContact2.setTextColor(getColor(R.color.colorAccent))
            } else {
                buttonContact2.setTextColor(Color.GREEN)
            }
            buttonContact3.setTextColor(Color.BLACK)
        }
        buttonContact3.setOnClickListener {
            selectedName = preferences.getString("contact_3_name", defaultName) ?: defaultName
            selectedNumber = preferences.getString("contact_3_num", defaultNumber) ?: defaultNumber
            textViewCurrentContactSelected.text = "$selectedName $selectedNumber"
            buttonContact1.setTextColor(Color.BLACK)
            buttonContact2.setTextColor(Color.BLACK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonContact3.setTextColor(getColor(R.color.colorAccent))
            } else {
                buttonContact3.setTextColor(Color.GREEN)
            }
        }

        // ---------------------- Main FAB Buttons ----------------------
        findViewById<FloatingActionButton>(R.id.fabWarning).setOnClickListener { view ->
            if (this::locationManagerGPS.isInitialized || this::telephonyManagerGSM.isInitialized) {
                if (selectedName != "" && selectedNumber != "") {
                    val message = "Ahoj $selectedName, pomoc.\nGPS: " +
                            "Lat:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.latitude) else ""} " +
                            "Long:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.longitude) else ""}\nGSM: " +
                            "mcc:${telephonyManagerGSM.networkOperator.substring(0, 3)} " +
                            "mnc:${telephonyManagerGSM.networkOperator.substring(3)} " +
                            "cid:${gsmLocation?.cid ?: ""} lac:${gsmLocation?.lac ?: ""} " +
                            "- ${preferences.getString("user", "clovek")}"
                    @RequiresApi(Build.VERSION_CODES.M)
                    if (applicationContext.checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(view, message)
                    } else {
                        Snackbar.make(view, "PLEASE ENABLE SENDING SMS", Snackbar.LENGTH_SHORT).show()
                        requestPermissions(perms, permissionsCode)
                    }
                } else {
                    Snackbar.make(view, "SELECT A CONTACT", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TURN ON GPS", Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), locResult)
            }
        }

        findViewById<FloatingActionButton>(R.id.fabAlarming).setOnClickListener { view ->
            if (this::locationManagerGPS.isInitialized || this::telephonyManagerGSM.isInitialized) {
                if (selectedName != "" && selectedNumber != "") {
                    val message = "Ahoj $selectedName, pomoc, ponahlaj sa.\nGPS: " +
                            "Lat:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.latitude) else ""} " +
                            "Long:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.longitude) else ""} \nGSM: " +
                            "mcc:${telephonyManagerGSM.networkOperator.substring(0, 3)} " +
                            "mnc:${telephonyManagerGSM.networkOperator.substring(3)} " +
                            "cid:${gsmLocation?.cid ?: ""} lac:${gsmLocation?.lac ?: ""} " +
                            "- ${preferences.getString("user", "clovek")}"
                    @RequiresApi(Build.VERSION_CODES.M)
                    if (applicationContext.checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(view, message)
                    } else {
                        Snackbar.make(view, "PLEASE ENABLE SENDING SMS", Snackbar.LENGTH_SHORT).show()
                        requestPermissions(perms, permissionsCode)
                    }
                } else {
                    Snackbar.make(view, "SELECT A CONTACT", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TURN ON GPS", Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), locResult)
            }
        }

        findViewById<FloatingActionButton>(R.id.fabUrgent).setOnClickListener { view ->
            if (this::locationManagerGPS.isInitialized || this::telephonyManagerGSM.isInitialized) {
                if (selectedName != "" && selectedNumber != "") {
                    val message = "Ahoj $selectedName, umieram, pomoc.\nGPS: " +
                            "Lat:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.latitude) else ""} " +
                            "Long:${if (this::lastLocation.isInitialized)
                                "%.6f".format(lastLocation.longitude) else ""}\nGSM: " +
                            "mcc:${telephonyManagerGSM.networkOperator.substring(0, 3)} " +
                            "mnc:${telephonyManagerGSM.networkOperator.substring(3)} " +
                            "cid:${gsmLocation?.cid ?: ""} lac:${gsmLocation?.lac ?: ""} " +
                            "- ${preferences.getString("user", "clovek")}"
                    @RequiresApi(Build.VERSION_CODES.M)
                    if (applicationContext.checkSelfPermission(Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                        sendSMS(view, message)
                    } else {
                        Snackbar.make(view, "PLEASE ENABLE SENDING SMS", Snackbar.LENGTH_SHORT).show()
                        requestPermissions(perms, permissionsCode)
                    }
                } else {
                    Snackbar.make(view, "SELECT A CONTACT", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "TURN ON GPS", Toast.LENGTH_SHORT).show()
                startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), locResult)
            }
        }
    }

    // ---------------------- ToolBar Menu ----------------------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    // ---------------------- ToolBar Menu settings ----------------------
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                startActivityForResult(
                    Intent(this, SettingsActivity::class.java),
                    settingsResult
                )
            }
            R.id.gpsSettings -> {
                startActivityForResult(
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                    locResult
                )
            }
        }
        return true
    }

    // ---------------------- Return From Different Activity ----------------------
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent? ) {
        Log.d(tag, "ACTIVITY RESULT req:$requestCode | res:$resultCode | data:$data")
        super.onActivityResult(requestCode, resultCode, data)
        val preferences = getDefaultSharedPreferences(this)
        when (requestCode) {
            settingsResult -> {
                Log.d(tag, "return settings")
                buttonContact1.text = preferences.getString("contact_1_name", defaultName)
                buttonContact2.text = preferences.getString("contact_2_name", defaultName)
                buttonContact3.text = preferences.getString("contact_3_name", defaultName)
            }
            locResult -> {
                createLocationManager()
            }
        }
    }

    // ---------------------- Resume Activity ----------------------
    override fun onResume() {
        super.onResume()
        Log.d(tag, "main resumed")
        val preferences = getDefaultSharedPreferences(this)
        buttonContact1.text = preferences.getString("contact_1_name", defaultName)
        buttonContact2.text = preferences.getString("contact_2_name", defaultName)
        buttonContact3.text = preferences.getString("contact_3_name", defaultName)
        textViewCurrentContactSelected.text = getString(R.string.currentContact)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "perm ok")
                if (this::locationManagerGPS.isInitialized) {
                    Log.d(TAG, "innit ok")
                    if (locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Log.d(TAG, "new man ok")
                    } else {
                        Log.d(TAG, "new man not ok")
                        Toast.makeText(this, "You should to turn on GPS", Toast.LENGTH_LONG).show()
                        Log.d(tag, "${showDialogBox()}")
                    }
                } else {
                    createLocationManager()
                }
            } else {
                requestPermissions(perms, permissionsCode)
                if (this::locationManagerGPS.isInitialized && locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.d(TAG, "new man ok")
                } else {
                    Log.d(TAG, "new man not ok")
                    Toast.makeText(this, "You should to turn on GPS for better location", Toast.LENGTH_LONG).show()
                    showDialogBox()
                }
            }
        }
        if (selectedName != "" && selectedNumber != "")
        textViewCurrentContactSelected.text = "$selectedName $selectedNumber"
    }

    // ------------------------------ GPS Location ---------------------------
    // ------------------------------ GSM Location ---------------------------
    // ---------------------- Create GPS and GSM Managers ----------------------
    private fun createLocationManager() {
        Log.d(TAG, "GPS ON")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "LOCATION")
            if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "FINE GRANTED")
                locationManagerGPS =
                    getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManagerGPS.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0F,
                    locListener
                )
                try {
                    Log.d(TAG, "LOC TRY")
                    if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        Log.d(TAG, "LAST LOC BEFORE")
                        if (locationManagerGPS.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
                            Log.d(TAG, "LAST LOC INITIALIZED")
                            lastLocation =
                                locationManagerGPS.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                        }
                    }
                } catch (e: Exception) {
                }
            }
            if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                if (applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "FINE/COARSE GRANTED")
                    telephonyManagerGSM = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    gsmLocation = telephonyManagerGSM.cellLocation as GsmCellLocation?
                }
            }
        }
    }

    // ---------------------- Pause Activity ----------------------
    override fun onPause() {
        super.onPause()
        if (this::locationManagerGPS.isInitialized) {
            locationManagerGPS.removeUpdates(locListener)
        }
    }

    // ---------------------- SMS Send ----------------------
    private fun sendSMS(v : View, message : String) {
        Log.d(tag, "SMS sending")
        val smsManager = SmsManager.getDefault()
        val phoneNumber = selectedNumber
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        Log.d(tag, "SMS sent")
        Snackbar.make(v, "Message sent to $selectedName $selectedNumber", Snackbar.LENGTH_SHORT).show()

    }

    // ---------------------- Location Listener ----------------------
    private val locListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            lastLocation = location
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun showDialogBox() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("GPS is turned off")
        dialog.setMessage("Keeping this setting off will prevent this application from accessing your location.\n\nDo you want to turn it on?")
        val preference = getDefaultSharedPreferences(this)
        val editor = preference.edit()
        dialog.setPositiveButton("YES") { dialogInterface: DialogInterface, i: Int ->
            Log.d(tag, "dialInter: $dialogInterface | i: $i")
            startActivityForResult(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), locResult)
            editor.putBoolean("gps_location", true)
            editor.apply()
        }
        dialog.setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
            Log.d(tag, "dialInter: $dialogInterface | i: $i")
            editor.putBoolean("gps_location", false)
            editor.apply()
        }
        val dialogBox = dialog.create()
        dialogBox.setCanceledOnTouchOutside(false)
        dialog.show()
    }
}
