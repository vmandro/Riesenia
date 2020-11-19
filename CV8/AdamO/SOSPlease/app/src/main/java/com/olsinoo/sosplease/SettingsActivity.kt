package com.olsinoo.sosplease

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

const val TAG = "SOSAPP"

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    // ---------------------- Preference Activity as a Fragment ----------------------
    class SettingsFragment : PreferenceFragmentCompat() {

        // ---------------------- Contact IDs ----------------------
        private val contact1Picked = 111
        private val contact2Picked = 222
        private val contact3Picked = 333

        // ---------------------- Fill Preference Screen with Options ----------------------
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings, rootKey)
        }

        // ---------------------- Resume Fragment ----------------------
        override fun onResume() {
            super.onResume()
            // ---------------------- Preference Change Listeners ----------------------
            findPreference<Preference>("contact_1")?.setOnPreferenceClickListener {
                Log.d(TAG, "cont1_sett")
                val contactPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                startActivityForResult(contactPickerIntent, contact1Picked)
                true
            }

            findPreference<Preference>("contact_2")?.setOnPreferenceClickListener {
                Log.d(TAG, "cont2_sett")
                val contactPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                startActivityForResult(contactPickerIntent, contact2Picked)
                true
            }

            findPreference<Preference>("contact_3")?.setOnPreferenceClickListener {
                Log.d(TAG, "cont3_sett")
                val contactPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                )
                startActivityForResult(contactPickerIntent, contact3Picked)
                true
            }

            findPreference<EditTextPreference>("user")?.setOnPreferenceChangeListener { preference, newValue ->
                preference.title = newValue.toString()
                true
            }
            findPreference<EditTextPreference>("phone")?.setOnPreferenceChangeListener { preference, newValue ->
                preference.title = newValue.toString()
                true
            }

            // ---------------------- Values of Preferences ----------------------
            val preferences = getDefaultSharedPreferences(context)
            findPreference<EditTextPreference>("user")?.title = preferences.getString("user", "No name")
            findPreference<EditTextPreference>("phone")?.title = preferences.getString("phone", "No phone number")
            findPreference<Preference>("contact_1")?.summary = preferences.getString("contact_1_name", "")
            findPreference<Preference>("contact_2")?.summary = preferences.getString("contact_2_name", "")
            findPreference<Preference>("contact_3")?.summary = preferences.getString("contact_3_name", "")
        }

        // ---------------------- Idea z Prednasky/Cvicenia 8 ----------------------
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            Log.d(TAG, "Sett activity result")
            super.onActivityResult(requestCode, resultCode, data)
            val uri = data?.data
            if (uri != null) {
                val cursor = context?.contentResolver?.query(uri, null, null, null, null)
                if (cursor != null) {
                    cursor.moveToFirst()
                    val phoneIndex: Int =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val nameIndex: Int =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val preference = getDefaultSharedPreferences(context)
                    val editor = preference.edit()
                    when (requestCode) {
                        contact1Picked -> {
                            Log.d(TAG, "picked 1")
                            editor.putString("contact_1_name", cursor.getString(nameIndex))
                            editor.putString("contact_1_num", cursor.getString(phoneIndex))
                        }
                        contact2Picked -> {
                            Log.d(TAG, "picked 2")
                            editor.putString("contact_2_name", cursor.getString(nameIndex))
                            editor.putString("contact_2_num", cursor.getString(phoneIndex))
                        }
                        contact3Picked -> {
                            Log.d(TAG, "picked 3")
                            editor.putString("contact_3_name", cursor.getString(nameIndex))
                            editor.putString("contact_3_num", cursor.getString(phoneIndex))
                        }
                    }
                    editor.apply()
                }
                cursor?.close()
            }
        }
    }
}
