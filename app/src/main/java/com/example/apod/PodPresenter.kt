package com.example.apod

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PodPresenter : PodContract.Presenter {
    private val retrofitImpl: PodRetrofitImpl = PodRetrofitImpl()

    private var view: PodContract.View? = null

    override fun attach(view: PodContract.View) {
        this.view = view
    }

    override fun onCreate() {
        sendServerRequest()
    }

    private fun sendServerRequest() {
        // Устанавливаем состояние загрузки
        view?.showProgress()

        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            view?.showError("You need API key")
        } else {
            // Если ключ нормальный, получаем объект Retrofit, вызываем метод
            // получения данных, в результате получаем Call с объектом
            // PODServerResponseData, а чтобы получить сам этот объект, вызываем
            // метод enqueue.
            retrofitImpl.getRetrofitImpl().getPictureOfTheDay(apiKey)
                .enqueue(object :
                    Callback<PodServerResponseData> {
                    // Как водится, переопределяем два метода на случаи ответа и ошибки.
                    override fun onResponse(
                        call: Call<PodServerResponseData>,
                        response: Response<PodServerResponseData>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            // Вот здесь всё прошло успешно!!!
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
                        call: Call<PodServerResponseData>, t:
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