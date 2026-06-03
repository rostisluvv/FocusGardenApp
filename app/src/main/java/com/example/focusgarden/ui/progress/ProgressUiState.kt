package com.example.focusgarden.ui.progress

import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.data.model.GardenPlantView

data class WeekFocusPoint(
    val label: String,
    val minutes: Int
)

data class ProgressUiState(
    val totalFocusSeconds: Long = 0,
    val completedSessions: Int = 0,
    val failedSessions: Int = 0,
    val categoryStats: List<CategoryStats> = emptyList(),
    val recentSessions: List<FocusSessionView> = emptyList(),
    val plants: List<GardenPlantView> = emptyList(),
    val weekFocus: List<WeekFocusPoint> = emptyList(),
    val isLoading: Boolean = true
) {
    val totalSessions: Int get() = completedSessions + failedSessions
    val successRate: Int get() = if (totalSessions == 0) 0 else ((completedSessions.toFloat() / totalSessions) * 100).toInt()
}
