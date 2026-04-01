package com.example.avito_intership.domain.repository

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.model.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): AppResult<UserProfile>
    suspend fun register(email: String, password: String): AppResult<UserProfile>
    suspend fun logout(): AppResult<Unit>
    suspend fun getCurrentUser(): AppResult<UserProfile?>
}
