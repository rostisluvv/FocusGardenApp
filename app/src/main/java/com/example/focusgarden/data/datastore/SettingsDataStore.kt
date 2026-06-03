package com.example.focusgarden.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "focus_garden_settings")

class SettingsDataStore(private val context: Context) {
    private object Keys {
        val DEFAULT_DURATION = intPreferencesKey("default_duration_minutes")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val STRICT_MODE_ENABLED = booleanPreferencesKey("strict_mode_enabled")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val settings: Flow<AppSettings> = context.settingsDataStore.data.map { preferences ->
        AppSettings(
            defaultDurationMinutes = SettingsRules.normalizeDuration(preferences[Keys.DEFAULT_DURATION] ?: 25),
            soundEnabled = preferences[Keys.SOUND_ENABLED] ?: true,
            vibrationEnabled = preferences[Keys.VIBRATION_ENABLED] ?: true,
            strictModeEnabled = preferences[Keys.STRICT_MODE_ENABLED] ?: false,
            themeMode = SettingsRules.normalizeThemeMode(preferences[Keys.THEME_MODE] ?: "SYSTEM")
        )
    }

    suspend fun setDefaultDuration(minutes: Int) {
        context.settingsDataStore.edit { it[Keys.DEFAULT_DURATION] = SettingsRules.normalizeDuration(minutes) }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setStrictModeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.STRICT_MODE_ENABLED] = enabled }
    }

    suspend fun setThemeMode(mode: String) {
        context.settingsDataStore.edit { it[Keys.THEME_MODE] = SettingsRules.normalizeThemeMode(mode) }
    }
}
