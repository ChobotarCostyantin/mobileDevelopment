package com.example.ukrainehistorylearner.repository

import com.example.ukrainehistorylearner.model.User
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import kotlinx.coroutines.delay
import java.time.LocalDate

data class UserStats(
    val articlesRead: Int,
    val averageQuizScore: Float,
    val totalScore: Int,
    val quizzesCompleted: Int,
    val favoritePeriodsCount: Int
)

class UserRepository {

    private var currentUser: User? = User(
        id = "user123",
        username = "admin",
        password = "password",
        birthDate = "1995-05-15",
        favoriteHistoricalPeriods = listOf(
            HistoricalPeriod.KYIV_RUS,
            HistoricalPeriod.COSSACK_ERA
        ),
        readArticlesIds = listOf(1, 2, 3, 5, 8)
    )

    // Симуляція статистики користувача
    private val userStats = mapOf(
        "user123" to UserStats(
            articlesRead = 5,
            averageQuizScore = 87.5f,
            totalScore = 1250,
            quizzesCompleted = 12,
            favoritePeriodsCount = 2
        )
    )

    // Симуляція досягнень користувача
    private val userAchievements = mapOf(
        "user123" to listOf(
            "Прочитано 5 статей",
            "Середній бал вище 80%",
            "Завершено 10+ вікторин",
            "Обрано улюблені періоди"
        )
    )

    // Симуляція рекомендованих статей
    private val recommendedArticles = mapOf(
        "user123" to listOf(
            "Київська Русь: заснування держави",
            "Козацька доба: Богдан Хмельницький",
            "Українська революція 1917-1921",
            "Незалежність України 1991"
        )
    )

    // Симуляція всіх користувачів для перевірки унікальності
    private val existingUsers = mutableSetOf("admin", "taken_username")

    suspend fun getCurrentUser(): User? {
        // Симуляція мережевого запиту
        delay(500)
        return currentUser
    }

    suspend fun updateUser(user: User) {
        // Симуляція мережевого запиту
        delay(800)

        // Симуляція перевірки унікальності логіну (якщо логін змінився)
        if (currentUser?.username != user.username && existingUsers.contains(user.username)) {
            throw Exception("Користувач з таким логіном вже існує")
        }

        // Оновлення списку існуючих користувачів
        currentUser?.let { oldUser ->
            if (oldUser.username != user.username) {
                existingUsers.remove(oldUser.username)
                existingUsers.add(user.username)
            }
        }

        currentUser = user
        println("Користувач оновлений: ${user.username}")
    }

    suspend fun getUserStats(userId: String): UserStats {
        // Симуляція мережевого запиту
        delay(300)
        return userStats[userId] ?: UserStats(0, 0f, 0, 0, 0)
    }

    suspend fun getRecentAchievements(userId: String): List<String> {
        // Симуляція мережевого запиту
        delay(200)
        return userAchievements[userId] ?: emptyList()
    }

    suspend fun getRecommendedArticles(userId: String): List<String> {
        // Симуляція мережевого запиту
        delay(300)
        return recommendedArticles[userId] ?: emptyList()
    }

    suspend fun markArticleAsClicked(articleId: Int) {
        // Симуляція запиту для відстеження кліків по статтях
        delay(100)

        currentUser?.let { user ->
            val updatedReadArticles = user.readArticlesIds.toMutableList()
            if (!updatedReadArticles.contains(articleId)) {
                updatedReadArticles.add(articleId)
                currentUser = user.copy(readArticlesIds = updatedReadArticles)
            }
        }

        println("Стаття з ID $articleId помічена як переглянута")
    }

    suspend fun login(username: String, password: String): User {
        // Симуляція мережевого запиту для авторизації
        delay(1000)

        // Симуляція перевірки облікових даних
        if (username == "admin" && password == "password") {
            currentUser = User(
                id = "user123",
                username = username,
                password = password,
                birthDate = "1995-05-15",
                favoriteHistoricalPeriods = listOf(
                    HistoricalPeriod.KYIV_RUS,
                    HistoricalPeriod.COSSACK_ERA
                ),
                readArticlesIds = listOf(1, 2, 3, 5, 8)
            )
            return currentUser!!
        } else {
            throw Exception("Невірний логін або пароль")
        }
    }

    suspend fun register(username: String, password: String, birthDate: LocalDate): User {
        // Симуляція мережевого запиту для реєстрації
        delay(1200)

        // Перевірка унікальності логіну
        if (existingUsers.contains(username)) {
            throw Exception("Користувач з таким логіном вже існує")
        }

        // Створення нового користувача
        val newUser = User(
            id = "user_${System.currentTimeMillis()}",
            username = username,
            password = password,
            birthDate = birthDate.toString(),
            favoriteHistoricalPeriods = emptyList(),
            readArticlesIds = emptyList()
        )

        existingUsers.add(username)
        currentUser = newUser

        println("Новий користувач зареєстрований: $username")
        return newUser
    }

    suspend fun logout() {
        // Симуляція очищення даних
        delay(200)
        currentUser = null
        println("Користувач вийшов з системи")
    }

    suspend fun isUsernameAvailable(username: String): Boolean {
        // Симуляція перевірки доступності логіну
        delay(300)
        return !existingUsers.contains(username)
    }

    // Допоміжний метод для отримання всіх доступних історичних періодів
    fun getAvailableHistoricalPeriods(): List<HistoricalPeriod> {
        return HistoricalPeriod.entries
    }

    // Метод для отримання поточного стану авторизації
    fun isUserLoggedIn(): Boolean {
        return currentUser != null
    }

    // Метод для оновлення статистики після завершення вікторини
    suspend fun updateQuizStats(userId: String, score: Int) {
        delay(200)
        println("Оновлено статистику вікторини для користувача $userId з балом $score")
    }

    // Метод для додавання нового досягнення
    suspend fun addAchievement(userId: String, achievement: String) {
        delay(100)
        println("Додано досягнення для користувача $userId: $achievement")
    }
}