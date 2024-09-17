package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

// URL a la API en localhost
private const val BASE_URL = "http://10.0.2.2:5000/"

interface ApiService {
    @POST("users")
    suspend fun createUser(@Body user: UserRequest): ApiResponse

    @POST("users/verify")
    suspend fun verifyUser(@Body loginRequest: LoginRequest): ApiResponse

    @GET("espacios")
    suspend fun getEspacios(): List<String>

    @POST("reservas")
    suspend fun crearReserva(@Body reserva: ReservaRequest): ApiResponse
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

data class ReservaRequest(
    val nombre: String,
    val rut: String,
    val carrera: String,
    val cancha: String,
    val duracion: String
)

data class UserRequest(val rut: String, val username: String, val password: String, val email: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val error: String? = null)
