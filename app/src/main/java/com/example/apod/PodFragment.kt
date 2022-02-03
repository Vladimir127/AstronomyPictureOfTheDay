package com.example.apod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import coil.api.load
import com.example.apod.databinding.FragmentPodStartBinding

class PodFragment : Fragment(), PodContract.View {

    private var _binding: FragmentPodStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: PodContract.Presenter

    /** Флаг, определяющий, показывать текст или скрывать */
    private var show = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener{ if (show) hideComponents() else
            showComponents() }

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

    private fun showComponents(){
        show = true

        // Создаём ConstraintSet - этот класс позволяет
        // программно создавать ограничения для ConstraintLayout
        val constraintSet = ConstraintSet()

        // Метод clone копирует ограничения из данного макета.
        // То есть тут мы считываем ограничения из конечного макета и записываем их в этот набор.
        constraintSet.clone(context, R.layout.fragment_pod_end)

        // Создаём переход типа ChangeBounds (перемещение объектов),
        // устанавливаем ему длительность и интерполятор.
        // AnticipateOvershootInterpolator позволяет добиться анимации отскока
        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1200

        // Как обычно, запускаем анимацию.
        TransitionManager.beginDelayedTransition(binding.constraintContainer, transition)

        // А теперь берём этот конечный набор ограничений
        // и применяем его к начальному контейнеру.
        constraintSet.applyTo(binding.constraintContainer)
    }

    private fun hideComponents(){
        show = false

        val constraintSet = ConstraintSet()
        constraintSet.clone(context, R.layout.fragment_pod_start)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1200

        TransitionManager.beginDelayedTransition(binding.constraintContainer, transition)
        constraintSet.applyTo(binding.constraintContainer)
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

        binding.textViewTitle.text = title

        if (serverResponseData.copyright.isNullOrEmpty()) {
            binding.textViewAuthor.text = getString(R.string.app_name)
        } else {
            binding.textViewAuthor.text = serverResponseData.copyright
        }

        binding.textViewDescription.text = serverResponseData.explanation
        binding.textViewDate.text = serverResponseData.date
    }
}