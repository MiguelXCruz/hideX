package com.hidex.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hidex.app.ui.screens.*
import com.hidex.app.ui.viewmodels.*

/**
 * Navigation routes for the app.
 */
sealed class Screen(val route: String) {
    object FirstLaunch : Screen("first_launch")
    object Main : Screen("main")
    object AppSelection : Screen("app_selection")
    object Settings : Screen("settings")
}

/**
 * Navigation graph for the app.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    firstLaunchViewModel: FirstLaunchViewModel,
    mainViewModel: MainViewModel,
    appSelectionViewModel: AppSelectionViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // First launch screen
        composable(Screen.FirstLaunch.route) {
            FirstLaunchScreen(
                viewModel = firstLaunchViewModel,
                onPasswordCreated = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.FirstLaunch.route) { inclusive = true }
                    }
                }
            )
        }
        
        // Main screen
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = mainViewModel,
                onNavigateToAppSelection = {
                    navController.navigate(Screen.AppSelection.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // App selection screen
        composable(Screen.AppSelection.route) {
            AppSelectionScreen(
                viewModel = appSelectionViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Settings screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
