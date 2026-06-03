package com.example.focusgarden.data.datastore

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRulesTest {
    @Test
    fun defaultSettingsMatchFocusSessionDefaults() {
        val settings = AppSettings()

        assertEquals(25, settings.defaultDurationMinutes)
        assertTrue(settings.soundEnabled)
        assertTrue(settings.vibrationEnabled)
        assertFalse(settings.strictModeEnabled)
        assertEquals("SYSTEM", settings.themeMode)
    }

    @Test
    fun durationIsLimitedToSupportedOptions() {
        assertEquals(25, SettingsRules.normalizeDuration(17))
        assertEquals(45, SettingsRules.normalizeDuration(45))
    }

    @Test
    fun themeModeIsLimitedToKnownValues() {
        assertEquals("SYSTEM", SettingsRules.normalizeThemeMode("unknown"))
        assertEquals("LIGHT", SettingsRules.normalizeThemeMode("LIGHT"))
        assertEquals("DARK", SettingsRules.normalizeThemeMode("DARK"))
    }
}
