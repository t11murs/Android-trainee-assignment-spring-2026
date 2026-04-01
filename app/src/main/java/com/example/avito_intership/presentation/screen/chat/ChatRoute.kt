package com.example.avito_intership.presentation.screen.chat

import androidx.compose.runtime.Composable
import com.example.avito_intership.presentation.components.PlaceholderScreen
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ChatRoute(
    onNavigateBack: () -> Unit,
) {
    ScreenScaffold(title = "Чат") {
        PlaceholderScreen(
            title = "Диалог",
            description = "Здесь будут отображаться сообщения",
            primaryActionLabel = "Назад",
            onPrimaryAction = onNavigateBack,
        )
    }
}
