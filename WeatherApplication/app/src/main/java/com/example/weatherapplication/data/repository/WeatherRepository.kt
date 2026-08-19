package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.local.WeatherCache
import com.example.weatherapplication.data.local.WeatherDao
import com.example.weatherapplication.data.remote.CurrentWeather
import com.example.weatherapplication.data.remote.DailyWeather
import com.example.weatherapplication.data.remote.HourlyWeather
import com.example.weatherapplication.data.remote.WeatherApi
import com.example.weatherapplication.data.remote.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao
) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        locationName: String
    ): WeatherResult {

        return try {


            val response = weatherApi.getWeather(
                latitude = latitude,
                longitude = longitude
            )


            val cache = WeatherCache(

                locationName = locationName,

                latitude = latitude,

                longitude = longitude,


                temperature =
                    response.current.temperature_2m,

                feelsLike =
                    response.current.apparent_temperature,

                humidity =
                    response.current.relative_humidity_2m,

                weatherCode =
                    response.current.weather_code,

                windSpeed =
                    response.current.wind_speed_10m,


                hourlyTime =
                    response.hourly.time.joinToString("|"),

                hourlyTemperature =
                    response.hourly.temperature_2m.joinToString("|"),

                hourlyApparentTemperature =
                    response.hourly.apparent_temperature.joinToString("|"),

                hourlyPrecipitationProbability =
                    response.hourly.precipitation_probability
                        .joinToString("|"),

                hourlyWeatherCode =
                    response.hourly.weather_code.joinToString("|"),


                dailyTime =
                    response.daily.time.joinToString("|"),

                dailyWeatherCode =
                    response.daily.weather_code.joinToString("|"),

                dailyTemperatureMax =
                    response.daily.temperature_2m_max
                        .joinToString("|"),

                dailyTemperatureMin =
                    response.daily.temperature_2m_min
                        .joinToString("|"),

                dailyApparentTemperatureMax =
                    response.daily.apparent_temperature_max
                        .joinToString("|"),

                dailyApparentTemperatureMin =
                    response.daily.apparent_temperature_min
                        .joinToString("|"),

                dailyPrecipitationProbabilityMax =
                    response.daily.precipitation_probability_max
                        .joinToString("|"),

                dailySunrise =
                    response.daily.sunrise.joinToString("|"),

                dailySunset =
                    response.daily.sunset.joinToString("|"),

                cachedAt =
                    System.currentTimeMillis()
            )


            weatherDao.insertWeather(cache)


            WeatherResult(
                weather = response,
                locationName = locationName,
                isFromCache = false,
                cachedAt = null
            )

        } catch (e: Exception) {


            val cached =
                weatherDao.getCachedWeather()

            if (cached != null) {

                WeatherResult(
                    weather = cacheToWeatherResponse(cached),
                    locationName = cached.locationName,
                    isFromCache = true,
                    cachedAt = cached.cachedAt
                )

            } else {

                throw e
            }
        }
    }

    private fun cacheToWeatherResponse(
        cache: WeatherCache
    ): WeatherResponse {

        return WeatherResponse(

            current = CurrentWeather(
                temperature_2m = cache.temperature,
                relative_humidity_2m = cache.humidity,
                apparent_temperature = cache.feelsLike,
                weather_code = cache.weatherCode,
                wind_speed_10m = cache.windSpeed
            ),

            hourly = HourlyWeather(
                time = cache.hourlyTime.split("|"),

                temperature_2m =
                    cache.hourlyTemperature
                        .split("|")
                        .map { it.toDouble() },

                apparent_temperature =
                    cache.hourlyApparentTemperature
                        .split("|")
                        .map { it.toDouble() },

                precipitation_probability =
                    cache.hourlyPrecipitationProbability
                        .split("|")
                        .map { it.toInt() },

                weather_code =
                    cache.hourlyWeatherCode
                        .split("|")
                        .map { it.toInt() }
            ),

            daily = DailyWeather(
                time = cache.dailyTime.split("|"),

                weather_code =
                    cache.dailyWeatherCode
                        .split("|")
                        .map { it.toInt() },

                temperature_2m_max =
                    cache.dailyTemperatureMax
                        .split("|")
                        .map { it.toDouble() },

                temperature_2m_min =
                    cache.dailyTemperatureMin
                        .split("|")
                        .map { it.toDouble() },

                apparent_temperature_max =
                    cache.dailyApparentTemperatureMax
                        .split("|")
                        .map { it.toDouble() },

                apparent_temperature_min =
                    cache.dailyApparentTemperatureMin
                        .split("|")
                        .map { it.toDouble() },

                precipitation_probability_max =
                    cache.dailyPrecipitationProbabilityMax
                        .split("|")
                        .map { it.toInt() },

                sunrise =
                    cache.dailySunrise.split("|"),

                sunset =
                    cache.dailySunset.split("|")
            )
        )
    }
}