package com.example.apod

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import coil.api.load
import com.example.apod.databinding.FragmentDetailBinding
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

        binding.toolbarLayout.title = podData?.title
        binding.detailContainer.textViewDescription.text = podData?.explanation

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
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
            return true
        } else if (item.itemId == R.id.action_share) {
            Utils.share(requireContext(), resources, podData)
            return true
        } else if (item.itemId == R.id.action_download) {
            checkPermission()
            return true
        }
        return super.onOptionsItemSelected(item)
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