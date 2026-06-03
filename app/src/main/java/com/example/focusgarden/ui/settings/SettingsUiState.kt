package com.example.focusgarden.ui.settings

import com.example.focusgarden.data.datastore.AppSettings

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val isLoading: Boolean = true
)
