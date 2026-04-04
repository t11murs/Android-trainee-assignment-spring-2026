package com.example.avito_intership.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.core.theme.ThemeMode
import com.example.avito_intership.domain.repository.SettingsRepository
import com.example.avito_intership.domain.usecase.auth.GetCurrentUserUseCase
import com.example.avito_intership.domain.usecase.auth.LogoutUseCase
import com.example.avito_intership.presentation.common.UiMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val settingsRepository: SettingsRepository,
    private val uiMessageMapper: UiMessageMapper,
) : ViewModel() {

    private val userState = MutableStateFlow(ProfileUiState())
    private val _effects = MutableSharedFlow<ProfileEffect>()
    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ProfileUiState> = combine(
        userState,
        settingsRepository.observeThemeMode(),
    ) { currentState, themeMode ->
        currentState.copy(themeMode = themeMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState(),
    )

    init {
        loadProfile()
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.ThemeSelected -> updateTheme(action.themeMode)
            ProfileAction.LogoutClicked -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            when (val result = getCurrentUserUseCase()) {
                is AppResult.Success -> {
                    val user = result.data
                    userState.value = ProfileUiState(
                        isLoading = false,
                        displayName = user?.name?.takeIf { it.isNotBlank() }
                            ?: user?.email?.substringBefore("@")
                            ?: "Пользователь",
                        email = user?.email,
                        phone = user?.phone,
                        photoUrl = user?.photoUrl,
                        tokens = 0,
                    )
                }

                is AppResult.Error -> {
                    userState.value = ProfileUiState(isLoading = false)
                    _effects.emit(ProfileEffect.ShowMessage(uiMessageMapper.map(result.error)))
                }
            }
        }
    }

    private fun updateTheme(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(themeMode)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is AppResult.Success -> _effects.emit(ProfileEffect.LoggedOut)
                is AppResult.Error -> _effects.emit(ProfileEffect.ShowMessage(uiMessageMapper.map(result.error)))
            }
        }
    }
}
