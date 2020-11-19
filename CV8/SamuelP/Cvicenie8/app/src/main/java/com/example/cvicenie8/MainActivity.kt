package com.example.cvicenie8

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), LocationListener {
    val TAG = "SOS"
    internal val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 777
    internal val RESULT_PICK_CONTACT = 789

    lateinit var lm : LocationManager
    lateinit var provider : String
    lateinit var lastLocation : Location

    lateinit var wifiManager : WifiManager
    // ------------- WIFI

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ----- Preferences & Settings
        val sharedPrefs = getDefaultSharedPreferences(this)
        sharedPrefs.getString("username", "NULL")
        sharedPrefs.getString("phonenumber", "123")
        sharedPrefs.getString("phonenumber2", "123")
        sharedPrefs.getString("phonenumberalarm", "123")
        sharedPrefs.getString("phonenumberalarm2", "123")
        sharedPrefs.getString("phonenumberurgent", "123")
        sharedPrefs.getString("phonenumberurgent2", "123")
        showPrefs()
        // ----- permissions
        addPermissions()
        // ----- GPS
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
        // ----- WIFI
        if (sharedPrefs.getBoolean("useWIFI", true)) {
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }

        }
    }
    // Setting menu -----------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }
    val RESULT_SETTINGS = 777
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.menu_settings -> {
                val i = Intent(this, SettingActivity::class.java)
                startActivityForResult(i, RESULT_SETTINGS)
            }
        }
        return true
    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent? ) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_SETTINGS -> showPrefs()
            RESULT_PICK_CONTACT -> {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        var phoneNo: String? = null
                        var name: String? = null
                        val uri: Uri? = data?.data
                        if (uri != null) {
                            val cursor = contentResolver.query(uri, null, null, null, null)
                            if (cursor != null) {
                                cursor.moveToFirst()
                                val phoneIndex: Int =
                                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                                val nameIndex: Int =
                                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                                val sharedPrefs = getDefaultSharedPreferences(this)
                                sharedPrefs.edit().apply() {
                                    putString("username", cursor.getString(nameIndex))
                                    putString("phonenumber", cursor.getString(phoneIndex))
                                    putString("phonenumber2", cursor.getString(phoneIndex))
                                    putString("phonenumberalarm", cursor.getString(phoneIndex))
                                    putString("phonenumberalarm2", cursor.getString(phoneIndex))
                                    putString("phonenumberurgent", cursor.getString(phoneIndex))
                                    putString("phonenumberurgent2", cursor.getString(phoneIndex))
                                }
                                showPrefs()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Log.e("Failed", "Not able to pick contact")
                }
            }
        }
    }
    fun showPrefs() {
        val sharedPrefs = getDefaultSharedPreferences(this)
        val uname = sharedPrefs.getString("username", "NULL")
        val pnumber = sharedPrefs.getString("phonenumber", "123")
        val pnumber2 = sharedPrefs.getString("phonenumber2", "123")
        val pnumberalarm = sharedPrefs.getString("phonenumberalarm", "123")
        val pnumberalarm2 = sharedPrefs.getString("phonenumberalarm2", "123")
        val pnumberurgent = sharedPrefs.getString("phonenumberurgent", "123")
        val pnumberurgent2 = sharedPrefs.getString("phonenumberurgent2", "123")
      //  textUserSettings.text = "ICE:$uname/$pnumber"
    }
    //----- permissions
    private fun addPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissionsNeeded = mutableListOf<String>()
        val permissionsList = mutableListOf<String>()
        if (addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS")
        if (addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SMS")
        if (addPermission(permissionsList, Manifest.permission.ACCESS_WIFI_STATE))
            permissionsNeeded.add("WIFI")
        if (addPermission(permissionsList, Manifest.permission.CHANGE_NETWORK_STATE))
            permissionsNeeded.add("WIFI")
        if (addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                var message = "You need to grant access to " + permissionsNeeded[0]
                showMessageOKCancel(
                    permissionsNeeded.joinToString(separator = ", "),
                    DialogInterface.OnClickListener { dialog, which ->
                        //@Override
                        requestPermissions(permissionsList.toTypedArray<String>(),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
                    })
                return
            }
            requestPermissions(permissionsList.toTypedArray<String>(),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
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
    private fun addPermission(permissionsList: MutableList<String>,
                              permission: String): Boolean {
        if (applicationContext.checkSelfPermission(permission)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!shouldShowRequestPermissionRationale(permission)) return true
        }
        return false
    }
    // ---- GPS
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
        // patri sa to vypnut
        lm.removeUpdates(this)
    }
    //-------------- SEND SMS
    fun sendSMS(v : View) {
        val smsManager = SmsManager.getDefault()
        val sharedPrefs = getDefaultSharedPreferences(this)

        val tManager: TelephonyManager = baseContext
            .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        // Get carrier name (Network Operator Name)

        // Get carrier name (Network Operator Name)
        val carrierName: String = tManager.getNetworkOperatorName()

        val uname = sharedPrefs.getString("username", "NULL")
        val pnumber = sharedPrefs.getString("phonenumber", "123")
        val pnumber2 = sharedPrefs.getString("phonenumber2", "123")
        val message = "Ahoj $uname, moja poloha je: ${lastLocation.latitude}, " +
                        "${lastLocation.longitude} + ${carrierName}"
        smsManager.sendTextMessage(pnumber, null, message, null, null)
        smsManager.sendTextMessage(pnumber2, null, message, null, null)


    }
    fun sendSMSAlarm(v : View) {
        val smsManager = SmsManager.getDefault()
        val sharedPrefs = getDefaultSharedPreferences(this)
        val uname = sharedPrefs.getString("username", "NULL")
        val pnumberA = sharedPrefs.getString("phonenumberalarm", "123")
        val pnumberA2 = sharedPrefs.getString("phonenumberalarm2", "123")
        val message = "POZOR ALARM $uname, moja poloha je: ${lastLocation.latitude}, " +
                "${lastLocation.longitude}"
        smsManager.sendTextMessage(pnumberA, null, message, null, null)
        smsManager.sendTextMessage(pnumberA2, null, message, null, null)

    }
    fun sendSMSUrgent(v : View) {
        val smsManager = SmsManager.getDefault()
        val sharedPrefs = getDefaultSharedPreferences(this)
        val uname = sharedPrefs.getString("username", "NULL")
        val pnumberU = sharedPrefs.getString("phonenumberurgent", "123")
        val pnumberU2 = sharedPrefs.getString("phonenumberurgent2", "123")
        val message = "URGENT URGENT URGENT $uname, moja poloha je: ${lastLocation.latitude}, " +
                "${lastLocation.longitude}"
        smsManager.sendTextMessage(pnumberU, null, message, null, null)
        smsManager.sendTextMessage(pnumberU2, null, message, null, null)


    }
    //-------------- GPS
    fun displayLoc(loc : Location) {
        //lastLocationTV.text = "${"%.4f".format(loc.latitude)}, ${"%.4f".format(loc.longitude)}/$provider"
    }
    @SuppressLint("MissingPermission")
    fun gps(v : View) {
        val loc = lm.getLastKnownLocation(provider)
        if (loc != null) {
            lastLocation = loc
            displayLoc(lastLocation)
        }
    }

    //-------------- CONTACT PICKER
    fun contactPicker(v : View) {
        val contactPickerIntent = Intent(Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
    }
}