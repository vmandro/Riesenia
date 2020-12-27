package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class WeatherSystem(
    @SerializedName("type") val type: Int,  // server internal parameter
    @SerializedName("id") val id: Int,  // server internal parameter
    @SerializedName("country") val countryCode: String,
    @SerializedName("sunrise") val sunrise: Long,  // Unix UTC
    @SerializedName("sunset") val sunset: Long  // Unix UTC
)
