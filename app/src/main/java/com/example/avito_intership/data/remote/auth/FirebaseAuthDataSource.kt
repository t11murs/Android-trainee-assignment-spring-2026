package com.example.avito_intership.data.remote.auth

import android.content.Context
import com.example.avito_intership.core.result.AppError
import com.example.avito_intership.core.result.AppResult
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun login(email: String, password: String): AppResult<FirebaseUser> {
        val auth = authOrNull() ?: return AppResult.Error(AppError.Configuration)
        return runCatching {
            auth.signInWithEmailAndPassword(email, password).await().user
        }.fold(
            onSuccess = { user ->
                if (user == null) AppResult.Error(AppError.Unknown("User is missing"))
                else AppResult.Success(user)
            },
            onFailure = { throwable -> AppResult.Error(throwable.toAppError()) },
        )
    }

    suspend fun register(email: String, password: String): AppResult<FirebaseUser> {
        val auth = authOrNull() ?: return AppResult.Error(AppError.Configuration)
        return runCatching {
            auth.createUserWithEmailAndPassword(email, password).await().user
        }.fold(
            onSuccess = { user ->
                if (user == null) AppResult.Error(AppError.Unknown("User is missing"))
                else AppResult.Success(user)
            },
            onFailure = { throwable -> AppResult.Error(throwable.toAppError()) },
        )
    }

    fun logout(): AppResult<Unit> {
        val auth = authOrNull() ?: return AppResult.Error(AppError.Configuration)
        auth.signOut()
        return AppResult.Success(Unit)
    }

    fun getCurrentUser(): AppResult<FirebaseUser?> {
        val auth = authOrNull() ?: return AppResult.Error(AppError.Configuration)
        return AppResult.Success(auth.currentUser)
    }

    private fun authOrNull(): FirebaseAuth? {
        val app = FirebaseApp.initializeApp(context)
        return app?.let(FirebaseAuth::getInstance)
    }

    private fun Throwable.toAppError(): AppError {
        return when (this) {
            is FirebaseNetworkException -> AppError.Network
            is FirebaseTooManyRequestsException -> AppError.Network
            is FirebaseAuthInvalidUserException -> AppError.Unauthorized
            is FirebaseAuthInvalidCredentialsException -> AppError.Firebase(errorCode, localizedMessage)
            is FirebaseAuthException -> AppError.Firebase(errorCode, localizedMessage)
            else -> AppError.Unknown(localizedMessage)
        }
    }
}
