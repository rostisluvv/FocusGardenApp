package com.example.focusgarden.ui.theme

import androidx.compose.ui.graphics.luminance
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeVisualsTest {
    @Test
    fun lightThemeUsesReadableLightSurfaces() {
        val visuals = FocusGardenThemeTokens.visuals(focusGardenColorScheme(darkTheme = false))

        assertTrue(visuals.backgroundGradient.all { it.luminance() > 0.78f })
        assertTrue(visuals.glassCardContainer.luminance() > 0.78f)
        assertTrue(visuals.bottomBarContainer.luminance() > 0.78f)
        assertTrue(visuals.sheetContainer.luminance() > 0.78f)
        assertTrue(visuals.primaryText.luminance() < 0.20f)
    }

    @Test
    fun darkThemeKeepsLowLightSurfaces() {
        val visuals = FocusGardenThemeTokens.visuals(focusGardenColorScheme(darkTheme = true))

        assertTrue(visuals.backgroundGradient.all { it.luminance() < 0.04f })
        assertTrue(visuals.glassCardContainer.luminance() < 0.06f)
        assertTrue(visuals.bottomBarContainer.luminance() < 0.05f)
        assertTrue(visuals.sheetContainer.luminance() < 0.05f)
        assertTrue(visuals.primaryText.luminance() > 0.80f)
    }
}
