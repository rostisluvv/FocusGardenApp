package com.example.focusgarden.di

import android.content.Context
import com.example.focusgarden.data.database.AppDatabase
import com.example.focusgarden.data.datastore.SettingsDataStore
import com.example.focusgarden.data.repository.CategoryRepository
import com.example.focusgarden.data.repository.DemoDataSeeder
import com.example.focusgarden.data.repository.FocusSessionRepository
import com.example.focusgarden.data.repository.GardenRepository
import com.example.focusgarden.data.repository.SettingsRepository
import com.example.focusgarden.data.repository.StatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppContainer(context: Context) {
    private val database = AppDatabase.create(context)
    private val dataStore = SettingsDataStore(context)
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val categoryRepository = CategoryRepository(database.categoryDao())
    val focusSessionRepository = FocusSessionRepository(database)
    val gardenRepository = GardenRepository(database.gardenPlantDao())
    val statisticsRepository = StatisticsRepository(database)
    val settingsRepository = SettingsRepository(dataStore)
    private val demoDataSeeder = DemoDataSeeder(database)

    init {
        applicationScope.launch {
            categoryRepository.seedDefaultsIfNeeded()
            demoDataSeeder.seedIfEmpty()
        }
    }
}
