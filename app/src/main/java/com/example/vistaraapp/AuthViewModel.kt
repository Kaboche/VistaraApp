package com.example.vistaraapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // Registration state
    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    // Login state
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    // Registration
    fun registerUser(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String,
        nationalId: String,
        emergencyContactName: String,
        emergencyContactPhone: String
    ) {
        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading

            val result = repository.registerUser(
                email = email,
                password = password,
                fullName = fullName,
                phoneNumber = phoneNumber,
                nationalId = nationalId,
                emergencyContactName = emergencyContactName,
                emergencyContactPhone = emergencyContactPhone
            )

            _registerState.value = when (result) {
                is RegisterResult.Success -> RegisterUiState.Success(result.response)
                is RegisterResult.Error -> RegisterUiState.Error(result.message)
            }
        }
    }

    // Login
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading

            val result = repository.loginUser(email, password)

            _loginState.value = when (result) {
                is LoginResult.Success -> LoginUiState.Success(result.response)
                is LoginResult.Error -> LoginUiState.Error(result.message)
            }
        }
    }

    // ✅ Reset registration state
    fun resetRegisterState() {
        _registerState.value = RegisterUiState.Idle
    }

    // ✅ Reset login state
    fun resetLoginState() {
        _loginState.value = LoginUiState.Idle
    }
}

// Registration UI State
sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val response: RegisterResponse) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

// Login UI State
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val response: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}