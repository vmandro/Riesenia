package com.example.weather

data class ApiResponse (
    val weather: List<Weather>, val main: Main, val visibility: Int, val wind: Wind,
    val clouds: Clouds, val dt: Long, val sys: Sys, val timezone: Int, val id: Int,
    val name: String, val cod: Int
)

data class Weather(val id: Int, val main: String, val description: String, val icon: String)

data class Main(
    val temp: Float, val feels_like: Float, val temp_min: Float,
    val temp_max: Float, val pressure: Int, val humidity: Int
)

data class Wind(val speed: Float, val deg: Int)

data class Clouds(val all: Int)

data class Sys(
    val type: Int, val id: Int, val country: String,
    val sunrise: Long, val sunset: Long
)