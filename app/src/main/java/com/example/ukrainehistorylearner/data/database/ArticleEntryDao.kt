package com.example.ukrainehistorylearner.data.database

import androidx.room.*
import com.example.ukrainehistorylearner.model.HistoricalPeriod

@Dao
interface ArticleEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: ArticleEntry)

    @Delete
    suspend fun delete(article: ArticleEntry): Int

    @Update
    suspend fun update(article: ArticleEntry): Int

    @Query("SELECT * FROM article_entry")
    suspend fun getAllArticles(): List<ArticleEntry>

    @Query("DELETE FROM article_entry")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM article_entry WHERE id = :id")
    suspend fun getArticleById(id: String): ArticleEntry

    // Новый метод для фильтрации по периоду и поисковому запросу
    @Query("SELECT * FROM article_entry WHERE (title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%') AND period = :period")
    suspend fun getFilteredArticles(period: HistoricalPeriod, query: String): List<ArticleEntry>

    // Метод для фильтрации только по поисковому запросу (без периода)
    @Query("SELECT * FROM article_entry WHERE title LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    suspend fun getArticlesByQuery(query: String): List<ArticleEntry>

    // Метод для фильтрации только по периоду
    @Query("SELECT * FROM article_entry WHERE period = :period")
    suspend fun getArticlesByPeriod(period: HistoricalPeriod): List<ArticleEntry>
}