package com.example.avito_intership.presentation.screen.chatlist

data class ChatListUiState(
    val query: String = "",
)

data class ChatListItemUi(
    val id: String,
    val title: String,
    val subtitle: String,
)

sealed interface ChatListEffect {
    data class OpenChat(val chatId: String) : ChatListEffect
    data class ShowMessage(val message: String) : ChatListEffect
}
