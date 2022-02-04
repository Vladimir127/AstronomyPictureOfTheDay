package com.example.apod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import coil.api.load
import com.example.apod.databinding.FragmentFullScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val ARG_TITLE = "title"
private const val ARG_URL = "url"

class FullScreenFragment : Fragment() {
    private var title: String? = null
    private var url: String? = null

    private var _binding: FragmentFullScreenBinding? = null
    private val binding get() = _binding!!

    private var systemBarsHidden = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            url = it.getString(ARG_URL)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFullScreenBinding.inflate(inflater, container,
            false)
        initToolbar()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.load(url){
            error(R.drawable.ic_load_error)
        }

        binding.imageView.setOnClickListener{
            toggleSystemBars(view)
        }
    }

    private fun toggleSystemBars(view: View) {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(view) ?: return

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        if (systemBarsHidden) {
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.visibility = View.VISIBLE
        } else {
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            binding.toolbar.visibility = View.GONE
        }

        systemBarsHidden = !systemBarsHidden
    }

    private fun initToolbar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home){
            activity?.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navigation)
        bottomNavigationView?.visibility = View.VISIBLE
    }

    companion object {
        /**
         * Используется для создания экземпляра фрагмента с параметрами
         *
         * @param title Заголовок записи (название изображения)
         * @param url Ссылка на изображение
         * @return Новый экземпляр фрагмента DetailFragment.
         */
        @JvmStatic fun newInstance(title: String?, url: String?) =
                FullScreenFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_TITLE, title)
                        putString(ARG_URL, url)
                    }
                }
    }
}