package com.example.ukrainehistorylearner.ui.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.model.HistoricalPeriod
import com.example.ukrainehistorylearner.model.User
import com.example.ukrainehistorylearner.ui.components.AdaptiveDatePickerDialog
import com.example.ukrainehistorylearner.ui.viewmodels.ProfileViewModel
import com.example.ukrainehistorylearner.ui.viewmodels.ProfileEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onNavigateToLogin()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Loading indicator
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Error message
        uiState.errorMessageResId?.let { resId ->
            item {
                val errorMessageOnly = uiState.errorMessageResId?.let { resId -> stringResource(resId) }
                val errorMessage = stringResource(resId)
                val details = uiState.errorMessageDetails
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (details != null) {
                                "$errorMessage: $details"
                            } else {
                                errorMessageOnly!!
                            },
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.handleEvent(ProfileEvent.ClearMessages) }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Закрити",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        // Success message
        uiState.successMessageResId?.let { resId ->
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(resId),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = { viewModel.handleEvent(ProfileEvent.ClearMessages) }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Закрити",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Profile Header
        item {
            ProfileHeaderSection(
                user = uiState.user,
                isEditing = uiState.isEditing,
                editUsername = uiState.editUsername,
                editBirthDate = uiState.editBirthDate,
                onEvent = viewModel::handleEvent
            )
        }

        // User Statistics
        if (!uiState.isEditing && uiState.user != null) {
            item {
                UserStatsSection(
                    articlesRead = uiState.totalArticlesRead,
                    averageScore = uiState.quizScoreAverage
                )
            }
        }

        // Historical Periods (when editing)
        if (uiState.isEditing) {
            item {
                HistoricalPeriodsSection(
                    selectedPeriods = uiState.selectedHistoricalPeriods,
                    onPeriodToggled = { period ->
                        viewModel.handleEvent(ProfileEvent.HistoricalPeriodToggled(period))
                    },
                    context = context
                )
            }
        } else if (uiState.user != null) {
            // Show favorite periods when not editing
            item {
                FavoritePeriodsSection(
                    context = context,
                    favoritePeriods = uiState.user!!.favoriteHistoricalPeriods)
            }
        }

        // Action Buttons
        item {
            ActionButtonsSection(
                isEditing = uiState.isEditing,
                isFormValid = viewModel.isEditFormValid,
                onEvent = viewModel::handleEvent
            )
        }
    }

    // DatePicker Dialog
    if (uiState.showDatePicker) {
        AdaptiveDatePickerDialog(
            onDateSelected = { date ->
                viewModel.handleEvent(ProfileEvent.EditBirthDateChanged(date))
            },
            onDismiss = {
                viewModel.handleEvent(ProfileEvent.HideDatePicker)
            },
            windowSize = currentWindowAdaptiveInfo().windowSizeClass
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileHeaderSection(
    user: User?,
    isEditing: Boolean,
    editUsername: String,
    editBirthDate: LocalDate?,
    onEvent: (ProfileEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (isEditing) {
                // Edit mode
                OutlinedTextField(
                    value = editUsername,
                    onValueChange = { onEvent(ProfileEvent.EditUsernameChanged(it)) },
                    label = { Text(stringResource(R.string.username)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editBirthDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.birthdate)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEvent(ProfileEvent.ShowDatePicker) },
                    enabled = false,
                    trailingIcon = {
                        IconButton(onClick = { onEvent(ProfileEvent.ShowDatePicker) }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Обрати дату")
                        }
                    }
                )
            } else {
                // View mode
                Text(
                    text = user?.username ?: stringResource(R.string.not_specified),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${stringResource(R.string.birthdate)}: ${user?.birthDate?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: stringResource(R.string.not_specified)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun UserStatsSection(
    articlesRead: Int,
    averageScore: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.home_statistics_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Info,
                    value = articlesRead.toString(),
                    label = stringResource(R.string.home_statistics_articles_read)
                )

                StatItem(
                    icon = Icons.Default.Star,
                    value = "${averageScore.toInt()}%",
                    label = stringResource(R.string.profile_statistics_average_score)
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun HistoricalPeriodsSection(
    context: Context,
    selectedPeriods: List<HistoricalPeriod>,
    onPeriodToggled: (HistoricalPeriod) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_historical_periods_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(HistoricalPeriod.entries.toTypedArray()) { period ->
                    FilterChip(
                        onClick = { onPeriodToggled(period) },
                        label = {
                            Text(
                                text = period.getYearRange(context),
                                fontSize = 12.sp
                            )
                        },
                        selected = selectedPeriods.contains(period)
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritePeriodsSection(
    context: Context,
    favoritePeriods: List<HistoricalPeriod>
) {
    if (favoritePeriods.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.profile_favorite_periods_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favoritePeriods) { period ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = period.getYearRange(context),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButtonsSection(
    isEditing: Boolean,
    isFormValid: Boolean,
    onEvent: (ProfileEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (isEditing) {
            // Edit mode buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onEvent(ProfileEvent.CancelEditing) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = { onEvent(ProfileEvent.SaveProfile) },
                    modifier = Modifier.weight(1f),
                    enabled = isFormValid
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.save))
                }
            }
        } else {
            // View mode buttons
            Button(
                onClick = { onEvent(ProfileEvent.StartEditing) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.profile_edit_profile))
            }

            OutlinedButton(
                onClick = { onEvent(ProfileEvent.Logout) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.profile_logout))
            }
        }
    }
}