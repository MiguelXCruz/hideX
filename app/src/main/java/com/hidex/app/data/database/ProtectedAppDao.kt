package com.hidex.app.data.database

import androidx.room.*
import com.hidex.app.data.model.ProtectedApp
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for protected apps.
 * Provides methods to interact with the protected_apps table.
 */
@Dao
interface ProtectedAppDao {
    
    /**
     * Get all protected apps as a Flow for reactive updates.
     */
    @Query("SELECT * FROM protected_apps ORDER BY appName ASC")
    fun getAllProtectedApps(): Flow<List<ProtectedApp>>
    
    /**
     * Get all protected apps as a list (one-time query).
     */
    @Query("SELECT * FROM protected_apps")
    suspend fun getAllProtectedAppsList(): List<ProtectedApp>
    
    /**
     * Check if an app is protected.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM protected_apps WHERE packageName = :packageName)")
    suspend fun isAppProtected(packageName: String): Boolean
    
    /**
     * Insert a new protected app.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtectedApp(app: ProtectedApp)
    
    /**
     * Insert multiple protected apps.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtectedApps(apps: List<ProtectedApp>)
    
    /**
     * Delete a protected app.
     */
    @Delete
    suspend fun deleteProtectedApp(app: ProtectedApp)
    
    /**
     * Delete a protected app by package name.
     */
    @Query("DELETE FROM protected_apps WHERE packageName = :packageName")
    suspend fun deleteProtectedAppByPackage(packageName: String)
    
    /**
     * Delete all protected apps.
     */
    @Query("DELETE FROM protected_apps")
    suspend fun deleteAllProtectedApps()
}
