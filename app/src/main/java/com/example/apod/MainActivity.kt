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

class MainActivity : AppCompatActivity(), ColorDialogFragment.Contract {
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
            sharedPreferences.getBoolean("night_mode", true)
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate
                    .MODE_NIGHT_NO
            )
        }

        val color = sharedPreferences.getString("color", "blue")
        when {
            color.equals("orange") -> {
                setTheme(R.style.OrangeTheme)
            }
            color.equals("green") -> {
                setTheme(R.style.GreenTheme)
            }
            else -> {
                setTheme(R.style.BlueTheme)
            }
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
        when (itemId) {
            R.id.navigation_pod -> {
                loadFragment(PodFragment())
                binding.toolbar.visibility = View.GONE
            }
            R.id.navigation_wikipedia -> {
                loadFragment(CardsListFragment())
                binding.toolbar.visibility = View.GONE
            }
            else -> {
                // TODO: Можно ли и у SettingsFragment сделать свой
                //  собственный AppBarLayout, а у Activity - совсем его
                //  убрать (учитывая, что у SettingsFragment не обычный
                //  макет, а root_preferences.xml с корневым элементом
                //  PreferenceScreen)?
                loadFragment(SettingsFragment())
                binding.toolbar.apply {
                    visibility = View.VISIBLE
                    title = getString(R.string.navigation_settings)
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment
        ).commitNow()
    }

    override fun changeColor(color: String){
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(this)

        sharedPreferences.edit().putString("color", color).apply()
    }
}