package com.example.weatherapplication.data.remote

data class WeatherResponse(
    val current: CurrentWeather,
    val hourly: HourlyWeather,
    val daily: DailyWeather
)

data class CurrentWeather(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val weather_code: Int,
    val wind_speed_10m: Double
)

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val apparent_temperature: List<Double>,
    val precipitation_probability: List<Int>,
    val weather_code: List<Int>
)

data class DailyWeather(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val apparent_temperature_max: List<Double>,
    val apparent_temperature_min: List<Double>,
    val precipitation_probability_max: List<Int>,
    val sunrise: List<String>,
    val sunset: List<String>
)