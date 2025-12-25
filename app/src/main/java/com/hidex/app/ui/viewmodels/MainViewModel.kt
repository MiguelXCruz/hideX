package com.hidex.app.ui.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidex.app.data.model.ProtectedApp
import com.hidex.app.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen showing protected apps.
 */
class MainViewModel(private val repository: AppRepository) : ViewModel() {
    
    val protectedApps: StateFlow<List<ProtectedApp>> = repository.getProtectedApps()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    val isFirstLaunch: Boolean
        get() = repository.isFirstLaunch()
    
    fun removeProtectedApp(packageName: String) {
        viewModelScope.launch {
            repository.removeProtectedApp(packageName)
        }
    }
    
    fun launchApp(context: Context, packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}
