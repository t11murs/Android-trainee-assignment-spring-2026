package com.example.avito_intership.presentation.screen.auth

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
)

sealed interface RegisterAction {
    data class EmailChanged(val value: String) : RegisterAction
    data class PasswordChanged(val value: String) : RegisterAction
    data class RepeatPasswordChanged(val value: String) : RegisterAction
    data object SubmitClicked : RegisterAction
    data object RetryClicked : RegisterAction
}

sealed interface RegisterEffect {
    data object NavigateToChatList : RegisterEffect
    data class ShowError(val message: String, val canRetry: Boolean) : RegisterEffect
}
