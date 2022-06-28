package com.example.apod

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.*

class ColorDialogFragment : DialogFragment() {
    private lateinit var title: String
    private lateinit var entries: Array<CharSequence>
    private lateinit var entryValues: Array<CharSequence>
    private var index: Int = 0

    companion object {
        const val EXTRA_TITLE_KEY = "EXTRA_TITLE_KEY"
        const val EXTRA_INDEX_KEY = "EXTRA_INDEX_KEY"
        const val EXTRA_ENTRIES_KEY = "EXTRA_ENTRIES_KEY"
        const val EXTRA_VALUES_KEY = "EXTRA_VALUES_KEY"

        @JvmStatic
        fun newInstance(bundle: Bundle): ColorDialogFragment {
            val fragment = ColorDialogFragment()

            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (arguments != null) {
            title = requireArguments().getString(EXTRA_TITLE_KEY).toString()
            entries =
                requireArguments().getCharSequenceArray(EXTRA_ENTRIES_KEY) as Array<CharSequence>
            entryValues =
                requireArguments().getCharSequenceArray(EXTRA_VALUES_KEY) as
                        Array<CharSequence>
            index = requireArguments().getInt(EXTRA_INDEX_KEY)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Инициализируем массив самих цветов
        val colors = requireContext().resources.getIntArray(R.array.grid_colors)

        // Создаём коллекцию цветных кружочков для отображения в диалоге.
        // Заполняем её объектами, передавая каждому из массивов название и
        // код цвета
        val colorItems: MutableList<ColorItem> = ArrayList<ColorItem>()
        for (i in entries.indices) {
            val item = ColorItem(
                entries[i].toString(),
                colors[i]
            )
            colorItems.add(item)
        }

        // Создаём адаптер и передаём туда этот индекс, чтобы установить
        // соответствующему кружочку значение Checked.
        val adapter = ColorAdapter(
            context,
            colorItems
        )
        adapter.setChecked(index)

        // Вытаскиваем макет диалога
        // https://stackoverflow.com/questions/15151783/stackoverflowerror-when-trying-to-inflate-a-custom-layout-for-an-alertdialog-ins
        val contentView: View = requireActivity().layoutInflater.inflate(
            R.layout.dialog_select_color,
            null
        )

        // Получаем из макета диалога GridView и устанавливаем ему адаптер,
        // чтобы в таблице отобразились цветные кружочки
        val gridView = contentView.findViewById<GridView>(R.id.grid_view)
        gridView.adapter = adapter

        val builder: AlertDialog.Builder =
            AlertDialog.Builder(requireActivity())
                .setTitle(title)
                .setView(contentView)
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ -> dismiss() }
        val dialog = builder.create()

        gridView.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                val color = entryValues[position].toString()
                dialog.dismiss()

                // Смену цвета пришлось осуществлять вот таким извращённым
                // образом: поскольку в DialogFragment нельзя передать ссылку
                // на объект ColorPreference, из которого он был вызван,
                // приходится оповещать о выборе саму активность, которая
                // специально для этого реализует интерфейс Contract.
                getContract()?.changeColor(color)
            }
        return dialog
    }

    private fun getContract(): Contract? {
        return activity as Contract?
    }

    internal interface Contract {
        fun changeColor(color: String)
    }
}