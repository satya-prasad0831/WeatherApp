package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.remote.WeatherResponse

data class WeatherResult(
    val weather: WeatherResponse,
    val locationName: String,
    val isFromCache: Boolean = false,
    val cachedAt: Long? = null
)