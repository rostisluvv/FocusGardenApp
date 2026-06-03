package com.example.focusgarden.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.data.repository.GardenRepository
import com.example.focusgarden.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(
    gardenRepository: GardenRepository,
    statisticsRepository: StatisticsRepository
) : ViewModel() {
    private data class StatsBase(
        val totalSeconds: Long,
        val completed: Int,
        val failed: Int,
        val categoryStats: List<CategoryStats>,
        val recent: List<FocusSessionView>
    )

    private val statsBase = combine(
        statisticsRepository.observeTotalFocusSeconds(),
        statisticsRepository.observeCompletedSessions(),
        statisticsRepository.observeFailedSessions(),
        statisticsRepository.observeCategoryStats(),
        statisticsRepository.observeRecentSessions(200)
    ) { totalSeconds, completed, failed, categoryStats, recent ->
        StatsBase(totalSeconds, completed, failed, categoryStats, recent)
    }

    val uiState = combine(
        statsBase,
        gardenRepository.observePlants()
    ) { stats, plants ->
        ProgressUiState(
            totalFocusSeconds = stats.totalSeconds,
            completedSessions = stats.completed,
            failedSessions = stats.failed,
            categoryStats = stats.categoryStats,
            recentSessions = stats.recent.take(20),
            plants = plants,
            weekFocus = ProgressCalculator.buildWeekFocus(stats.recent),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressUiState())
}
