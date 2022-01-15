package com.example.apod

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()

        // TODO: Если поставить здесь точку останова, то всё работает
        //  нормально: каждый раз при изменении цвета и пересоздании активности
        //  программа заходит сюда, регистрирует обработчик и снова ждёт
        //  изменения цвета. Цвет на экране меняется. Если же точки останова
        //  нет, то после третьего изменения цвета программа перестаёт сюда
        //  заходить, обработчик не регистрируется, и при дальнейшем
        //  изменении цвета на экране ничего не меняется.
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