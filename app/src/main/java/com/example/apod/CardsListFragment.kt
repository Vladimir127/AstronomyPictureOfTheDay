package com.example.apod

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apod.databinding.FragmentListBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker

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
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cards_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_select_range){
            openDatePicker()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openDatePicker() {
        // Создаём ограничения, чтобы нельзя было выбрать дату позже
        // сегодняшнего дня
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())

        // Создаём Date Range Picker
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.select_dates))
                // Диапазон по умолчанию - от начала месяца до сегодняшнего дня
                .setSelection(Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()))
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            // it - это пара (объект Pair) двух выбранных дат, представленных
            // в виде чисел Long
            val firstDate = it.first
            val secondDate = it.second

            // Отключаем обработчик прокрутки, чтобы он не подгружал
            // дополнительные записи, когда у нас выбран диапазон
            scrollListener.isEnabled = false

            presenter.onDateRangeSelected(firstDate, secondDate)
        }

        dateRangePicker.show(parentFragmentManager, "tag")
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