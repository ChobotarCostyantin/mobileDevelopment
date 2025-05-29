package com.example.ukrainehistorylearner.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.ukrainehistorylearner.model.HistoricalPeriod

data class QuizUiState(
    val selectedDifficulty: Float = 0.5f,
    val selectedPeriod: HistoricalPeriod = HistoricalPeriod.INDEPENDENCE,
    val questionCount: Int = 5,
    val showInfoAfterQuestion: Boolean = true,
    val expandedDropdown: Boolean = false,
    val isQuizStarted: Boolean = false
)

sealed class QuizEvent {
    data class DifficultyChanged(val difficulty: Float) : QuizEvent()
    data class PeriodChanged(val period: HistoricalPeriod) : QuizEvent()
    data class QuestionCountChanged(val count: Int) : QuizEvent()
    data class ShowInfoToggled(val show: Boolean) : QuizEvent()
    object ToggleDropdown : QuizEvent()
    object StartQuiz : QuizEvent()
    object ResetQuiz : QuizEvent()
}

class QuizViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun handleEvent(event: QuizEvent) {
        when (event) {
            is QuizEvent.DifficultyChanged -> {
                val normalizedDifficulty = when {
                    event.difficulty < 0.25f -> 0.0f
                    event.difficulty < 0.75f -> 0.5f
                    else -> 1.0f
                }
                _uiState.value = _uiState.value.copy(selectedDifficulty = normalizedDifficulty)
            }
            is QuizEvent.PeriodChanged -> {
                _uiState.value = _uiState.value.copy(
                    selectedPeriod = event.period,
                    expandedDropdown = false
                )
            }
            is QuizEvent.QuestionCountChanged -> {
                _uiState.value = _uiState.value.copy(questionCount = event.count)
            }
            is QuizEvent.ShowInfoToggled -> {
                _uiState.value = _uiState.value.copy(showInfoAfterQuestion = event.show)
            }
            is QuizEvent.ToggleDropdown -> {
                _uiState.value = _uiState.value.copy(
                    expandedDropdown = !_uiState.value.expandedDropdown
                )
            }
            is QuizEvent.StartQuiz -> {
                startQuiz()
            }
            is QuizEvent.ResetQuiz -> {
                _uiState.value = _uiState.value.copy(isQuizStarted = false)
            }
        }
    }

    private fun startQuiz() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isQuizStarted = true)
    }

    val availableQuestionCounts = listOf(5, 10, 15)
}