package com.example.ukrainehistorylearner.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.data.repository.ArticlesRepository
import com.example.ukrainehistorylearner.data.repository.ArticlesRepositoryImpl
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.utils.ArticleDataBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ArticlesUiState(
    val articles: List<HistoricalArticle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val showAddDialog: Boolean = false,
    val showUpdateDialog: Boolean = false,
    val selectedMaterial: HistoricalArticle? = null,
    val searchQuery: String = "",
    val selectedPeriod: HistoricalPeriod? = null
)

sealed class ArticlesEvent {
    object ShowAddDialog : ArticlesEvent()
    object ShowUpdateDialog : ArticlesEvent()
    object HideAddDialog : ArticlesEvent()
    object HideUpdateDialog : ArticlesEvent()
    data class AddArticle(val article: HistoricalArticle) : ArticlesEvent()
    data class RemoveArticle(val article: HistoricalArticle) : ArticlesEvent()
    data class UpdateArticle(val article: HistoricalArticle) : ArticlesEvent()
    data class SelectArticle(val material: HistoricalArticle?) : ArticlesEvent()
    data class SearchQueryChanged(val query: String) : ArticlesEvent()
    data class PeriodFilterChanged(val period: HistoricalPeriod?) : ArticlesEvent()
    object ClearError : ArticlesEvent()
}

class ArticlesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: ArticlesRepository by lazy {
        val database = ArticleDataBase.getDatabase(application)
        ArticlesRepositoryImpl(database.articleEntryDao())
    }

    private val _uiState = MutableStateFlow(ArticlesUiState())
    val uiState: StateFlow<ArticlesUiState> = _uiState.asStateFlow()

    init {
        loadArticles()
        viewModelScope.launch {
            repository.articles.collect {
                updateFilteredArticles()
            }
        }
    }

    fun handleEvent(event: ArticlesEvent) {
        when (event) {
            is ArticlesEvent.ShowAddDialog -> {
                _uiState.value = _uiState.value.copy(showAddDialog = true)
            }
            is ArticlesEvent.ShowUpdateDialog -> {
                _uiState.value = _uiState.value.copy(showUpdateDialog = true)
            }
            is ArticlesEvent.HideAddDialog -> {
                _uiState.value = _uiState.value.copy(showAddDialog = false)
            }
            is ArticlesEvent.HideUpdateDialog -> {
                _uiState.value = _uiState.value.copy(showUpdateDialog = false)
            }
            is ArticlesEvent.AddArticle -> {
                addArticle(event.article)
            }
            is ArticlesEvent.UpdateArticle -> {
                updateArticle(event.article)
            }
            is ArticlesEvent.RemoveArticle -> {
                removeArticle(event.article)
            }
            is ArticlesEvent.SelectArticle -> {
                _uiState.value = _uiState.value.copy(selectedMaterial = event.material)
            }
            is ArticlesEvent.SearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
                updateFilteredArticles()
            }
            is ArticlesEvent.PeriodFilterChanged -> {
                _uiState.value = _uiState.value.copy(selectedPeriod = event.period)
                updateFilteredArticles()
            }
            is ArticlesEvent.ClearError -> {
                _uiState.value = _uiState.value.copy(errorMessageResId = null)
            }
        }
    }

    private fun loadArticles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val currentState = _uiState.value
                val filteredArticles = repository.getFilteredArticles(
                    period = currentState.selectedPeriod,
                    query = currentState.searchQuery
                )

                _uiState.value = currentState.copy(
                    articles = filteredArticles,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.article_error_loading_articles
                )
            }
        }
    }

    private fun addArticle(article: HistoricalArticle) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null)

            val success = repository.addArticle(article)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    showAddDialog = false,
                    isLoading = false
                )
                updateFilteredArticles()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.article_error_adding_article
                )
            }
        }
    }

    private fun removeArticle(article: HistoricalArticle) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null)

            val success = repository.removeArticle(article)
            if (success) {
                _uiState.value = _uiState.value.copy(isLoading = false)
                updateFilteredArticles()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.article_error_removing_article
                )
            }
        }
    }

    private fun updateArticle(article: HistoricalArticle) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessageResId = null)
            val success = repository.updateArticle(article)
            if (success) {
                _uiState.value = _uiState.value.copy(
                    showUpdateDialog = false,
                    selectedMaterial = null,
                    isLoading = false)
                updateFilteredArticles()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.article_error_updating_article
                )
            }
        }
    }

    private fun updateFilteredArticles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val currentState = _uiState.value
                val filteredArticles = repository.getFilteredArticles(
                    period = currentState.selectedPeriod,
                    query = currentState.searchQuery
                )

                _uiState.value = currentState.copy(
                    articles = filteredArticles,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageResId = R.string.article_error_filtering_articles
                )
            }
        }
    }
}