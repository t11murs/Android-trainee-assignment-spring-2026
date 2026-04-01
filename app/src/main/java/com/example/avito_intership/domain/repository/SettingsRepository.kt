package com.example.avito_intership.domain.repository

import com.example.avito_intership.core.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeThemeMode(): Flow<ThemeMode>
}
