package com.example.avito_intership.presentation.common

import com.example.avito_intership.core.result.AppError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiMessageMapper @Inject constructor() {

    fun map(error: AppError): String {
        return when (error) {
            AppError.Network -> "Ошибка сети. Проверьте подключение и повторите попытку."
            AppError.Unauthorized -> "Неверный email или пароль."
            AppError.NotFound -> "Запрошенные данные не найдены."
            AppError.Configuration -> "Firebase не настроен. Добавьте файл google-services.json в модуль app."
            is AppError.Firebase -> mapFirebaseError(error)
            is AppError.Validation -> error.message
            is AppError.Unknown -> error.message ?: "Что-то пошло не так. Повторите попытку."
        }
    }

    private fun mapFirebaseError(error: AppError.Firebase): String {
        return when (error.code) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Этот email уже используется."
            "ERROR_WEAK_PASSWORD" -> "Слишком простой пароль."
            "ERROR_INVALID_EMAIL" -> "Введите корректный email."
            "ERROR_WRONG_PASSWORD" -> "Неверный email или пароль."
            "ERROR_USER_NOT_FOUND" -> "Аккаунт не найден."
            else -> error.message ?: "Ошибка авторизации. Повторите попытку."
        }
    }
}
