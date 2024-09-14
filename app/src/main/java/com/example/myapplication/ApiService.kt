package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// URL a la API en localhost
private const val BASE_URL = "http://10.0.2.2:5000/"

interface ApiService {
    @POST("users")
    suspend fun createUser(@Body user: UserRequest): ApiResponse

    @POST("users/verify")
    suspend fun verifyUser(@Body loginRequest: LoginRequest): ApiResponse
}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

data class UserRequest(val rut: String, val username: String, val password: String, val email: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val error: String? = null)
