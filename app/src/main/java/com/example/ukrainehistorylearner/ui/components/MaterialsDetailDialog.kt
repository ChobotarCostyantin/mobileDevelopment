package com.example.ukrainehistorylearner.ui.components

import android.content.Context
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalMaterial

@Composable
fun MaterialDetailsDialog(
    context: Context,
    material: HistoricalMaterial,
    onEdit: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onEdit) {
                Text(stringResource(R.string.edit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        title = { Text(material.title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("${stringResource(R.string.period)}: ${material.period.getYearRange(context)}")
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
        Text("${stringResource(R.string.article_author)}: ${article.author}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("${stringResource(R.string.article_word_count)}: ${article.wordCount}")
        Spacer(modifier = Modifier.height(4.dp))
        Text("${stringResource(R.string.article_read_time)}: ~${article.getReadTime()}.")

        if (article.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("${stringResource(R.string.article_tags)}:", fontWeight = FontWeight.Bold)
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