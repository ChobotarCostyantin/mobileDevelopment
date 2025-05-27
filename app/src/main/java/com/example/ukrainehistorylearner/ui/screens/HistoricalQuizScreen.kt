package com.example.ukrainehistorylearner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.ui.viewmodels.QuizViewModel
import com.example.ukrainehistorylearner.ui.viewmodels.QuizEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalQuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Вікторина з історії України",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        // Компонент 1: Вибір складності через слайдер
        Text(
            text = "Виберіть рівень складності: ${viewModel.getDifficultyText(uiState.selectedDifficulty)}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Slider(
            value = uiState.selectedDifficulty,
            onValueChange = { value ->
                viewModel.handleEvent(QuizEvent.DifficultyChanged(value))
            },
            steps = 1,
            valueRange = 0f..1f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Компонент 2: Вибір історичного періоду через випадаюче меню
        Text(
            text = "Виберіть історичний період:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = uiState.expandedDropdown,
            onExpandedChange = { viewModel.handleEvent(QuizEvent.ToggleDropdown) },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                readOnly = true,
                value = uiState.selectedPeriod.getYearRange(),
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = uiState.expandedDropdown)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = uiState.expandedDropdown,
                onDismissRequest = { viewModel.handleEvent(QuizEvent.ToggleDropdown) }
            ) {
                HistoricalPeriod.entries.forEach { period ->
                    DropdownMenuItem(
                        text = { Text(period.getYearRange()) },
                        onClick = {
                            viewModel.handleEvent(QuizEvent.PeriodChanged(period))
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Компонент 3: Вибір кількості питань через радіо кнопки
        Text(
            text = "Кількість питань у вікторині:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            viewModel.availableQuestionCounts.forEach { count ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = uiState.questionCount == count,
                        onClick = {
                            viewModel.handleEvent(QuizEvent.QuestionCountChanged(count))
                        }
                    )

                    Text(
                        text = "$count питань",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Компонент 4: Перемикач для додаткової інформації після кожного питання
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Switch(
                checked = uiState.showInfoAfterQuestion,
                onCheckedChange = {
                    viewModel.handleEvent(QuizEvent.ShowInfoToggled(it))
                }
            )

            Text(
                text = "Показувати історичну довідку після кожного питання",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.handleEvent(QuizEvent.StartQuiz)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Розпочати вікторину")
        }

        // Показати стан після початку вікторини (для демонстрації)
        if (uiState.isQuizStarted) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Вікторину розпочато!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = "Налаштування: ${viewModel.getDifficultyText(uiState.selectedDifficulty)}, " +
                                "${uiState.selectedPeriod.getYearRange()}, " +
                                "${uiState.questionCount} питань",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    viewModel.handleEvent(QuizEvent.ResetQuiz)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Повернутися до налаштувань")
            }
        }
    }
}