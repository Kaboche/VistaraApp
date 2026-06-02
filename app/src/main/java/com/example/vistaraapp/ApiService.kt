package com.example.vistaraapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

// ========== DATA MODELS ==========

// Request model for registration
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phoneNumber: String,
    val nationalId: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String
)

// Login request
data class LoginRequest(
    val email: String,
    val password: String
)

// Response model for registration
data class RegisterResponse(
    val success: Boolean,
    val message: String? = null,
    val userId: Int? = null,
    val token: String? = null
)

// Response for login
data class LoginResponse(
    val success: Boolean,
    val message: String? = null,
    val userId: Int? = null,
    val token: String? = null,
    val fullName: String? = null,
    val email: String? = null
)

// ========== API INTERFACE ==========

interface ApiService {
    @POST("auth/register/tourist")
    suspend fun registerUser(@Body request: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun loginUser(@Body request: LoginRequest): LoginResponse
}

// ========== RETROFIT CLIENT ==========

object RetrofitClient {
    // Use your actual BASE_URL - replace with your ngrok URL
    private const val BASE_URL = "https://undrafted-erasable-crevice.ngrok-free.dev/api/v1/"

    // For now, use this hardcoded URL until BuildConfig works
    // private const val BASE_URL = com.example.vistaraapp.BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("ngrok-skip-browser-warning", "true")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}