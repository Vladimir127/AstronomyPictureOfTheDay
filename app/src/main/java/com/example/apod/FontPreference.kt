package com.example.apod

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.preference.ListPreference

class FontPreference(context: Context?, attrs: AttributeSet?) :
    ListPreference(context, attrs) {

    override fun onClick() {
        // Инициализируем массив путей к файлам шрифтов - он нужен для того,
        // чтобы отобразить каждую строку в списке соответствующим шрифтом
        val paths: Array<String> =
            context.resources.getStringArray(R.array.font_paths)
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.font_style)
        builder.setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }

        // Создаём коллекцию пунктов со шрифтами для отображения в диалоге.
        // Заполняем её объектами, передавая каждому из массивов название и
        // путь к файлу шрифта
        val fontItems: MutableList<FontItem> = ArrayList()
        for (i in entries.indices) {
            val item = FontItem(
                entries[i].toString(),
                paths[i]
            )
            fontItems.add(item)
        }

        // Получаем из настройки текущее выбранное значение, а из него - его
        // индекс.
        //val value = value
        val index = findIndexOfValue(value)

        // Создаём адаптер и передаём туда этот индекс, чтобы установить
        // соответствующему RadioButton значение Checked.
        val adapter = FontAdapter(
            context,
            R.layout.font_list_item, fontItems
        )
        adapter.setChecked(index)

        builder.setSingleChoiceItems(adapter, index) {_, _ ->}

        val dialog = builder.create()

        // Устанавливаем адаптеру обработчик нажатия с помощью колбэка,
        // поскольку стандартным способом это сделать не удалось.
        adapter.setOnItemClickListener(object :
            FontAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val choice = entryValues[position].toString()
                value = choice
                dialog.dismiss()
            }
        })
        dialog.show()
    }
}