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
}