package com.example.myapplication

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

// URL a la API en localhost
private const val BASE_URL = "http://192.168.1.10:5000/"

interface ApiService {
    @POST("users")
    suspend fun createUser(@Body user: UserRequest): ApiResponse

    @POST("users/verify")
    suspend fun verifyUser(@Body loginRequest: LoginRequest): ApiResponse

    @GET("espacios")
    suspend fun getEspacios(): List<String>

    @POST("reservas")
    suspend fun crearReserva(@Body reserva: ReservaRequest): ApiResponse

    @POST("/users/getByEmail")
    suspend fun getUserByEmail(@Body emailRequest: EmailRequest): UserResponse

    @GET("reservas/{rut}")
    suspend fun getReservasByRut(@Path("rut") rut: String): List<ReservaResponse>

    @PUT("reservas/{id}")
    suspend fun updateReserva(
        @Path("id") id: String,
        @Body reserva: ReservaRequest
    ): ApiResponse


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

data class EmailRequest(
    val email: String
)

data class UserResponse(
    val username: String,
    val email: String,
    val rut: String
)

data class ReservaRequest(
    val nombre: String,
    val rut: String,
    val carrera: String,
    val cancha: String,
    val duracion: String,
    val mes: String,
    val dia: String
)
data class ReservaResponse(
    val id: String,
    val nombre: String,
    val rut: String,
    val carrera: String,
    val cancha: String,
    val duracion: String,
    val mes: String,
    val dia: String
)

data class UserRequest(val rut: String, val username: String, val password: String, val email: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val error: String? = null)
