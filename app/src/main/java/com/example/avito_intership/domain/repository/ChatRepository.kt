package com.example.avito_intership.domain.repository

import com.example.avito_intership.domain.model.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeChats(): Flow<List<Chat>>
    fun observeChat(chatId: String): Flow<Chat?>
    suspend fun getChat(chatId: String): Chat?
    suspend fun searchChats(query: String): List<Chat>
    suspend fun upsertChat(chat: Chat)
    suspend fun upsertChats(chats: List<Chat>)
}
