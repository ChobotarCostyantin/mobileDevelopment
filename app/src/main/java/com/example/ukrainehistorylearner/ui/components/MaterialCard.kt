package com.example.ukrainehistorylearner.ui.components

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.*
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalArticle

@Composable
fun MaterialCard(
    context: Context,
    material: HistoricalArticle,
    onClick: () -> Unit = {},
    onDelete: (HistoricalArticle) -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

    ) {
        ListItem(
            overlineContent = {
                Text(
                    text = material.author
                )
            },
            headlineContent = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = material.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Видалити")
                    }
                }
            },
            supportingContent = {
                Text(text = material.period.getYearRange(context))
            },
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.article_delete_title)) },
            text = { Text("${stringResource(R.string.article_delete_message)}: \"${material.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(material)
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
