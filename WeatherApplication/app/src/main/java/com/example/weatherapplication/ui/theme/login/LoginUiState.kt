package com.example.weatherapplication.ui.login

data class LoginUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val message: String = ""
)
