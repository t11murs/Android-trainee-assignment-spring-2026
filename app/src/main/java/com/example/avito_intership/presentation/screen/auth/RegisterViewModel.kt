package com.example.avito_intership.presentation.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.usecase.auth.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val uiMessageMapper: UiMessageMapper,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<RegisterEffect>()
    val effects: SharedFlow<RegisterEffect> = _effects.asSharedFlow()

    private var lastSubmit: Triple<String, String, String>? = null

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.EmailChanged -> _uiState.update { it.copy(email = action.value) }
            is RegisterAction.PasswordChanged -> _uiState.update { it.copy(password = action.value) }
            is RegisterAction.RepeatPasswordChanged -> _uiState.update { it.copy(repeatPassword = action.value) }
            RegisterAction.SubmitClicked -> register()
            RegisterAction.RetryClicked -> retry()
        }
    }

    private fun retry() {
        val cached = lastSubmit ?: return
        register(cached.first, cached.second, cached.third)
    }

    private fun register(
        email: String = uiState.value.email,
        password: String = uiState.value.password,
        repeatPassword: String = uiState.value.repeatPassword,
    ) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = registerUseCase(email, password, repeatPassword)) {
                is AppResult.Success -> {
                    lastSubmit = null
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(RegisterEffect.NavigateToChatList)
                }

                is AppResult.Error -> {
                    lastSubmit = Triple(email, password, repeatPassword)
                    _uiState.update { it.copy(isLoading = false) }
                    _effects.emit(
                        RegisterEffect.ShowError(
                            message = uiMessageMapper.map(result.error),
                            canRetry = result.error == AppError.Network,
                        ),
                    )
                }
            }
        }
    }
}
