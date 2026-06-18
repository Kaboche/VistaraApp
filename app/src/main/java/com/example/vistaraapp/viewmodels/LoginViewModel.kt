package com.example.vistaraapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vistaraapp.repositories.AuthRepository
import com.example.vistaraapp.repositories.LoginResult
import com.example.vistaraapp.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.SetEmail -> _state.update {
                it.copy(email = event.email, errorMessage = null)
            }

            is LoginEvent.SetPassword -> _state.update {
                it.copy(password = event.password, errorMessage = null)
            }

            is LoginEvent.ClearError -> _state.update {
                it.copy(errorMessage = null)
            }

            is LoginEvent.LoginClicked -> performLogin()
        }
    }

    private fun performLogin() {

        val email = _state.value.email.trim().lowercase()
        val password = _state.value.password.trim()

        if (email.isEmpty() || password.isEmpty()) {
            _state.update {
                it.copy(errorMessage = "Email and password cannot be empty")
            }
            return
        }

        _state.update {
            it.copy(isLoading = true, errorMessage = null)
        }

        viewModelScope.launch {

            when (val result = authRepository.loginUser(email, password)) {

                is LoginResult.Success -> {

                    val token = result.response.getActualToken()

                    if (token != null) {

                        sessionManager.saveToken(token)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                isLoginSuccess = true,
                                token = token
                            )
                        }

                    } else {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Token missing from response"
                            )
                        }
                    }
                }

                is LoginResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
}