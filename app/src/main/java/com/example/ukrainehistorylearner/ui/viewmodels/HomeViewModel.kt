package com.example.ukrainehistorylearner.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.User
import com.example.ukrainehistorylearner.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val currentUser: User? = null,
    val articlesReadCount: Int = 0,
    val quizzesCompletedCount: Int = 0,
    val totalScore: Int = 0,
    val recentAchievements: List<String> = emptyList(),
    val recommendedArticles: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val errorMessageDetails: String? = null
)

sealed class HomeEvent {
    object LoadData : HomeEvent()
    data class ArticleClicked(val articleId: Int) : HomeEvent()
    object RefreshData : HomeEvent()
    object ClearError : HomeEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadData -> {
                loadHomeData()
            }
            is HomeEvent.ArticleClicked -> {
                handleArticleClick(event.articleId)
            }
            is HomeEvent.RefreshData -> {
                refreshData()
            }
            is HomeEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessageResId = null)
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null)

            try {
                // Завантаження даних користувача
                val currentUser = userRepository.getCurrentUser()
                val userStats = userRepository.getUserStats(currentUser?.id ?: "")
                val achievements = userRepository.getRecentAchievements(currentUser?.id ?: "")
                val recommendedArticles = userRepository.getRecommendedArticles(currentUser?.id ?: "")

                _uiState.value = _uiState.value.copy(
                    currentUser = currentUser,
                    articlesReadCount = userStats.articlesRead,
                    quizzesCompletedCount = userStats.quizzesCompleted,
                    totalScore = userStats.totalScore,
                    recentAchievements = achievements,
                    recommendedArticles = recommendedArticles,
                    isLoading = false
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.home_error_loading_data,
                    errorMessageDetails = e.message
                )
            }
        }
    }

    private fun handleArticleClick(articleId: Int) {
        viewModelScope.launch {
            try {
                userRepository.markArticleAsClicked(articleId)
                // Оновлення статистики після кліку на статтю
                loadHomeData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessageResId = R.string.home_error_statistics_update,
                    errorMessageDetails = e.message
                )
            }
        }
    }

    private fun refreshData() {
        loadHomeData()
    }
}