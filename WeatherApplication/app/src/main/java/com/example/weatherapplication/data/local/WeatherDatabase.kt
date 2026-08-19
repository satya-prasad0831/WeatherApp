package com.example.weatherapplication.data.local

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [WeatherCache::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
}