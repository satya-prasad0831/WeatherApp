package com.example.weatherapplication.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}
