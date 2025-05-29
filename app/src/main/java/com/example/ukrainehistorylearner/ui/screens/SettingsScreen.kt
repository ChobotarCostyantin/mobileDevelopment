package com.example.ukrainehistorylearner.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.ui.viewmodels.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    val notifications by viewModel.notifications.collectAsState()
    val sound         by viewModel.sound.collectAsState()
    val language      by viewModel.language.collectAsState()
    val theme         by viewModel.theme.collectAsState()

    // Тоногенератор для тестового звуку
    val toneGen = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Сповіщення
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_notifications),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = notifications,
                onCheckedChange = { viewModel.toggleNotifications() }
            )
        }

        // 2. Звук
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.settings_sound),
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = sound,
                onCheckedChange = { viewModel.toggleSound() }
            )
        }

        // 3. Мова
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.bodyLarge
        )

        val languages = listOf(
            "uk" to stringResource(R.string.language_ukrainian),
            "en" to stringResource(R.string.language_english)
        )
        var expandedLanguage by remember { mutableStateOf(false) }

        Box {
            OutlinedButton(
                onClick = { expandedLanguage = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(languages.first { it.first == language }.second)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Вибрати"
                )
            }
            DropdownMenu(
                expanded = expandedLanguage,
                onDismissRequest = { expandedLanguage = false }
            ) {
                languages.forEach { (code, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.setLanguage(code)
                            expandedLanguage = false
                        }
                    )
                }
            }
        }

        // 4. Тема
        Text(
            text = stringResource(R.string.settings_theme),
            style = MaterialTheme.typography.bodyLarge
        )

        val themes = listOf(
            "light" to stringResource(R.string.theme_light),
            "dark" to stringResource(R.string.theme_dark),
            "system" to stringResource(R.string.theme_system)
        )
        var expandedTheme by remember { mutableStateOf(false) }

        Box {
            OutlinedButton(
                onClick = { expandedTheme = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(themes.first { it.first == theme }.second)
                Spacer(Modifier.weight(1f))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Вибрати"
                )
            }
            DropdownMenu(
                expanded = expandedTheme,
                onDismissRequest = { expandedTheme = false }
            ) {
                themes.forEach { (code, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            viewModel.setTheme(code)
                            expandedTheme = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка для тестування звуку
        Button(
            onClick = {
                if (sound) {
                    toneGen.startTone(ToneGenerator.TONE_PROP_BEEP)
                } else if(notifications) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.sound_disabled),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.settings_test_sound))
        }

        // Кнопка для тестування сповіщень
        Button(
            onClick = {
                if (notifications) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.test_notification_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.settings_test_notification))
        }
    }
}