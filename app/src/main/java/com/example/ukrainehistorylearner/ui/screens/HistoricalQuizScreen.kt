package com.example.ukrainehistorylearner.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.ui.viewmodels.QuizViewModel
import com.example.ukrainehistorylearner.ui.viewmodels.QuizEvent
import com.example.ukrainehistorylearner.ui.viewmodels.QuizUiState

@Composable
fun HistoricalQuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // Визначаємо тип макету на основі розміру екрану
    val useCompactLayout = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val context = LocalContext.current

    val difficultyLabels = mapOf(
        0.0f to stringResource(R.string.quiz_difficulty_easy),
        0.5f to stringResource(R.string.quiz_difficulty_medium),
        1.0f to stringResource(R.string.quiz_difficulty_hard)
    )

    var difficultyText = difficultyLabels[uiState.selectedDifficulty] ?: stringResource(R.string.quiz_difficulty_unknown)

    if (useCompactLayout) {
        CompactQuizLayout(
            viewModel = viewModel,
            uiState = uiState,
            difficultyText = difficultyText,
            context = context
        )
    } else {
        ListDetailQuizLayout(
            viewModel = viewModel,
            uiState = uiState,
            difficultyText = difficultyText,
            context = context
        )
    }
}

@Composable
private fun CompactQuizLayout(
    context: Context,
    viewModel: QuizViewModel,
    uiState: QuizUiState,
    difficultyText: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.quiz_titles),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        QuizSettingsContent(
            viewModel = viewModel,
            uiState = uiState,
            difficultyText = difficultyText,
            context = context
        )
    }
}

@Composable
private fun ListDetailQuizLayout(
    context: Context,
    viewModel: QuizViewModel,
    uiState: QuizUiState,
    difficultyText: String
) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Ліва панель - налаштування (List)
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.quiz_settings),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                QuizSettingsContent(
                    viewModel = viewModel,
                    uiState = uiState,
                    difficultyText = difficultyText,
                    context = context
                )
            }
        }

        // Права панель - попередній перегляд та результати (Detail)
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.quiz_preview),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                QuizPreviewContent(
                    viewModel = viewModel,
                    uiState = uiState,
                    difficultyText = difficultyText,
                    context = context
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSettingsContent(
    context: Context,
    viewModel: QuizViewModel,
    uiState: QuizUiState,
    difficultyText: String
) {
    // Компонент 1: Вибір складності через слайдер
    Text(
        text = "${stringResource(R.string.quiz_difficulty)}: $difficultyText",
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
        text = "${stringResource(R.string.quiz_period)}:",
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
            value = uiState.selectedPeriod.getYearRange(context),
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
                    text = { Text(period.getYearRange(context)) },
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
        text = "${stringResource(R.string.quiz_question_count)}:",
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
                    text = "$count ${stringResource(R.string.quiz_questions)}",
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
            text = stringResource(R.string.quiz_show_info_after_question),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Компонент 5: Кнопка "Розпочати вікторину"
    Button(
        onClick = { viewModel.handleEvent(QuizEvent.StartQuiz) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.quiz_start_quiz),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun QuizPreviewContent(
    context: Context,
    viewModel: QuizViewModel,
    uiState: QuizUiState,
    difficultyText: String
) {
    // Попередній перегляд налаштувань
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${stringResource(R.string.quiz_current_settings)}:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "• ${stringResource(R.string.quiz_chosen_difficulty)}: $difficultyText",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "• ${stringResource(R.string.period)}: ${uiState.selectedPeriod.getYearRange(context)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "• ${stringResource(R.string.quiz_chosen_question_count)}: ${uiState.questionCount}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "• ${stringResource(R.string.quiz_background_info)}: ${if (uiState.showInfoAfterQuestion) stringResource(R.string.yes) else stringResource(R.string.no)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = {
            viewModel.handleEvent(QuizEvent.StartQuiz)
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(stringResource(R.string.quiz_start_quiz))
    }

    // Показати стан після початку вікторини
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
                    text = "${stringResource(R.string.quiz_started)}!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "• ${stringResource(R.string.nav_settings)}: $difficultyText, " +
                            "${uiState.selectedPeriod.getYearRange(context)}, " +
                            "${uiState.questionCount} ${stringResource(R.string.quiz_questions)}",
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
            Text(stringResource(R.string.quiz_return_to_settings))
        }
    }
}