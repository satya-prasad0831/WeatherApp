package com.example.weatherapplication.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.data.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())

    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _uiState.value = LoginUiState(
                isLoading = true
            )

            kotlinx.coroutines.delay(2000)

            if (
                email == "demo@gmail.com" &&
                password == "123456"
            ) {

                _uiState.value = LoginUiState(
                    isLoading = false,
                    success = true,
                    message = "Login successful"
                )

            } else {

                _uiState.value = LoginUiState(
                    isLoading = false,
                    success = false,
                    message = "Invalid email or password"
                )
            }
        }
    }
}

