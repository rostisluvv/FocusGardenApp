package com.example.focusgarden.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focusgarden.data.entity.FocusSessionEntity
import com.example.focusgarden.data.model.FocusSessionView
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: FocusSessionEntity): Long

    @Query("SELECT COUNT(*) FROM focus_sessions")
    suspend fun countAll(): Int

    @Update
    suspend fun update(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FocusSessionEntity?

    @Query("""
        SELECT fs.*, c.name AS categoryName, c.icon AS categoryIcon
        FROM focus_sessions fs
        JOIN categories c ON c.id = fs.categoryId
        WHERE fs.id = :id
        LIMIT 1
    """)
    fun observeViewById(id: Long): Flow<FocusSessionView?>

    @Query("""
        SELECT fs.*, c.name AS categoryName, c.icon AS categoryIcon
        FROM focus_sessions fs
        JOIN categories c ON c.id = fs.categoryId
        WHERE fs.id = :id
        LIMIT 1
    """)
    suspend fun getViewById(id: Long): FocusSessionView?

    @Query("""
        SELECT fs.*, c.name AS categoryName, c.icon AS categoryIcon
        FROM focus_sessions fs
        JOIN categories c ON c.id = fs.categoryId
        ORDER BY fs.startedAt DESC
        LIMIT :limit
    """)
    fun observeRecent(limit: Int): Flow<List<FocusSessionView>>

    @Query("""
        SELECT COALESCE(SUM(actualDurationSeconds), 0)
        FROM focus_sessions
        WHERE status = 'COMPLETED' AND startedAt >= :startMillis
    """)
    fun observeCompletedSecondsSince(startMillis: Long): Flow<Long>

    @Query("""
        SELECT COUNT(*)
        FROM focus_sessions
        WHERE status = 'COMPLETED' AND startedAt >= :startMillis
    """)
    fun observeCompletedCountSince(startMillis: Long): Flow<Int>
}
