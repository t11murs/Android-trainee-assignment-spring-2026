package com.example.avito_intership.presentation.screen.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.avito_intership.domain.model.Chat
import com.example.avito_intership.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")
    private val _effects = MutableSharedFlow<ChatListEffect>()
    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ChatListUiState> = query
        .map { ChatListUiState(query = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatListUiState(),
        )

    val chats: Flow<PagingData<ChatListItemUi>> = query
        .flatMapLatest { currentQuery ->
            chatRepository.observePagedChats(currentQuery)
        }
        .map { pagingData ->
            pagingData.map { chat ->
                chat.toUi()
            }
        }
        .cachedIn(viewModelScope)

    fun onQueryChanged(value: String) {
        query.value = value
    }

    fun createChat() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val chatNumber = chatRepository.searchChats("").size + 1
            val chatId = UUID.randomUUID().toString()

            chatRepository.upsertChat(
                Chat(
                    id = chatId,
                    title = "Чат $chatNumber",
                    createdAt = now,
                    updatedAt = now,
                    lastMessagePreview = "Создан локально",
                ),
            )

            _effects.emit(ChatListEffect.OpenChat(chatId))
        }
    }

    fun openChat(chatId: String) {
        viewModelScope.launch {
            _effects.emit(ChatListEffect.OpenChat(chatId))
        }
    }

    private fun Chat.toUi(): ChatListItemUi {
        return ChatListItemUi(
            id = id,
            title = title,
            subtitle = lastMessagePreview ?: "Пустой чат",
        )
    }
}
