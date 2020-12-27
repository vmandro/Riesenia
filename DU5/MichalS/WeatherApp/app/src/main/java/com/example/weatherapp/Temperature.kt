package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class Temperature(
    @SerializedName("temp") val temperature: Double,  // Celsius
    @SerializedName("feels_like") val sensoryTemperature: Double,  // Celsius
    @SerializedName("pressure") val atmosphericPressure: Int,  // hPa
    @SerializedName("humidity") val humidity: Int,  // percent
    @SerializedName("temp_min") val minTemperature: Double,  // Celsius
    @SerializedName("temp_max") val maxTemperature: Double  // Celsius
)
