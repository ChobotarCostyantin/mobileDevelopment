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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalArticle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveUpdateArticleDialog(
    onDismiss: () -> Unit,
    onUpdate: (HistoricalArticle) -> Unit,
    article: HistoricalArticle,
    windowSize: WindowSizeClass
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(article.title) }
    var selectedPeriod by remember { mutableStateOf(article.period) }
    var author by remember { mutableStateOf(article.author) }
    var wordCount by remember { mutableStateOf(article.wordCount.toString()) }
    var tags by remember { mutableStateOf(article.tags.joinToString(", ")) }

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
                id = article.id,
                title = title,
                period = selectedPeriod,
                author = author,
                wordCount = wordCount.toInt(),
                tags = tagsList
            )
            onUpdate(newArticle)
        }
    }

    val content: @Composable () -> Unit = {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.article_title)) },
            isError = titleError,
            supportingText = {
                if (titleError) Text(stringResource(R.string.article_title_error))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        PeriodDropdown(
            selected = selectedPeriod,
            onPeriodSelected = { selectedPeriod = it },
            context = context
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text(stringResource(R.string.article_author)) },
            isError = authorError,
            supportingText = {
                if (authorError) Text(stringResource(R.string.article_author_error))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = wordCount,
            onValueChange = { wordCount = it },
            label = { Text(stringResource(R.string.article_word_count)) },
            isError = wordCountError,
            supportingText = {
                if (wordCountError) Text(stringResource(R.string.article_word_count_error))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text(stringResource(R.string.article_add_and_edit_tags)) },
            modifier = Modifier.fillMaxWidth()
        )
    }

    when (windowSize.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(onClick = confirmAction) {
                        Text(stringResource(R.string.update))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                title = { Text(stringResource(R.string.article_edit_title)) },
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
                            text = stringResource(R.string.article_edit_title),
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
                                Text(stringResource(R.string.cancel), fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = confirmAction) {
                                Text(stringResource(R.string.update), fontSize = 14.sp)
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
                        Text(stringResource(R.string.update))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                title = { Text(stringResource(R.string.article_edit_title)) },
                text = content
            )
        }
    }
}