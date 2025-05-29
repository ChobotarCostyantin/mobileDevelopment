package com.example.ukrainehistorylearner.model

import java.time.LocalDate

data class User(
    val id: String,
    val username: String,
    val password: String,
    val birthDate: LocalDate,
    val favoriteHistoricalPeriods: List<HistoricalPeriod> = emptyList(),
    val readArticlesIds: List<Int> = emptyList()
)