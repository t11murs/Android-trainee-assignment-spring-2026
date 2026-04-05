package com.example.avito_intership.domain.usecase.ai

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.repository.AiRepository
import javax.inject.Inject

class GenerateAssistantReplyUseCase @Inject constructor(
    private val aiRepository: AiRepository,
) {
    suspend operator fun invoke(prompt: String): AppResult<String> {
        return aiRepository.generateReply(prompt)
    }
}
