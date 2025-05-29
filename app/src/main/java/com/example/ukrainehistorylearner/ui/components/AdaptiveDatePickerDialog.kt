package com.example.ukrainehistorylearner.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.example.ukrainehistorylearner.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    windowSize: WindowSizeClass
) {
    val datePickerState = rememberDatePickerState()

    when (windowSize.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT -> {
            // Компактний — bottom sheet style
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onDateSelected(date)
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                },
                modifier = Modifier
                    .widthIn(min = 400.dp, max = 600.dp)
                    .padding(24.dp)
            ) {
                DatePicker(state = datePickerState)
            }
        }
        WindowWidthSizeClass.MEDIUM -> {
            // Середній — звичайний діалог по центру без змін
            DatePickerDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onDateSelected(date)
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
        WindowWidthSizeClass.EXPANDED -> {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 8.dp)
                    ) {
                        DatePicker(
                            state = datePickerState,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text(stringResource(R.string.cancel), fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        val date = Instant.ofEpochMilli(millis)
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                        onDateSelected(date)
                                    }
                                }
                            ) {
                                Text("OK", fontSize = 14.sp)
                            }
                        }
                    }
                }
            )
        }
        else -> {
            // За замовчуванням — як середній
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                onDateSelected(date)
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                },
                text = {
                    DatePicker(state = datePickerState)
                }
            )
        }
    }
}