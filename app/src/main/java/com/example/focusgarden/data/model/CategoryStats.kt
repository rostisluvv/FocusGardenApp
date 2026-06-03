package com.example.focusgarden.data.model

import androidx.room.ColumnInfo

data class CategoryStats(
    val categoryId: Long,
    @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryIcon") val categoryIcon: String,
    val totalFocusSeconds: Long,
    val completedSessions: Int,
    val failedSessions: Int,
    val points: Int
)
