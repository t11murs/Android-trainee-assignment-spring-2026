package com.example.avito_intership.core.result

sealed interface AppError {
    data object Network : AppError
    data object Unauthorized : AppError
    data object NotFound : AppError
    data object Configuration : AppError
    data class Firebase(val code: String?, val message: String?) : AppError
    data class Validation(val message: String) : AppError
    data class Unknown(val message: String? = null) : AppError
}
