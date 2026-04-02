package com.example.avito_intership.data.repository

import com.example.avito_intership.core.theme.ThemeMode
import com.example.avito_intership.data.local.preferences.SettingsDataStore
import com.example.avito_intership.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {

    override fun observeThemeMode(): Flow<ThemeMode> = settingsDataStore.observeThemeMode()

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        settingsDataStore.updateThemeMode(themeMode)
    }
}
