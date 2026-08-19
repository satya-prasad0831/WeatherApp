package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.remote.GeocodingApi
import com.example.weatherapplication.data.remote.LocationResult
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val geocodingApi: GeocodingApi
) {

    suspend fun searchLocation(
        query: String
    ): List<LocationResult> {

        return geocodingApi
            .searchLocation(query)
            .results
            ?: emptyList()
    }
}