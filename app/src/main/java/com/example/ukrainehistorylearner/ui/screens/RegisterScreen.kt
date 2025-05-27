package com.example.ukrainehistorylearner.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.ui.components.AdaptiveDatePickerDialog
import com.example.ukrainehistorylearner.ui.viewmodels.RegisterEvent
import com.example.ukrainehistorylearner.ui.viewmodels.RegisterViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Навігація при успішній реєстрації
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Реєстрація нового облікового запису",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.handleEvent(RegisterEvent.UsernameChanged(it)) },
            label = { Text("Логін") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.handleEvent(RegisterEvent.PasswordChanged(it)) },
            label = { Text("Пароль") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { viewModel.handleEvent(RegisterEvent.TogglePasswordVisibility) }) {
                    Text(text = if (uiState.isPasswordVisible) "🙈" else "👁️")
                }
            },
            singleLine = true,
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.handleEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
            label = { Text("Підтвердження паролю") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { viewModel.handleEvent(RegisterEvent.TogglePasswordVisibility) }) {
                    Text(text = if (uiState.isPasswordVisible) "🙈" else "👁️")
                }
            },
            isError = uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank() && !viewModel.passwordsMatch,
            supportingText = {
                if (uiState.password.isNotBlank() && uiState.confirmPassword.isNotBlank() && !viewModel.passwordsMatch) {
                    Text("Паролі не збігаються")
                }
            },
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Поле для вибору дати народження
        OutlinedTextField(
            value = if (uiState.birthDate != null) {
                uiState.birthDate!!.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            } else {
                ""
            },
            onValueChange = { /* Поле лише для відображення */ },
            label = { Text("Дата народження") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.handleEvent(RegisterEvent.ShowDatePicker) }) {
                    Text("📅")
                }
            },
            enabled = !uiState.isLoading
        )

        // Перемикач згоди з політикою конфіденційності
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.acceptedPrivacyPolicy,
                onCheckedChange = { viewModel.handleEvent(RegisterEvent.PrivacyPolicyAcceptanceChanged(it)) },
                enabled = !uiState.isLoading
            )

            Text(
                text = "Я ознайомився та приймаю політику конфіденційності",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Відображення помилок
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Button(
            onClick = { viewModel.handleEvent(RegisterEvent.Register) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = viewModel.isFormValid && !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Зареєструватися")
            }
        }
    }

    // Діалог вибору дати
    if (uiState.showDatePicker) {
        AdaptiveDatePickerDialog(
            onDateSelected = { date ->
                viewModel.handleEvent(RegisterEvent.BirthDateChanged(date))
            },
            onDismiss = {
                viewModel.handleEvent(RegisterEvent.HideDatePicker)
            },
            windowSize = currentWindowAdaptiveInfo().windowSizeClass
        )
    }
}