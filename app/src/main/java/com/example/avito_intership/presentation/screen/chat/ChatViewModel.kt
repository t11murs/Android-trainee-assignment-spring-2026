package com.example.avito_intership.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.domain.model.Message
import com.example.avito_intership.domain.model.MessageRole
import com.example.avito_intership.domain.model.MessageStatus
import com.example.avito_intership.domain.repository.ChatRepository
import com.example.avito_intership.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
) : ViewModel() {

    private val chatId = savedStateHandle.get<String>("chatId").orEmpty()
    private val input = MutableStateFlow("")
    private val _effects = MutableSharedFlow<ChatEffect>()
    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ChatUiState> = combine(
        chatRepository.observeChat(chatId),
        messageRepository.observeMessages(chatId),
        input,
    ) { chat, messages, inputValue ->
        ChatUiState(
            title = chat?.title ?: "Чат",
            input = inputValue,
            messages = messages.map { message ->
                ChatMessageUi(
                    id = message.id,
                    text = message.text,
                    isFromUser = message.role == MessageRole.USER,
                    status = message.status,
                    canRetry = message.role == MessageRole.ASSISTANT && message.status == MessageStatus.ERROR,
                    canShare = message.role == MessageRole.ASSISTANT && message.status == MessageStatus.SENT,
                )
            },
            isGenerating = messages.any {
                it.role == MessageRole.ASSISTANT && it.status == MessageStatus.GENERATING
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState(),
    )

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.InputChanged -> input.update { action.value }
            ChatAction.SendClicked -> sendMessage()
            is ChatAction.RetryAssistant -> retryAssistant(action.messageId)
            is ChatAction.ShareAssistant -> shareAssistant(action.messageId)
        }
    }

    private fun sendMessage() {
        val trimmed = input.value.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.USER,
                text = trimmed,
                createdAt = now,
                status = MessageStatus.SENT,
            )
            val assistantMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = MessageRole.ASSISTANT,
                text = "Ассистент думает...",
                createdAt = now + 1,
                status = MessageStatus.GENERATING,
            )

            input.value = ""
            messageRepository.upsertMessages(listOf(userMessage, assistantMessage))
            updateChatPreview(userMessage.text)
            generateAssistantReply(assistantMessage.id, userMessage.text)
        }
    }

    private fun retryAssistant(messageId: String) {
        viewModelScope.launch {
            val messages = uiState.value.messages
            val failedIndex = messages.indexOfFirst { it.id == messageId }
            if (failedIndex <= 0) return@launch

            val userText = messages.subList(0, failedIndex).lastOrNull { it.isFromUser }?.text ?: return@launch

            messageRepository.upsertMessage(
                Message(
                    id = messageId,
                    chatId = chatId,
                    role = MessageRole.ASSISTANT,
                    text = "Ассистент думает...",
                    createdAt = System.currentTimeMillis(),
                    status = MessageStatus.GENERATING,
                ),
            )
            generateAssistantReply(messageId, userText)
        }
    }

    private fun shareAssistant(messageId: String) {
        viewModelScope.launch {
            val message = messageRepository.getMessage(messageId) ?: return@launch
            if (message.role == MessageRole.ASSISTANT && message.status == MessageStatus.SENT) {
                _effects.emit(ChatEffect.ShareText(message.text))
            }
        }
    }

    private suspend fun generateAssistantReply(
        assistantMessageId: String,
        userText: String,
    ) {
        delay(900)

        if (shouldFail(userText)) {
            messageRepository.upsertMessage(
                Message(
                    id = assistantMessageId,
                    chatId = chatId,
                    role = MessageRole.ASSISTANT,
                    text = "Не удалось получить ответ. Попробуйте ещё раз.",
                    createdAt = System.currentTimeMillis(),
                    status = MessageStatus.ERROR,
                ),
            )
            _effects.emit(ChatEffect.ShowMessage("Локальная генерация завершилась ошибкой"))
            return
        }

        val reply = buildReply(userText)
        messageRepository.upsertMessage(
            Message(
                id = assistantMessageId,
                chatId = chatId,
                role = MessageRole.ASSISTANT,
                text = reply,
                createdAt = System.currentTimeMillis(),
                status = MessageStatus.SENT,
            ),
        )
        updateChatPreview(reply)
    }

    private suspend fun updateChatPreview(lastText: String) {
        val chat = chatRepository.getChat(chatId) ?: return
        chatRepository.upsertChat(
            chat.copy(
                updatedAt = System.currentTimeMillis(),
                lastMessagePreview = lastText.take(80),
            ),
        )
    }

    private fun shouldFail(text: String): Boolean {
        val normalized = text.lowercase()
        return "error" in normalized || "ошибка" in normalized
    }

    private fun buildReply(text: String): String {
        return "Локальный ответ на сообщение: $text. Это подготовка к следующему этапу с AI-слоем."
    }
}
