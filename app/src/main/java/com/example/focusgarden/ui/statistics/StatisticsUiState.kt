package com.example.focusgarden.ui.statistics

import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView

data class StatisticsUiState(
    val totalFocusSeconds: Long = 0,
    val completedSessions: Int = 0,
    val failedSessions: Int = 0,
    val categoryStats: List<CategoryStats> = emptyList(),
    val recentSessions: List<FocusSessionView> = emptyList(),
    val isLoading: Boolean = true
) {
    val totalSessions: Int get() = completedSessions + failedSessions
    val successRate: Int get() = if (totalSessions == 0) 0 else ((completedSessions.toFloat() / totalSessions) * 100).toInt()
}
