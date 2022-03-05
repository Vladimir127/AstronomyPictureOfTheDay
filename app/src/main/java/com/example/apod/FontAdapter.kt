package com.example.apod

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton

/**
 * Адаптер для отображения списка шрифтов в настройках. Название каждого
 * шрифта отображает соответствующим шрифтом.
 */
class FontAdapter(
    context: Context,
    resource: Int,
    private val data: List<FontItem>
) :
    ArrayAdapter<FontItem?>(context, resource, data) {

    // Слушатель будет устанавливаться извне
    private var itemClickListener: OnItemClickListener? = null

    /**
     * Устанавливает переключателю (RadioButton) свойство Checked в true, а
     * всем остальным - в false
     *
     * @param index Индекс нужного переключателя
     */
    fun setChecked(index: Int) {
        if (index >= 0) {
            for (item in data) {
                item.isChecked = false
            }
            val item = getItem(index)
            item?.isChecked = true
        }
    }

    override fun getItem(position: Int): FontItem {
        return data[position]
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view: View
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.font_list_item, null)
            val viewHolder = ViewHolder()
            viewHolder.radioButton = view.findViewById(R.id.radio_button_font)
            view.tag = viewHolder
        } else {
            view = convertView
        }

        // При нажатии на View вызываем колбэк, который будет обрабатываться
        // снаружи
        view.setOnClickListener {
            if (itemClickListener != null) {
                itemClickListener!!.onItemClick(position)
            }
        }
        val holder = view.tag as ViewHolder
        val item = getItem(position)
        if (item != null) {
            holder.radioButton?.text = item.name
            holder.radioButton?.isChecked = item.isChecked

            // Самый первый элемент массива - шрифт по умолчанию (sans-serif),
            // поэтому его задаём с помощью метода create(). Остальные - с помощью
            // метода createFromAsset().
            val font: Typeface = if (position == 0) {
                Typeface.create(item.path, Typeface.NORMAL)
            } else {
                Typeface.createFromAsset(
                    context.assets,
                    item.path
                )
            }
            holder.radioButton?.typeface = font
        }
        return view
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    internal class ViewHolder {
        var radioButton: RadioButton? = null
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}