package com.example.focusgarden.ui.theme

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolverTest {
    @Test
    fun darkModeAlwaysUsesDarkPalette() {
        assertTrue(ThemeResolver.shouldUseDarkTheme("DARK", systemDark = false))
    }

    @Test
    fun lightModeAlwaysUsesLightPalette() {
        assertFalse(ThemeResolver.shouldUseDarkTheme("LIGHT", systemDark = true))
    }

    @Test
    fun systemModeFollowsSystemTheme() {
        assertTrue(ThemeResolver.shouldUseDarkTheme("SYSTEM", systemDark = true))
        assertFalse(ThemeResolver.shouldUseDarkTheme("SYSTEM", systemDark = false))
    }

    @Test
    fun unknownModeFallsBackToSystemTheme() {
        assertTrue(ThemeResolver.shouldUseDarkTheme("UNKNOWN", systemDark = true))
        assertFalse(ThemeResolver.shouldUseDarkTheme("UNKNOWN", systemDark = false))
    }
}
