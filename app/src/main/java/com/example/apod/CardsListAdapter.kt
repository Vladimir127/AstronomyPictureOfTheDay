package com.example.apod

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import java.util.regex.Matcher
import java.util.regex.Pattern

const val VIEW_TYPE_LOADING = 0
const val VIEW_TYPE_IMAGE = 1
const val VIEW_TYPE_VIDEO = 2

class CardsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: ArrayList<PodServerResponseData?> = ArrayList()
    var itemClickListener: OnItemClickListener? = null

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
        // Теперь у нас могут быть пункты списка трёх типов: фотографии, видео
        // и колёсико ProgressBar, появляющееся на время загрузки, когда мы
        // пролистываем список до конца. Сначала проверяем его: этот пункт
        // списка не содержит никаких данных, поэтому таким хитрым способом мы
        // и определяем, что это пункт именно такого типа. Если данные есть,
        // то по полю mediaType мы определяем, фото это или видео.
        return when {
            data[position] == null -> {
                VIEW_TYPE_LOADING
            }
            data[position]!!.mediaType.equals("video") -> { // TODO: в константу
                VIEW_TYPE_VIDEO
            }
            else -> {
                VIEW_TYPE_IMAGE
            }
        }
    }

    /**
     * В этом методе создаётся и возвращается новый элемент View для одного пункта списка
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            ImageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card_image, parent, false)
                        as View
            )
        } else if (viewType == VIEW_TYPE_VIDEO) {
            VideoViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_card_video, parent, false)
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
        if (holder.itemViewType == VIEW_TYPE_IMAGE) {
            (holder as ImageViewHolder).bind(data[position])
        } else if (holder.itemViewType == VIEW_TYPE_VIDEO) {
            (holder as VideoViewHolder).bind(data[position])
        }
    }

    /**
     * Этот метод вызывается layout-менеджером
     */
    override fun getItemCount(): Int {
        return data.size
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener?) {
        this.itemClickListener = itemClickListener
    }

    /**
     * Интерфейс для обработки нажатий
     */
    interface OnItemClickListener {
        fun onItemClick(item: PodServerResponseData?)
    }

    /**
     * ViewHolder для обычного пункта списка, содержащего фото
     */
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PodServerResponseData?) {
            itemView.apply {
                findViewById<TextView>(R.id.text_view_title).text = item?.title
                findViewById<TextView>(R.id.text_view_date).text = item?.date
                findViewById<ImageView>(R.id.image_view).load(item?.url) {
                    //lifecycle()
                    error(R.drawable.ic_load_error)
                    //placeholder(R.drawable.ic_no_photo_vector)
                }
                setOnClickListener {
                    if (itemClickListener != null) {
                        itemClickListener?.onItemClick(item)
                    }
                }
            }
        }
    }

    /**
     * ViewHolder для обычного пункта списка, содержащего видео
     */
    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: PodServerResponseData?) {
            val videoId = getYouTubeId(item?.url!!) // TODO: Разобраться с null
            val imageUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

            itemView.apply {
                findViewById<TextView>(R.id.text_view_title).text = item.title
                findViewById<TextView>(R.id.text_view_date).text = item.date
                findViewById<ImageView>(R.id.image_view).load(imageUrl) {
                    error(R.drawable.ic_load_error)
                }
            }
        }

        /**
         * Получает ID видео с Ютуба для дальнейшего получения его обложки
         * (thumbnail) и отображения её в списке. Например, для адреса
         * "http://www.youtube.com/watch?v=0zM4nApSvMg" ID будет равен
         * "0zM4nApSvMg". Метод скачан из интернета, использует регулярные выражения.
         * @param youTubeUrl Ссылка на ролик на YouTube
         * @return ID ролика
         */
        private fun getYouTubeId(youTubeUrl: String): String? {
            val pattern =
                "(?<=youtu.be/|watch\\?v=|/videos/|embed/)[^#&?]*"
            val compiledPattern: Pattern = Pattern.compile(pattern)
            val matcher: Matcher = compiledPattern.matcher(youTubeUrl)
            return if (matcher.find()) {
                matcher.group()
            } else {
                "error"
            }
        }

        /*
        // Ещё один метод для того же
        public String extractYoutubeId(String url) throws MalformedURLException {
            String query = new URL(url).getQuery();
            String[] param = query.split("&");
            String id = null;
            for (String row : param) {
                String[] param1 = row.split("=");
                if (param1[0].equals("v")) {
                    id = param1[1];
                }
            }
            return id;
        }
         */
    }

    /**
     * ViewHolder для специального пункта списка, содержащего колёсико
     * ProgressBar, которое будет отображаться внизу списка при загрузке данных
     */
    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}