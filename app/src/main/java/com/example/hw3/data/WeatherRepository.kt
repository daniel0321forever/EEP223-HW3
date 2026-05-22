package com.example.hw3.data

import android.content.Context
import com.example.hw3.BuildConfig
import com.example.hw3.data.api.ApiClient
import com.example.hw3.data.model.WeatherResponse
import com.example.hw3.data.model.WeatherUiModel
import com.example.hw3.util.DateTimeUtils
import com.example.hw3.util.GeoCoordinates
import com.example.hw3.util.LocationHelper
import com.example.hw3.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException
import kotlin.math.roundToInt

sealed class WeatherResult {
    data class Success(val weather: WeatherUiModel) : WeatherResult()
    data class Error(val message: String) : WeatherResult()
}

class WeatherRepository(
    private val context: Context,
    private val locationHelper: LocationHelper = LocationHelper(context),
) {

    private val api = ApiClient.openWeatherApi
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    suspend fun loadWeatherForCurrentLocation(): WeatherResult = withContext(Dispatchers.IO) {
        if (!NetworkUtils.isOnline(context)) {
            return@withContext WeatherResult.Error(NO_INTERNET_MESSAGE)
        }
        try {
            val coords = locationHelper.getCoordinates()
            fetchByCoordinates(coords)
        } catch (e: Exception) {
            mapException(e)
        }
    }

    suspend fun loadWeatherForCity(city: String): WeatherResult = withContext(Dispatchers.IO) {
        val trimmed = city.trim()
        if (trimmed.isEmpty()) {
            return@withContext WeatherResult.Error(BLANK_CITY_MESSAGE)
        }
        if (!NetworkUtils.isOnline(context)) {
            return@withContext WeatherResult.Error(NO_INTERNET_MESSAGE)
        }
        try {
            val response = api.getWeatherByCity(trimmed, apiKey = apiKey)
            if (response.cod != 200) {
                return@withContext WeatherResult.Error(CITY_NOT_FOUND_MESSAGE)
            }
            WeatherResult.Success(response.toUiModel())
        } catch (e: Exception) {
            mapException(e)
        }
    }

    private suspend fun fetchByCoordinates(coords: GeoCoordinates): WeatherResult {
        val response = api.getWeatherByCoordinates(
            lat = coords.latitude,
            lon = coords.longitude,
            apiKey = apiKey,
        )
        if (response.cod != 200) {
            return WeatherResult.Error(GENERIC_ERROR_MESSAGE)
        }
        return WeatherResult.Success(response.toUiModel())
    }

    private fun mapException(e: Exception): WeatherResult {
        return when {
            e is UnknownHostException || (e is IOException && e !is HttpException) ->
                WeatherResult.Error(NO_INTERNET_MESSAGE)
            e is HttpException && e.code() == 404 ->
                WeatherResult.Error(CITY_NOT_FOUND_MESSAGE)
            e is HttpException && e.code() == 401 ->
                WeatherResult.Error("Invalid API key. Check local.properties.")
            else -> WeatherResult.Error(GENERIC_ERROR_MESSAGE)
        }
    }

    private fun WeatherResponse.toUiModel(): WeatherUiModel {
        val weatherItem = weather.firstOrNull()
        val description = weatherItem?.description?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        } ?: "Unknown"
        val icon = weatherItem?.icon ?: "01d"
        return WeatherUiModel(
            cityName = name,
            temperatureC = main.temp.roundToInt(),
            condition = description,
            minC = main.tempMin.roundToInt(),
            maxC = main.tempMax.roundToInt(),
            sunrise = DateTimeUtils.formatUnixTime(sys.sunrise),
            sunset = DateTimeUtils.formatUnixTime(sys.sunset),
            windSpeedMs = "${wind?.speed?.let { "%.1f".format(it) } ?: "0.0"} m/s",
            pressureHpa = "${main.pressure} hPa",
            humidityPercent = "${main.humidity}%",
            iconUrl = "https://openweathermap.org/img/wn/$icon@4x.png",
            conditionMain = weatherItem?.main ?: "Clear",
        )
    }

    companion object {
        const val BLANK_CITY_MESSAGE = "City Name cannot be blank"
        const val NO_INTERNET_MESSAGE = "Please connect to internet"
        const val CITY_NOT_FOUND_MESSAGE = "City not found. Please check the name."
        const val GENERIC_ERROR_MESSAGE = "Unable to load weather. Please try again."
    }
}
