package com.example.apod

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListPresenter : ListContract.Presenter{
    private val retrofitImpl: PodRetrofitImpl = PodRetrofitImpl()

    private val countEntries = 3

    private var view: ListContract.View? = null

    override fun attach(view: ListContract.View) {
        this.view = view
    }

    override fun onCreate() {
        sendServerRequest()
    }

    private fun sendServerRequest() {
        view?.showProgress()

        val apiKey: String = BuildConfig.NASA_API_KEY
        if (apiKey.isBlank()) {
            view?.showError("You need API key")
        } else {
            // У объекта Retrofit вызываем уже другой метод - getPicturesList
            // для получения списка объектов и дальнейшего их отображения
            // в RecyclerView. Второй параметр - количество загружаемых объектов
            // TODO: Реализовать подгрузку карточек по мере пролистывания списка
            retrofitImpl.getRetrofitImpl().getPicturesList(apiKey, countEntries)
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