package com.example.avito_intership.presentation.screen.splash

import androidx.compose.runtime.Composable
import com.example.avito_intership.presentation.components.PlaceholderScreen
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun SplashRoute(
    onOpenAuth: () -> Unit,
    @Suppress("UNUSED_PARAMETER")
    onOpenChatList: () -> Unit,
) {
    ScreenScaffold(title = "Splash") {
        PlaceholderScreen(
            title = "AI Assistant",
            description = "Loading your workspace",
            primaryActionLabel = "Continue",
            onPrimaryAction = onOpenAuth,
        )
    }
}
