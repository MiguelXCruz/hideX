package com.hidex.app.data.database

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manages secure storage of sensitive data like passwords using EncryptedSharedPreferences.
 * 
 * Security Features:
 * - Uses AES256 encryption for data
 * - Master key is stored in Android Keystore
 * - Encrypted both keys and values
 */
class SecureStorageManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    /**
     * Save the master password (hashed).
     */
    fun savePassword(password: String) {
        // In production, you should hash the password before storing
        // For now, we'll use a simple hash
        val hashedPassword = hashPassword(password)
        sharedPreferences.edit().putString(KEY_PASSWORD, hashedPassword).apply()
    }
    
    /**
     * Verify if the provided password matches the stored password.
     */
    fun verifyPassword(password: String): Boolean {
        val storedHash = sharedPreferences.getString(KEY_PASSWORD, null) ?: return false
        return storedHash == hashPassword(password)
    }
    
    /**
     * Check if a password has been set.
     */
    fun hasPassword(): Boolean {
        return sharedPreferences.contains(KEY_PASSWORD)
    }
    
    /**
     * Enable or disable biometric authentication.
     */
    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    /**
     * Check if biometric authentication is enabled.
     */
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    /**
     * Check if this is the first launch (no password set).
     */
    fun isFirstLaunch(): Boolean {
        return !hasPassword()
    }
    
    /**
     * Simple hash function for password.
     * In production, use a proper hashing algorithm like BCrypt or Argon2.
     */
    private fun hashPassword(password: String): String {
        return password.hashCode().toString() + password.length
    }
    
    /**
     * Clear all data (for testing or reset).
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    companion object {
        private const val PREFS_FILE_NAME = "hidex_secure_prefs"
        private const val KEY_PASSWORD = "master_password"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        
        @Volatile
        private var INSTANCE: SecureStorageManager? = null
        
        fun getInstance(context: Context): SecureStorageManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SecureStorageManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
