package com.example.cvicenie8
import android.os.Bundle
import android.preference.PreferenceActivity

// https://developer.android.com/reference/android/preference/PreferenceActivity
// https://www.viralpatel.net/android-preferences-activity-example/

@Suppress("DEPRECATION")
class SettingActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}