package com.hidex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hidex.app.data.database.HideXDatabase
import com.hidex.app.data.database.SecureStorageManager
import com.hidex.app.data.repository.AppRepositoryImpl
import com.hidex.app.ui.navigation.NavGraph
import com.hidex.app.ui.navigation.Screen
import com.hidex.app.ui.theme.HideXTheme
import com.hidex.app.ui.viewmodels.*

/**
 * Main Activity - Entry point of the app.
 */
class MainActivity : ComponentActivity() {
    
    private lateinit var repository: AppRepositoryImpl
    private lateinit var firstLaunchViewModel: FirstLaunchViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var appSelectionViewModel: AppSelectionViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize repository and view models
        initializeDependencies()
        
        setContent {
            HideXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Determine start destination based on whether password is set
                    val startDestination = if (repository.isFirstLaunch()) {
                        Screen.FirstLaunch.route
                    } else {
                        Screen.Main.route
                    }
                    
                    NavGraph(
                        navController = navController,
                        startDestination = startDestination,
                        firstLaunchViewModel = firstLaunchViewModel,
                        mainViewModel = mainViewModel,
                        appSelectionViewModel = appSelectionViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
    
    /**
     * Initialize dependencies.
     * In a production app, you would use Dependency Injection (Hilt/Koin).
     */
    private fun initializeDependencies() {
        val database = HideXDatabase.getDatabase(this)
        val secureStorage = SecureStorageManager.getInstance(this)
        repository = AppRepositoryImpl(this, database, secureStorage)
        
        // Initialize ViewModels
        firstLaunchViewModel = FirstLaunchViewModel(repository)
        mainViewModel = MainViewModel(repository)
        appSelectionViewModel = AppSelectionViewModel(repository)
        settingsViewModel = SettingsViewModel(repository)
    }
}
