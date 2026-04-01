package com.example.avito_intership.presentation.screen.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun SplashRoute(
    onOpenAuth: () -> Unit,
    onOpenChatList: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.destination) {
        when (uiState.destination) {
            SplashDestination.AUTH -> onOpenAuth()
            SplashDestination.CHAT_LIST -> onOpenChatList()
            null -> Unit
        }
    }

    ScreenScaffold(title = "Загрузка") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "AI Ассистент")
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = viewModel::resolveDestination) {
                    Text(text = "Повторить")
                }
            }
        }
    }
}
