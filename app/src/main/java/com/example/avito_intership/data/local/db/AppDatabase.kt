package com.example.avito_intership.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.avito_intership.data.local.dao.ChatDao
import com.example.avito_intership.data.local.dao.MessageDao
import com.example.avito_intership.data.local.entity.ChatEntity
import com.example.avito_intership.data.local.entity.MessageEntity

@Database(
    entities = [ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
