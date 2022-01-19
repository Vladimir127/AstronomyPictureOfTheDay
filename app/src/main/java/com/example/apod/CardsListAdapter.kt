package com.example.apod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load

const val VIEW_TYPE_ITEM = 0
const val VIEW_TYPE_LOADING = 1

class CardsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: ArrayList<PodServerResponseData?> = ArrayList()

    fun setData(data: List<PodServerResponseData>) {
        this.data = ArrayList(data)
        notifyDataSetChanged()
    }

    fun addData(data: List<PodServerResponseData?>) {
        this.data.addAll(data)
        notifyItemRangeInserted(this.data.size - 1, data.size)
    }

    fun addLoadingView() {
        data.add(null)
        notifyItemInserted(data.size - 1)
    }

    fun removeLoadingView() {
        if (data.size != 0) {
            data.removeAt(data.size - 1)
            notifyItemRemoved(data.size)
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Теперь у нас могут быть пункты списка двух типов: непосредственно 
        // объекты и колёсико ProgressBar, появляющееся на время загрузки, 
        // когда мы пролистываем список до конца. Последний не содержит
        // никаких данных, поэтому таким хитрым способом мы и определяем, что
        // это пункт именно такого типа.
        return if (data[position] == null) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    /**
     * В этом методе создаётся и возвращается новый элемент View для одного пункта списка
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card, parent, false)
                        as View
            )
        } else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.progress_loading, parent, false)
                        as View
            )
        }
    }

    /**
     * В этом методе происходит заполнение пункта списка данными из источника
     */
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position:
        Int
    ) {
        // Привязку выполняем только в том случае, если это обычный пункт
        // списка, так как в пункте с колёсиком привязывать нечего
        if (holder.itemViewType == VIEW_TYPE_ITEM) {
            (holder as ItemViewHolder).bind(data[position])
        }
    }

    /**
     * Этот метод вызывается layout-менеджером
     */
    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * ViewHolder для обычного пункта списка, содержащего непосредственно объект
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PodServerResponseData?) {
            itemView.apply {
                findViewById<TextView>(R.id.text_view_title).text = item?.title
                findViewById<TextView>(R.id.text_view_date).text = item?.date
                findViewById<ImageView>(R.id.image_view).load(item?.url) {
                    //lifecycle()
                    error(R.drawable.ic_load_error)
                    //placeholder(R.drawable.ic_no_photo_vector)
                }
            }
        }
    }

    /**
     * ViewHolder для специального пункта списка, содержащего колёсико
     * ProgressBar, которое будет отображаться внизу списка при загрузке данных
     */
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}