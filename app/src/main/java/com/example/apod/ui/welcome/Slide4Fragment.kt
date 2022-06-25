package com.example.apod.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apod.databinding.FragmentSlide4Binding
import com.example.apod.domain.Utils

class Slide4Fragment : Fragment() {
    private var _binding: FragmentSlide4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlide4Binding.inflate(inflater, container, false)
        val font = Utils.getFont(requireContext())
        binding.textViewTitle.typeface = font
        binding.textViewSubtitle.typeface = font
        return binding.root
    }
}