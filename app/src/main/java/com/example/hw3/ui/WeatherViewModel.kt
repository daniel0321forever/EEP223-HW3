package com.example.hw3.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw3.data.WeatherRepository
import com.example.hw3.data.WeatherResult
import com.example.hw3.data.model.WeatherUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class WeatherUiState {
    data object Idle : WeatherUiState()
    data object Loading : WeatherUiState()
    data class Success(val weather: WeatherUiModel) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application.applicationContext)

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var locationLoadRequested = false

    fun loadCurrentLocationWeather() {
        if (locationLoadRequested && _uiState.value is WeatherUiState.Success) return
        locationLoadRequested = true
        viewModelScope.launch {
            _uiState.update { WeatherUiState.Loading }
            when (val result = repository.loadWeatherForCurrentLocation()) {
                is WeatherResult.Success -> _uiState.update { WeatherUiState.Success(result.weather) }
                is WeatherResult.Error -> _uiState.update { WeatherUiState.Error(result.message) }
            }
        }
    }

    fun searchCity(city: String) {
        viewModelScope.launch {
            val trimmed = city.trim()
            if (trimmed.isEmpty()) {
                _uiState.update { WeatherUiState.Error(WeatherRepository.BLANK_CITY_MESSAGE) }
                return@launch
            }
            _uiState.update { WeatherUiState.Loading }
            when (val result = repository.loadWeatherForCity(city)) {
                is WeatherResult.Success -> _uiState.update { WeatherUiState.Success(result.weather) }
                is WeatherResult.Error -> _uiState.update { WeatherUiState.Error(result.message) }
            }
        }
    }

}
