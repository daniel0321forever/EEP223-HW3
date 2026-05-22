package com.example.hw3.data.model

data class WeatherUiModel(
    val cityName: String,
    val temperatureC: Int,
    val condition: String,
    val minC: Int,
    val maxC: Int,
    val sunrise: String,
    val sunset: String,
    val windSpeedMs: String,
    val pressureHpa: String,
    val humidityPercent: String,
    val iconUrl: String,
    val conditionMain: String,
)
