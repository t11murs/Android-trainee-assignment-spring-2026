package com.example.avito_intership.presentation.screen.chat

import com.example.avito_intership.domain.model.MessageStatus

data class ChatUiState(
    val title: String = "Чат",
    val input: String = "",
    val messages: List<ChatMessageUi> = emptyList(),
    val isGenerating: Boolean = false,
)

data class ChatMessageUi(
    val id: String,
    val text: String,
    val isFromUser: Boolean,
    val status: MessageStatus,
    val canRetry: Boolean,
    val canShare: Boolean,
)

sealed interface ChatAction {
    data class InputChanged(val value: String) : ChatAction
    data object SendClicked : ChatAction
    data class RetryAssistant(val messageId: String) : ChatAction
    data class ShareAssistant(val messageId: String) : ChatAction
}

sealed interface ChatEffect {
    data class ShareText(val text: String) : ChatEffect
    data class ShowMessage(val message: String) : ChatEffect
}
