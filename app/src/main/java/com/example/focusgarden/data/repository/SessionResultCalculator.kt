package com.example.focusgarden.data.repository

import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.PlantStatus
import com.example.focusgarden.model.SessionStatus
import kotlin.math.min

data class SessionResult(
    val actualDurationSeconds: Int,
    val sessionStatus: String,
    val points: Int,
    val failReason: String?,
    val plantStatus: String,
    val growthStage: String
)

object SessionResultCalculator {
    fun complete(plannedDurationMinutes: Int): SessionResult {
        return SessionResult(
            actualDurationSeconds = plannedDurationMinutes * 60,
            sessionStatus = SessionStatus.COMPLETED.name,
            points = plannedDurationMinutes,
            failReason = null,
            plantStatus = PlantStatus.GROWN.name,
            growthStage = PlantStage.FULL_TREE.name
        )
    }

    fun fail(
        plannedDurationMinutes: Int,
        startedAt: Long,
        now: Long,
        reason: String
    ): SessionResult {
        val plannedSeconds = plannedDurationMinutes * 60
        val elapsedSeconds = min(
            ((now - startedAt) / 1000).toInt().coerceAtLeast(0),
            plannedSeconds
        )
        return SessionResult(
            actualDurationSeconds = elapsedSeconds,
            sessionStatus = SessionStatus.FAILED.name,
            points = 0,
            failReason = reason,
            plantStatus = PlantStatus.WITHERED.name,
            growthStage = PlantStage.WITHERED_TREE.name
        )
    }
}
