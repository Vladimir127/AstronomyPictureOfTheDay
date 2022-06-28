package com.example.apod.ui

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.apod.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()

        registerChangeListener()
    }

    private fun registerChangeListener() {
        // Далее мы устанавливаем обработчик изменения настроек. Это нужно
        // для того, чтобы сразу после изменения некоторых настроек
        // (например, языка или цвета) сразу же изменился внешний вид самой
        // SettingsActivity.
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val listener =
            OnSharedPreferenceChangeListener { prefs: SharedPreferences, key: String ->
                if (key == "night_mode") {
                    val isNightModeEnabled = prefs.getBoolean(key, false)

                    if (isNightModeEnabled) {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES
                        )
                    } else {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO
                        )
                    }
                }

                if (key == "color") {
                    activity?.recreate()
                }
            }

        preferences.registerOnSharedPreferenceChangeListener(listener)
    }
}