package com.example.apod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.apod.databinding.FragmentListBinding

class ListFragment : Fragment(), ListContract.View {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: ListContract.Presenter

    private lateinit var cardsList: List<PodServerResponseData>

    private val adapter = CardsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        presenter = ListPresenter()
        presenter.attach(this)
        presenter.onCreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showError(error: String) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    override fun renderData(data: List<PodServerResponseData>) {
        cardsList = data
        adapter.setData(data)
    }
}