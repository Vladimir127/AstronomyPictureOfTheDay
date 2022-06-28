package com.example.apod

import com.google.gson.annotations.SerializedName

/**
 * Объекты этого класса, обёрнутые в Call, возвращаются в результате выполнения
 * запросов с помощью Retrofit. У каждого поля задан атрибут SerializedName,
 * в котором указано имя поля в JSON-ответе.
 */
data class PodServerResponseData (
    @field:SerializedName("copyright") val copyright: String?,
    @field:SerializedName("date") val date: String?,
    @field:SerializedName("explanation") val explanation: String?,
    @field:SerializedName("media_type") val mediaType: String?,
    @field:SerializedName("title") val title: String?,
    @field:SerializedName("url") val url: String?,
    @field:SerializedName("hdurl") val hdurl: String?
)