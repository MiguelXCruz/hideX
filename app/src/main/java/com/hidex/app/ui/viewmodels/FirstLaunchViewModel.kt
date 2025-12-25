package com.hidex.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidex.app.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the first launch password setup screen.
 */
class FirstLaunchViewModel(private val repository: AppRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow<FirstLaunchUiState>(FirstLaunchUiState.Input)
    val uiState: StateFlow<FirstLaunchUiState> = _uiState
    
    fun createPassword(password: String, confirmPassword: String) {
        viewModelScope.launch {
            // Validate
            when {
                password.isEmpty() -> {
                    _uiState.value = FirstLaunchUiState.Error("Password cannot be empty")
                }
                password.length < 4 -> {
                    _uiState.value = FirstLaunchUiState.Error("Password must be at least 4 characters")
                }
                password != confirmPassword -> {
                    _uiState.value = FirstLaunchUiState.Error("Passwords don't match")
                }
                else -> {
                    repository.savePassword(password)
                    _uiState.value = FirstLaunchUiState.Success
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = FirstLaunchUiState.Input
    }
}

sealed class FirstLaunchUiState {
    object Input : FirstLaunchUiState()
    data class Error(val message: String) : FirstLaunchUiState()
    object Success : FirstLaunchUiState()
}
