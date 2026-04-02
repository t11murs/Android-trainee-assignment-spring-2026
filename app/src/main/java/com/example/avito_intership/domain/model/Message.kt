package com.example.avito_intership.domain.model

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM,
}

enum class MessageStatus {
    SENDING,
    SENT,
    GENERATING,
    ERROR,
}

data class Message(
    val id: String,
    val chatId: String,
    val role: MessageRole,
    val text: String,
    val createdAt: Long,
    val status: MessageStatus,
)
