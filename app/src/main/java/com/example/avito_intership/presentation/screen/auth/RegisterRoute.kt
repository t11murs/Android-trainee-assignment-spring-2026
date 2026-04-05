package com.example.avito_intership.presentation.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun RegisterRoute(
    onNavigateBack: () -> Unit,
    onRegistered: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToChatList -> onRegistered()
                is RegisterEffect.ShowError -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = if (effect.canRetry) "Повторить" else null,
                        duration = SnackbarDuration.Long,
                    )
                    if (effect.canRetry && result == SnackbarResult.ActionPerformed) {
                        viewModel.onAction(RegisterAction.RetryClicked)
                    }
                }
            }
        }
    }

    ScreenScaffold(
        title = "Регистрация",
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = "Создайте аккаунт")
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onAction(RegisterAction.EmailChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("E-mail") },
                enabled = !uiState.isLoading,
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onAction(RegisterAction.PasswordChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Пароль") },
                enabled = !uiState.isLoading,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )
            OutlinedTextField(
                value = uiState.repeatPassword,
                onValueChange = { viewModel.onAction(RegisterAction.RepeatPasswordChanged(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Повторите пароль") },
                enabled = !uiState.isLoading,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )
            Button(
                onClick = { viewModel.onAction(RegisterAction.SubmitClicked) },
                enabled = !uiState.isLoading,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(2.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(text = "Зарегистрироваться")
                }
            }
            TextButton(onClick = onNavigateBack) {
                Text(text = "Назад ко входу")
            }
        }
    }
}
