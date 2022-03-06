package com.codehospital.worldclocks

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class WcPreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .commit()
    }
}
