package com.example.focusgarden.data.model

import androidx.room.ColumnInfo

data class GardenPlantView(
    val id: Long,
    val sessionId: Long,
    val categoryId: Long,
    val plantType: String,
    val growthStage: String,
    val status: String,
    val createdAt: Long,
    @ColumnInfo(name = "categoryName") val categoryName: String,
    @ColumnInfo(name = "categoryIcon") val categoryIcon: String,
    val plannedDurationMinutes: Int
)
