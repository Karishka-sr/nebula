package com.nebula.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nebula.app.data.NebulaRepository
import com.nebula.app.ui.screens.DashboardScreen
import com.nebula.app.ui.screens.HabitsScreen
import com.nebula.app.ui.screens.NotesScreen
import com.nebula.app.ui.screens.PomodoroScreen
import com.nebula.app.ui.screens.SettingsScreen
import com.nebula.app.ui.screens.StatsScreen
import com.nebula.app.ui.screens.TasksScreen
import com.nebula.app.ui.screens.WallpapersScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val TASKS = "tasks"
    const val NOTES = "notes"
    const val HABITS = "habits"
    const val POMODORO = "pomodoro"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val WALLPAPERS = "wallpapers"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, "Главная", Icons.Filled.Home),
    BottomNavItem(Routes.TASKS, "Задачи", Icons.Filled.Checklist),
    BottomNavItem(Routes.NOTES, "Заметки", Icons.AutoMirrored.Filled.Note),
    BottomNavItem(Routes.HABITS, "Привычки", Icons.Filled.CheckCircle),
    BottomNavItem(Routes.POMODORO, "Фокус", Icons.Filled.Timer)
)

@Composable
fun NebulaNavHost(
    modifier: Modifier = Modifier,
    repository: NebulaRepository
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                NebulaBottomBar(currentDestination = currentDestination, onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    repository = repository,
                    onOpenNotes = { navController.navigate(Routes.NOTES) },
                    onOpenStats = { navController.navigate(Routes.STATS) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenTasks = { navController.navigate(Routes.TASKS) }
                )
            }
            composable(Routes.TASKS) {
                TasksScreen(repository)
            }
            composable(Routes.NOTES) {
                NotesScreen(repository)
            }
            composable(Routes.HABITS) {
                HabitsScreen(repository)
            }
            composable(Routes.POMODORO) {
                PomodoroScreen(repository)
            }
            composable(Routes.STATS) {
                StatsScreen(repository)
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(repository, onOpenWallpapers = { navController.navigate(Routes.WALLPAPERS) })
            }
            composable(Routes.WALLPAPERS) {
                WallpapersScreen()
            }
        }
    }
}

@Composable
private fun NebulaBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = MaterialTheme.typography.labelSmall.fontSize) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}