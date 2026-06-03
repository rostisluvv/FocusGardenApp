package com.example.focusgarden.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class StatisticsViewModel(repository: StatisticsRepository) : ViewModel() {
    val uiState = combine(
        repository.observeTotalFocusSeconds(),
        repository.observeCompletedSessions(),
        repository.observeFailedSessions(),
        repository.observeCategoryStats(),
        repository.observeRecentSessions()
    ) { totalSeconds, completed, failed, categoryStats, recent ->
        StatisticsUiState(
            totalFocusSeconds = totalSeconds,
            completedSessions = completed,
            failedSessions = failed,
            categoryStats = categoryStats,
            recentSessions = recent,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsUiState())
}
