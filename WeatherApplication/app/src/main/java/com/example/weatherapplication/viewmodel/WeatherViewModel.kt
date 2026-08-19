package com.example.weatherapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.remote.WeatherResponse
import com.example.weatherapplication.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val locationName: String = "",
    val isFromCache: Boolean = false,
    val cachedAt: Long? = null,
    val error: String? = null
)

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())

    val uiState: StateFlow<WeatherUiState> = _uiState

    fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String
    ) {

        viewModelScope.launch {

            _uiState.value = WeatherUiState(
                isLoading = true
            )

            try {

                val result = repository.getWeather(
                    latitude = latitude,
                    longitude = longitude,
                    locationName = locationName
                )

                _uiState.value = WeatherUiState(
                    isLoading = false,
                    weather = result.weather,
                    locationName = result.locationName,
                    isFromCache = result.isFromCache,
                    cachedAt = result.cachedAt
                )
            } catch (e: Exception) {

                _uiState.value = WeatherUiState(
                    isLoading = false,
                    error = e.message ?: "Unable to load weather"
                )
            }
        }
    }
}