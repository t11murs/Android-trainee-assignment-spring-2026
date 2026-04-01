package com.example.avito_intership.presentation.screen.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

sealed interface AuthAction {
    data class EmailChanged(val value: String) : AuthAction
    data class PasswordChanged(val value: String) : AuthAction
    data object SubmitClicked : AuthAction
    data object RetryClicked : AuthAction
}

sealed interface AuthEffect {
    data object NavigateToChatList : AuthEffect
    data class ShowError(val message: String, val canRetry: Boolean) : AuthEffect
}
