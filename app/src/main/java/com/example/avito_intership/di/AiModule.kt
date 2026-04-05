package com.example.avito_intership.di

import com.example.avito_intership.data.remote.ai.AiRemoteDataSource
import com.example.avito_intership.data.remote.ai.GigaChatRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRemoteDataSource(
        dataSource: GigaChatRemoteDataSource,
    ): AiRemoteDataSource
}
