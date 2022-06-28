package com.example.apod.ui.color

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import androidx.fragment.app.DialogFragment
import androidx.preference.ListPreference
import com.example.apod.ui.MainActivity

class ColorPreference(context: Context, attrs: AttributeSet): ListPreference
    (context, attrs) {

    override fun onClick() {
        // Получаем из настройки текущее выбранное значение, а из него - его
        // индекс.
        val value = value
        val index = findIndexOfValue(value)

        // Создаём и вызываем ColorDialogFragment, передавая в него
        // дополнительные данные: заголовок для отображения в диалоговом
        // окне, массивы записей и их названий, а также индекс выбранного в
        // данный момент значения, чтобы диалог поставил на соответствующем
        // кружочке галку.
        val dialogFragment: DialogFragment =
            ColorDialogFragment.newInstance(Bundle().apply {
                putString(ColorDialogFragment.EXTRA_TITLE_KEY, title.toString())
                putCharSequenceArray(
                    ColorDialogFragment.EXTRA_ENTRIES_KEY,
                    entries
                )
                putCharSequenceArray(
                    ColorDialogFragment.EXTRA_VALUES_KEY,
                    entryValues
                )
                putInt(ColorDialogFragment.EXTRA_INDEX_KEY, index)
            })

        dialogFragment.show(
            (context as MainActivity).supportFragmentManager,
            "transactionTag"
        )
    }
}