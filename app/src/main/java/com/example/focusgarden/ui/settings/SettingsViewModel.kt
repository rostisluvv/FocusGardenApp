package com.example.focusgarden.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val uiState = repository.settings
        .map { SettingsUiState(settings = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setDefaultDuration(minutes: Int) = viewModelScope.launch { repository.setDefaultDuration(minutes) }
    fun setSoundEnabled(enabled: Boolean) = viewModelScope.launch { repository.setSoundEnabled(enabled) }
    fun setVibrationEnabled(enabled: Boolean) = viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    fun setStrictModeEnabled(enabled: Boolean) = viewModelScope.launch { repository.setStrictModeEnabled(enabled) }
    fun setThemeMode(mode: String) = viewModelScope.launch { repository.setThemeMode(mode) }
}
