package com.example.avito_intership.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.avito_intership.data.local.dao.ChatDao
import com.example.avito_intership.data.mapper.LocalStorageMapper
import com.example.avito_intership.domain.model.Chat
import com.example.avito_intership.domain.repository.ChatRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomChatRepository @Inject constructor(
    private val chatDao: ChatDao,
    private val mapper: LocalStorageMapper,
) : ChatRepository {

    override fun observeChats(): Flow<List<Chat>> {
        return chatDao.observeChats().map { chats -> chats.map(mapper::toDomain) }
    }

    override fun observePagedChats(query: String): Flow<PagingData<Chat>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { chatDao.pagingSource(query.trim()) },
        ).flow.map { pagingData -> pagingData.map(mapper::toDomain) }
    }

    override fun observeChat(chatId: String): Flow<Chat?> {
        return chatDao.observeChat(chatId).map { chat -> chat?.let(mapper::toDomain) }
    }

    override suspend fun getChat(chatId: String): Chat? {
        return chatDao.getChat(chatId)?.let(mapper::toDomain)
    }

    override suspend fun searchChats(query: String): List<Chat> {
        return chatDao.searchChats(query).map(mapper::toDomain)
    }

    override suspend fun upsertChat(chat: Chat) {
        chatDao.upsertChat(mapper.toEntity(chat))
    }

    override suspend fun upsertChats(chats: List<Chat>) {
        chatDao.upsertChats(chats.map(mapper::toEntity))
    }
}
