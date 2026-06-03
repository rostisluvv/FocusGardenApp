package com.example.focusgarden.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusgarden.data.entity.GardenPlantEntity
import com.example.focusgarden.data.model.GardenPlantView
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenPlantDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(plant: GardenPlantEntity): Long

    @Query("SELECT * FROM garden_plants WHERE sessionId = :sessionId LIMIT 1")
    suspend fun getBySessionId(sessionId: Long): GardenPlantEntity?

    @Query("""
        SELECT gp.*, c.name AS categoryName, c.icon AS categoryIcon, fs.plannedDurationMinutes
        FROM garden_plants gp
        JOIN categories c ON c.id = gp.categoryId
        JOIN focus_sessions fs ON fs.id = gp.sessionId
        ORDER BY gp.createdAt DESC
    """)
    fun observeAllPlants(): Flow<List<GardenPlantView>>
}
