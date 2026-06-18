package com.example.vistaraapp.repositories

import com.example.vistaraapp.api.RetrofitClient
import com.example.vistaraapp.api_requests_responses.ForgotPasswordRequest
import com.example.vistaraapp.api_requests_responses.LoginRequest
import com.example.vistaraapp.api_requests_responses.LoginResponse
import com.example.vistaraapp.api_requests_responses.RegisterRequest
import com.example.vistaraapp.api_requests_responses.RegisterResponse
import com.example.vistaraapp.database.ContactDao
import com.example.vistaraapp.database.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(private val contactDao: ContactDao) {

    //  1. USER REGISTRATION (ONLINE WITH LOCAL CACHING)
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
                val sanitizedPhone = phoneNumber.replace(Regex("[^0-9]"), "")
                val sanitizedEmergencyPhone = emergencyContactPhone.replace(Regex("[^0-9]"), "")

                val request = RegisterRequest(
                    email = email,
                    password = password,
                    fullName = fullName,
                    phoneNumber = sanitizedPhone,
                    nationalId = nationalId,
                    emergencyContactName = emergencyContactName,
                    emergencyContactPhone = sanitizedEmergencyPhone
                )

                val response = RetrofitClient.instance.registerUser(request)

                if (response.success) {
                    // Cache the user's details locally in Room database upon successful network registration
                    val newUser = Contact(
                        id = 1, // Force ID = 1 to overwrite any existing cached user profile safely
                        fullName = fullName,
                        email = email.trim().lowercase(),
                        phoneNumber = sanitizedPhone,
                        idNumber = nationalId,
                        emergencyNumber = sanitizedEmergencyPhone,
                        password = password
                    )
                    contactDao.upsertContact(newUser)
                    RegisterResult.Success(response)
                } else {
                    RegisterResult.Error(response.message ?: "Registration failed")
                }
            } catch (e: HttpException) {
                RegisterResult.Error("Server error: ${e.code()} - ${e.message()}")
            } catch (_: IOException) {
                RegisterResult.Error("Network error: Please check your internet connection")
            } catch (e: Exception) {
                RegisterResult.Error(e.message ?: "An unknown registration error occurred")
            }
        }
    }

    //  2. USER LOGIN (ONLINE WITH OFFLINE ROOM FALLBACK)
    suspend fun loginUser(email: String, password: String): LoginResult {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(email = email, password = password)

                // Try remote API login first
                val response = RetrofitClient.instance.loginUser(request)

                if (response.success) {
                    LoginResult.Success(response)
                } else {
                    LoginResult.Error(response.message ?: "Invalid email or password")
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> LoginResult.Error("Invalid email or password")
                    else -> LoginResult.Error("Server error: ${e.code()}")
                }
            } catch (_: IOException) {
                // Device is offline or server cannot be reached; check Room cache
                val localContact = contactDao.getContactByEmail(email)


                if (localContact != null && localContact.password == password) {
                    val offlineResponse = LoginResponse(
                        success = true,
                        message = "Logged in successfully offline",
                        token = "offline_session_token"
                    )
                    LoginResult.Success(offlineResponse)
                } else {
                    LoginResult.Error("Offline mode: No matching account records found on this device.")
                }
            } catch (e: Exception) {
                LoginResult.Error(e.message ?: "An unexpected login error occurred")
            }
        }
    }

    //  3. FORGOT PASSWORD OPERATION
    suspend fun forgotPassword(email: String): ForgotPasswordResult {
        return withContext(Dispatchers.IO) {
            try {
                val request = ForgotPasswordRequest(email = email)
                val response = RetrofitClient.instance.forgotPassword(request)

                if (response.success) {
                    ForgotPasswordResult.Success(response.message ?: "A reset link has been sent to your email.")
                } else {
                    ForgotPasswordResult.Error(response.message ?: "Failed to process request.")
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    ForgotPasswordResult.Error("No account found with this email address.")
                } else {
                    ForgotPasswordResult.Error("Server error occurred.")
                }
            } catch (_: IOException) {
                ForgotPasswordResult.Error("Network error: Please verify your internet connectivity.")
            } catch (e: Exception) {
                ForgotPasswordResult.Error(e.message ?: "An unexpected recovery error occurred")
            }
        }
    }
}

// RESULT SEALED CLASSES REQUIRED BY THE AUTHVIEWMODEL

sealed class RegisterResult {
    data class Success(val response: RegisterResponse) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

sealed class LoginResult {
    data class Success(val response: LoginResponse) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class ForgotPasswordResult {
    data class Success(val message: String) : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
}