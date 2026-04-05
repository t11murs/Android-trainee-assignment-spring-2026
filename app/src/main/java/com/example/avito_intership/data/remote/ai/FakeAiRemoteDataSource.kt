package com.example.avito_intership.data.remote.ai

import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.data.remote.ai.dto.AiGenerateRequestDto
import com.example.avito_intership.data.remote.ai.dto.AiGenerateResponseDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay

@Singleton
class FakeAiRemoteDataSource @Inject constructor() : AiRemoteDataSource {

    override suspend fun generateReply(request: AiGenerateRequestDto): AppResult<AiGenerateResponseDto> {
        delay(900)

        val normalized = request.prompt.lowercase()
        if ("error" in normalized || "ошибка" in normalized) {
            return AppResult.Error(
                AppError.Unknown("Локальная генерация завершилась ошибкой"),
            )
        }

        return AppResult.Success(
            AiGenerateResponseDto(
                text = "Локальный AI-ответ на сообщение: ${request.prompt}. " +
                    "Слой уже вынесен в отдельный репозиторий и готов к замене на backend.",
            ),
        )
    }
}
