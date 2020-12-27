package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class WeatherInfo(
    @SerializedName("id") val id: Int,
    @SerializedName("main") val weatherCategory: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val iconName: String
)
