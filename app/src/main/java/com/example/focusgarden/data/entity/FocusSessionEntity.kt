package com.example.focusgarden.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "focus_sessions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("categoryId"), Index("startedAt"), Index("status")]
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val title: String? = null,
    val plannedDurationMinutes: Int,
    val actualDurationSeconds: Int = 0,
    val startedAt: Long,
    val endedAt: Long? = null,
    val status: String,
    val points: Int = 0,
    val failReason: String? = null
)
