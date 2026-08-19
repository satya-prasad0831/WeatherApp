package com.example.weatherapplication.data.remote

data class GeocodingResponse(
    val results: List<LocationResult>?
)

data class LocationResult(
    val id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    val admin1: String?
)