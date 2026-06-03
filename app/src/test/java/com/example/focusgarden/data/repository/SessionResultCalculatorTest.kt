package com.example.focusgarden.data.repository

import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.PlantStatus
import com.example.focusgarden.model.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionResultCalculatorTest {
    @Test
    fun completeSessionResultUsesPlannedDurationAndAwardsPoints() {
        val result = SessionResultCalculator.complete(plannedDurationMinutes = 25)

        assertEquals(25 * 60, result.actualDurationSeconds)
        assertEquals(SessionStatus.COMPLETED.name, result.sessionStatus)
        assertEquals(25, result.points)
        assertEquals(PlantStatus.GROWN.name, result.plantStatus)
        assertEquals(PlantStage.FULL_TREE.name, result.growthStage)
    }

    @Test
    fun failSessionResultCapsElapsedSecondsAndAwardsNoPoints() {
        val result = SessionResultCalculator.fail(
            plannedDurationMinutes = 25,
            startedAt = 1_000L,
            now = 30_000L,
            reason = "USER_CANCELLED"
        )

        assertEquals(29, result.actualDurationSeconds)
        assertEquals(SessionStatus.FAILED.name, result.sessionStatus)
        assertEquals(0, result.points)
        assertEquals("USER_CANCELLED", result.failReason)
        assertEquals(PlantStatus.WITHERED.name, result.plantStatus)
        assertEquals(PlantStage.WITHERED_TREE.name, result.growthStage)
    }

    @Test
    fun failSessionResultDoesNotExceedPlannedDuration() {
        val result = SessionResultCalculator.fail(
            plannedDurationMinutes = 10,
            startedAt = 1_000L,
            now = 1_000_000L,
            reason = "APP_BACKGROUND"
        )

        assertEquals(10 * 60, result.actualDurationSeconds)
    }
}
