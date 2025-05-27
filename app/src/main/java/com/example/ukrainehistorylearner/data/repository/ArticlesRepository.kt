package com.example.ukrainehistorylearner.data.repository

import android.util.Log
import com.example.ukrainehistorylearner.data.database.ArticleEntry
import com.example.ukrainehistorylearner.data.database.ArticleEntryDao
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.*

interface ArticlesRepository {
    val articles: StateFlow<List<HistoricalArticle>>
    suspend fun addArticle(article: HistoricalArticle): Boolean
    suspend fun removeArticle(article: HistoricalArticle): Boolean
    suspend fun updateArticle(article: HistoricalArticle): Boolean
    suspend fun getFilteredArticles(period: HistoricalPeriod?, query: String): List<HistoricalArticle>
    suspend fun getArticleById(id: String): ArticleEntry?
    suspend fun refreshArticles()
}

class ArticlesRepositoryImpl(
    private val articleDao: ArticleEntryDao
) : ArticlesRepository {

    private val _articles = MutableStateFlow<List<HistoricalArticle>>(emptyList())
    override val articles: StateFlow<List<HistoricalArticle>> = _articles.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            articleDao.deleteAllArticles()
            initializeDatabase()
            refreshArticles()
        }
    }

    private suspend fun initializeDatabase() {
        try {
            val existingArticles = articleDao.getAllArticles()
            if (existingArticles.isEmpty()) {
                val initialArticles = getInitialArticleEntries()
                initialArticles.forEach { article ->
                    articleDao.insert(article)
                }
            }
        } catch (e: Exception) {
            // Обробка помилки ініціалізації
        }
    }

    override suspend fun refreshArticles() {
        try {
            val articleEntries = articleDao.getAllArticles()
            val historicalArticles = articleEntries.map { it.toHistoricalArticle() }
            _articles.value = historicalArticles
        } catch (e: Exception) {
            // Обробка помилки
        }
    }

    // Новый метод для фильтрации с использованием запросов к базе данных
    override suspend fun getFilteredArticles(period: HistoricalPeriod?, query: String): List<HistoricalArticle> {
        return try {
            val articleEntries = when {
                period != null && query.isNotBlank() -> {
                    articleDao.getFilteredArticles(period, query)
                }
                period != null && query.isBlank() -> {
                    articleDao.getArticlesByPeriod(period)
                }
                period == null && query.isNotBlank() -> {
                    articleDao.getArticlesByQuery(query)
                }
                else -> {
                    articleDao.getAllArticles()
                }
            }
            articleEntries.map { it.toHistoricalArticle() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getArticleById(id: String): ArticleEntry? {
        return try {
            articleDao.getArticleById(id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addArticle(article: HistoricalArticle): Boolean {
        return try {
            val articleEntry = article.toArticleEntry()
            articleDao.insert(articleEntry)
            refreshArticles()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun removeArticle(article: HistoricalArticle): Boolean {
        return try {
            val articleEntry = article.toArticleEntry()
            val rowsDeleted = articleDao.delete(articleEntry)
            Log.d("ArticlesRepository", "Deleted rows: $rowsDeleted")
            refreshArticles()
            true
        } catch (e: Exception) {
            Log.e("ArticlesRepository", "Error removing article ${article.title}", e)
            false
        }
    }

    override suspend fun updateArticle(article: HistoricalArticle): Boolean {
        return try {
            val oldArticle = getArticleById(article.id)
            if (oldArticle != null) {
                Log.d("ArticlesRepository", "Old article id: ${oldArticle.id}")
            }
            val articleEntry = article.toArticleEntry()
            Log.d("ArticlesRepository", "New article id: ${articleEntry.id}")
            val rowsUpdated = articleDao.update(articleEntry)
            Log.d("ArticlesRepository", "Updated rows: $rowsUpdated")
            refreshArticles()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Функції конвертації між ArticleEntry та HistoricalArticle
    private fun HistoricalArticle.toArticleEntry(): ArticleEntry {
        return ArticleEntry(
            id = this.id,
            title = this.title,
            period = this.period,
            author = this.author,
            wordCount = this.wordCount,
            tags = this.tags
        )
    }

    private fun ArticleEntry.toHistoricalArticle(): HistoricalArticle {
        return HistoricalArticle(
            id = this.id,
            title = this.title,
            period = this.period,
            author = this.author,
            wordCount = this.wordCount,
            tags = this.tags
        )
    }

    private fun getInitialArticleEntries(): List<ArticleEntry> {
        return listOf(
            HistoricalArticle(
                title = "Київська Русь",
                period = HistoricalPeriod.KYIV_RUS,
                author = "Нестор Літописець",
                wordCount = 900,
                tags = listOf("давня історія", "релігія")
            ),
            HistoricalArticle(
                title = "Утворення УНР",
                period = HistoricalPeriod.REVOLUTION_PERIOD,
                author = "Михайло Грушевський",
                wordCount = 1200,
                tags = listOf("революція", "національний рух")
            ),
            HistoricalArticle(
                title = "Проголошення незалежності",
                period = HistoricalPeriod.INDEPENDENCE,
                author = "Леонід Кравчук",
                wordCount = 1500,
                tags = listOf("1991", "державність")
            )
        ).map { it.toArticleEntry() }
    }
}