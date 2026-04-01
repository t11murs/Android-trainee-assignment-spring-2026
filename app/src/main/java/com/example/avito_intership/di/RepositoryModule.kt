package com.example.avito_intership.di

import com.example.avito_intership.data.repository.FirebaseAuthRepository
import com.example.avito_intership.data.repository.FakeSettingsRepository
import com.example.avito_intership.domain.repository.AuthRepository
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
        repository: FakeSettingsRepository,
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        repository: FirebaseAuthRepository,
    ): AuthRepository
}
