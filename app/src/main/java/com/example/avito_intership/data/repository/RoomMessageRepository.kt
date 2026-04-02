package com.example.avito_intership.data.repository

import com.example.avito_intership.data.local.dao.MessageDao
import com.example.avito_intership.data.mapper.LocalStorageMapper
import com.example.avito_intership.domain.model.Message
import com.example.avito_intership.domain.repository.MessageRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomMessageRepository @Inject constructor(
    private val messageDao: MessageDao,
    private val mapper: LocalStorageMapper,
) : MessageRepository {

    override fun observeMessages(chatId: String): Flow<List<Message>> {
        return messageDao.observeMessages(chatId).map { messages -> messages.map(mapper::toDomain) }
    }

    override suspend fun getMessage(messageId: String): Message? {
        return messageDao.getMessage(messageId)?.let(mapper::toDomain)
    }

    override suspend fun upsertMessage(message: Message) {
        messageDao.upsertMessage(mapper.toEntity(message))
    }

    override suspend fun upsertMessages(messages: List<Message>) {
        messageDao.upsertMessages(messages.map(mapper::toEntity))
    }

    override suspend fun deleteMessagesByChat(chatId: String) {
        messageDao.deleteMessagesByChat(chatId)
    }
}
