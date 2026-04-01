package com.example.avito_intership.presentation.screen.profile

import androidx.compose.runtime.Composable
import com.example.avito_intership.presentation.components.PlaceholderScreen
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
) {
    ScreenScaffold(title = "Profile") {
        PlaceholderScreen(
            title = "Your profile",
            description = "User details and theme settings will appear here",
            primaryActionLabel = "Back",
            onPrimaryAction = onNavigateBack,
        )
    }
}
