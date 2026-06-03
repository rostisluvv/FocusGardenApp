package com.example.focusgarden.data.datastore

import com.example.focusgarden.model.ThemeMode

object SettingsRules {
    private val supportedDurations = setOf(10, 25, 45, 60)

    fun normalizeDuration(minutes: Int): Int {
        return if (minutes in supportedDurations) minutes else 25
    }

    fun normalizeThemeMode(mode: String): String {
        val normalized = mode.uppercase()
        return if (ThemeMode.entries.any { it.name == normalized }) {
            normalized
        } else {
            ThemeMode.SYSTEM.name
        }
    }
}
