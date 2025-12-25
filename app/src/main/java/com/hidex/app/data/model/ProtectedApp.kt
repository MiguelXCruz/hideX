package com.hidex.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a protected app in the database.
 * 
 * @property packageName The unique package name of the protected app
 * @property appName The display name of the app
 * @property addedTimestamp When the app was added to protection
 */
@Entity(tableName = "protected_apps")
data class ProtectedApp(
    @PrimaryKey
    val packageName: String,
    val appName: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)
