package com.example.avito_intership.domain.usecase.auth

import android.util.Patterns
import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthValidator @Inject constructor() {

    fun validateEmail(email: String): AppResult<Unit> {
        return when {
            email.isBlank() -> AppResult.Error(AppError.Validation("Введите email"))
            !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                AppResult.Error(AppError.Validation("Введите корректный email"))
            }

            else -> AppResult.Success(Unit)
        }
    }

    fun validatePassword(password: String): AppResult<Unit> {
        return when {
            password.isBlank() -> AppResult.Error(AppError.Validation("Введите пароль"))
            password.length < MIN_PASSWORD_LENGTH -> {
                AppResult.Error(
                    AppError.Validation("Пароль должен быть не короче $MIN_PASSWORD_LENGTH символов"),
                )
            }

            else -> AppResult.Success(Unit)
        }
    }

    fun validateRepeatedPassword(
        password: String,
        repeatPassword: String,
    ): AppResult<Unit> {
        return when {
            repeatPassword.isBlank() -> AppResult.Error(AppError.Validation("Повторите пароль"))
            password != repeatPassword -> AppResult.Error(AppError.Validation("Пароли не совпадают"))
            else -> AppResult.Success(Unit)
        }
    }

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
