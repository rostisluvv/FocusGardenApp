package com.example.focusgarden.ui.focus

import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.SessionStatus

data class FocusSessionUiState(
    val sessionId: Long = 0,
    val categoryName: String = "",
    val categoryIcon: String = "",
    val plannedDurationSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val progress: Float = 0f,
    val plantStage: PlantStage = PlantStage.SEED,
    val status: SessionStatus = SessionStatus.ACTIVE,
    val strictModeEnabled: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val isCancelDialogVisible: Boolean = false,
    val isLoading: Boolean = true
)
