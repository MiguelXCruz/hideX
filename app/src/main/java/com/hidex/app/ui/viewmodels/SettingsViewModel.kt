package com.hidex.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidex.app.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the settings screen.
 */
class SettingsViewModel(private val repository: AppRepository) : ViewModel() {
    
    private val _biometricEnabled = MutableStateFlow(repository.isBiometricEnabled())
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled
    
    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Input)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState
    
    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            repository.setBiometricEnabled(enabled)
            _biometricEnabled.value = enabled
        }
    }
    
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            when {
                currentPassword.isEmpty() || newPassword.isEmpty() -> {
                    _changePasswordState.value = ChangePasswordState.Error("Fields cannot be empty")
                }
                !repository.verifyPassword(currentPassword) -> {
                    _changePasswordState.value = ChangePasswordState.Error("Current password is incorrect")
                }
                newPassword.length < 4 -> {
                    _changePasswordState.value = ChangePasswordState.Error("Password must be at least 4 characters")
                }
                newPassword != confirmPassword -> {
                    _changePasswordState.value = ChangePasswordState.Error("New passwords don't match")
                }
                else -> {
                    repository.savePassword(newPassword)
                    _changePasswordState.value = ChangePasswordState.Success
                }
            }
        }
    }
    
    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Input
    }
}

sealed class ChangePasswordState {
    object Input : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
    object Success : ChangePasswordState()
}
