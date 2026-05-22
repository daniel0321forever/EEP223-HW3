package com.example.hw3

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.example.hw3.data.WeatherRepository
import com.example.hw3.data.model.WeatherUiModel
import com.example.hw3.databinding.ActivityMainBinding
import com.example.hw3.databinding.ItemWeatherDetailBinding
import com.example.hw3.ui.WeatherUiState
import com.example.hw3.ui.WeatherViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { _ ->
        viewModel.loadCurrentLocationWeather()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupDetailCards()
        setupSearch()
        observeUiState()
        requestLocationAndLoad()
    }

    private fun setupDetailCards() {
        bindDetail(binding.detailSunrise, R.drawable.ic_sunrise, getString(R.string.sunrise), "—")
        bindDetail(binding.detailSunset, R.drawable.ic_sunset, getString(R.string.sunset), "—")
        bindDetail(binding.detailWind, R.drawable.ic_wind, getString(R.string.wind_speed), "—")
        bindDetail(binding.detailPressure, R.drawable.ic_pressure, getString(R.string.pressure), "—")
        bindDetail(binding.detailHumidity, R.drawable.ic_humidity, getString(R.string.humidity), "—")
    }

    private fun bindDetail(detail: ItemWeatherDetailBinding, iconRes: Int, label: String, value: String) {
        detail.detailIcon.setImageResource(iconRes)
        detail.detailLabel.text = label
        detail.detailValue.text = value
    }

    private fun setupSearch() {
        binding.searchButton.setOnClickListener {
            binding.cityInputLayout.error = null
            viewModel.searchCity(binding.cityInput.text?.toString().orEmpty())
        }
        binding.cityInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchButton.performClick()
                true
            } else {
                false
            }
        }
        binding.cityInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.cityInputLayout.error = null
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is WeatherUiState.Idle -> Unit
                        is WeatherUiState.Loading -> showLoading(true)
                        is WeatherUiState.Success -> {
                            showLoading(false)
                            binding.cityInputLayout.error = null
                            displayWeather(state.weather)
                        }
                        is WeatherUiState.Error -> {
                            showLoading(false)
                            handleError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun handleError(message: String) {
        when (message) {
            WeatherRepository.BLANK_CITY_MESSAGE -> {
                binding.cityInputLayout.error = message
            }
            else -> {
                binding.cityInputLayout.error = null
                Snackbar.make(binding.main, message, Snackbar.LENGTH_LONG)
                    .setAnchorView(binding.searchCard)
                    .show()
            }
        }
    }

    private fun displayWeather(weather: WeatherUiModel) {
        binding.cityName.text = weather.cityName
        binding.temperature.text = getString(R.string.temp_format, weather.temperatureC)
        binding.condition.text = weather.condition
        binding.highTemp.text = getString(R.string.high_temp, weather.maxC)
        binding.lowTemp.text = getString(R.string.low_temp, weather.minC)
        binding.weatherIcon.load(weather.iconUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_sunrise)
        }
        binding.heroCard.setBackgroundResource(gradientForCondition(weather.conditionMain))

        updateDetail(binding.detailSunrise, weather.sunrise)
        updateDetail(binding.detailSunset, weather.sunset)
        updateDetail(binding.detailWind, weather.windSpeedMs)
        updateDetail(binding.detailPressure, weather.pressureHpa)
        updateDetail(binding.detailHumidity, weather.humidityPercent)

        binding.searchButton.isEnabled = true
        binding.cityInput.isEnabled = true
    }

    private fun updateDetail(detail: ItemWeatherDetailBinding, value: String) {
        detail.detailValue.text = value
    }

    private fun gradientForCondition(main: String): Int {
        return when {
            main.contains("Rain", ignoreCase = true) ||
                main.contains("Drizzle", ignoreCase = true) ||
                main.contains("Thunderstorm", ignoreCase = true) -> R.drawable.bg_gradient_rain
            main.contains("Cloud", ignoreCase = true) ||
                main.contains("Mist", ignoreCase = true) ||
                main.contains("Fog", ignoreCase = true) ||
                main.contains("Haze", ignoreCase = true) -> R.drawable.bg_gradient_cloudy
            main.contains("Clear", ignoreCase = true) -> {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                if (hour in 6..19) R.drawable.bg_gradient_clear else R.drawable.bg_gradient_night
            }
            else -> R.drawable.bg_gradient_cloudy
        }
    }

    private fun showLoading(loading: Boolean) {
        binding.loadingOverlay.visibility = if (loading) View.VISIBLE else View.GONE
        binding.searchButton.isEnabled = !loading
        binding.cityInput.isEnabled = !loading
    }

    private fun requestLocationAndLoad() {
        if (hasLocationPermission()) {
            viewModel.loadCurrentLocationWeather()
            return
        }
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ),
        )
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }
}
