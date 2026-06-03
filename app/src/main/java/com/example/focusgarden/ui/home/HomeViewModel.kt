package com.example.focusgarden.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.entity.CategoryEntity
import com.example.focusgarden.data.repository.CategoryRepository
import com.example.focusgarden.data.repository.FocusSessionRepository
import com.example.focusgarden.data.repository.SettingsRepository
import com.example.focusgarden.data.repository.StatisticsRepository
import com.example.focusgarden.util.DateTimeUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepository: CategoryRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val statisticsRepository: StatisticsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val selectedCategoryId = MutableStateFlow<Long?>(null)
    private val selectedDuration = MutableStateFlow<Int?>(null)
    private val startOfDay = DateTimeUtils.startOfTodayMillis()

    val navigationEvents = MutableSharedFlow<Long>()

    private data class HomeBaseState(
        val categories: List<CategoryEntity>,
        val selectedId: Long?,
        val duration: Int?,
        val todaySeconds: Long,
        val todayCount: Int
    )

    private val baseState: Flow<HomeBaseState> = combine(
        categoryRepository.observeCategories(),
        selectedCategoryId,
        selectedDuration,
        statisticsRepository.observeTodayFocusSeconds(startOfDay),
        statisticsRepository.observeTodayCompletedCount(startOfDay)
    ) { categories, selectedId, duration, todaySeconds, todayCount ->
        HomeBaseState(
            categories = categories,
            selectedId = selectedId,
            duration = duration,
            todaySeconds = todaySeconds,
            todayCount = todayCount
        )
    }

    val uiState = combine(
        baseState,
        settingsRepository.settings
    ) { base, settings ->
        val resolvedCategoryId = base.selectedId ?: base.categories.firstOrNull()?.id
        if (base.selectedId == null && resolvedCategoryId != null) {
            selectedCategoryId.value = resolvedCategoryId
        }

        HomeUiState(
            categories = base.categories,
            selectedCategoryId = resolvedCategoryId,
            selectedDurationMinutes = base.duration ?: settings.defaultDurationMinutes,
            todayFocusSeconds = base.todaySeconds,
            completedTodayCount = base.todayCount,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isLoading = true))

    fun selectCategory(id: Long) {
        selectedCategoryId.value = id
    }

    fun selectDuration(minutes: Int) {
        selectedDuration.value = minutes
    }

    fun startSession() {
        val categoryId = uiState.value.selectedCategoryId ?: return
        val duration = uiState.value.selectedDurationMinutes
        viewModelScope.launch {
            val sessionId = focusSessionRepository.startSession(categoryId, duration)
            navigationEvents.emit(sessionId)
        }
    }
}
