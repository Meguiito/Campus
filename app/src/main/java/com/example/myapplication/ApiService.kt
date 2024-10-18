package com.example.myapplication

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("reservas")
    suspend fun getReservas(): List<ReservaResponse>

    @GET("reservas/{rut}")
    suspend fun getReservasByRut(@Path("rut") rut: String): List<ReservaResponse>

    @PUT("reservas/{id}")
    suspend fun updateReserva(
        @Path("id") id: String,
        @Body reserva: ReservaRequest
    ): ApiResponse

    @DELETE("reservas/{id}")
    suspend fun deleteReserva(@Path("id") id: String): ApiResponse

    @Multipart
    @PUT("/users/{user_id}/profile")
    suspend fun uploadProfileImage(
        @Path("user_id") userId: String,
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>  // Cambiado a Response<ResponseBody>

    @GET("reservas/mes/{mes}")
    suspend fun getReservasMes2(@Path("mes") mes: String): DiasReservadosResponse

    @GET("/disponibilidad")
    suspend fun getDisponibilidad(
        @Query("cancha") cancha: String,
        @Query("mes") mes: String,
        @Query("dia") dia: String
    ): DisponibilidadResponse

    @GET("reservas/mes/{mes}")
    suspend fun getReservasMes(@Path("mes") mes: String): ReservasMesResponse

    @POST("/users/uploadImage")
    suspend fun uploadProfileImage(@Body imageRequest: ImageRequest): ApiResponse
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

// Data classes
data class EmailRequest(val email: String)
data class ImageRequest(val email: String, val image: String)  // Imagen en base64


data class UserResponse(
    val username: String,
    val email: String,
    val rut: String,
    val image: String // Propiedad para la imagen de perfil
)

data class ReservasMesResponse(
    val dias_reservados_parciales: List<Int>,
    val dias_reservados_completos: List<Int>,
    val dias_no_reservados: List<Int>
)

data class DisponibilidadResponse(
    val horarios_ocupados: List<String>
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

data class DiasReservadosResponse(
    val dias_reservados_parciales: List<Int>, // Días con reservas parciales
    val dias_reservados_completos: List<Int>  // Días completamente reservados
)

data class UserRequest(val rut: String, val username: String, val password: String, val email: String)
data class LoginRequest(val email: String, val password: String)
data class ApiResponse(val message: String, val error: String? = null)
