package com.example.apod

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.apod.databinding.FragmentListBinding
import com.example.apod.databinding.FragmentSlide1Binding

class Slide1Fragment : Fragment() {
    private var _binding: FragmentSlide1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSlide1Binding.inflate(inflater, container, false)
        val font = Utils.getFont(requireContext())
        binding.textViewTitle.typeface = font
        binding.textViewSubtitle.typeface = font
        return binding.root
    }
}