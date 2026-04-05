package com.example.avito_intership.data.remote.ai

import com.example.avito_intership.data.remote.ai.dto.GigaChatTokenResponseDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GigaChatTokenStore @Inject constructor() {

    @Volatile
    private var tokenData: GigaChatTokenResponseDto? = null

    fun getValidToken(nowMillis: Long): String? {
        val current = tokenData ?: return null
        return if (current.expiresAtMillis > nowMillis + 60_000) current.accessToken else null
    }

    fun save(token: GigaChatTokenResponseDto) {
        tokenData = token
    }
}
