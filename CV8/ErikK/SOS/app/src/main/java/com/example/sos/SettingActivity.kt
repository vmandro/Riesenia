package com.example.sos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.provider.ContactsContract
import java.lang.Exception

@SuppressLint("ExportedPreferenceActivity")
@Suppress("DEPRECATION")
class SettingActivity : PreferenceActivity() {
    private val RESULT_PICK_CONTACT1 = 887
    private val RESULT_PICK_CONTACT2 = 888
    private val RESULT_PICK_CONTACT3 = 889

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        val contactPickerIntent = Intent(
            Intent.ACTION_PICK,
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        var p = findPreference("contact1") as Preference

        p.setOnPreferenceClickListener  {
            startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT1)
            true
        }

        p = findPreference("contact2") as Preference
        p.setOnPreferenceClickListener  {
            startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT2)
            true
        }

        p = findPreference("contact3") as Preference
        p.setOnPreferenceClickListener  {
            startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT3)
            true
        }
    }

    @SuppressLint("Recycle")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent? )
    {
        super.onActivityResult(requestCode, resultCode, data)
        var phoneIndex = 0
        var nameIndex = 0
        var cursor : Cursor? = null
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (resultCode == Activity.RESULT_OK) {
            try {
                val uri: Uri? = data?.data
                if (uri != null) {
                    cursor = contentResolver.query(uri, null, null, null, null)
                    if (cursor != null) {
                        cursor.moveToFirst()
                        phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        nameIndex  = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    }
                }
            } catch (e: Exception) {}
        }
        when (requestCode) {
            RESULT_PICK_CONTACT1 -> {
                if (cursor != null) {
                    sharedPrefs.edit().apply {
                        putString("username1", cursor.getString(nameIndex))
                        putString("phonenumber1", cursor.getString(phoneIndex))
                        apply()
                    }
                }
            }
            RESULT_PICK_CONTACT2 -> {
                if (cursor != null) {
                    sharedPrefs.edit().apply {
                        putString("username2", cursor.getString(nameIndex))
                        putString("phonenumber2", cursor.getString(phoneIndex))
                        apply()
                    }
                }
            }
            RESULT_PICK_CONTACT3 -> {
                if (cursor != null) {
                    sharedPrefs.edit().apply {
                        putString("username3", cursor.getString(nameIndex))
                        putString("phonenumber3", cursor.getString(phoneIndex))
                        apply()
                    }
                }
            }
        }
    }
}