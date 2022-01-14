package com.example.apod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.api.load
import com.example.apod.databinding.FragmentPodBinding

class PodFragment : Fragment(), PodContract.View {

    private var _binding: FragmentPodBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: PodContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Получаем presenter, сохранённый в методе onRetainCustomNonConfigurationInstance()
        // при пересоздании Activity. Если presenter = null, создаём его заново.
        // TODO: Что такое as с вопросительным знаком?
        // lastCustomNonConfigurationInstance объявлен устаревшим (deprecated),
        // потому что теперь разработчики Android топят за ЬММЬ и рекомендуют
        // пользоватлься этим паттерном - хранить данные, которые нужно
        // сохранить при повороте экрана, во ViewModel.
        // TODO: Как добраться до lastCustomNonConfigurationInstance во
        //  фрагменте?
        //presenter = lastCustomNonConfigurationInstance as? PodContract.Presenter ?: PodPresenter()
        presenter = PodPresenter()
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

    override fun renderData(serverResponseData: PodServerResponseData) {
        val url = serverResponseData.hdurl
        if (url.isNullOrEmpty()) {
            showError(getString(R.string.error_bad_link))
        } else {
            //Coil в работе: достаточно вызвать у нашего ImageView
            //нужную extension-функцию и передать ссылку и заглушки для placeholder
            binding.imageView.load(url) {
                    lifecycle(lifecycle)
                    error(R.drawable.ic_load_error)
                    //placeholder(R.drawable.ic_no_photo_vector)
                }
        }

        val title = serverResponseData.title

        binding.textView.text = title
    }
}