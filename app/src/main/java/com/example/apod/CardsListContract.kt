package com.example.apod

/**
 * Контракт взаимодействия Presenterа и View - фрагмент со списком карточек
 */
class CardsListContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun renderData(data: List<PodServerResponseData>)
    }

    interface Presenter {
        fun attach(view: View)
        fun onCreate()
        fun detach()
    }
}