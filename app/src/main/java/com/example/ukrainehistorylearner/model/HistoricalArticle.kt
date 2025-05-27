package com.example.ukrainehistorylearner.model

import java.util.UUID

class HistoricalArticle(
    id: String = UUID.randomUUID().hashCode().toString(),
    title: String,
    period: HistoricalPeriod,
    val author: String,
    val wordCount: Int,
    val tags: List<String> = emptyList()
) : HistoricalMaterial(id, title, period) {

    override fun display() {
        println("Стаття: $title (Автор: $author, Період: ${period.getYearRange()})")
        if (tags.isNotEmpty()) {
            println("Теги: ${tags.joinToString(", ")}")
        }
    }

    override fun getFullInfo(): String {
        return super.getFullInfo() + ", автор: $author, слів: $wordCount"
    }

    fun getReadTime(): Int = (wordCount / 200) + 1
}