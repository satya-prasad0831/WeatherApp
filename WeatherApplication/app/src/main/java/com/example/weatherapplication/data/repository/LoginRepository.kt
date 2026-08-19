package com.example.weatherapplication.data.repository

import com.example.weatherapplication.data.remote.LoginApi
import com.example.weatherapplication.data.remote.LoginRequest
import com.example.weatherapplication.data.remote.LoginResponse
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginApi: LoginApi
) {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse {

        val request = LoginRequest(
            email = email,
            password = password
        )

        return loginApi.login(request)
    }
}