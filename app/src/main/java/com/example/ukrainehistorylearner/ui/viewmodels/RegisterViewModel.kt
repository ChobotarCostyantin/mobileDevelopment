package com.example.ukrainehistorylearner.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthDate: LocalDate? = null,
    val isPasswordVisible: Boolean = false,
    val acceptedPrivacyPolicy: Boolean = false,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

sealed class RegisterEvent {
    data class UsernameChanged(val username: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data class BirthDateChanged(val birthDate: LocalDate) : RegisterEvent()
    object TogglePasswordVisibility : RegisterEvent()
    data class PrivacyPolicyAcceptanceChanged(val accepted: Boolean) : RegisterEvent()
    object ShowDatePicker : RegisterEvent()
    object HideDatePicker : RegisterEvent()
    object Register : RegisterEvent()
    object ClearError : RegisterEvent()
}

class RegisterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UsernameChanged -> {
                _uiState.value = _uiState.value.copy(
                    username = event.username,
                    errorMessage = null
                )
            }
            is RegisterEvent.PasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    password = event.password,
                    errorMessage = null
                )
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(
                    confirmPassword = event.confirmPassword,
                    errorMessage = null
                )
            }
            is RegisterEvent.BirthDateChanged -> {
                _uiState.value = _uiState.value.copy(
                    birthDate = event.birthDate,
                    showDatePicker = false,
                    errorMessage = null
                )
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _uiState.value = _uiState.value.copy(
                    isPasswordVisible = !_uiState.value.isPasswordVisible
                )
            }
            is RegisterEvent.PrivacyPolicyAcceptanceChanged -> {
                _uiState.value = _uiState.value.copy(
                    acceptedPrivacyPolicy = event.accepted,
                    errorMessage = null
                )
            }
            is RegisterEvent.ShowDatePicker -> {
                _uiState.value = _uiState.value.copy(showDatePicker = true)
            }
            is RegisterEvent.HideDatePicker -> {
                _uiState.value = _uiState.value.copy(showDatePicker = false)
            }
            is RegisterEvent.Register -> {
                performRegistration()
            }
            is RegisterEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessage = null)
            }
        }
    }

    private fun performRegistration() {
        val currentState = _uiState.value

        val validationError = validateForm(currentState)
        if (validationError != null) {
            _uiState.value = currentState.copy(errorMessage = validationError)
            return
        }

        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        if (currentState.username == "admin") {
            _uiState.value = currentState.copy(
                isLoading = false,
                errorMessage = "Користувач з таким логіном вже існує"
            )
        } else {
            _uiState.value = currentState.copy(
                isLoading = false,
                isRegistrationSuccessful = true
            )
            println("Успішна реєстрація: логін - ${currentState.username}, дата народження - ${currentState.birthDate}")
        }
    }

    private fun validateForm(state: RegisterUiState): String? {
        return when {
            state.username.isBlank() -> "Логін не може бути порожнім"
            state.username.length < 3 -> "Логін повинен містити принаймні 3 символи"
            state.password.isBlank() -> "Пароль не може бути порожнім"
            state.password.length < 6 -> "Пароль повинен містити принаймні 6 символів"
            state.confirmPassword.isBlank() -> "Підтвердження паролю не може бути порожнім"
            state.password != state.confirmPassword -> "Паролі не збігаються"
            state.birthDate == null -> "Оберіть дату народження"
            !state.acceptedPrivacyPolicy -> "Необхідно прийняти політику конфіденційності"
            else -> null
        }
    }

    val passwordsMatch: Boolean
        get() = _uiState.value.password == _uiState.value.confirmPassword

    val isFormValid: Boolean
        get() = validateForm(_uiState.value) == null
}