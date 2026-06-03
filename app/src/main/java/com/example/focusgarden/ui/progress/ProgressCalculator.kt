package com.example.focusgarden.ui.progress

import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.model.SessionStatus
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

data class BalanceZone(
    val name: String,
    val minutes: Int,
    val completedSessions: Int,
    val score: Float,
    val level: Int
)

object ProgressCalculator {
    private val zoneNames = listOf("Тело", "Разум", "Дело", "Порядок", "Люди")
    private const val BALANCE_PROFILE_TARGET_MINUTES = 450f

    fun buildWeekFocus(
        sessions: List<FocusSessionView>,
        today: LocalDate = LocalDate.now(ZoneId.systemDefault()),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<WeekFocusPoint> {
        return (6 downTo 0).map { offset ->
            val day = today.minusDays(offset.toLong())
            val seconds = sessions
                .asSequence()
                .filter { it.status == SessionStatus.COMPLETED.name }
                .filter { Instant.ofEpochMilli(it.startedAt).atZone(zoneId).toLocalDate() == day }
                .sumOf { it.actualDurationSeconds }
            WeekFocusPoint(
                label = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru")).take(2),
                minutes = seconds / 60
            )
        }
    }

    fun buildBalanceZones(
        sessions: List<FocusSessionView>,
        today: LocalDate = LocalDate.now(ZoneId.systemDefault()),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): List<BalanceZone> {
        val start = today.minusDays(6)
        val completedThisWeek = sessions.filter { session ->
            if (session.status != SessionStatus.COMPLETED.name) return@filter false
            val day = Instant.ofEpochMilli(session.startedAt).atZone(zoneId).toLocalDate()
            !day.isBefore(start) && !day.isAfter(today)
        }

        return zoneNames.map { name ->
            val categorySessions = completedThisWeek.filter { it.categoryName == name }
            val minutes = categorySessions.sumOf { it.actualDurationSeconds } / 60
            BalanceZone(
                name = name,
                minutes = minutes,
                completedSessions = categorySessions.size,
                score = (minutes.toFloat() / BALANCE_PROFILE_TARGET_MINUTES).coerceIn(0f, 1f),
                level = levelForMinutes(minutes)
            )
        }
    }

    fun buildAllTimeBalanceZones(stats: List<CategoryStats>): List<BalanceZone> {
        val statsByName = stats.associateBy { it.categoryName }
        return zoneNames.map { name ->
            val category = statsByName[name]
            val minutes = ((category?.totalFocusSeconds ?: 0L) / 60L).toInt()
            BalanceZone(
                name = name,
                minutes = minutes,
                completedSessions = category?.completedSessions ?: 0,
                score = (minutes.toFloat() / BALANCE_PROFILE_TARGET_MINUTES).coerceIn(0f, 1f),
                level = levelForMinutes(minutes)
            )
        }
    }

    fun calculateStreak(
        sessions: List<FocusSessionView>,
        today: LocalDate = LocalDate.now(ZoneId.systemDefault()),
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int {
        val completedDays = sessions
            .asSequence()
            .filter { it.status == SessionStatus.COMPLETED.name }
            .map { Instant.ofEpochMilli(it.startedAt).atZone(zoneId).toLocalDate() }
            .toSet()
        if (completedDays.isEmpty()) return 0

        var day = today
        var streak = 0
        while (completedDays.contains(day)) {
            streak++
            day = day.minusDays(1)
        }
        return streak
    }

    fun levelForMinutes(minutes: Int): Int = when {
        minutes <= 0 -> 0
        minutes <= 30 -> 1
        minutes <= 90 -> 2
        minutes <= 180 -> 3
        minutes <= 300 -> 4
        else -> 5
    }
}
