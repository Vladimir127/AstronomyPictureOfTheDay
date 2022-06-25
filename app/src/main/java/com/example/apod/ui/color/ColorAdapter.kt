package com.example.apod.ui.color

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.apod.R

/**
 * Адаптер для отображения таблицы цветов в настройках.
 */
class ColorAdapter(
    private val context: Context?,
    private val data: List<ColorItem>
) :  BaseAdapter() {

    /**
     * Устанавливает нужному цветному кружочку свойство Checked в true
     * (отображает поверх него галочку), а всем остальным - в false
     *
     * @param index Индекс нужного кружочка
     */
    fun setChecked(index: Int) {
        if (index >= 0) {
            for (item in data) {
                item.isChecked = false
            }
            val item = getItem(index)
            item.isChecked = true
        }
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): ColorItem {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val view: View
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.color_grid_item, null)
            val viewHolder = ViewHolder()
            viewHolder.imageView = view.findViewById(R.id.color_image_view)
            view.tag = viewHolder
        } else {
            view = convertView
        }
        val holder = view.tag as ViewHolder
        val item = getItem(position)
        if (item != null) {
            // Меняем цвет кружочка. У ImageView в каждой ячейке таблицы этот
            // кружочек (Drawable resource file) установлен в качестве
            // значения свойства Background, а галка, если нужно,
            // устанавливается в качестве значения свойства Src.
            // Если мы просто возьмём и присвоим свойству Background значение
            // цвета, мы потеряем кружок и получим просто прямоугольное
            // изображение, закрашенное нужным цветом. Поэтому нам надо
            // получить сам кружок, перекрасить его и установить изображению
            // в качестве background.
            val drawable =
                context?.getDrawable(R.drawable.color_grid_item_background)

            // TODO: На странице StackOverflow, на которой я подсмотрел
            //  решение этой задачи (https://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically),
            //  прописаны сразу три варианта приведения
            //  типа Drawable: это может оказаться GradientDrawable,
            //  ShapeDrawable и ColorDrawable. От чего это зависит и нужно ли
            //  тут проверять все три варианта?
            val gradientDrawable = drawable as GradientDrawable
            gradientDrawable.setColor(item.value)
            holder.imageView!!.background = gradientDrawable
            if (item.isChecked) {
                holder.imageView!!.setImageResource(R.drawable.ic_check)
            }
        }
        return view
    }

    internal class ViewHolder {
        var imageView: ImageView? = null
    }
}