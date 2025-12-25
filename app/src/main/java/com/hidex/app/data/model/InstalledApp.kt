package com.hidex.app.data.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed app on the device.
 * Used for displaying apps in the selection screen.
 * 
 * @property packageName The package name of the app
 * @property appName The display name of the app
 * @property icon The app icon drawable
 * @property isProtected Whether this app is currently protected
 */
data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isProtected: Boolean = false
)
