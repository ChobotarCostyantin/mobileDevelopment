package com.example.ukrainehistorylearner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalPeriod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveAddArticleDialog(
    onDismiss: () -> Unit,
    onAdd: (HistoricalArticle) -> Unit,
    windowSize: WindowSizeClass
) {
    var title by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf(HistoricalPeriod.INDEPENDENCE) }
    var author by remember { mutableStateOf("") }
    var wordCount by remember { mutableStateOf("0") }
    var tags by remember { mutableStateOf("") }

    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }
    var wordCountError by remember { mutableStateOf(false) }

    val confirmAction = {
        titleError = title.isBlank()
        authorError = author.isBlank()
        wordCountError = wordCount.toIntOrNull() == null || wordCount.toInt() < 1

        if (!titleError && !authorError && !wordCountError) {
            val tagsList = if (tags.isBlank()) emptyList() else tags.split(",").map { it.trim() }
            val newArticle = HistoricalArticle(
                id = System.currentTimeMillis().toString(),
                title = title,
                period = selectedPeriod,
                author = author,
                wordCount = wordCount.toInt(),
                tags = tagsList
            )
            onAdd(newArticle)
        }
    }

    val content: @Composable () -> Unit = {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Назва") },
            isError = titleError,
            supportingText = {
                if (titleError) Text("Назва не може бути порожньою")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        PeriodDropdown(
            selected = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Автор") },
            isError = authorError,
            supportingText = {
                if (authorError) Text("Автор не може бути порожнім")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = wordCount,
            onValueChange = { wordCount = it },
            label = { Text("Кількість слів") },
            isError = wordCountError,
            supportingText = {
                if (wordCountError) Text("Введіть коректну кількість (> 0)")
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Теги (через кому)") },
            modifier = Modifier.fillMaxWidth()
        )
    }

    when (windowSize.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = confirmAction) {
                        Text("Додати")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Скасувати")
                    }
                },
                title = { Text("Нова стаття") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        content()
                    }
                }
            )
        }

        WindowWidthSizeClass.MEDIUM, WindowWidthSizeClass.EXPANDED -> {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Нова стаття",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            fontSize = 20.sp
                        )

                        content()

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Скасувати", fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = confirmAction) {
                                Text("Додати", fontSize = 14.sp)
                            }
                        }
                    }
                }
            )
        }
        else -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = confirmAction) {
                        Text("Додати")
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text("Скасувати")
                    }
                },
                title = { Text("Нова стаття") },
                text = content
            )
        }
    }
}
