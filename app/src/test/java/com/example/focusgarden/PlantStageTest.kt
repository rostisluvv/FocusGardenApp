package com.example.focusgarden

import com.example.focusgarden.model.PlantStage
import org.junit.Assert.assertEquals
import org.junit.Test

class PlantStageTest {
    @Test
    fun progressMapsToPlantStage() {
        assertEquals(PlantStage.SEED, PlantStage.fromProgress(0.1f))
        assertEquals(PlantStage.SPROUT, PlantStage.fromProgress(0.3f))
        assertEquals(PlantStage.SMALL_TREE, PlantStage.fromProgress(0.6f))
        assertEquals(PlantStage.BIG_TREE, PlantStage.fromProgress(0.8f))
        assertEquals(PlantStage.FULL_TREE, PlantStage.fromProgress(1f))
    }

    @Test
    fun progressBoundariesMapToNextPlantStage() {
        assertEquals(PlantStage.SEED, PlantStage.fromProgress(0f))
        assertEquals(PlantStage.SPROUT, PlantStage.fromProgress(0.25f))
        assertEquals(PlantStage.SMALL_TREE, PlantStage.fromProgress(0.50f))
        assertEquals(PlantStage.BIG_TREE, PlantStage.fromProgress(0.75f))
        assertEquals(PlantStage.FULL_TREE, PlantStage.fromProgress(1.25f))
    }
}
