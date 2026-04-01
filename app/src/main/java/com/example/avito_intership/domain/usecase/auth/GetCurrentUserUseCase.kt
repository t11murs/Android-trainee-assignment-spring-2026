package com.example.avito_intership.domain.usecase.auth

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.model.UserProfile
import com.example.avito_intership.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): AppResult<UserProfile?> = authRepository.getCurrentUser()
}
