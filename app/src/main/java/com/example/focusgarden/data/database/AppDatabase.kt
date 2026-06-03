package com.example.focusgarden.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.focusgarden.data.entity.CategoryEntity
import com.example.focusgarden.data.entity.FocusSessionEntity
import com.example.focusgarden.data.entity.GardenPlantEntity
import com.example.focusgarden.data.entity.GardenProgressEntity

@Database(
    entities = [
        CategoryEntity::class,
        FocusSessionEntity::class,
        GardenPlantEntity::class,
        GardenProgressEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun gardenPlantDao(): GardenPlantDao
    abstract fun gardenProgressDao(): GardenProgressDao

    companion object {
        fun create(context: Context): AppDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "focus_garden.db"
        ).build()
    }
}
