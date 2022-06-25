package com.example.apod.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Typeface.BOLD
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import coil.api.load
import com.example.apod.R
import com.example.apod.databinding.FragmentDetailBinding
import com.example.apod.domain.PodServerResponseData
import com.example.apod.domain.REQUEST_CODE
import com.example.apod.domain.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val ARG_POD_DATA = "podData"

class DetailFragment : Fragment() {
    private var podData: PodServerResponseData? = null

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            podData = it.getParcelable(ARG_POD_DATA)
        }

        postponeEnterTransition()

        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.shared_image)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTextViews()

        binding.toolbarLayout.title = podData?.title

        val spannable = createSpannableString()

        binding.detailContainer.textViewDescription.text = spannable


        binding.expandedImage.transitionName = "transition_image"

        binding.expandedImage.apply {
            load(podData?.url) {
                error(R.drawable.ic_load_error)
                listener(
                    onError = { _, _ -> startPostponedEnterTransition() },
                    onSuccess = { _, _ -> startPostponedEnterTransition() }
                )
            }

            setOnClickListener {
                val bottomNavigationView =
                    activity?.findViewById<BottomNavigationView>(R.id.navigation)
                bottomNavigationView?.visibility = View.GONE

                val fragment = FullScreenFragment.newInstance(
                    podData?.title,
                    podData?.hdurl
                )

                activity?.supportFragmentManager?.beginTransaction()
                    ?.addToBackStack(null)
                    ?.addSharedElement(it, "transition_image")
                    ?.replace(R.id.container, fragment)?.commit()
            }
        }

        initToolBar()
    }

    private fun createSpannableString(): SpannableStringBuilder {
        // Отображаем заголовок крупным жирным шрифтом
        val spannable = SpannableStringBuilder(podData?.title)
        spannable.setSpan(
            AbsoluteSizeSpan(100),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            StyleSpan(BOLD),
            0,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Если указан автор, отображаем его не таким крупным и уже не жирным
        // шрифтом
        if (podData?.copyright != null) {
            spannable.append("\n\n" + podData?.copyright)

            spannable.setSpan(
                AbsoluteSizeSpan(60),
                spannable.length - podData?.copyright!!.length - 1,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Отображаем дату подчёркнутым шрифтом
        if (podData?.date != null) {
            spannable.append("\n\n" + podData?.date)

            spannable.setSpan(
                UnderlineSpan(),
                spannable.length - podData?.date!!.length - 1,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        // Отображаем описание обычным шрифтом
        spannable.append("\n\n" + podData?.explanation)

        return spannable
    }

    private fun initTextViews() {
        val font = Utils.getFont(requireContext())
        binding.detailContainer.textViewDescription.typeface = font
        binding.toolbarLayout.setCollapsedTitleTypeface(font)
        binding.toolbarLayout.setExpandedTitleTypeface(font)
    }

    private fun initToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(
            true
        )
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_scrolling, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
            R.id.action_share -> {
                Utils.share(requireContext(), resources, podData)
                return true
            }
            R.id.action_download -> {
                checkPermission()
                return true
            }
            R.id.action_wallpaper -> {
                showWallpaperConfirmationDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showWallpaperConfirmationDialog() {
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

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        /**
         * Используется для создания экземпляра фрагмента с параметрами
         *
         * @param item Выбранная запись
         * @return Новый экземпляр фрагмента DetailFragment.
         */
        @JvmStatic
        fun newInstance(item: PodServerResponseData?) =
            DetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_POD_DATA, item)
                }
            }
    }
}