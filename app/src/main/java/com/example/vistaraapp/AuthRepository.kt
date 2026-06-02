package com.example.vistaraapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository {

    // Registration
    suspend fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        nationalId: String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ): RegisterResult {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    nationalId = nationalId,
                    emergencyContactName = emergencyContactName,
                    emergencyContactPhone = emergencyContactPhone
                )

                val response = RetrofitClient.instance.registerUser(request)

                if (response.success) {
                    RegisterResult.Success(response)
                } else {
                    RegisterResult.Error(response.message ?: "Registration failed")
                }
            } catch (e: HttpException) {
                RegisterResult.Error("Network error: ${e.code()} - ${e.message()}")
            } catch (e: IOException) {
                RegisterResult.Error("Network error: Please check your internet connection")
            } catch (e: Exception) {
                RegisterResult.Error("Error: ${e.message}")
            }
        }
    }

    // Login
    suspend fun loginUser(
        email: String,
        password: String
    ): LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(
                    email = email,
                    password = password
                )

                val response = RetrofitClient.instance.loginUser(request)

                if (response.success) {
                    LoginResult.Success(response)
                } else {
                    LoginResult.Error(response.message ?: "Login failed")
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> LoginResult.Error("Invalid email or password")
                    404 -> LoginResult.Error("Login service not found")
                    else -> LoginResult.Error("Network error: ${e.code()} - ${e.message()}")
                }
            } catch (e: IOException) {
                LoginResult.Error("Network error: Please check your internet connection")
            } catch (e: Exception) {
                LoginResult.Error("Error: ${e.message}")
            }
        }
    }
}

// Registration Result
sealed class RegisterResult {
    data class Success(val response: RegisterResponse) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

// Login Result
sealed class LoginResult {
    data class Success(val response: LoginResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
}