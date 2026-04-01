package com.example.avito_intership.presentation.screen.chat

import androidx.compose.runtime.Composable
import com.example.avito_intership.presentation.components.PlaceholderScreen
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ChatRoute(
    onNavigateBack: () -> Unit,
) {
    ScreenScaffold(title = "Chat") {
        PlaceholderScreen(
            title = "Conversation",
            description = "Messages will appear here",
            primaryActionLabel = "Back",
            onPrimaryAction = onNavigateBack,
        )
    }
}
