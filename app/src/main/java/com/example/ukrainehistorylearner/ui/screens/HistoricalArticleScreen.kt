package com.example.ukrainehistorylearner.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalArticle
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.ui.components.AdaptiveAddArticleDialog
import com.example.ukrainehistorylearner.ui.components.AdaptiveUpdateArticleDialog
import com.example.ukrainehistorylearner.ui.components.DemoCollectionsView
import com.example.ukrainehistorylearner.ui.components.MaterialCard
import com.example.ukrainehistorylearner.ui.components.MaterialDetailsDialog
import com.example.ukrainehistorylearner.ui.viewmodels.ArticlesEvent
import com.example.ukrainehistorylearner.ui.viewmodels.ArticlesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalArticleScreen(
    viewModel: ArticlesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleEvent(ArticlesEvent.ShowAddDialog) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Додати")
            }
        }
    ) { innerPadding ->

        Column(modifier = Modifier.padding(innerPadding)) {
            // Пошук та фільтри
            SearchAndFiltersSection(
                searchQuery = uiState.searchQuery,
                selectedPeriod = uiState.selectedPeriod,
                onSearchQueryChanged = { viewModel.handleEvent(ArticlesEvent.SearchQueryChanged(it)) },
                onPeriodFilterChanged = { viewModel.handleEvent(ArticlesEvent.PeriodFilterChanged(it)) }
            )

            // Повідомлення про помилки
            uiState.errorMessageResId?.let { resId ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(resId),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.handleEvent(ArticlesEvent.ClearError) }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Закрити",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            // Індикатор завантаження
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Список матеріалів
            LazyColumn {
                items(uiState.articles, key = { it.id }) { material ->
                    MaterialCard(
                        material = material,
                        onClick = { viewModel.handleEvent(ArticlesEvent.SelectArticle(material)) },
                        onDelete = { viewModel.handleEvent(ArticlesEvent.RemoveArticle(material)) },
                        context = LocalContext.current
                    )
                }

//                item {
//                    Spacer(Modifier.height(24.dp))
//                    // Демонстрація списку і плитки
//                    DemoCollectionsView(uiState.articles)
//                }
            }
        }

        // Діалог додавання статті
        if (uiState.showAddDialog) {
            AdaptiveAddArticleDialog(
                onDismiss = { viewModel.handleEvent(ArticlesEvent.HideAddDialog) },
                onAdd = { newArticle ->
                    viewModel.handleEvent(ArticlesEvent.AddArticle(newArticle))
                },
                windowSize = currentWindowAdaptiveInfo().windowSizeClass
            )
        }

        // Діалог оновлення статті
        if (uiState.showUpdateDialog) {
            AdaptiveUpdateArticleDialog(
                onDismiss = {
                    viewModel.handleEvent(ArticlesEvent.SelectArticle(null))
                    viewModel.handleEvent(ArticlesEvent.HideUpdateDialog) },
                onUpdate = { newArticle ->
                    viewModel.handleEvent(ArticlesEvent.UpdateArticle(newArticle))
                },
                article = uiState.selectedMaterial!!,
                windowSize = currentWindowAdaptiveInfo().windowSizeClass
            )
        }

        // Діалог деталей матеріалу
        uiState.selectedMaterial?.let { material ->
            MaterialDetailsDialog(
                material = material,
                onEdit = {
                    viewModel.handleEvent(ArticlesEvent.ShowUpdateDialog)
                },
                onDismiss = { viewModel.handleEvent(ArticlesEvent.SelectArticle(null)) },
                context = LocalContext.current
            )
        }
    }
}

@Composable
fun SearchAndFiltersSection(
    searchQuery: String,
    selectedPeriod: HistoricalPeriod?,
    onSearchQueryChanged: (String) -> Unit,
    onPeriodFilterChanged: (HistoricalPeriod?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Пошук
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text(stringResource(R.string.search)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Пошук") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Очистити")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Фільтр по періоду
        PeriodFilterDropdown(
            context = LocalContext.current,
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodFilterChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterDropdown(
    context: Context,
    selectedPeriod: HistoricalPeriod?,
    onPeriodSelected: (HistoricalPeriod?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedPeriod?.getYearRange(context) ?: stringResource(R.string.all_periods),
            onValueChange = {},
            label = { Text(stringResource(R.string.period_filter)) },
            trailingIcon = {
                Row {
                    if (selectedPeriod != null) {
                        IconButton(onClick = { onPeriodSelected(null) }) {
                            Icon(Icons.Default.Clear, contentDescription = "Очистити фільтр")
                        }
                    }
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_periods)) },
                onClick = {
                    onPeriodSelected(null)
                    expanded = false
                }
            )
            HistoricalPeriod.entries.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period.getYearRange(context)) },
                    onClick = {
                        onPeriodSelected(period)
                        expanded = false
                    }
                )
            }
        }
    }
}
