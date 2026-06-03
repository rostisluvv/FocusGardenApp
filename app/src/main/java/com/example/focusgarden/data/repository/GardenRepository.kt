package com.example.focusgarden.data.repository

import com.example.focusgarden.data.database.GardenPlantDao
import com.example.focusgarden.data.model.GardenPlantView
import kotlinx.coroutines.flow.Flow

class GardenRepository(private val dao: GardenPlantDao) {
    fun observePlants(): Flow<List<GardenPlantView>> = dao.observeAllPlants()
}
