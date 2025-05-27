package com.example.ukrainehistorylearner.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.utils.Converters
import java.util.UUID

@Entity(tableName = "article_entry")
@TypeConverters(Converters::class)
data class ArticleEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val period: HistoricalPeriod,
    val author: String,
    val wordCount: Int,
    val tags: List<String>
)