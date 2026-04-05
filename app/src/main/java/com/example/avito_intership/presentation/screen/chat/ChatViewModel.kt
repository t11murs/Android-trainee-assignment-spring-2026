package com.example.avito_intership.presentation.screen.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.model.Chat
import com.example.avito_intership.domain.model.Message
import com.example.avito_intership.domain.model.MessageRole
import com.example.avito_intership.domain.model.MessageStatus
import com.example.avito_intership.domain.repository.ChatRepository
import com.example.avito_intership.domain.repository.MessageRepository
import com.example.avito_intership.domain.usecase.ai.GenerateAssistantReplyUseCase
import com.example.avito_intership.presentation.common.UiMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val generateAssistantReplyUseCase: GenerateAssistantReplyUseCase,
    private val uiMessageMapper: UiMessageMapper,
) : ViewModel() {

    private val chatId = MutableStateFlow(savedStateHandle.get<String>("chatId").orEmpty())
    private val input = MutableStateFlow("")
    private val optimisticMessages = MutableStateFlow<List<Message>>(emptyList())
    private val _effects = MutableSharedFlow<ChatEffect>()
    val effects = _effects.asSharedFlow()

    val uiState: StateFlow<ChatUiState> = combine(
        chatId.flatMapLatest { currentChatId -> chatRepository.observeChat(currentChatId) },
        chatId.flatMapLatest { currentChatId -> messageRepository.observeMessages(currentChatId) },
        optimisticMessages,
        input,
    ) { chat, storedMessages, optimistic, inputValue ->
        val mergedMessages = mergeMessages(storedMessages, optimistic)

        ChatUiState(
            title = chat?.title ?: "Чат",
            input = inputValue,
            messages = mergedMessages.map { message ->
                ChatMessageUi(
                    id = message.id,
                    text = message.text,
                    isFromUser = message.role == MessageRole.USER,
                    status = message.status,
                    canRetry = message.role == MessageRole.ASSISTANT && message.status == MessageStatus.ERROR,
                    canShare = message.role == MessageRole.ASSISTANT && message.status == MessageStatus.SENT,
                )
            },
            isGenerating = mergedMessages.any {
                it.role == MessageRole.ASSISTANT && it.status == MessageStatus.GENERATING
            },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ChatUiState(),
    )

    fun attachChat(chatId: String) {
        if (chatId.isNotBlank() && this.chatId.value != chatId) {
            this.chatId.value = chatId
            optimisticMessages.value = emptyList()
        }
    }

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
        val currentChatId = chatId.value
        if (trimmed.isBlank() || currentChatId.isBlank()) return

        viewModelScope.launch {
            ensureChatExists(currentChatId)

            val now = System.currentTimeMillis()
            val userMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = currentChatId,
                role = MessageRole.USER,
                text = trimmed,
                createdAt = now,
                status = MessageStatus.SENT,
            )
            val assistantMessage = Message(
                id = UUID.randomUUID().toString(),
                chatId = currentChatId,
                role = MessageRole.ASSISTANT,
                text = "Ассистент думает...",
                createdAt = now + 1,
                status = MessageStatus.GENERATING,
            )

            input.value = ""
            addOptimisticMessages(userMessage, assistantMessage)
            messageRepository.upsertMessages(listOf(userMessage, assistantMessage))
            updateChatPreview(userMessage.text)
            resolveAssistantReply(assistantMessage.id, userMessage.text)
        }
    }

    private fun retryAssistant(messageId: String) {
        val currentChatId = chatId.value
        if (currentChatId.isBlank()) return

        viewModelScope.launch {
            ensureChatExists(currentChatId)

            val messages = mergeMessages(emptyList(), optimisticMessages.value.ifEmpty {
                uiState.value.messages.map { message ->
                    Message(
                        id = message.id,
                        chatId = currentChatId,
                        role = if (message.isFromUser) MessageRole.USER else MessageRole.ASSISTANT,
                        text = message.text,
                        createdAt = 0L,
                        status = message.status,
                    )
                }
            })
            val failedIndex = messages.indexOfFirst { it.id == messageId }
            if (failedIndex <= 0) return@launch

            val userText = messages.subList(0, failedIndex).lastOrNull { it.role == MessageRole.USER }?.text
                ?: return@launch
            val retryMessage = Message(
                id = messageId,
                chatId = currentChatId,
                role = MessageRole.ASSISTANT,
                text = "Ассистент думает...",
                createdAt = System.currentTimeMillis(),
                status = MessageStatus.GENERATING,
            )

            replaceOptimisticMessage(retryMessage)
            messageRepository.upsertMessage(retryMessage)
            resolveAssistantReply(messageId, userText)
        }
    }

    private fun shareAssistant(messageId: String) {
        viewModelScope.launch {
            val message = messageRepository.getMessage(messageId)
                ?: optimisticMessages.value.firstOrNull { it.id == messageId }
                ?: return@launch

            if (message.role == MessageRole.ASSISTANT && message.status == MessageStatus.SENT) {
                _effects.emit(ChatEffect.ShareText(message.text))
            }
        }
    }

    private suspend fun resolveAssistantReply(
        assistantMessageId: String,
        userText: String,
    ) {
        val currentChatId = chatId.value
        if (currentChatId.isBlank()) return

        when (val result = generateAssistantReplyUseCase(userText)) {
            is AppResult.Success -> {
                val resolvedMessage = Message(
                    id = assistantMessageId,
                    chatId = currentChatId,
                    role = MessageRole.ASSISTANT,
                    text = result.data,
                    createdAt = System.currentTimeMillis(),
                    status = MessageStatus.SENT,
                )
                replaceOptimisticMessage(resolvedMessage)
                messageRepository.upsertMessage(resolvedMessage)
                updateChatPreview(result.data)
            }

            is AppResult.Error -> {
                val failedMessage = Message(
                    id = assistantMessageId,
                    chatId = currentChatId,
                    role = MessageRole.ASSISTANT,
                    text = "Не удалось получить ответ. Попробуйте ещё раз.",
                    createdAt = System.currentTimeMillis(),
                    status = MessageStatus.ERROR,
                )
                replaceOptimisticMessage(failedMessage)
                messageRepository.upsertMessage(failedMessage)
                _effects.emit(ChatEffect.ShowMessage(uiMessageMapper.map(result.error)))
            }
        }
    }

    private suspend fun updateChatPreview(lastText: String) {
        val currentChatId = chatId.value
        if (currentChatId.isBlank()) return

        val chat = chatRepository.getChat(currentChatId) ?: return
        chatRepository.upsertChat(
            chat.copy(
                updatedAt = System.currentTimeMillis(),
                lastMessagePreview = lastText.take(80),
            ),
        )
    }

    private suspend fun ensureChatExists(currentChatId: String) {
        val existingChat = chatRepository.getChat(currentChatId)
        if (existingChat != null) return

        val now = System.currentTimeMillis()
        chatRepository.upsertChat(
            Chat(
                id = currentChatId,
                title = "Новый чат",
                createdAt = now,
                updatedAt = now,
                lastMessagePreview = null,
            ),
        )
    }

    private fun addOptimisticMessages(vararg messages: Message) {
        optimisticMessages.update { current ->
            mergeMessages(current, messages.toList())
        }
    }

    private fun replaceOptimisticMessage(message: Message) {
        optimisticMessages.update { current ->
            mergeMessages(current.filterNot { it.id == message.id }, listOf(message))
        }
    }

    private fun mergeMessages(
        stored: List<Message>,
        local: List<Message>,
    ): List<Message> {
        return (stored + local)
            .groupBy { it.id }
            .map { (_, versions) -> versions.maxBy { it.createdAt } }
            .sortedBy { it.createdAt }
    }
}
