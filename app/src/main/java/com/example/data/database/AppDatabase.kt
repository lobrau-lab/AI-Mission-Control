package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AgentSessionEntity::class,
        TerminalLogEntity::class,
        OrchestratedTaskEntity::class,
        JobAutomationEntity::class,
        MemoryItemEntity::class,
        ServerHealthEntity::class,
        OllamaInstanceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agentSessionDao(): AgentSessionDao
    abstract fun terminalLogDao(): TerminalLogDao
    abstract fun orchestratedTaskDao(): OrchestratedTaskDao
    abstract fun jobAutomationDao(): JobAutomationDao
    abstract fun memoryItemDao(): MemoryItemDao
    abstract fun serverHealthDao(): ServerHealthDao
    abstract fun ollamaInstanceDao(): OllamaInstanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ai_mission_control_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
