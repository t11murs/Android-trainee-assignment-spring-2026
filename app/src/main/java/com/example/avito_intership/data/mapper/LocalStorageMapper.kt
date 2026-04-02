package com.example.avito_intership.data.mapper

import com.example.avito_intership.data.local.entity.ChatEntity
import com.example.avito_intership.data.local.entity.MessageEntity
import com.example.avito_intership.domain.model.Chat
import com.example.avito_intership.domain.model.Message
import com.example.avito_intership.domain.model.MessageRole
import com.example.avito_intership.domain.model.MessageStatus
import javax.inject.Inject

class LocalStorageMapper @Inject constructor() {

    fun toDomain(entity: ChatEntity): Chat {
        return Chat(
            id = entity.id,
            title = entity.title,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            lastMessagePreview = entity.lastMessagePreview,
        )
    }

    fun toEntity(model: Chat): ChatEntity {
        return ChatEntity(
            id = model.id,
            title = model.title,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt,
            lastMessagePreview = model.lastMessagePreview,
        )
    }

    fun toDomain(entity: MessageEntity): Message {
        return Message(
            id = entity.id,
            chatId = entity.chatId,
            role = MessageRole.valueOf(entity.role),
            text = entity.text,
            createdAt = entity.createdAt,
            status = MessageStatus.valueOf(entity.status),
        )
    }

    fun toEntity(model: Message): MessageEntity {
        return MessageEntity(
            id = model.id,
            chatId = model.chatId,
            role = model.role.name,
            text = model.text,
            createdAt = model.createdAt,
            status = model.status.name,
        )
    }
}
