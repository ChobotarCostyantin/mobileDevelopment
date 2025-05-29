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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ukrainehistorylearner.R
import com.example.ukrainehistorylearner.ui.components.AdaptiveDatePickerDialog
import com.example.ukrainehistorylearner.ui.viewmodels.RegisterError
import com.example.ukrainehistorylearner.ui.viewmodels.RegisterEvent
import com.example.ukrainehistorylearner.ui.viewmodels.RegisterViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    viewModel: RegisterViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val errorMessage = when (uiState.errorMessage) {
        RegisterError.EMPTY_USERNAME -> stringResource(R.string.register_error_empty_username)
        RegisterError.SHORT_USERNAME -> stringResource(R.string.register_error_short_username)
        RegisterError.EMPTY_PASSWORD -> stringResource(R.string.register_error_empty_password)
        RegisterError.SHORT_PASSWORD -> stringResource(R.string.register_error_short_password)
        RegisterError.EMPTY_CONFIRM_PASSWORD -> stringResource(R.string.register_error_empty_confirm_password)
        RegisterError.PASSWORD_MISMATCH -> stringResource(R.string.register_error_passwords_mismatch)
        RegisterError.NO_BIRTHDATE -> stringResource(R.string.register_error_no_birthdate)
        RegisterError.BIRTHDATE_IN_FUTURE -> stringResource(R.string.register_error_birthdate_in_future)
        RegisterError.PRIVACY_POLICY_NOT_ACCEPTED -> stringResource(R.string.register_error_privacy_policy_not_accepted)
        RegisterError.USER_ALREADY_EXISTS -> stringResource(R.string.register_error_user_already_exists)
        null -> null
    }

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
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.handleEvent(RegisterEvent.UsernameChanged(it)) },
            label = { Text(stringResource(R.string.username)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.username.isNotBlank() && uiState.username.length < 3,
            supportingText = {
                if (uiState.username.isNotBlank() && uiState.username.length < 3) {
                    Text(
                        text = stringResource(R.string.register_error_short_username),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.handleEvent(RegisterEvent.PasswordChanged(it)) },
            label = { Text(stringResource(R.string.password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { viewModel.handleEvent(RegisterEvent.TogglePasswordVisibility) }) {
                    Text(text = if (uiState.isPasswordVisible) "🙈" else "👁️")
                }
            },
            isError = uiState.password.isNotBlank() && uiState.password.length < 6,
            supportingText = {
                if (uiState.password.isNotBlank() && uiState.password.length < 6) {
                    Text(
                        text = stringResource(R.string.register_error_short_password),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            enabled = !uiState.isLoading
        )

        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = { viewModel.handleEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
            label = { Text(stringResource(R.string.register_confirm_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
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
                    Text(
                        text = stringResource(R.string.register_error_passwords_mismatch),
                        color = MaterialTheme.colorScheme.error
                    )
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
            label = { Text(stringResource(R.string.birthdate)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.handleEvent(RegisterEvent.ShowDatePicker) }) {
                    Text("📅")
                }
            },
            isError = uiState.birthDate != null && uiState.birthDate!!.isAfter(LocalDate.now()),
            supportingText = {
                if (uiState.birthDate != null && uiState.birthDate!!.isAfter(LocalDate.now())) {
                    Text(
                        text = stringResource(R.string.register_error_birthdate_in_future),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
            enabled = !uiState.isLoading
        )

        // Перемикач згоди з політикою конфіденційності
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = uiState.acceptedPrivacyPolicy,
                onCheckedChange = { viewModel.handleEvent(RegisterEvent.PrivacyPolicyAcceptanceChanged(it)) },
                enabled = !uiState.isLoading
            )

            Text(
                text = stringResource(R.string.register_accept_privacy_policy),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Відображення помилок
        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
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
                Text(stringResource(R.string.register_register))
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