package com.example.avito_intership.domain.repository

import com.example.avito_intership.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    fun observeMessages(chatId: String): Flow<List<Message>>
    suspend fun getMessage(messageId: String): Message?
    suspend fun upsertMessage(message: Message)
    suspend fun upsertMessages(messages: List<Message>)
    suspend fun deleteMessagesByChat(chatId: String)
}
