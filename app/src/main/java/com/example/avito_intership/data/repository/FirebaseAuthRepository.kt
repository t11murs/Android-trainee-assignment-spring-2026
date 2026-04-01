package com.example.avito_intership.data.repository

import com.example.avito_intership.core.result.AppResult
import com.example.avito_intership.data.mapper.FirebaseUserMapper
import com.example.avito_intership.data.remote.auth.FirebaseAuthDataSource
import com.example.avito_intership.domain.model.UserProfile
import com.example.avito_intership.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val dataSource: FirebaseAuthDataSource,
    private val mapper: FirebaseUserMapper,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AppResult<UserProfile> {
        return when (val result = dataSource.login(email, password)) {
            is AppResult.Success -> AppResult.Success(mapper.map(result.data))
            is AppResult.Error -> result
        }
    }

    override suspend fun register(email: String, password: String): AppResult<UserProfile> {
        return when (val result = dataSource.register(email, password)) {
            is AppResult.Success -> AppResult.Success(mapper.map(result.data))
            is AppResult.Error -> result
        }
    }

    override suspend fun logout(): AppResult<Unit> = dataSource.logout()

    override suspend fun getCurrentUser(): AppResult<UserProfile?> {
        return when (val result = dataSource.getCurrentUser()) {
            is AppResult.Success -> AppResult.Success(result.data?.let(mapper::map))
            is AppResult.Error -> result
        }
    }
}
