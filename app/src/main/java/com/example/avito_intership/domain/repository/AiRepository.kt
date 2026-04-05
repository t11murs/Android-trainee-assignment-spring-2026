package com.example.avito_intership.domain.repository

import com.example.avito_intership.core.result.AppResult

interface AiRepository {
    suspend fun generateReply(prompt: String): AppResult<String>
}
