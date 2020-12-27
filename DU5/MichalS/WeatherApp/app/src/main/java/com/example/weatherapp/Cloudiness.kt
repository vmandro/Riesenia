package com.example.weatherapp

import com.google.gson.annotations.SerializedName

data class Cloudiness(
    @SerializedName("all") val percent: Int,
)
