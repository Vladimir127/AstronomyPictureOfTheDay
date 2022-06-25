package com.example.apod.ui.cardslist

import com.example.apod.domain.PodServerResponseData

/**
 * Контракт взаимодействия Presenterа и View - фрагмент со списком карточек
 */
class CardsListContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun renderData(data: List<PodServerResponseData>)
        fun addData(data: List<PodServerResponseData>)
    }

    interface Presenter {
        fun attach(view: View)
        fun onCreate()
        fun onScroll()
        fun onDateRangeSelected(firstDateMillis: Long, secondDateMillis: Long)
        fun detach()
    }
}