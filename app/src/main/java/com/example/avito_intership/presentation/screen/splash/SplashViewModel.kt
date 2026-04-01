package com.example.avito_intership.presentation.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.usecase.auth.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SplashDestination {
    AUTH,
    CHAT_LIST,
}

data class SplashUiState(
    val isLoading: Boolean = true,
    val destination: SplashDestination? = null,
)

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        resolveDestination()
    }

    fun resolveDestination() {
        viewModelScope.launch {
            _uiState.value = SplashUiState(isLoading = true, destination = null)
            _uiState.value = when (val result = getCurrentUserUseCase()) {
                is AppResult.Success -> SplashUiState(
                    isLoading = false,
                    destination = if (result.data == null) SplashDestination.AUTH else SplashDestination.CHAT_LIST,
                )

                is AppResult.Error -> SplashUiState(
                    isLoading = false,
                    destination = SplashDestination.AUTH,
                )
            }
        }
    }
}
