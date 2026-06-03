package com.example.focusgarden.ui.home

import com.example.focusgarden.data.entity.CategoryEntity

data class HomeUiState(
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedDurationMinutes: Int = 25,
    val todayFocusSeconds: Long = 0,
    val completedTodayCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
