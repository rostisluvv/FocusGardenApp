package com.example.focusgarden.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.focusgarden.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC")
    suspend fun getAllOnce(): List<CategoryEntity>

    @Query("UPDATE categories SET name = :name, icon = :icon, color = :color WHERE sortOrder = :sortOrder AND isDefault = 1")
    suspend fun updateDefaultBySortOrder(sortOrder: Int, name: String, icon: String, color: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
}
