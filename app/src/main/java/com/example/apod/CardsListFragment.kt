package com.example.apod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apod.databinding.FragmentListBinding

class CardsListFragment : Fragment(), CardsListContract.View {
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: CardsListContract.Presenter

    private lateinit var cardsList: List<PodServerResponseData>

    private val adapter = CardsListAdapter()

    private lateinit var scrollListener: RecyclerViewLoadMoreScroll

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()

        presenter = CardsListPresenter()
        presenter.attach(this)
        presenter.onCreate()
    }

    private fun initRecyclerView(){
        binding.recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager

        scrollListener = RecyclerViewLoadMoreScroll(layoutManager)
        scrollListener.setOnLoadMoreListener(object : OnLoadMoreListener{
            override fun onLoadMore() {
                loadMoreData()
            }
        })
        binding.recyclerView.addOnScrollListener(scrollListener)
    }

    fun loadMoreData(){
        adapter.addLoadingView()
        presenter.onScroll()
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

    override fun addData(data: List<PodServerResponseData>) {
        adapter.removeLoadingView()
        adapter.addData(data)
        scrollListener.setLoaded()
    }
}