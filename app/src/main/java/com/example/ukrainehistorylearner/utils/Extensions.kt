package com.example.ukrainehistorylearner.utils

import com.example.ukrainehistorylearner.model.HistoricalMaterial
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.model.User

fun String.toHistoricalFormat(): String {
    return when {
        matches(Regex("\\d{4}")) -> "$this рік"
        matches(Regex("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) -> "Дата: $this"
        else -> this
    }
}

fun HistoricalMaterial.getDifficultyLevel(): Int = when (this.period) {
    HistoricalPeriod.ANCIENT_TIMES, HistoricalPeriod.KYIV_RUS -> 3
    HistoricalPeriod.INDEPENDENCE -> 1
    else -> 2
}
