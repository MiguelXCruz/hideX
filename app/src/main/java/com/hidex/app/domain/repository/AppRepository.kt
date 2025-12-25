package com.hidex.app.domain.repository

import com.hidex.app.data.model.InstalledApp
import com.hidex.app.data.model.ProtectedApp
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app-related operations.
 */
interface AppRepository {
    
    // Protected Apps
    fun getProtectedApps(): Flow<List<ProtectedApp>>
    suspend fun getProtectedAppsList(): List<ProtectedApp>
    suspend fun isAppProtected(packageName: String): Boolean
    suspend fun addProtectedApp(app: ProtectedApp)
    suspend fun removeProtectedApp(packageName: String)
    suspend fun updateProtectedApps(apps: List<ProtectedApp>)
    
    // Installed Apps
    suspend fun getInstalledApps(): List<InstalledApp>
    
    // Password Management
    fun hasPassword(): Boolean
    fun savePassword(password: String)
    fun verifyPassword(password: String): Boolean
    fun isFirstLaunch(): Boolean
    
    // Biometric
    fun isBiometricEnabled(): Boolean
    fun setBiometricEnabled(enabled: Boolean)
}
