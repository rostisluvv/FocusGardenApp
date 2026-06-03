package com.example.focusgarden.data.repository

import com.example.focusgarden.data.database.CategoryDao
import com.example.focusgarden.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDao) {
    fun observeCategories(): Flow<List<CategoryEntity>> = dao.observeAll()

    suspend fun seedDefaultsIfNeeded() {
        val defaults = listOf(
            CategoryEntity(name = "Тело", icon = "BODY", color = "#37E68B", sortOrder = 1),
            CategoryEntity(name = "Разум", icon = "MIND", color = "#7CFFB2", sortOrder = 2),
            CategoryEntity(name = "Дело", icon = "DO", color = "#C9FF69", sortOrder = 3),
            CategoryEntity(name = "Порядок", icon = "ORDER", color = "#72E6D0", sortOrder = 4),
            CategoryEntity(name = "Люди", icon = "PEOPLE", color = "#A7F28A", sortOrder = 5)
        )

        if (dao.count() == 0) {
            dao.insertAll(defaults)
        } else {
            defaults.forEach { item ->
                dao.updateDefaultBySortOrder(
                    sortOrder = item.sortOrder,
                    name = item.name,
                    icon = item.icon,
                    color = item.color
                )
            }
        }
    }
}
