package com.example.weatherapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.remote.LocationResult
import com.example.weatherapplication.data.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {

    private val _locations =
        MutableStateFlow<List<LocationResult>>(emptyList())

    val locations: StateFlow<List<LocationResult>> = _locations

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> = _error

    fun searchLocation(query: String) {

        if (query.isBlank()) {
            _locations.value = emptyList()
            return
        }

        viewModelScope.launch {

            _isLoading.value = true
            _error.value = null

            try {

                _locations.value =
                    repository.searchLocation(query)

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Unable to search location"

            } finally {

                _isLoading.value = false
            }
        }
    }
    fun clearLocations() {
        _locations.value = emptyList()
    }
}