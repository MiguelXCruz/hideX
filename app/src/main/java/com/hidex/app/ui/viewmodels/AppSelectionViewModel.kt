package com.hidex.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidex.app.data.model.InstalledApp
import com.hidex.app.data.model.ProtectedApp
import com.hidex.app.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the app selection screen.
 */
class AppSelectionViewModel(private val repository: AppRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AppSelectionUiState>(AppSelectionUiState.Loading)
    val uiState: StateFlow<AppSelectionUiState> = _uiState
    
    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps
    
    init {
        loadInstalledApps()
    }
    
    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.value = AppSelectionUiState.Loading
            try {
                val apps = repository.getInstalledApps()
                _uiState.value = AppSelectionUiState.Success(apps)
                
                // Set initially selected apps
                _selectedApps.value = apps.filter { it.isProtected }
                    .map { it.packageName }
                    .toSet()
            } catch (e: Exception) {
                _uiState.value = AppSelectionUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun toggleAppSelection(packageName: String) {
        val current = _selectedApps.value.toMutableSet()
        if (packageName in current) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _selectedApps.value = current
    }
    
    fun saveSelection(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is AppSelectionUiState.Success) {
                val selectedPackages = _selectedApps.value
                val protectedApps = currentState.apps
                    .filter { it.packageName in selectedPackages }
                    .map { ProtectedApp(it.packageName, it.appName) }
                
                repository.updateProtectedApps(protectedApps)
                onSuccess()
            }
        }
    }
}

sealed class AppSelectionUiState {
    object Loading : AppSelectionUiState()
    data class Success(val apps: List<InstalledApp>) : AppSelectionUiState()
    data class Error(val message: String) : AppSelectionUiState()
}
