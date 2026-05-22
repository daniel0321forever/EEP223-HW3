package com.example.hw3.data.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val cod: Int,
    val name: String,
    val main: Main,
    val weather: List<WeatherItem>,
    val wind: Wind?,
    val sys: Sys,
)

data class Main(
    val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
)

data class WeatherItem(
    val main: String,
    val description: String,
    val icon: String,
)

data class Wind(
    val speed: Double,
)

data class Sys(
    val sunrise: Long,
    val sunset: Long,
)
