package com.example.focusgarden.data.repository

import androidx.room.withTransaction
import com.example.focusgarden.data.database.AppDatabase
import com.example.focusgarden.data.entity.CategoryEntity
import com.example.focusgarden.data.entity.FocusSessionEntity
import com.example.focusgarden.data.entity.GardenPlantEntity
import com.example.focusgarden.data.entity.GardenProgressEntity
import com.example.focusgarden.model.PlantStage
import com.example.focusgarden.model.PlantStatus
import com.example.focusgarden.model.SessionStatus
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Demo dataset for visual testing of the Progress screen.
 * It is inserted only when the local database has no focus sessions.
 */
class DemoDataSeeder(private val database: AppDatabase) {
    private val categoryDao = database.categoryDao()
    private val sessionDao = database.focusSessionDao()
    private val plantDao = database.gardenPlantDao()
    private val progressDao = database.gardenProgressDao()

    suspend fun seedIfEmpty() {
        if (sessionDao.countAll() > 0) return

        val categories = categoryDao.getAllOnce()
        if (categories.isEmpty()) return
        val byName = categories.associateBy { it.name }

        val demoSessions = listOf(
            // Today
            DemoSession("Разум", 60, 0, 9, true),
            DemoSession("Дело", 25, 0, 14, true),
            DemoSession("Люди", 10, 0, 19, false),

            // Yesterday
            DemoSession("Разум", 60, 1, 10, true),
            DemoSession("Тело", 25, 1, 18, true),
            DemoSession("Порядок", 25, 1, 21, false),

            // 2 days ago
            DemoSession("Разум", 45, 2, 11, true),
            DemoSession("Дело", 45, 2, 16, true),

            // 3 days ago
            DemoSession("Разум", 25, 3, 9, true),
            DemoSession("Тело", 25, 3, 17, true),
            DemoSession("Люди", 10, 3, 20, true),

            // 4 days ago
            DemoSession("Разум", 60, 4, 10, true),
            DemoSession("Дело", 45, 4, 15, false),

            // 5 days ago
            DemoSession("Разум", 45, 5, 9, true),
            DemoSession("Дело", 45, 5, 15, true),
            DemoSession("Порядок", 25, 5, 19, true),

            // 6 days ago
            DemoSession("Разум", 25, 6, 8, true),
            DemoSession("Тело", 10, 6, 18, true)
        )

        database.withTransaction {
            val progress = mutableMapOf<Long, ProgressAccumulator>()

            demoSessions.forEach { demo ->
                val category = byName[demo.categoryName] ?: return@forEach
                val startedAt = demoStartMillis(daysAgo = demo.daysAgo, hour = demo.hour)
                val plannedSeconds = demo.minutes * 60
                val actualSeconds = if (demo.completed) plannedSeconds else (plannedSeconds / 3).coerceAtLeast(60)
                val endedAt = startedAt + actualSeconds * 1000L
                val status = if (demo.completed) SessionStatus.COMPLETED.name else SessionStatus.FAILED.name
                val points = if (demo.completed) demo.minutes else 0

                val sessionId = sessionDao.insert(
                    FocusSessionEntity(
                        categoryId = category.id,
                        title = null,
                        plannedDurationMinutes = demo.minutes,
                        actualDurationSeconds = actualSeconds,
                        startedAt = startedAt,
                        endedAt = endedAt,
                        status = status,
                        points = points,
                        failReason = if (demo.completed) null else "DEMO_INTERRUPTED"
                    )
                )

                plantDao.insert(
                    GardenPlantEntity(
                        sessionId = sessionId,
                        categoryId = category.id,
                        plantType = plantTypeForDuration(demo.minutes),
                        growthStage = if (demo.completed) PlantStage.FULL_TREE.name else PlantStage.WITHERED_TREE.name,
                        status = if (demo.completed) PlantStatus.GROWN.name else PlantStatus.WITHERED.name,
                        createdAt = endedAt
                    )
                )

                val accumulator = progress.getOrPut(category.id) { ProgressAccumulator() }
                if (demo.completed) {
                    accumulator.totalFocusSeconds += actualSeconds.toLong()
                    accumulator.completedSessions += 1
                    accumulator.points += demo.minutes
                } else {
                    accumulator.failedSessions += 1
                }
            }

            categories.forEach { category ->
                val accumulator = progress[category.id] ?: ProgressAccumulator()
                progressDao.upsert(
                    GardenProgressEntity(
                        categoryId = category.id,
                        level = levelForMinutes((accumulator.totalFocusSeconds / 60).toInt()),
                        points = accumulator.points,
                        totalFocusSeconds = accumulator.totalFocusSeconds,
                        completedSessions = accumulator.completedSessions,
                        failedSessions = accumulator.failedSessions
                    )
                )
            }
        }
    }

    private fun demoStartMillis(daysAgo: Int, hour: Int): Long {
        val zone = ZoneId.systemDefault()
        return LocalDate.now(zone)
            .minusDays(daysAgo.toLong())
            .atTime(LocalTime.of(hour, 0))
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }

    private fun plantTypeForDuration(minutes: Int): String = when {
        minutes >= 60 -> "RARE_TREE"
        minutes >= 45 -> "BIG_TREE"
        minutes >= 25 -> "TREE"
        else -> "SMALL_PLANT"
    }

    private fun levelForMinutes(minutes: Int): Int = when {
        minutes <= 0 -> 0
        minutes <= 30 -> 1
        minutes <= 90 -> 2
        minutes <= 180 -> 3
        minutes <= 300 -> 4
        else -> 5
    }

    private data class DemoSession(
        val categoryName: String,
        val minutes: Int,
        val daysAgo: Int,
        val hour: Int,
        val completed: Boolean
    )

    private data class ProgressAccumulator(
        var totalFocusSeconds: Long = 0,
        var completedSessions: Int = 0,
        var failedSessions: Int = 0,
        var points: Int = 0
    )
}
