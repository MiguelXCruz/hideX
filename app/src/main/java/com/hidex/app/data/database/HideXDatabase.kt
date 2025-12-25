package com.hidex.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hidex.app.data.model.ProtectedApp

/**
 * Room Database for hideX app.
 * Contains tables for protected apps and related data.
 */
@Database(
    entities = [ProtectedApp::class],
    version = 1,
    exportSchema = false
)
abstract class HideXDatabase : RoomDatabase() {
    
    abstract fun protectedAppDao(): ProtectedAppDao
    
    companion object {
        @Volatile
        private var INSTANCE: HideXDatabase? = null
        
        fun getDatabase(context: Context): HideXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HideXDatabase::class.java,
                    "hidex_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
