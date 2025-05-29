package com.example.ukrainehistorylearner.model

import android.content.Context
import java.util.UUID

class HistoricalQuiz(
    id: String = UUID.randomUUID().hashCode().toString(),
    title: String,
    period: HistoricalPeriod,
    val questionCount: Int,
    val difficulty: Int = 2,
    var completionRate: Float = 0.0f
) : HistoricalMaterial(id, title, period) {

    override fun display(context: Context) {
        println("Тест: $title, Період: ${period.getYearRange(context)} (Питань: $questionCount, Складність: $difficulty)")
    }

    fun calculateScore(correctAnswers: Int, timeSpent: Int = 0): Float {
        val baseScore = (correctAnswers.toFloat() / questionCount) * 100
        return if (timeSpent > 0) {
            val bonus = (30 - timeSpent) * 0.5f
            baseScore + maxOf(0f, bonus)
        } else baseScore
    }
}
