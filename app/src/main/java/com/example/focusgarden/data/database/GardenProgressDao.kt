package com.example.focusgarden.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusgarden.data.entity.GardenProgressEntity
import com.example.focusgarden.data.model.CategoryStats
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenProgressDao {
    @Query("SELECT * FROM garden_progress WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getByCategoryId(categoryId: Long): GardenProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: GardenProgressEntity)

    @Query("""
        SELECT gp.categoryId, c.name AS categoryName, c.icon AS categoryIcon,
               gp.totalFocusSeconds, gp.completedSessions, gp.failedSessions, gp.points
        FROM garden_progress gp
        JOIN categories c ON c.id = gp.categoryId
        ORDER BY gp.totalFocusSeconds DESC, gp.points DESC
    """)
    fun observeCategoryStats(): Flow<List<CategoryStats>>

    @Query("SELECT COALESCE(SUM(totalFocusSeconds), 0) FROM garden_progress")
    fun observeTotalFocusSeconds(): Flow<Long>

    @Query("SELECT COALESCE(SUM(completedSessions), 0) FROM garden_progress")
    fun observeCompletedSessions(): Flow<Int>

    @Query("SELECT COALESCE(SUM(failedSessions), 0) FROM garden_progress")
    fun observeFailedSessions(): Flow<Int>
}
