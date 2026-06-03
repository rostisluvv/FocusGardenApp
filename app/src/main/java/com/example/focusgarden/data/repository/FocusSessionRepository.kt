package com.example.focusgarden.data.repository

import androidx.room.withTransaction
import com.example.focusgarden.data.database.AppDatabase
import com.example.focusgarden.data.entity.FocusSessionEntity
import com.example.focusgarden.data.entity.GardenPlantEntity
import com.example.focusgarden.data.entity.GardenProgressEntity
import com.example.focusgarden.data.model.FocusSessionView
import com.example.focusgarden.model.SessionStatus
import kotlinx.coroutines.flow.Flow

class FocusSessionRepository(private val database: AppDatabase) {
    private val sessionDao = database.focusSessionDao()
    private val plantDao = database.gardenPlantDao()
    private val progressDao = database.gardenProgressDao()

    suspend fun startSession(categoryId: Long, durationMinutes: Int, title: String? = null): Long {
        return sessionDao.insert(
            FocusSessionEntity(
                categoryId = categoryId,
                title = title,
                plannedDurationMinutes = durationMinutes,
                startedAt = System.currentTimeMillis(),
                status = SessionStatus.ACTIVE.name
            )
        )
    }

    fun observeSession(sessionId: Long): Flow<FocusSessionView?> = sessionDao.observeViewById(sessionId)

    suspend fun getSession(sessionId: Long): FocusSessionView? = sessionDao.getViewById(sessionId)

    suspend fun completeSession(sessionId: Long) {
        database.withTransaction {
            val session = sessionDao.getById(sessionId) ?: return@withTransaction
            if (session.status != SessionStatus.ACTIVE.name) return@withTransaction

            val now = System.currentTimeMillis()
            val result = SessionResultCalculator.complete(session.plannedDurationMinutes)
            val completed = session.copy(
                actualDurationSeconds = result.actualDurationSeconds,
                endedAt = now,
                status = result.sessionStatus,
                points = result.points,
                failReason = result.failReason
            )
            sessionDao.update(completed)
            createPlantIfNeeded(completed, result.plantStatus, result.growthStage, now)
            updateProgress(completed.categoryId, result.actualDurationSeconds.toLong(), true, result.points)
        }
    }

    suspend fun failSession(sessionId: Long, reason: String) {
        database.withTransaction {
            val session = sessionDao.getById(sessionId) ?: return@withTransaction
            if (session.status != SessionStatus.ACTIVE.name) return@withTransaction

            val now = System.currentTimeMillis()
            val result = SessionResultCalculator.fail(
                plannedDurationMinutes = session.plannedDurationMinutes,
                startedAt = session.startedAt,
                now = now,
                reason = reason
            )
            val failed = session.copy(
                actualDurationSeconds = result.actualDurationSeconds,
                endedAt = now,
                status = result.sessionStatus,
                points = result.points,
                failReason = result.failReason
            )
            sessionDao.update(failed)
            createPlantIfNeeded(failed, result.plantStatus, result.growthStage, now)
            updateProgress(failed.categoryId, 0, false, 0)
        }
    }

    private suspend fun createPlantIfNeeded(
        session: FocusSessionEntity,
        plantStatus: String,
        growthStage: String,
        createdAt: Long
    ) {
        if (plantDao.getBySessionId(session.id) != null) return
        plantDao.insert(
            GardenPlantEntity(
                sessionId = session.id,
                categoryId = session.categoryId,
                plantType = plantTypeForDuration(session.plannedDurationMinutes),
                growthStage = growthStage,
                status = plantStatus,
                createdAt = createdAt
            )
        )
    }

    private suspend fun updateProgress(categoryId: Long, focusSeconds: Long, completed: Boolean, pointsToAdd: Int) {
        val current = progressDao.getByCategoryId(categoryId)
        val next = if (current == null) {
            GardenProgressEntity(
                categoryId = categoryId,
                level = 1,
                points = pointsToAdd,
                totalFocusSeconds = focusSeconds,
                completedSessions = if (completed) 1 else 0,
                failedSessions = if (completed) 0 else 1
            )
        } else {
            val nextPoints = current.points + pointsToAdd
            current.copy(
                points = nextPoints,
                level = (nextPoints / 100) + 1,
                totalFocusSeconds = current.totalFocusSeconds + focusSeconds,
                completedSessions = current.completedSessions + if (completed) 1 else 0,
                failedSessions = current.failedSessions + if (completed) 0 else 1
            )
        }
        progressDao.upsert(next)
    }

    private fun plantTypeForDuration(minutes: Int): String = when {
        minutes >= 60 -> "RARE_TREE"
        minutes >= 45 -> "BIG_TREE"
        minutes >= 25 -> "TREE"
        else -> "SMALL_PLANT"
    }
}
