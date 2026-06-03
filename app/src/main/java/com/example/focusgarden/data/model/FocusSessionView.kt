package com.example.focusgarden.data.model

import androidx.room.ColumnInfo

data class FocusSessionView(
    val id: Long,
    val categoryId: Long,
    val title: String?,
    val plannedDurationMinutes: Int,
    val actualDurationSeconds: Int,
    val startedAt: Long,
    val endedAt: Long?,
    val status: String,
    val points: Int,
    val failReason: String?,
    @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryIcon") val categoryIcon: String
)
