package com.example.ukrainehistorylearner.model

data class User(
    val id: String,
    val username: String,
    val password: String,
    val birthDate: String,
    val favoriteHistoricalPeriods: List<HistoricalPeriod> = emptyList(),
    val readArticlesIds: List<Int> = emptyList()
)