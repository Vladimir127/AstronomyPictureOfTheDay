package com.example.apod.ui.pod

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import coil.api.load
import com.example.apod.*
import com.example.apod.databinding.FragmentPodStartBinding
import com.example.apod.domain.PodServerResponseData
import com.example.apod.domain.REQUEST_CODE
import com.example.apod.domain.Utils
import com.example.apod.ui.MainActivity

class PodFragment : Fragment(), PodContract.View {

    private var _binding: FragmentPodStartBinding? = null
    private val binding get() = _binding!!

    private lateinit var presenter: PodContract.Presenter

    /** Флаг, определяющий, показывать текст или скрывать */
    private var show = false

    /** Текущая запись о сегодняшнем фото дня */
    private lateinit var podData: PodServerResponseData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodStartBinding.inflate(inflater, container, false)

        initTextViews()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView.setOnClickListener {
            if (show) hideComponents() else
                showComponents()
        }

        binding.imageButtonShare.setOnClickListener {
            Utils.share(requireContext(), resources, podData)
        }

        binding.imageButtonDownload.setOnClickListener {
            checkPermission()
        }

        binding.imageButtonWallpaper.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.app_name))
                .setMessage(
                    getString(R.string.title_set_wallpaper)
                )
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    Utils.setAsWallpaper(requireContext(), podData)
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }

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

    /**
     * Устанавливает элементам TextView шрифт, считанный из настроек
     */
    private fun initTextViews() {
        // Устанавливаем шрифт
        val font = Utils.getFont(requireContext())
        binding.textViewAuthor.typeface = font
        binding.textViewDate.typeface = font
        binding.textViewDescription.typeface = font
        binding.textViewShowDescription.typeface = font
        binding.textViewTitle.typeface = font
    }

    /**
     * Проверяет разрешения для загрузки изображений. При необходимости
     * запрашивает их. Если разрешения получены, загружает изображения.
     */
    private fun checkPermission() {
        val activity = requireActivity() as MainActivity

        activity.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {

                    //Доступ к хранилищу на телефоне есть, загружаем изображение
                    Utils.download(podData)
                }

                // Отобразим пояснение перед запросом разрешения
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) -> {
                    AlertDialog.Builder(it)
                        .setTitle(getString(R.string.title_access_storage))
                        .setMessage(
                            getString(R.string.text_access_storage_explanation)
                        )
                        .setPositiveButton(getString(R.string.dialog_acess_storage_allow)) { _, _ ->
                            requestPermission()
                        }
                        .setNegativeButton(getString(R.string.dialog_access_storage_deny)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
                else -> {
                    //Запрашиваем разрешение
                    requestPermission()
                }
            }
        }
    }

    /**
     * Запрашивает разрешения на запись в хранилище
     */
    private fun requestPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE
        )
    }

    /**
     * Обратный вызов после получения разрешений от пользователя
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                // Проверяем, дано ли пользователем разрешение по нашему
                // запросу. Если дано, загружаем изображение
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Utils.download(podData)
                } else {
                    // Если пользователь не дал разрешения, поясняем, что
                    // скачать изображение не получится
                    context?.let {
                        AlertDialog.Builder(it)
                            .setTitle(getString(R.string.title_access_storage))
                            .setMessage(
                                getString(R.string.text_access_storage_denied)
                            )
                            .setNegativeButton(getString(R.string.dialog_close)) { dialog, _ -> dialog.dismiss() }
                            .create()
                            .show()
                    }
                }
                return
            }
        }
    }

    private fun showComponents() {
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
        TransitionManager.beginDelayedTransition(
            binding.constraintContainer,
            transition
        )

        // А теперь берём этот конечный набор ограничений
        // и применяем его к начальному контейнеру.
        constraintSet.applyTo(binding.constraintContainer)
    }

    private fun hideComponents() {
        show = false

        val constraintSet = ConstraintSet()
        constraintSet.clone(context, R.layout.fragment_pod_start)

        val transition = ChangeBounds()
        transition.interpolator = AnticipateOvershootInterpolator(1.0f)
        transition.duration = 1200

        TransitionManager.beginDelayedTransition(
            binding.constraintContainer,
            transition
        )
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
        podData = serverResponseData

        val url = serverResponseData.url
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