package com.example.apod

/**
 * Контракт взаимодействия Presenterа и View. Существует много разных библиотек
 * для работы с MVP, но у нас "самописный" MVP, а в таких случаях лучше всего
 * определять вот такой контракт. Его удобство в том, что посторонний читатель
 * нашего кода может быстро просмотреть этот контракт и сразу же понять, что
 * может делать наше приложение.
 */
class PodContract {
    interface View {
        fun showProgress()
        fun hideProgress()
        fun showError(error: String)
        fun renderData(serverResponseData: PodServerResponseData)
    }

    interface Presenter {
        fun attach(view: View)
        fun onCreate()
        fun detach()
    }
}