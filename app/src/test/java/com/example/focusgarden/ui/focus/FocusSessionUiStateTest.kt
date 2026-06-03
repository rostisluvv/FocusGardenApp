package com.example.focusgarden.ui.focus

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusSessionUiStateTest {
    @Test
    fun feedbackSettingsArePartOfFocusState() {
        val state = FocusSessionUiState(
            soundEnabled = false,
            vibrationEnabled = false
        )

        assertFalse(state.soundEnabled)
        assertFalse(state.vibrationEnabled)
    }

    @Test
    fun feedbackSettingsDefaultToEnabled() {
        val state = FocusSessionUiState()

        assertTrue(state.soundEnabled)
        assertTrue(state.vibrationEnabled)
    }
}
