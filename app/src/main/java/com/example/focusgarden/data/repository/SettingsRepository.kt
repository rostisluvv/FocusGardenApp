package com.example.focusgarden.data.repository

import com.example.focusgarden.data.datastore.AppSettings
import com.example.focusgarden.data.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStore: SettingsDataStore) {
    val settings: Flow<AppSettings> = dataStore.settings
    suspend fun setDefaultDuration(minutes: Int) = dataStore.setDefaultDuration(minutes)
    suspend fun setSoundEnabled(enabled: Boolean) = dataStore.setSoundEnabled(enabled)
    suspend fun setVibrationEnabled(enabled: Boolean) = dataStore.setVibrationEnabled(enabled)
    suspend fun setStrictModeEnabled(enabled: Boolean) = dataStore.setStrictModeEnabled(enabled)
    suspend fun setThemeMode(mode: String) = dataStore.setThemeMode(mode)
}
