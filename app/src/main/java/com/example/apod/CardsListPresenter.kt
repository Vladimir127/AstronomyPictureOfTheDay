package com.example.apod

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CardsListPresenter : CardsListContract.Presenter {
    private val retrofitImpl: PodRetrofitImpl = PodRetrofitImpl()

    private val countEntries = -9

    private var view: CardsListContract.View? = null

    override fun attach(view: CardsListContract.View) {
        this.view = view
    }

    override fun onCreate() {
        sendServerRequest()
    }

    private fun sendServerRequest() {
        view?.showProgress()

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        val endDate = simpleDateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, countEntries)
        val startDate = simpleDateFormat.format(calendar.time)

        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            view?.showError("You need API key")
        } else {
            // У объекта Retrofit вызываем уже другой метод - getPicturesList
            // для получения списка объектов и дальнейшего их отображения
            // в RecyclerView. Второй параметр - количество загружаемых объектов
            // TODO: Реализовать подгрузку карточек по мере пролистывания списка
            retrofitImpl.getRetrofitImpl()
                .getPicturesList(apiKey, startDate, endDate)
                .enqueue(object :
                    Callback<List<PodServerResponseData>> {

                    override fun onResponse(
                        call: Call<List<PodServerResponseData>>,
                        response: Response<List<PodServerResponseData>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            view?.renderData(response.body()!!)
                        } else {
                            val message = response.message()
                            if (message.isNullOrEmpty()) {
                                view?.showError("Unidentified error")
                            } else {
                                view?.showError(message)
                            }
                        }
                        view?.hideProgress()
                    }

                    override fun onFailure(
                        call: Call<List<PodServerResponseData>>, t:
                        Throwable
                    ) {
                        view?.showError(t.message ?: "")
                        view?.hideProgress()
                    }
                })
        }
    }

    override fun detach() {
        view = null
    }
}