package com.example.focusgarden.ui.garden

import com.example.focusgarden.data.model.GardenPlantView

data class GardenUiState(
    val plants: List<GardenPlantView> = emptyList(),
    val isLoading: Boolean = true
)
