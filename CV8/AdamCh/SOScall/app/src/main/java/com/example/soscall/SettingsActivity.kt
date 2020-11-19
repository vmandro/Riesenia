package com.example.soscall

import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.Preference
import android.preference.PreferenceActivity
import android.widget.CheckBox

class SettingsActivity: PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        val box1 = findPreference("warning") as CheckBoxPreference
        val box2 = findPreference("alarm") as CheckBoxPreference
        val box3 = findPreference("urgent") as CheckBoxPreference

        box1.setOnPreferenceChangeListener { preference, any ->
            box1.isChecked = true
            box2.isChecked = false
            box3.isChecked = false
            true
        }

        box2.setOnPreferenceChangeListener { preference, any ->
            box2.isChecked = true
            box1.isChecked = false
            box3.isChecked = false
            true
        }

        box3.setOnPreferenceChangeListener { preference, any ->
            box3.isChecked = true
            box1.isChecked = false
            box2.isChecked = false
            true
        }
    }
}