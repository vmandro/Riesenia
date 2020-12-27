package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    fun getWeatherDataByCityId(
        @Query("id") cityId: Int,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("appid") apiKey: String
    ): Call<Weather>
}