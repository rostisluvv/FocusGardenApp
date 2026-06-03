package com.example.focusgarden.ui.theme

import com.example.focusgarden.model.ThemeMode

object ThemeResolver {
    fun shouldUseDarkTheme(mode: String, systemDark: Boolean): Boolean {
        return when (mode.uppercase()) {
            ThemeMode.DARK.name -> true
            ThemeMode.LIGHT.name -> false
            else -> systemDark
        }
    }
}
