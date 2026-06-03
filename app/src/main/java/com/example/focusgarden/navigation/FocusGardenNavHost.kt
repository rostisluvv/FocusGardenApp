package com.example.focusgarden.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.focusgarden.di.AppContainer
import com.example.focusgarden.ui.common.SimpleViewModelFactory
import com.example.focusgarden.ui.focus.FocusSessionScreen
import com.example.focusgarden.ui.focus.FocusSessionViewModel
import com.example.focusgarden.ui.home.HomeScreen
import com.example.focusgarden.ui.home.HomeViewModel
import com.example.focusgarden.ui.progress.ProgressScreen
import com.example.focusgarden.ui.progress.ProgressViewModel
import com.example.focusgarden.ui.result.ResultScreen
import com.example.focusgarden.ui.result.ResultViewModel
import com.example.focusgarden.ui.settings.SettingsScreen
import com.example.focusgarden.ui.settings.SettingsViewModel
import com.example.focusgarden.ui.theme.focusGardenVisuals

@Composable
fun FocusGardenNavHost(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                FocusGardenBottomBar(
                    currentRoute = currentRoute,
                    navController = navController
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                val vm: HomeViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        HomeViewModel(
                            categoryRepository = appContainer.categoryRepository,
                            focusSessionRepository = appContainer.focusSessionRepository,
                            statisticsRepository = appContainer.statisticsRepository,
                            settingsRepository = appContainer.settingsRepository
                        )
                    }
                )
                HomeScreen(
                    viewModel = vm,
                    onOpenFocus = { navController.navigate(Screen.Focus.createRoute(it)) }
                )
            }
            composable(
                route = Screen.Focus.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                val vm: FocusSessionViewModel = viewModel(
                    key = "focus_$sessionId",
                    factory = SimpleViewModelFactory {
                        FocusSessionViewModel(
                            sessionId = sessionId,
                            repository = appContainer.focusSessionRepository,
                            settingsRepository = appContainer.settingsRepository
                        )
                    }
                )
                FocusSessionScreen(
                    viewModel = vm,
                    onOpenResult = {
                        navController.navigate(Screen.Result.createRoute(it)) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Result.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                val vm: ResultViewModel = viewModel(
                    key = "result_$sessionId",
                    factory = SimpleViewModelFactory {
                        ResultViewModel(sessionId, appContainer.focusSessionRepository)
                    }
                )
                ResultScreen(
                    viewModel = vm,
                    onHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onGarden = {
                        navController.navigate(Screen.Progress.route) {
                            popUpTo(Screen.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(Screen.Progress.route) {
                val vm: ProgressViewModel = viewModel(
                    factory = SimpleViewModelFactory {
                        ProgressViewModel(
                            gardenRepository = appContainer.gardenRepository,
                            statisticsRepository = appContainer.statisticsRepository
                        )
                    }
                )
                ProgressScreen(vm)
            }
            composable(Screen.Settings.route) {
                val vm: SettingsViewModel = viewModel(
                    factory = SimpleViewModelFactory { SettingsViewModel(appContainer.settingsRepository) }
                )
                SettingsScreen(vm)
            }
        }
    }
}

@Composable
private fun FocusGardenBottomBar(
    currentRoute: String?,
    navController: NavHostController
) {
    val visuals = focusGardenVisuals()
    Box(modifier = Modifier.padding(horizontal = 34.dp, vertical = 10.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(34.dp),
            color = visuals.bottomBarContainer,
            tonalElevation = 0.dp,
            shadowElevation = 18.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentRoute == item.route
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (selected) visuals.navigationSelectedContainer else Color.Transparent
                            )
                            .clickable { navController.navigateTopLevel(item.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        NavigationGlyph(
                            route = item.route,
                            selected = selected
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationGlyph(route: String, selected: Boolean) {
    val visuals = focusGardenVisuals()
    val color = if (selected) visuals.accent else visuals.navigationUnselected
    Canvas(modifier = Modifier.size(27.dp)) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.09f, cap = StrokeCap.Round)

        when (route) {
            Screen.Home.route -> {
                drawCircle(
                    color = color,
                    center = Offset(w * 0.50f, h * 0.50f),
                    radius = w * 0.34f,
                    style = stroke
                )
                val play = Path().apply {
                    moveTo(w * 0.43f, h * 0.34f)
                    lineTo(w * 0.43f, h * 0.66f)
                    lineTo(w * 0.68f, h * 0.50f)
                    close()
                }
                drawPath(play, color = color)
            }
            Screen.Progress.route -> {
                drawLine(color, Offset(w * 0.50f, h * 0.84f), Offset(w * 0.50f, h * 0.42f), strokeWidth = w * 0.08f, cap = StrokeCap.Round)
                drawOval(
                    color = color,
                    topLeft = Offset(w * 0.20f, h * 0.24f),
                    size = Size(w * 0.34f, h * 0.25f),
                    style = stroke
                )
                drawOval(
                    color = color,
                    topLeft = Offset(w * 0.48f, h * 0.20f),
                    size = Size(w * 0.32f, h * 0.26f),
                    style = stroke
                )
                val barWidth = w * 0.10f
                drawRoundRect(color, Offset(w * 0.18f, h * 0.73f), Size(barWidth, h * 0.14f), cornerRadius = CornerRadius(5f, 5f))
                drawRoundRect(color, Offset(w * 0.32f, h * 0.65f), Size(barWidth, h * 0.22f), cornerRadius = CornerRadius(5f, 5f))
                drawRoundRect(color, Offset(w * 0.67f, h * 0.62f), Size(barWidth, h * 0.25f), cornerRadius = CornerRadius(5f, 5f))
                drawRoundRect(color, Offset(w * 0.81f, h * 0.54f), Size(barWidth, h * 0.33f), cornerRadius = CornerRadius(5f, 5f))
            }
            Screen.Settings.route -> {
                val outerRadius = w * 0.36f
                val innerRadius = w * 0.15f
                val cx = w * 0.50f
                val cy = h * 0.50f
                val hex = Path().apply {
                    repeat(6) { index ->
                        val angle = (-90.0 + index * 60.0) * PI / 180.0
                        val x = cx + cos(angle).toFloat() * outerRadius
                        val y = cy + sin(angle).toFloat() * outerRadius
                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                    }
                    close()
                }
                drawPath(hex, color = color, style = stroke)
                drawCircle(
                    color = color,
                    radius = innerRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = w * 0.085f, cap = StrokeCap.Round)
                )
            }
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    if (currentDestination?.route == route) return

    if (route == Screen.Home.route) {
        navigate(Screen.Home.route) {
            popUpTo(graph.findStartDestination().id) {
                inclusive = false
                saveState = false
            }
            launchSingleTop = true
            restoreState = false
        }
        return
    }

    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
