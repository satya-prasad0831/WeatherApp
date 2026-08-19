package com.example.weatherapplication.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCache(

    @PrimaryKey
    val id: Int = 1,

    val locationName: String,

    val latitude: Double,

    val longitude: Double,


    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val weatherCode: Int,
    val windSpeed: Double,


    val hourlyTime: String,
    val hourlyTemperature: String,
    val hourlyApparentTemperature: String,
    val hourlyPrecipitationProbability: String,
    val hourlyWeatherCode: String,


    val dailyTime: String,
    val dailyWeatherCode: String,
    val dailyTemperatureMax: String,
    val dailyTemperatureMin: String,
    val dailyApparentTemperatureMax: String,
    val dailyApparentTemperatureMin: String,
    val dailyPrecipitationProbabilityMax: String,
    val dailySunrise: String,
    val dailySunset: String,

    val cachedAt: Long
)