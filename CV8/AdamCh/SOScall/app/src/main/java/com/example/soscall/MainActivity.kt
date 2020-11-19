package com.example.soscall

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 111
    private val RESULT_SETTINGS = 222
    private val RESULT_PICK_CONTACT = 333

    lateinit var locMan: LocationManager
    lateinit var provider: String
    lateinit var lastLocation: Location
    private var currentCallerId = 0
    private var urgency = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showPrefs()
        addPermissions()
        getMyLocation()

        sosBtn.setOnClickListener {
            getMyLocation()
            sendSMS()
        }

        caller1.setOnClickListener {
            currentCallerId = 1
            contactPicker()
        }

        caller2.setOnClickListener {
            currentCallerId = 2
            contactPicker()
        }

        caller3.setOnClickListener {
            currentCallerId = 3
            contactPicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                startActivityForResult(Intent(this, SettingsActivity::class.java), RESULT_SETTINGS)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_SETTINGS -> {
                showPrefs()
                displayUrgency()
            }
            RESULT_PICK_CONTACT -> saveContact(resultCode, data)
        }
    }

    @SuppressLint("SetTextI18n")
    fun showPrefs() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        caller1.text = "${sharedPrefs.getString("username1", "caller 1")}\n${sharedPrefs.getString("phonenumber1", "")}"
        caller2.text = "${sharedPrefs.getString("username2", "caller 2")}\n${sharedPrefs.getString("phonenumber2", "")}"
        caller3.text = "${sharedPrefs.getString("username3", "caller 3")}\n${sharedPrefs.getString("phonenumber3", "")}"
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPrefs.getBoolean("useGPS", true)) {
            locMan = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            provider = LocationManager.GPS_PROVIDER
            if (!locMan.isProviderEnabled(provider)) {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            val loc = locMan.getLastKnownLocation(provider)
            if (loc != null) {
                lastLocation = loc
                displayLoc(lastLocation)
                displayUrgency()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun displayUrgency() {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPrefs.getBoolean("warning", false)) {
            urgency = "WARNING"
        } else if (sharedPrefs.getBoolean("alarm", false)) {
            urgency = "ALARM"
        } else if (sharedPrefs.getBoolean("urgent", false)) {
            urgency = "URGENT"
        }
        urgencyTV.text = "Your urgency level is set to $urgency"
        return
    }

    fun saveContact(resultCode: Int, data: Intent?) {
        val sharedPrefs = getDefaultSharedPreferences(this)
        if (resultCode == Activity.RESULT_OK) {
            try {
                var phoneNo: String? = null
                var name: String? = null
                val uri: Uri? = data?.data
                if (uri != null) {
                    var cursor = contentResolver.query(uri, null, null, null, null)
                    if (cursor != null) {
                        cursor?.moveToFirst()
                        val phoneIndex: Int =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val nameIndex: Int =
                            cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        sharedPrefs.edit().apply() {
                            putString("username$currentCallerId", cursor.getString(nameIndex))
                            putString("phonenumber$currentCallerId", cursor.getString(phoneIndex))
                            commit()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.e("Failed1", "Not able to pick contact")
            sharedPrefs.edit().apply() {
                putString("username$currentCallerId", "caller $currentCallerId")
                putString("phonenumber$currentCallerId", "")
                commit()
            }
        }
        showPrefs()
    }

    private fun addPermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val permissionsNeeded = mutableListOf<String>()
        val permissionsList = mutableListOf<String>()
        if(addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS")
        if(addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SMS")
        if(addPermission(permissionsList, Manifest.permission.INTERNET))
            permissionsNeeded.add("INTERNET")

        if (permissionsList.size > 0) {
            if (permissionsNeeded.size > 0) {
                var message = "You need to grant access to " + permissionsNeeded[0]
                showMessageOKCancel(
                    permissionsNeeded.joinToString(separator = ", ")
                ) { dialog, which -> requestPermissions(permissionsList.toTypedArray<String>(),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)}
                    return
            }
            requestPermissions(permissionsList.toTypedArray<String>(),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage("This app is asking for these permissions:\n$message")
            .setPositiveButton("OK", okListener)
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun addPermission(permissionsList: MutableList<String>, permission: String): Boolean {
        if (applicationContext.checkSelfPermission(permission)
            != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
            if (!shouldShowRequestPermissionRationale(permission)) return true
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    fun displayLoc(loc: Location?) {
        if (loc != null) {
            locationTV.text = "Your last GPS location:\n" +
                    "Latitude: %.4f".format(loc.latitude) +
                    "\n" +
                    "Longitude: %.4f".format(loc.longitude)
        }
    }

    fun contactPicker() {
        val contactPickerIntent = Intent(Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT)
    }

    fun sendSMS() {
        val smsManager = SmsManager.getDefault()
        val sharedPrefs = getDefaultSharedPreferences(this)
        var repeat = 1
        when(urgency) {
            "WARNING" -> repeat = 1
            "ALARM" -> repeat = 2
            "URGENT" -> repeat = 3
        }
        for (i in 1..3) {
            val uname = sharedPrefs.getString("username$i", "caller $i")
            val pnumber = sharedPrefs.getString("phonenumber$i", "")

            if (uname != "caller $i") {
                val message = "Pomoc $uname!\nMoja poloha je: ${lastLocation.latitude}, " + "${lastLocation.longitude}"
                for (j in 1..repeat) {
                    Log.d("test", "posielam sms")
                    smsManager.sendTextMessage(pnumber, null, message, null, null)
                }

            }
        }

    }
}