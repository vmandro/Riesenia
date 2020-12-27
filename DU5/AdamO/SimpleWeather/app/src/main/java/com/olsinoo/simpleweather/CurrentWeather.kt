package com.olsinoo.simpleweather

import com.google.gson.annotations.SerializedName

data class CurrentWeather (
    @SerializedName("coord") val coords: Coords,
    @SerializedName("weather") val weatherState: List<Weather>?,
    @SerializedName("main") val weatherDetail: WeatherDetail,
    @SerializedName("wind") val windDetail: Wind,
    @SerializedName("sys") val sunDetail: Sun,
    @SerializedName("name") val name: String?
)

data class Wind (
    @SerializedName("speed") val speed: Float?,
    @SerializedName("deg") val degree: Int?
)

data class Coords (
    @SerializedName("lon") val longitude: Float?,
    @SerializedName("lat") val latitude: Float?
)

data class Weather (
    @SerializedName("main") val weatherCondition: String?,
    @SerializedName("description") val description: String?
)

data class WeatherDetail (
    @SerializedName("temp") val temperature: Float?,
    @SerializedName("feels_like") val feeling: Float?,
    @SerializedName("temp_min") val minTemperature: Float?,
    @SerializedName("temp_max") val maxTemperature: Float?,
    @SerializedName("pressure") val pressure: Float?,
    @SerializedName("humidity") val humidity: Float?
)

data class Sun (
    @SerializedName("sunrise") val sunrise: Long?,
    @SerializedName("sunset") val sunset: Long?
)
