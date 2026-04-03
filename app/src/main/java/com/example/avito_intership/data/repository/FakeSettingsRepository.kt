package com.example.avito_intership.data.repository

import com.example.avito_intership.core.theme.ThemeMode
import com.example.avito_intership.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSettingsRepository @Inject constructor() : SettingsRepository {
    private val themeMode = MutableStateFlow(ThemeMode.SYSTEM)

    override fun observeThemeMode(): Flow<ThemeMode> = themeMode

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        this.themeMode.value = themeMode
    }
}
