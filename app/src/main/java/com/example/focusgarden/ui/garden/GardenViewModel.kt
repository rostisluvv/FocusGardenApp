package com.example.focusgarden.ui.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.repository.GardenRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class GardenViewModel(repository: GardenRepository) : ViewModel() {
    val uiState = repository.observePlants()
        .map { GardenUiState(plants = it, isLoading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GardenUiState())
}
