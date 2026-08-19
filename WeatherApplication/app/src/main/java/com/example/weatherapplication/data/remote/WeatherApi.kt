package com.example.weatherapplication.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,

        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,weather_code,wind_speed_10m",

        @Query("hourly")
        hourly: String =
            "temperature_2m,apparent_temperature,precipitation_probability,weather_code",

        @Query("daily")
        daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,precipitation_probability_max,sunrise,sunset",

        @Query("timezone")
        timezone: String = "auto"
    ): WeatherResponse
}