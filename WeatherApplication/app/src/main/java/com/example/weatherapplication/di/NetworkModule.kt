package com.example.weatherapplication.di

import com.example.weatherapplication.data.remote.GeocodingApi
import com.example.weatherapplication.data.remote.LoginApi
import com.example.weatherapplication.data.remote.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.inject.Qualifier
import androidx.room3.Room
import com.example.weatherapplication.data.local.WeatherDatabase
import com.example.weatherapplication.data.local.WeatherDao


@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoginRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WeatherRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GeocodingRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://your-api-url.com/"
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/v1/"

    private const val GEOCODING_BASE_URL =
        "https://geocoding-api.open-meteo.com/v1/"

    @Provides
    @Singleton
    @LoginRetrofit
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    @Singleton
    fun provideLoginApi(
        @LoginRetrofit retrofit: Retrofit
    ): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }


    @Provides
    @Singleton
    @WeatherRetrofit
    fun provideWeatherRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApi(
        @WeatherRetrofit retrofit: Retrofit
    ): WeatherApi {
        return retrofit.create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    @GeocodingRetrofit
    fun provideGeocodingRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
    @Provides
    @Singleton
    fun provideGeocodingApi(
        @GeocodingRetrofit retrofit: Retrofit
    ): GeocodingApi {
        return retrofit.create(GeocodingApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): WeatherDatabase {

        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }
    @Provides
    fun provideWeatherDao(
        database: WeatherDatabase
    ): WeatherDao {

        return database.weatherDao()
    }

}