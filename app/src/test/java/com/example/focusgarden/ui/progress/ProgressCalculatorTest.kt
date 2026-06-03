package com.example.focusgarden.ui.progress

import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.model.SessionStatus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class ProgressCalculatorTest {
    private val zone: ZoneId = ZoneId.of("UTC")
    private val today: LocalDate = LocalDate.of(2026, 5, 20)

    @Test
    fun buildBalanceZonesUsesOnlyCompletedSessionsFromLastSevenDays() {
        val sessions = listOf(
            session("Разум", minutes = 25, daysAgo = 0, status = SessionStatus.COMPLETED.name),
            session("Разум", minutes = 10, daysAgo = 6, status = SessionStatus.COMPLETED.name),
            session("Разум", minutes = 60, daysAgo = 7, status = SessionStatus.COMPLETED.name),
            session("Тело", minutes = 45, daysAgo = 1, status = SessionStatus.FAILED.name),
            session("Дело", minutes = 45, daysAgo = 2, status = SessionStatus.COMPLETED.name)
        )

        val zones = ProgressCalculator.buildBalanceZones(
            sessions = sessions,
            today = today,
            zoneId = zone
        )

        assertEquals(35, zones.first { it.name == "Разум" }.minutes)
        assertEquals(0, zones.first { it.name == "Тело" }.minutes)
        assertEquals(45, zones.first { it.name == "Дело" }.minutes)
    }

    @Test
    fun buildAllTimeBalanceZonesUsesAccumulatedCategoryStats() {
        val stats = listOf(
            categoryStats("Разум", totalMinutes = 160, completed = 5, failed = 2),
            categoryStats("Тело", totalMinutes = 20, completed = 2, failed = 1),
            categoryStats("Дело", totalMinutes = 0, completed = 0, failed = 3)
        )

        val zones = ProgressCalculator.buildAllTimeBalanceZones(stats)

        assertEquals(5, zones.size)
        assertEquals(160, zones.first { it.name == "Разум" }.minutes)
        assertEquals(5, zones.first { it.name == "Разум" }.completedSessions)
        assertEquals(20, zones.first { it.name == "Тело" }.minutes)
        assertEquals(0, zones.first { it.name == "Дело" }.minutes)
        assertEquals(0, zones.first { it.name == "Порядок" }.completedSessions)
    }

    @Test
    fun buildWeekFocusCreatesSevenPointsInChronologicalOrder() {
        val sessions = listOf(
            session("Разум", minutes = 25, daysAgo = 0, status = SessionStatus.COMPLETED.name),
            session("Тело", minutes = 45, daysAgo = 3, status = SessionStatus.COMPLETED.name),
            session("Дело", minutes = 20, daysAgo = 8, status = SessionStatus.COMPLETED.name)
        )

        val points = ProgressCalculator.buildWeekFocus(
            sessions = sessions,
            today = today,
            zoneId = zone
        )

        assertEquals(7, points.size)
        assertEquals(45, points[3].minutes)
        assertEquals(25, points[6].minutes)
    }

    @Test
    fun calculateStreakCountsConsecutiveCompletedDaysEndingToday() {
        val sessions = listOf(
            session("Разум", minutes = 25, daysAgo = 0, status = SessionStatus.COMPLETED.name),
            session("Тело", minutes = 25, daysAgo = 1, status = SessionStatus.COMPLETED.name),
            session("Дело", minutes = 25, daysAgo = 3, status = SessionStatus.COMPLETED.name)
        )

        val streak = ProgressCalculator.calculateStreak(
            sessions = sessions,
            today = today,
            zoneId = zone
        )

        assertEquals(2, streak)
    }

    private fun session(
        categoryName: String,
        minutes: Int,
        daysAgo: Long,
        status: String
    ): FocusSessionView {
        val startedAt = today
            .minusDays(daysAgo)
            .atTime(9, 0)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
        return FocusSessionView(
            id = daysAgo + minutes,
            categoryId = minutes.toLong(),
            title = null,
            plannedDurationMinutes = minutes,
            actualDurationSeconds = minutes * 60,
            startedAt = startedAt,
            endedAt = startedAt + minutes * 60_000L,
            status = status,
            points = if (status == SessionStatus.COMPLETED.name) minutes else 0,
            failReason = if (status == SessionStatus.FAILED.name) "TEST" else null,
            categoryName = categoryName,
            categoryIcon = "TEST"
        )
    }

    private fun categoryStats(
        categoryName: String,
        totalMinutes: Int,
        completed: Int,
        failed: Int
    ): CategoryStats = CategoryStats(
        categoryId = totalMinutes.toLong() + completed,
        categoryName = categoryName,
        categoryIcon = "TEST",
        totalFocusSeconds = totalMinutes * 60L,
        completedSessions = completed,
        failedSessions = failed,
        points = totalMinutes
    )
}
