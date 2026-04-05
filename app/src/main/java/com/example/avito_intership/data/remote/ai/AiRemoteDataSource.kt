package com.example.avito_intership.data.remote.ai

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.data.remote.ai.dto.AiGenerateRequestDto
import com.example.avito_intership.data.remote.ai.dto.AiGenerateResponseDto

interface AiRemoteDataSource {
    suspend fun generateReply(request: AiGenerateRequestDto): AppResult<AiGenerateResponseDto>
}
