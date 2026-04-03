package com.example.avito_intership.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.avito_intership.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("SELECT * FROM chats ORDER BY updatedAt DESC")
    fun observeChats(): Flow<List<ChatEntity>>

    @Query(
        """
        SELECT * FROM chats
        WHERE (:query = '' OR title LIKE '%' || :query || '%')
        ORDER BY updatedAt DESC
        """,
    )
    fun pagingSource(query: String): PagingSource<Int, ChatEntity>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    fun observeChat(chatId: String): Flow<ChatEntity?>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    suspend fun getChat(chatId: String): ChatEntity?

    @Query(
        """
        SELECT * FROM chats
        WHERE title LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
        """,
    )
    suspend fun searchChats(query: String): List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChat(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChats(chats: List<ChatEntity>)
}
