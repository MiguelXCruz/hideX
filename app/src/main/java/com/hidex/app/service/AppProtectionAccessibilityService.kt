package com.hidex.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.hidex.app.data.database.HideXDatabase
import com.hidex.app.ui.screens.PasswordOverlayActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Accessibility Service that monitors app launches and shows password overlay for protected apps.
 * 
 * How it works:
 * 1. Listens to window state changes (when apps are opened)
 * 2. Extracts the package name of the opened app
 * 3. Checks if the app is in the protected list
 * 4. If protected, launches the password overlay activity
 * 
 * Security Considerations:
 * - Service runs in the background continuously
 * - Minimal battery impact - only processes window change events
 * - Does not read app content, only package names
 * - Overlay prevents interaction with protected app until unlocked
 */
class AppProtectionAccessibilityService : AccessibilityService() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var database: HideXDatabase? = null
    private var lastProtectedPackage: String? = null
    private var lastEventTime: Long = 0
    
    companion object {
        private const val TAG = "AppProtectionService"
        private const val EVENT_DEBOUNCE_MS = 1000 // Prevent multiple triggers
        
        // Keep track of service status
        @Volatile
        var isRunning = false
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        database = HideXDatabase.getDatabase(applicationContext)
        isRunning = true
        Log.d(TAG, "AppProtectionAccessibilityService created")
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        
        // Only process window state changed events
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        
        // Get the package name of the app that was opened
        val packageName = event.packageName?.toString() ?: return
        
        // Ignore our own app and system UI
        if (packageName == this.packageName || 
            packageName == "com.android.systemui" ||
            packageName == "android") {
            return
        }
        
        // Debounce: Prevent multiple triggers for the same app in quick succession
        val currentTime = System.currentTimeMillis()
        if (packageName == lastProtectedPackage && 
            currentTime - lastEventTime < EVENT_DEBOUNCE_MS) {
            return
        }
        
        // Check if this app is protected
        serviceScope.launch {
            try {
                val dao = database?.protectedAppDao() ?: return@launch
                val isProtected = dao.isAppProtected(packageName)
                
                if (isProtected) {
                    Log.d(TAG, "Protected app detected: $packageName")
                    lastProtectedPackage = packageName
                    lastEventTime = currentTime
                    showPasswordOverlay(packageName)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking protected app: ${e.message}", e)
            }
        }
    }
    
    /**
     * Show the password overlay for the protected app.
     */
    private fun showPasswordOverlay(packageName: String) {
        try {
            val intent = Intent(this, PasswordOverlayActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                putExtra(PasswordOverlayActivity.EXTRA_PACKAGE_NAME, packageName)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error showing password overlay: ${e.message}", e)
        }
    }
    
    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        Log.d(TAG, "AppProtectionAccessibilityService destroyed")
    }
    
    /**
     * Called when the service is connected/enabled.
     */
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "AppProtectionAccessibilityService connected and ready")
    }
}
