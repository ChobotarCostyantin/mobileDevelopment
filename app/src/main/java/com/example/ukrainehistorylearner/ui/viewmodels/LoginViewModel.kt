package com.example.ukrainehistorylearner.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.ukrainehistorylearner.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val isLoginSuccessful: Boolean = false
)

sealed class LoginEvent {
    data class UsernameChanged(val username: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
    object Login : LoginEvent()
    object ClearError : LoginEvent()
}

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _uiState.value = _uiState.value.copy(
                    username = event.username,
                    errorMessageResId = null
                )
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    errorMessageResId = null
                )
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _uiState.value = _uiState.value.copy(
                    isPasswordVisible = !_uiState.value.isPasswordVisible
                )
            }
            is LoginEvent.Login -> {
                performLogin()
            }
            is LoginEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessageResId = null)
            }
        }
    }

    private fun performLogin() {
        val currentState = _uiState.value

        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessageResId = R.string.login_error_empty_fields
            )
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessageResId = null)

        if (currentState.username == "admin" && currentState.password == "password") {
            _uiState.value = currentState.copy(
                isLoading = false,
                isLoginSuccessful = true
            )
        } else {
            _uiState.value = currentState.copy(
                isLoading = false,
                errorMessageResId = R.string.login_error_incorrect_credentials
            )
        }
    }

    val isFormValid: Boolean
        get() = _uiState.value.username.isNotBlank() && _uiState.value.password.isNotBlank()
}