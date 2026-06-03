package com.example.focusgarden.data.datastore

data class AppSettings(
    val defaultDurationMinutes: Int = 25,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val strictModeEnabled: Boolean = false,
    val themeMode: String = "SYSTEM"
)
