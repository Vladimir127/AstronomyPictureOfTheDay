package com.example.apod.domain

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Класс, который будет отвечать за создание и возврат объекта Retrofit,
 * да ещё и объекта OkHttpClient зачем-то. TODO: Зачем?
 */
class PodRetrofitImpl {
    private val baseUrl = "https://api.nasa.gov/"

    fun getRetrofitImpl(): PictureOfTheDayApi {
        val podRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(PodInterceptor()))
            .build()
        return podRetrofit.create(PictureOfTheDayApi::class.java)
    }

    /**
     * Как я понимаю, этот метод создан ради того, чтобы прикрепить объект
     * Interceptor к объекту Retrofit, а коненчная цель всех этих действий -
     * чтобы с помощью объекта Interceptor выводить в логи историю запросов,
     * выполняемых объектом Retrofit, и ответов от сервера. TODO: так ли это?
     */
    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    /**
     * Наш собственный класс Interceptor, встраиваемый в Retrofit с помощью
     * OkHttpClient для того, чтобы выводить в логи историю запросов. TODO: Правильно ли я понимаю?
     * Точно такой же пример рассматривается в методичке №7.
     */
    inner class PodInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.proceed(chain.request())
        }
    }
}