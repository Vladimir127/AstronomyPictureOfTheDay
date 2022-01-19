package com.example.apod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load

class CardsListAdapter : RecyclerView.Adapter<CardsListAdapter.ViewHolder>() {

    private var data: List<PodServerResponseData> = listOf()

    fun setData(data: List<PodServerResponseData>) {
        // При присвоении пришлось сразу же добавить сортировку по убыванию,
        // так как API присылает данные шиворот-навыворот
        this.data = data.sortedByDescending { it.date }

        notifyDataSetChanged()
    }

    /**
     * В этом методе создаётся и возвращается новый элемент View для одного пункта списка
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card, parent, false)
                    as View
        )
    }

    /**
     * В этом методе происходит заполнение пункта списка данными из источника
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    /**
     * Этот метод вызывается layout-менеджером
     */
    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PodServerResponseData) {
            itemView.apply {
                findViewById<TextView>(R.id.text_view_title).text = item.title
                findViewById<TextView>(R.id.text_view_date).text = item.date
                findViewById<ImageView>(R.id.image_view).load(item.url) {
                    //lifecycle()
                    error(R.drawable.ic_load_error)
                    //placeholder(R.drawable.ic_no_photo_vector)
                }
            }
        }
    }
}