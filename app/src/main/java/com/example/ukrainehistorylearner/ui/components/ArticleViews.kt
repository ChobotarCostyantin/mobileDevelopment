package com.example.ukrainehistorylearner.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ukrainehistorylearner.model.HistoricalArticle

@Composable
fun ArticleListItem(article: HistoricalArticle, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(article.title) },
        supportingContent = { Text("Автор: ${article.author}") },
        overlineContent = { Text(article.period.getYearRange()) },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Composable
fun ArticleGridItem(article: HistoricalArticle, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .width(180.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(article.title, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(4.dp))
            Text("Автор: ${article.author}", style = MaterialTheme.typography.bodySmall)
            Text(article.period.getYearRange(), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun DemoCollectionsView(materials: List<HistoricalArticle>) {
    val longArticles: List<HistoricalArticle> = materials.filter { it.wordCount >= 1000 }

    val tagSet: Set<String> = materials.flatMap { it.tags }.toSet()

    val periodArticleCount: Map<String, Int> = materials
        .groupingBy { it.period.getYearRange() }
        .eachCount()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("📝 Статті з кількістю слів ≥ 1000", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ArticleListView(articles = longArticles)

        Spacer(Modifier.height(24.dp))
        Text("🧩 Усі статті у вигляді плитки", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        ArticleGridView(articles = materials)

        Spacer(Modifier.height(24.dp))
        Text("🔖 Унікальні теги:", style = MaterialTheme.typography.titleMedium)
        tagSet.forEach { tag ->
            Text("• $tag", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        Text("📊 Кількість статей за періодами:", style = MaterialTheme.typography.titleMedium)
        periodArticleCount.forEach { (period, count) ->
            Text("• $period: $count", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ArticleListView(articles: List<HistoricalArticle>) {
    Column {
        articles.forEach { article ->
            ArticleListItem(article = article) {
                println("Список: обрано ${article.title}")
            }
            HorizontalDivider()
        }
    }
}

@Composable
fun ArticleGridView(articles: List<HistoricalArticle>) {
    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        articles.forEach { article ->
            ArticleGridItem(article = article) {
                println("Плитка: обрано ${article.title}")
            }
        }
    }
}
