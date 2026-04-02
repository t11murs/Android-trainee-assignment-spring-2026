package com.example.avito_intership.presentation.screen.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.domain.model.Chat
import com.example.avito_intership.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatListUiState(
    val chats: List<ChatListItemUi> = emptyList(),
)

data class ChatListItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
)

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
) : ViewModel() {

    val uiState: StateFlow<ChatListUiState> = chatRepository
        .observeChats()
        .map { chats ->
            ChatListUiState(
                chats = chats.map { chat ->
                    ChatListItemUi(
                        id = chat.id,
                        title = chat.title,
                        subtitle = chat.lastMessagePreview ?: "Пустой чат",
                    )
                },
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ChatListUiState(),
        )

    fun createChat() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val chatNumber = uiState.value.chats.size + 1
            chatRepository.upsertChat(
                Chat(
                    id = UUID.randomUUID().toString(),
                    title = "Чат $chatNumber",
                    createdAt = now,
                    updatedAt = now,
                    lastMessagePreview = "Создан локально",
                ),
            )
        }
    }
}
