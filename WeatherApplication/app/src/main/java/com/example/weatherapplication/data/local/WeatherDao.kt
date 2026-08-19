package com.example.weatherapplication.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(
        weather: WeatherCache
    )

    @Query("SELECT * FROM weather_cache WHERE id = 1")
    suspend fun getCachedWeather(): WeatherCache?

    @Query("DELETE FROM weather_cache")
    suspend fun clearWeather()
}