package com.example.focusgarden.model

enum class PlantStage(val emoji: String, val title: String) {
    SEED("SEED", "Семя"),
    SPROUT("SPROUT", "Росток"),
    SMALL_TREE("SMALL_TREE", "Молодое растение"),
    BIG_TREE("BIG_TREE", "Растущее дерево"),
    FULL_TREE("FULL_TREE", "Выращенное дерево"),
    WITHERED_TREE("WITHERED_TREE", "Засохшее растение");

    companion object {
        fun fromProgress(progress: Float): PlantStage = when {
            progress >= 1f -> FULL_TREE
            progress >= 0.75f -> BIG_TREE
            progress >= 0.50f -> SMALL_TREE
            progress >= 0.25f -> SPROUT
            else -> SEED
        }
    }
}
