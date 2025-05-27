package com.example.ukrainehistorylearner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalMaterial

@Composable
fun MaterialDetailsDialog(
    material: HistoricalMaterial,
    onEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onEdit) {
                Text("Редагувати")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрити")
            }
        },
        title = { Text(material.title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Період: ${material.period.getYearRange()}")
                Spacer(modifier = Modifier.height(8.dp))

                // Відображення специфічної інформації в залежності від типу матеріалу
                when (material) {
                    is HistoricalArticle -> {
                        ArticleDetails(article = material)
                    }
                }
            }
        }
    )
}

@Composable
fun ArticleDetails(article: HistoricalArticle) {
    Column {
        Text("Автор: ${article.author}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Кількість слів: ${article.wordCount}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("Час читання: ~${article.getReadTime()} хв.")

        if (article.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Теги:", fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = article.tags.joinToString(", "),
                    modifier = Modifier.padding(8.dp),
                    fontSize = 14.sp
                )
            }
        }
    }
}