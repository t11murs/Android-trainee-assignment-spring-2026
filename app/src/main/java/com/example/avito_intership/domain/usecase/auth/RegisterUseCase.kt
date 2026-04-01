package com.example.avito_intership.domain.usecase.auth

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.domain.model.UserProfile
import com.example.avito_intership.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val authValidator: AuthValidator,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        repeatPassword: String,
    ): AppResult<UserProfile> {
        authValidator.validateEmail(email).let {
            if (it is AppResult.Error) return it
        }
        authValidator.validatePassword(password).let {
            if (it is AppResult.Error) return it
        }
        authValidator.validateRepeatedPassword(password, repeatPassword).let {
            if (it is AppResult.Error) return it
        }
        return authRepository.register(email.trim(), password)
    }
}
