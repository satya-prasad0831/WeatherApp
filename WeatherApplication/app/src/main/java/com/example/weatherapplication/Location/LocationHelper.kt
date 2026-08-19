package com.example.weatherapplication.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.util.Log.e
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(
    private val context: Context
) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {

        return suspendCancellableCoroutine { continuation ->

            fusedLocationClient
                .getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                    null
                )
                .addOnSuccessListener { location ->
                    Log.d(
                        "LOCATION_DEBUG",
                        "getCurrentLocation result = $location"
                    )

                    if (location != null) {

                        continuation.resume(location)

                    } else {

                        Log.d(
                            "LOCATION_DEBUG",
                            "Current location is NULL. Trying lastLocation..."
                        )

                        fusedLocationClient
                            .lastLocation
                            .addOnSuccessListener { lastLocation ->
                                Log.d(
                                    "LOCATION_DEBUG",
                                    "lastLocation result = $lastLocation"
                                )

                                continuation.resume(lastLocation)
                            }
                            .addOnFailureListener {exception ->
                                Log.e(
                                    "LOCATION_DEBUG",
                                    "lastLocation failed",
                                    exception
                                )

                                continuation.resume(null)
                            }
                    }
                }
                .addOnFailureListener {exception ->
                    Log.e(
                        "LOCATION_DEBUG",
                        "getCurrentLocation failed",
                        exception
                    )

                    fusedLocationClient
                        .lastLocation
                        .addOnSuccessListener { lastLocation ->
                            Log.d(
                                "LOCATION_DEBUG",
                                "Fallback lastLocation = $lastLocation"
                            )

                            continuation.resume(lastLocation)
                        }
                        .addOnFailureListener {fallbackException ->
                            Log.e(
                                "LOCATION_DEBUG",
                                "Fallback lastLocation failed",
                                fallbackException
                            )

                            continuation.resume(null)
                        }
                }
        }
    }
    fun getLocationName(
        location: Location
    ): String {

        val geocoder = android.location.Geocoder(
            context
        )

        return try {

            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {

                addresses[0].locality
                    ?: addresses[0].subAdminArea
                    ?: addresses[0].adminArea
                    ?: "Unknown location"

            } else {

                "Unknown location"
            }

        } catch (e: Exception) {

            "Unknown location"
        }
    }
}
