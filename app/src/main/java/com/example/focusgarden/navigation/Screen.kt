package com.example.focusgarden.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Focus : Screen("focus/{sessionId}") {
        fun createRoute(sessionId: Long) = "focus/$sessionId"
    }
    data object Result : Screen("result/{sessionId}") {
        fun createRoute(sessionId: Long) = "result/$sessionId"
    }
    data object Progress : Screen("progress")
    data object Settings : Screen("settings")
}
