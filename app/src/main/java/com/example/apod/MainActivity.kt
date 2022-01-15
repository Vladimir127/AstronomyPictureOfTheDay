package com.example.apod

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.apod.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate
        // .MODE_NIGHT_YES)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initToolbar()
        initNavigation()

        loadFragment(PodFragment())
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.visibility = View.GONE
    }

    private fun initNavigation() {
        binding.navigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener {
            if (it.itemId == R.id.navigation_pod) {
                loadFragment(PodFragment())
                binding.toolbar.visibility = View.GONE
                return@OnItemSelectedListener true
            } else {
                loadFragment(CardsListFragment())
                binding.toolbar.visibility = View.VISIBLE
                return@OnItemSelectedListener true
            }
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            fragment
        ).commitNow()
    }
}