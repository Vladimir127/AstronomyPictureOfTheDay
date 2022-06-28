package com.example.apod

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Представление API как объекта с запросами для Retrofit
 */
interface PictureOfTheDayApi {

    /**
     * Метод, представляющий собой запрос на получение фото дня. Помечен
     * аннотацией GET с указанием пути (причём путь неполный - в нём нет базового)
     * Принимает на вход параметры apiKey, значение которого будет подставлено
     * в запрос с названием api_key
     * Возвращает объект PODServerResponseData, обёрнутый в Call.
     */
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String):
            Call<PodServerResponseData>

    /**
     * Метод, представляющий собой запрос на получение нескольких фотографий
     * для их отображения в списке карточек.
     * Принимает на вход параметры apiKey и count
     * Возвращает список объектов PODServerResponseData, обёрнутый в Call.
     */
    @GET("planetary/apod")
    fun getPicturesList(
        @Query("api_key") apiKey: String, @Query("count")
        count: Int
    ): Call<List<PodServerResponseData>>

    /**
     * Метод, представляющий собой запрос на получение списка фотографий
     * за определённый период для их отображения в списке карточек.
     * @param apiKey Ключ API NASA
     * @param startDate Начальная дата диапазона
     * @param endDate Конечная дата диапазона
     * @return Список объектов PODServerResponseData, обёрнутый в Call.
     */
    @GET("planetary/apod")
    fun getPicturesList(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Call<List<PodServerResponseData>>
}