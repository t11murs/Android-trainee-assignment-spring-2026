package com.example.avito_intership.presentation.screen.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.usecase.auth.LogoutUseCase
import com.example.avito_intership.presentation.common.UiMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

sealed interface ChatListSessionEffect {
    data object LoggedOut : ChatListSessionEffect
    data class ShowMessage(val message: String) : ChatListSessionEffect
}

@HiltViewModel
class ChatListSessionViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val uiMessageMapper: UiMessageMapper,
) : ViewModel() {

    private val _effects = MutableSharedFlow<ChatListSessionEffect>()
    val effects: SharedFlow<ChatListSessionEffect> = _effects.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is AppResult.Success -> _effects.emit(ChatListSessionEffect.LoggedOut)
                is AppResult.Error -> {
                    _effects.emit(ChatListSessionEffect.ShowMessage(uiMessageMapper.map(result.error)))
                }
            }
        }
    }
}
