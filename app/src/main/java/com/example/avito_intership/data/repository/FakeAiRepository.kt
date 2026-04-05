package com.example.avito_intership.data.repository

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.data.remote.ai.AiRemoteDataSource
import com.example.avito_intership.data.remote.ai.dto.AiGenerateRequestDto
import com.example.avito_intership.domain.repository.AiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAiRepository @Inject constructor(
    private val remoteDataSource: AiRemoteDataSource,
) : AiRepository {

    override suspend fun generateReply(prompt: String): AppResult<String> {
        return when (val result = remoteDataSource.generateReply(AiGenerateRequestDto(prompt = prompt))) {
            is AppResult.Success -> AppResult.Success(result.data.text)
            is AppResult.Error -> result
        }
    }
}
