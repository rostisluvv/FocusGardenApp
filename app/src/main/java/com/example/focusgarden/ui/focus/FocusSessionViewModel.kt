package com.example.focusgarden.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.data.repository.FocusSessionRepository
import com.example.focusgarden.data.repository.SettingsRepository
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.SessionStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.min

class FocusSessionViewModel(
    private val sessionId: Long,
    private val repository: FocusSessionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FocusSessionUiState(sessionId = sessionId))
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FocusEvent>()
    val events = _events.asSharedFlow()

    private var currentSession: FocusSessionView? = null
    private var timerJob: Job? = null

    init {
        observeSettings()
        observeSession()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.value = _uiState.value.copy(
                    strictModeEnabled = settings.strictModeEnabled,
                    soundEnabled = settings.soundEnabled,
                    vibrationEnabled = settings.vibrationEnabled
                )
            }
        }
    }

    private fun observeSession() {
        viewModelScope.launch {
            repository.observeSession(sessionId).collect { session ->
                if (session == null) {
                    _events.emit(FocusEvent.NavigateBack)
                    return@collect
                }
                currentSession = session
                val status = SessionStatus.valueOf(session.status)
                _uiState.value = _uiState.value.copy(
                    categoryName = session.categoryName,
                    categoryIcon = session.categoryIcon,
                    plannedDurationSeconds = session.plannedDurationMinutes * 60,
                    status = status,
                    isLoading = false
                )
                if (status == SessionStatus.ACTIVE) {
                    startTimerIfNeeded()
                } else {
                    timerJob?.cancel()
                    _events.emit(FocusEvent.NavigateResult(session.id))
                }
            }
        }
    }

    private fun startTimerIfNeeded() {
        if (timerJob?.isActive == true) return
        timerJob = viewModelScope.launch {
            while (true) {
                val session = currentSession ?: return@launch
                if (session.status != SessionStatus.ACTIVE.name) return@launch
                val plannedSeconds = session.plannedDurationMinutes * 60
                val elapsedSeconds = min(
                    ((System.currentTimeMillis() - session.startedAt) / 1000).toInt().coerceAtLeast(0),
                    plannedSeconds
                )
                val remainingSeconds = (plannedSeconds - elapsedSeconds).coerceAtLeast(0)
                val progress = if (plannedSeconds == 0) 1f else elapsedSeconds.toFloat() / plannedSeconds.toFloat()

                _uiState.value = _uiState.value.copy(
                    elapsedSeconds = elapsedSeconds,
                    remainingSeconds = remainingSeconds,
                    progress = progress.coerceIn(0f, 1f),
                    plantStage = PlantStage.fromProgress(progress)
                )

                if (remainingSeconds <= 0) {
                    repository.completeSession(session.id)
                    return@launch
                }
                delay(1000)
            }
        }
    }

    fun showCancelDialog() {
        _uiState.value = _uiState.value.copy(isCancelDialogVisible = true)
    }

    fun hideCancelDialog() {
        _uiState.value = _uiState.value.copy(isCancelDialogVisible = false)
    }

    fun cancelSession() {
        viewModelScope.launch {
            repository.failSession(sessionId, "USER_CANCELLED")
        }
    }

    fun failByBackgroundIfStrictMode() {
        val state = _uiState.value
        if (state.strictModeEnabled && state.status == SessionStatus.ACTIVE) {
            viewModelScope.launch {
                repository.failSession(sessionId, "APP_BACKGROUND")
            }
        }
    }
}

sealed interface FocusEvent {
    data class NavigateResult(val sessionId: Long) : FocusEvent
    data object NavigateBack : FocusEvent
}
