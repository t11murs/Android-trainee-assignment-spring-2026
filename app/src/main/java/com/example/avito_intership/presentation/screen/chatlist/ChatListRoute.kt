package com.example.avito_intership.presentation.screen.chatlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ChatListRoute(
    onOpenChat: () -> Unit,
    onOpenProfile: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ChatListSessionViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ChatListSessionEffect.LoggedOut -> onLoggedOut()
                is ChatListSessionEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    ScreenScaffold(
        title = "Чаты",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBarActions = {
            TextButton(onClick = viewModel::logout) {
                Text(text = "Выйти")
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Поиск чатов") },
                enabled = false,
            )
            Text(
                text = "Чатов пока нет",
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(onClick = onOpenChat) {
                Text(text = "Новый чат")
            }
            TextButton(onClick = onOpenProfile) { Text(text = "Профиль") }
            OutlinedButton(onClick = onOpenChat) { Text(text = "Открыть пример чата") }
        }
    }
}
