package com.example.apod

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.apod.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        initNightModeAndTheme()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initToolbar()
        initNavigation()
    }

    override fun onResume() {
        super.onResume()

        val itemId = binding.navigation.selectedItemId
        chooseFragment(itemId)
    }

    private fun initNightModeAndTheme() {
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        val nightMode: Boolean =
            sharedPreferences.getBoolean("night_mode", false)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate
                    .MODE_NIGHT_NO
            )
        }

        val color = sharedPreferences.getString("color", "blue")
        if (color.equals("orange")){
            setTheme(R.style.OrangeTheme)
        } else if (color.equals("green")){
            setTheme(R.style.GreenTheme)
        } else {
            setTheme(R.style.BlueTheme)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.visibility = View.GONE
    }

    private fun initNavigation() {
        binding.navigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            chooseFragment(it.itemId)
            return@OnItemSelectedListener true
        })
    }

    private fun chooseFragment(itemId: Int) {
        if (itemId == R.id.navigation_pod) {
            loadFragment(PodFragment())
            binding.toolbar.visibility = View.GONE
        } else if (itemId == R.id.navigation_wikipedia) {
            loadFragment(CardsListFragment())
            binding.toolbar.apply {
                visibility = View.VISIBLE
                title = getString(R.string.navigation_ribbon)
            }
        } else {
            loadFragment(SettingsFragment())
            binding.toolbar.apply {
                visibility = View.VISIBLE
                title = getString(R.string.navigation_settings)
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment
        ).commitNow()
    }
}