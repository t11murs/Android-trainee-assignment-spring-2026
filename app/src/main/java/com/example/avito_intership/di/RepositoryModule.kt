package com.example.avito_intership.di

import com.example.avito_intership.data.repository.DataStoreSettingsRepository
import com.example.avito_intership.data.repository.FakeAiRepository
import com.example.avito_intership.data.repository.FirebaseAuthRepository
import com.example.avito_intership.data.repository.RoomChatRepository
import com.example.avito_intership.data.repository.RoomMessageRepository
import com.example.avito_intership.domain.repository.AiRepository
import com.example.avito_intership.domain.repository.AuthRepository
import com.example.avito_intership.domain.repository.ChatRepository
import com.example.avito_intership.domain.repository.MessageRepository
import com.example.avito_intership.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        repository: DataStoreSettingsRepository,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: FirebaseAuthRepository,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        repository: FakeAiRepository,
    ): AiRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        repository: RoomChatRepository,
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        repository: RoomMessageRepository,
    ): MessageRepository
}
