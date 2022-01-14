package com.example.apod

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.example.apod.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), PodContract.View {
    private lateinit var binding: ActivityMainBinding

    private lateinit var presenter: PodContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Получаем presenter, сохранённый в методе onRetainCustomNonConfigurationInstance()
        // при пересоздании Activity. Если presenter = null, создаём его заново.
        // TODO: Что такое as с вопросительным знаком?
        // lastCustomNonConfigurationInstance объявлен устаревшим (deprecated),
        // потому что теперь разработчики Android топят за ЬММЬ и рекомендуют
        // пользоватлься этим паттерном - хранить данные, которые нужно
        // сохранить при повороте экрана, во ViewModel.
        presenter = lastCustomNonConfigurationInstance as? PodContract.Presenter
            ?: PodPresenter()
        presenter.attach(this)
        presenter.onCreate()
    }

    override fun showProgress() {
        binding.progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        binding.progressBar.visibility = View.GONE
    }

    override fun showError(error: String) {
        Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
    }

    override fun renderData(serverResponseData: PodServerResponseData) {
        val url = serverResponseData.hdurl
        if (url.isNullOrEmpty()) {
            showError(getString(R.string.error_bad_link))
        } else {
            //Coil в работе: достаточно вызвать у нашего ImageView
            //нужную extension-функцию и передать ссылку и заглушки для placeholder
            binding.imageView.load(url) {
                lifecycle(this@MainActivity)
                error(R.drawable.ic_load_error)
                //placeholder(R.drawable.ic_no_photo_vector)
            }
        }

        val title = serverResponseData.title
        binding.textView.text = title
    }
}