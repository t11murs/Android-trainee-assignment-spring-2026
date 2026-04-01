package com.example.avito_intership.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.usecase.auth.LoginUseCase
import com.example.avito_intership.presentation.common.UiMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val uiMessageMapper: UiMessageMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AuthEffect>()
    val effects: SharedFlow<AuthEffect> = _effects.asSharedFlow()

    private var lastSubmit: Pair<String, String>? = null

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is AuthAction.PasswordChanged -> _uiState.update { it.copy(password = action.value) }
            AuthAction.SubmitClicked -> login()
            AuthAction.RetryClicked -> retry()
        }
    }

    private fun retry() {
        val cached = lastSubmit ?: return
        login(cached.first, cached.second)
    }

    private fun login(
        email: String = uiState.value.email,
        password: String = uiState.value.password,
    ) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = loginUseCase(email, password)) {
                is AppResult.Success -> {
                    lastSubmit = null
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(AuthEffect.NavigateToChatList)
                }

                is AppResult.Error -> {
                    lastSubmit = email to password
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(
                        AuthEffect.ShowError(
                            message = uiMessageMapper.map(result.error),
                            canRetry = result.error == AppError.Network,
                        ),
                    )
                }
            }
        }
    }
}
