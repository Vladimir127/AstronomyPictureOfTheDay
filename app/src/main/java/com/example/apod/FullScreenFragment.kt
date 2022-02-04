package com.example.apod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.example.apod.databinding.FragmentFullScreenBinding

private const val ARG_TITLE = "title"
private const val ARG_URL = "url"

class FullScreenFragment : Fragment() {
    private var title: String? = null
    private var url: String? = null

    private var _binding: FragmentFullScreenBinding? = null
    private val binding get() = _binding!!

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