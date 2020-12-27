package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    private val BASE_URL = "http://api.openweathermap.org/data/2.5/"
    private val API_KEY = "c6b77c830233d0d6ce1681cdc522b677"
    private val CITY_ID = 3057692
    private val UNITS = "metric"
    private val LANGUAGE = "sk"

    private val LAST_UPDATE_TIME_KEY = "time"
    private val JSON_WEATHER_DATA_KEY = "json"
    private val MAX_TIME_DIFFERENCE = 900L  // 15 min.

    private lateinit var weatherApi: WeatherApi
    private lateinit var sharedPreferences: SharedPreferences

    private var lastUpdateTime = 0L
    private var weatherData: Weather? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherApi = createRetrofitInterface()
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        loadCurrentWeather()
    }

    override fun onPause() {
        super.onPause()
        saveWeatherData()
    }

    private fun createRetrofitInterface(): WeatherApi =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()
            .create(WeatherApi::class.java)

    private fun loadCurrentWeather() {
        /*val file = application.assets.open("exampleData.json").bufferedReader().readText()
        weatherData = Gson().fromJson(file, Weather::class.java)*/  // load example data
        lastUpdateTime = sharedPreferences.getLong(LAST_UPDATE_TIME_KEY, 0L)
        if (weatherDataAreOutdated()) {
            loadWeatherDataFromServer()
        } else {
            loadSavedWeatherData()
        }
    }

    private fun loadWeatherDataFromServer() {
        val call = weatherApi.getWeatherDataByCityId(CITY_ID, UNITS, LANGUAGE, API_KEY)
        try {
            call.enqueue(object : Callback<Weather> {
                override fun onResponse(call: Call<Weather>?, response: Response<Weather>?) {
                    if (response != null && response.isSuccessful) {
                        weatherData = response.body()
                        lastUpdateTime = getCurrentTime()
                        showCurrentWeather()
                    } else {
                        showErrorScreen()
                    }
                }

                override fun onFailure(call: Call<Weather>?, t: Throwable?) {
                    showErrorScreen()
                }
            })
        } catch (e: Exception) {
            showErrorScreen()
        }
    }

    private fun loadSavedWeatherData() {
        val jsonWeatherData = sharedPreferences.getString(JSON_WEATHER_DATA_KEY, "")?: ""
        try {
            weatherData = Gson().fromJson(jsonWeatherData, Weather::class.java)
        } catch (e: Exception) {
            showErrorScreen()
        }
        showCurrentWeather()
    }

    private fun weatherDataAreOutdated() =
        lastUpdateTime + MAX_TIME_DIFFERENCE < getCurrentTime()

    private fun getCurrentTime() =
        Calendar.getInstance().timeInMillis / 1000

    @SuppressLint("SetTextI18n")
    private fun showCurrentWeather() {
        setBackgroundColor(weatherData!!.info[0].weatherCategory)
        descriptionView.text = weatherData!!.info[0].description
        temperatureView.text = "${weatherData!!.temperatureAndPressure.temperature}°C"
        sensoryTempView.text = "${weatherData!!.temperatureAndPressure.sensoryTemperature}°C"
        pressureView.text = "Atmosferický tlak: ${weatherData!!.temperatureAndPressure.atmosphericPressure} hPa"
        humidityView.text = "Vlhkosť: ${weatherData!!.temperatureAndPressure.humidity}%"
        cloudinessView.text = "Oblačnosť: ${weatherData!!.cloudiness.percent}%"
        windSpeedView.text = "${weatherData!!.wind.speed * 3.6} km/h"
        windArrow.rotation = weatherData!!.wind.direction.toFloat()
        sunriseView.text = "Východ slnka: ${convertTimeToString(weatherData!!.system.sunrise).substring(11)}"
        sunsetView.text = "Západ slnka: ${convertTimeToString(weatherData!!.system.sunset).substring(11)}"
        lastUpdateTimeView.text = convertTimeToString(lastUpdateTime)
    }

    private fun setBackgroundColor(category: String) {
        when (category) {
            "Thunderstorm" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.thunderstormBg))
            "Drizzle" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.drizzleBg))
            "Rain" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.rainBg))
            "Snow" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.snowBg))
            "Clear" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.clearBg))
            "Clouds" -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.cloudsBg))
            else -> constraintLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.thunderstormBg))
        }
    }

    private fun convertTimeToString(seconds: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val date = Date(seconds * 1000)
        return sdf.format(date)
    }

    private fun showErrorScreen() {
        // fix
    }

    private fun saveWeatherData() {
        val jsonWeatherData = if (weatherData != null) Gson().toJson(weatherData) else ""
        val editor = sharedPreferences.edit()
        editor.putLong(LAST_UPDATE_TIME_KEY, lastUpdateTime)
        editor.putString(JSON_WEATHER_DATA_KEY, jsonWeatherData)
        editor.apply()
    }
}