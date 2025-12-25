package com.hidex.app.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.hidex.app.data.database.HideXDatabase
import com.hidex.app.data.database.SecureStorageManager
import com.hidex.app.data.model.InstalledApp
import com.hidex.app.data.model.ProtectedApp
import com.hidex.app.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Implementation of AppRepository.
 * Handles data operations for protected apps, installed apps, and password management.
 */
class AppRepositoryImpl(
    private val context: Context,
    private val database: HideXDatabase,
    private val secureStorage: SecureStorageManager
) : AppRepository {
    
    private val protectedAppDao = database.protectedAppDao()
    private val packageManager = context.packageManager
    
    // ===== Protected Apps =====
    
    override fun getProtectedApps(): Flow<List<ProtectedApp>> {
        return protectedAppDao.getAllProtectedApps()
    }
    
    override suspend fun getProtectedAppsList(): List<ProtectedApp> {
        return protectedAppDao.getAllProtectedAppsList()
    }
    
    override suspend fun isAppProtected(packageName: String): Boolean {
        return protectedAppDao.isAppProtected(packageName)
    }
    
    override suspend fun addProtectedApp(app: ProtectedApp) {
        protectedAppDao.insertProtectedApp(app)
    }
    
    override suspend fun removeProtectedApp(packageName: String) {
        protectedAppDao.deleteProtectedAppByPackage(packageName)
    }
    
    override suspend fun updateProtectedApps(apps: List<ProtectedApp>) {
        // Clear existing and add new ones
        protectedAppDao.deleteAllProtectedApps()
        protectedAppDao.insertProtectedApps(apps)
    }
    
    // ===== Installed Apps =====
    
    override suspend fun getInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        val protectedApps = getProtectedAppsList()
        val protectedPackageNames = protectedApps.map { it.packageName }.toSet()
        
        val installedApps = mutableListOf<InstalledApp>()
        
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            
            // Skip our own app
            if (packageName == context.packageName) continue
            
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                val icon = packageManager.getApplicationIcon(appInfo)
                val isProtected = packageName in protectedPackageNames
                
                installedApps.add(
                    InstalledApp(
                        packageName = packageName,
                        appName = appName,
                        icon = icon,
                        isProtected = isProtected
                    )
                )
            } catch (e: Exception) {
                // Skip apps that can't be accessed
                continue
            }
        }
        
        // Sort by app name
        installedApps.sortedBy { it.appName }
    }
    
    // ===== Password Management =====
    
    override fun hasPassword(): Boolean {
        return secureStorage.hasPassword()
    }
    
    override fun savePassword(password: String) {
        secureStorage.savePassword(password)
    }
    
    override fun verifyPassword(password: String): Boolean {
        return secureStorage.verifyPassword(password)
    }
    
    override fun isFirstLaunch(): Boolean {
        return secureStorage.isFirstLaunch()
    }
    
    // ===== Biometric =====
    
    override fun isBiometricEnabled(): Boolean {
        return secureStorage.isBiometricEnabled()
    }
    
    override fun setBiometricEnabled(enabled: Boolean) {
        secureStorage.setBiometricEnabled(enabled)
    }
}
