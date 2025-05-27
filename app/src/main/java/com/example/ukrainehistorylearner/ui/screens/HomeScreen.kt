package com.example.ukrainehistorylearner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.ui.viewmodels.HomeViewModel
import com.example.ukrainehistorylearner.ui.viewmodels.HomeEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(HomeEvent.LoadData)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        WelcomeSection(username = uiState.currentUser?.username ?: "Користувач")

        Spacer(modifier = Modifier.height(24.dp))

        StatisticsSection(
            articlesRead = uiState.articlesReadCount,
            quizzesCompleted = uiState.quizzesCompletedCount,
            totalScore = uiState.totalScore
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.recentAchievements.isNotEmpty()) {
            RecentAchievementsSection(achievements = uiState.recentAchievements)
        }

        if (uiState.recommendedArticles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            RecommendedArticlesSection(
                articles = uiState.recommendedArticles,
                onArticleClick = { articleId ->
                    viewModel.handleEvent(HomeEvent.ArticleClicked(articleId))
                }
            )
        }
    }
}

@Composable
private fun WelcomeSection(username: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Вітаємо, $username!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Готові дізнатися більше про історію України?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun StatisticsSection(
    articlesRead: Int,
    quizzesCompleted: Int,
    totalScore: Int
) {
    Text(
        text = "Ваша статистика",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatisticCard(
            title = "Прочитано статей",
            value = articlesRead.toString(),
            icon = Icons.Default.Search,
            modifier = Modifier.weight(1f)
        )
        StatisticCard(
            title = "Завершено вікторин",
            value = quizzesCompleted.toString(),
            icon = Icons.Default.Info,
            modifier = Modifier.weight(1f)
        )
        StatisticCard(
            title = "Загальний бал",
            value = totalScore.toString(),
            icon = Icons.Default.Star,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RecentAchievementsSection(achievements: List<String>) {
    Text(
        text = "Останні досягнення",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
    ) {
        achievements.forEach { achievement ->
            ElevatedCard(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Text(
                    text = achievement,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun RecommendedArticlesSection(
    articles: List<String>,
    onArticleClick: (Int) -> Unit
) {
    Text(
        text = "Рекомендовані статті",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        articles.take(3).forEach { article ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onArticleClick(articles.indexOf(article)) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = article,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}