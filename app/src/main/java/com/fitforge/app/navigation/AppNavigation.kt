package com.fitforge.app.navigation

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.fitforge.app.presentation.checkin.CheckInScreen
import com.fitforge.app.presentation.dashboard.DashboardScreen
import com.fitforge.app.presentation.nutrition.NutritionScreen
import com.fitforge.app.presentation.onboarding.OnboardingScreen
import com.fitforge.app.presentation.sleep.SleepScreen
import com.fitforge.app.presentation.workout.WorkoutPlanScreen
import com.fitforge.app.presentation.workout.WorkoutDetailScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object Workout : Screen("workout")
    object WorkoutDetail : Screen("workout/{planId}") {
        fun createRoute(planId: String) = "workout/$planId"
    }
    object Nutrition : Screen("nutrition")
    object Sleep : Screen("sleep")
    object CheckIn : Screen("checkin")
}

data class BottomNavItem(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Dashboard, "Home", Icons.Default.Home),
    BottomNavItem(Screen.Workout, "Workout", Icons.Default.FitnessCenter),
    BottomNavItem(Screen.Nutrition, "Nutrition", Icons.Default.Restaurant),
    BottomNavItem(Screen.Sleep, "Sleep", Icons.Default.Bedtime),
    BottomNavItem(Screen.CheckIn, "Check-in", Icons.Default.Mood)
)

@Composable
fun AppNavigation(startOnboarding: Boolean) {
    val navController = rememberNavController()
    val startDest = if (startOnboarding) Screen.Onboarding.route else Screen.Dashboard.route

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDest = navBackStackEntry?.destination
    val showBottomBar = bottomNavItems.any { it.screen.route == currentDest?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDest?.hierarchy?.any { it.route == item.screen.route } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onGoToWorkout = { navController.navigate(Screen.Workout.route) },
                    onGoToCheckIn = { navController.navigate(Screen.CheckIn.route) }
                )
            }
            composable(Screen.Workout.route) {
                WorkoutPlanScreen(onViewDetail = { planId ->
                    navController.navigate(Screen.WorkoutDetail.createRoute(planId))
                })
            }
            composable(Screen.WorkoutDetail.route) { backStack ->
                val planId = backStack.arguments?.getString("planId") ?: ""
                WorkoutDetailScreen(planId = planId, onBack = { navController.popBackStack() })
            }
            composable(Screen.Nutrition.route) { NutritionScreen() }
            composable(Screen.Sleep.route) { SleepScreen() }
            composable(Screen.CheckIn.route) {
                CheckInScreen(onDone = { navController.popBackStack() })
            }
        }
    }
}
