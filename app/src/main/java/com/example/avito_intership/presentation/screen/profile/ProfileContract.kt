package com.example.avito_intership.presentation.screen.profile

import com.example.avito_intership.core.theme.ThemeMode

data class ProfileUiState(
    val isLoading: Boolean = true,
    val displayName: String = "Пользователь",
    val email: String? = null,
    val phone: String? = null,
    val photoUrl: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val tokens: Int = 0,
)

sealed interface ProfileAction {
    data class ThemeSelected(val themeMode: ThemeMode) : ProfileAction
    data object LogoutClicked : ProfileAction
}

sealed interface ProfileEffect {
    data object LoggedOut : ProfileEffect
    data class ShowMessage(val message: String) : ProfileEffect
}
