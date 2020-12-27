package com.olsinoo.simpleweather

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// API key >>> cf0958ccaed9a3de1388b0d81c8cd720
//http://api.openweathermap.org/data/2.5/weather?q=Bratislava&appid=cf0958ccaed9a3de1388b0d81c8cd720&units=metric

interface RetroInterface {
    @GET("weather?&appid=cf0958ccaed9a3de1388b0d81c8cd720&units=metric")
    suspend fun get(@Query("lat") latitude: Double,
                    @Query("lon") longitude: Double): CurrentWeather
}
object RetroService {
    private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"

    fun get(): RetroInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RetroInterface::class.java)
    }
}
