package com.example.ukrainehistorylearner.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ukrainehistorylearner.model.User
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val editUsername: String = "",
    val editBirthDate: LocalDate? = null,
    val selectedHistoricalPeriods: List<HistoricalPeriod> = emptyList(),
    val showDatePicker: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val totalArticlesRead: Int = 0,
    val quizScoreAverage: Float = 0f,
    val isLoggedOut: Boolean = false
)

sealed class ProfileEvent {
    object LoadProfile : ProfileEvent()
    object StartEditing : ProfileEvent()
    object CancelEditing : ProfileEvent()
    object SaveProfile : ProfileEvent()
    data class EditUsernameChanged(val username: String) : ProfileEvent()
    data class EditBirthDateChanged(val birthDate: LocalDate) : ProfileEvent()
    data class HistoricalPeriodToggled(val period: HistoricalPeriod) : ProfileEvent()
    object ShowDatePicker : ProfileEvent()
    object HideDatePicker : ProfileEvent()
    object Logout : ProfileEvent()
    object ClearMessages : ProfileEvent()
}

class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.StartEditing -> startEditing()
            is ProfileEvent.CancelEditing -> cancelEditing()
            is ProfileEvent.SaveProfile -> saveProfile()
            is ProfileEvent.EditUsernameChanged -> {
                _uiState.value = _uiState.value.copy(
                    editUsername = event.username,
                    errorMessage = null
                )
            }
            is ProfileEvent.EditBirthDateChanged -> {
                _uiState.value = _uiState.value.copy(
                    editBirthDate = event.birthDate,
                    showDatePicker = false,
                    errorMessage = null
                )
            }
            is ProfileEvent.HistoricalPeriodToggled -> {
                toggleHistoricalPeriod(event.period)
            }
            is ProfileEvent.ShowDatePicker -> {
                _uiState.value = _uiState.value.copy(showDatePicker = true)
            }
            is ProfileEvent.HideDatePicker -> {
                _uiState.value = _uiState.value.copy(showDatePicker = false)
            }
            is ProfileEvent.Logout -> logout()
            is ProfileEvent.ClearMessages -> {
                _uiState.value = _uiState.value.copy(
                    errorMessage = null,
                    successMessage = null
                )
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val user = userRepository.getCurrentUser()
                val stats = userRepository.getUserStats(user?.id ?: "")

                _uiState.value = _uiState.value.copy(
                    user = user,
                    isLoading = false,
                    totalArticlesRead = stats.articlesRead,
                    quizScoreAverage = stats.averageQuizScore
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Помилка завантаження профілю: ${e.message}"
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startEditing() {
        val currentUser = _uiState.value.user
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(
                isEditing = true,
                editUsername = currentUser.username,
                editBirthDate = try {
                    LocalDate.parse(currentUser.birthDate)
                } catch (e: Exception) {
                    null
                },
                selectedHistoricalPeriods = currentUser.favoriteHistoricalPeriods,
                errorMessage = null
            )
        }
    }

    private fun cancelEditing() {
        _uiState.value = _uiState.value.copy(
            isEditing = false,
            editUsername = "",
            editBirthDate = null,
            selectedHistoricalPeriods = emptyList(),
            showDatePicker = false,
            errorMessage = null
        )
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentUser = currentState.user

            if (currentUser == null) {
                _uiState.value = currentState.copy(
                    errorMessage = "Користувач не знайдений"
                )
                return@launch
            }

            val validationError = validateEditForm(currentState)
            if (validationError != null) {
                _uiState.value = currentState.copy(errorMessage = validationError)
                return@launch
            }

            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            try {
                val updatedUser = currentUser.copy(
                    username = currentState.editUsername,
                    birthDate = currentState.editBirthDate?.toString() ?: currentUser.birthDate,
                    favoriteHistoricalPeriods = currentState.selectedHistoricalPeriods
                )

                userRepository.updateUser(updatedUser)

                _uiState.value = currentState.copy(
                    user = updatedUser,
                    isLoading = false,
                    isEditing = false,
                    successMessage = "Профіль успішно оновлено",
                    editUsername = "",
                    editBirthDate = null,
                    selectedHistoricalPeriods = emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    errorMessage = "Помилка збереження профілю: ${e.message}"
                )
            }
        }
    }

    private fun toggleHistoricalPeriod(period: HistoricalPeriod) {
        val currentPeriods = _uiState.value.selectedHistoricalPeriods.toMutableList()
        if (currentPeriods.contains(period)) {
            currentPeriods.remove(period)
        } else {
            currentPeriods.add(period)
        }
        _uiState.value = _uiState.value.copy(
            selectedHistoricalPeriods = currentPeriods,
            errorMessage = null
        )
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _uiState.value = _uiState.value.copy(
                    isLoggedOut = true,
                    user = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Помилка виходу з системи: ${e.message}"
                )
            }
        }
    }

    private fun validateEditForm(state: ProfileUiState): String? {
        return when {
            state.editUsername.isBlank() -> "Логін не може бути порожнім"
            state.editUsername.length < 3 -> "Логін повинен містити принаймні 3 символи"
            state.editBirthDate == null -> "Оберіть дату народження"
            else -> null
        }
    }

    val isEditFormValid: Boolean
        get() = validateEditForm(_uiState.value) == null
}