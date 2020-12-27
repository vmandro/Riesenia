package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("coord") val coords: Coordinates,
    @SerializedName("weather") val info: List<WeatherInfo>,
    @SerializedName("base") val base: String,  // server internal parameter
    @SerializedName("main") val temperatureAndPressure: Temperature,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind") val wind: Wind,
    @SerializedName("clouds") val cloudiness: Cloudiness,
    @SerializedName("dt") val dateTime: Long,  // Unix UTC
    @SerializedName("sys") val system: WeatherSystem,
    @SerializedName("timezone") val timeShift: Long,  // Unix UTC
    @SerializedName("id") val cityId: Int,
    @SerializedName("name") val cityName: String,
    @SerializedName("cod") val cod: Int  // server internal parameter
)
