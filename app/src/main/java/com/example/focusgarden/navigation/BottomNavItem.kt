package com.example.focusgarden.navigation

import androidx.compose.runtime.Immutable

@Immutable
data class BottomNavItem(
    val route: String,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Главная"
    ),
    BottomNavItem(
        route = Screen.Progress.route,
        label = "Прогресс"
    ),
    BottomNavItem(
        route = Screen.Settings.route,
        label = "Настройки"
    )
)
