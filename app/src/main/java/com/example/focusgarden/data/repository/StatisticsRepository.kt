package com.example.focusgarden.data.repository

import com.example.focusgarden.data.database.AppDatabase
import com.example.focusgarden.data.model.CategoryStats
import com.example.focusgarden.data.model.FocusSessionView
import kotlinx.coroutines.flow.Flow

class StatisticsRepository(private val database: AppDatabase) {
    fun observeTotalFocusSeconds(): Flow<Long> = database.gardenProgressDao().observeTotalFocusSeconds()
    fun observeCompletedSessions(): Flow<Int> = database.gardenProgressDao().observeCompletedSessions()
    fun observeFailedSessions(): Flow<Int> = database.gardenProgressDao().observeFailedSessions()
    fun observeCategoryStats(): Flow<List<CategoryStats>> = database.gardenProgressDao().observeCategoryStats()
    fun observeRecentSessions(limit: Int = 20): Flow<List<FocusSessionView>> = database.focusSessionDao().observeRecent(limit)
    fun observeTodayFocusSeconds(startOfDayMillis: Long): Flow<Long> = database.focusSessionDao().observeCompletedSecondsSince(startOfDayMillis)
    fun observeTodayCompletedCount(startOfDayMillis: Long): Flow<Int> = database.focusSessionDao().observeCompletedCountSince(startOfDayMillis)
}
