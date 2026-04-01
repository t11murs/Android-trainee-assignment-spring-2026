package com.example.avito_intership.presentation.screen.profile

import androidx.compose.runtime.Composable
import com.example.avito_intership.presentation.components.PlaceholderScreen
import com.example.avito_intership.presentation.components.ScreenScaffold

@Composable
fun ProfileRoute(
    onNavigateBack: () -> Unit,
) {
    ScreenScaffold(title = "Профиль") {
        PlaceholderScreen(
            title = "Ваш профиль",
            description = "Здесь будут данные пользователя и настройки темы",
            primaryActionLabel = "Назад",
            onPrimaryAction = onNavigateBack,
        )
    }
}
