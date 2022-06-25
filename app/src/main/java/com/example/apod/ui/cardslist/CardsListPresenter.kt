package com.example.apod.ui.cardslist

import com.example.apod.BuildConfig
import com.example.apod.domain.PodRetrofitImpl
import com.example.apod.domain.PodServerResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardsListPresenter : CardsListContract.Presenter {
    private val retrofitImpl: PodRetrofitImpl = PodRetrofitImpl()

    private lateinit var calendar: Calendar
    private lateinit var simpleDateFormat: SimpleDateFormat

    private val countEntries = -9

    /**
     * В этом поле хранится дата последнего загруженного объекта, чтобы
     * Presenter знал, с какого дня подгружать новую порцию изображений
     */
    private lateinit var lastStartDate: Date

    private var view: CardsListContract.View? = null

    override fun attach(view: CardsListContract.View) {
        this.view = view
    }

    override fun onCreate() {
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val endDate = simpleDateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, countEntries)
        // TODO: Проверить, как это будет работать, если дата находится в прошлом году
        lastStartDate = calendar.time
        val startDate = simpleDateFormat.format(lastStartDate)

        sendServerRequest(startDate, endDate, true)
    }

    override fun onScroll() {
        calendar.time = lastStartDate
        //TODO: Нужно ли здесь это переприсваивание?

        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val endDate = simpleDateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, countEntries)
        lastStartDate = calendar.time
        val startDate = simpleDateFormat.format(lastStartDate)

        sendServerRequest(startDate, endDate, false)
    }

    override fun onDateRangeSelected(firstDateMillis: Long, secondDateMillis: Long) {
        val startDate = simpleDateFormat.format(firstDateMillis)
        val endDate = simpleDateFormat.format(secondDateMillis)

        sendServerRequest(startDate, endDate, true)
    }

    /**
     * Посылает на сервер запрос объектов за определённый период и отправляет
     * полученный результат во View
     * @param startDate Начальная дата периода в формате "yyyy-MM-dd"
     * @param endDate Конечная дата периода в формате "yyyy-MM-dd"
     * @param firstPage Признак того, что загружается первая порция объектов.
     * Если true (вызывается при создании фрагмента), то в центре экрана будет
     * отображено колёсико ProgressBar, а данные будут добавлены в адаптер с
     * помощью метода setData.
     * Если false (вызывается при прокрутке списка до конца) ProgressBar
     * добавлять уже не нужно (он добавляется в конец списка на стороне View),
     * а данные добавляются в адаптер с помощью метода setData
     */
    // TODO: Возможно, методы onCreate и onScroll можно объединить
    private fun sendServerRequest(startDate: String,
                                  endDate: String,
                                  firstPage: Boolean) {
        if (firstPage) view?.showProgress()

        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            view?.showError("You need API key")
        } else {
            // У объекта Retrofit вызываем уже другой метод - getPicturesList
            // для получения списка объектов и дальнейшего их отображения
            // в RecyclerView.
            retrofitImpl.getRetrofitImpl()
                .getPicturesList(apiKey, startDate, endDate)
                .enqueue(object :
                    Callback<List<PodServerResponseData>> {

                    override fun onResponse(
                        call: Call<List<PodServerResponseData>>,
                        response: Response<List<PodServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {

                            // Поскольку API присылает данные
                            // шиворот-навыворот, мы прямо тут засунем их в
                            // ArrayList и отсортируем по убыванию
                            val resultArrayList =
                                ArrayList<PodServerResponseData>(response.body()!!)
                            resultArrayList.sortByDescending { it.date }

                            // В зависимости от того, первый это запрос или
                            // не первый, отправляем данные во View тем или
                            // иным способом.
                            if (firstPage) {
                                view?.renderData(resultArrayList)
                            } else {
                                view?.addData(resultArrayList)
                            }
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                view?.showError("Unidentified error")
                            } else {
                                view?.showError(message)
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<List<PodServerResponseData>>, t:
                        Throwable
                    ) {
                        view?.showError(t.message ?: "")
                    }
                })
        }

        if (firstPage) view?.hideProgress()
    }

    override fun detach() {
        view = null
    }
}