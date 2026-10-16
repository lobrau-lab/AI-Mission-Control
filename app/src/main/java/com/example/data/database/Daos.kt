package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentSessionDao {
    @Query("SELECT * FROM agent_sessions ORDER BY ttySlot ASC")
    fun getAllSessionsFlow(): Flow<List<AgentSessionEntity>>

    @Query("SELECT * FROM agent_sessions WHERE ttySlot = :slot")
    fun getSessionBySlotFlow(slot: Int): Flow<AgentSessionEntity?>

    @Query("SELECT * FROM agent_sessions WHERE ttySlot = :slot")
    suspend fun getSessionBySlot(slot: Int): AgentSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSession(session: AgentSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessions: List<AgentSessionEntity>)

    @Query("UPDATE agent_sessions SET state = :state WHERE ttySlot = :slot")
    suspend fun updateState(slot: Int, state: String)

    @Query("UPDATE agent_sessions SET currentTask = :task, state = :state WHERE ttySlot = :slot")
    suspend fun updateTask(slot: Int, task: String, state: String)

    @Query("UPDATE agent_sessions SET autonomyLevel = :autonomy WHERE ttySlot = :slot")
    suspend fun updateAutonomy(slot: Int, autonomy: String)

    @Query("UPDATE agent_sessions SET modelName = :model, hostMachine = :host WHERE ttySlot = :slot")
    suspend fun updateRouting(slot: Int, model: String, host: String)

    @Query("UPDATE agent_sessions SET isEmergencyStopped = :stopped, state = 'INTERRUPTED'")
    suspend fun setEmergencyStopAll(stopped: Boolean)

    @Query("UPDATE agent_sessions SET lastHeartbeatMs = :timestamp WHERE ttySlot = :slot")
    suspend fun updateHeartbeat(slot: Int, timestamp: Long)
}

@Dao
interface TerminalLogDao {
    @Query("SELECT * FROM terminal_logs WHERE ttySlot = :slot ORDER BY id ASC")
    fun getLogsForSlotFlow(slot: Int): Flow<List<TerminalLogEntity>>

    @Query("SELECT * FROM terminal_logs ORDER BY id DESC LIMIT 100")
    fun getRecentLogsFlow(): Flow<List<TerminalLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TerminalLogEntity)

    @Query("DELETE FROM terminal_logs WHERE ttySlot = :slot")
    suspend fun clearLogsForSlot(slot: Int)

    @Query("UPDATE terminal_logs SET isApproved = :approved WHERE id = :logId")
    suspend fun setApproval(logId: Long, approved: Boolean)
}

@Dao
interface OrchestratedTaskDao {
    @Query("SELECT * FROM orchestrated_tasks ORDER BY createdAtMs DESC")
    fun getAllOrchestrationsFlow(): Flow<List<OrchestratedTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrchestration(task: OrchestratedTaskEntity)

    @Query("UPDATE orchestrated_tasks SET status = :status, consolidatedResult = :result WHERE id = :taskId")
    suspend fun updateStatusAndResult(taskId: String, status: String, result: String?)
}

@Dao
interface JobAutomationDao {
    @Query("SELECT * FROM job_automations ORDER BY createdAtMs DESC")
    fun getAllJobsFlow(): Flow<List<JobAutomationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobAutomationEntity)

    @Query("UPDATE job_automations SET status = :status WHERE id = :jobId")
    suspend fun updateJobStatus(jobId: Long, status: String)

    @Query("DELETE FROM job_automations WHERE id = :jobId")
    suspend fun deleteJob(jobId: Long)
}

@Dao
interface MemoryItemDao {
    @Query("SELECT * FROM memory_items ORDER BY isPinned DESC, timestampMs DESC")
    fun getAllMemoryItemsFlow(): Flow<List<MemoryItemEntity>>

    @Query("SELECT * FROM memory_items WHERE workspace = :workspace ORDER BY isPinned DESC, timestampMs DESC")
    fun getMemoryByWorkspaceFlow(workspace: String): Flow<List<MemoryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(item: MemoryItemEntity)

    @Query("UPDATE memory_items SET isPinned = :pinned WHERE id = :id")
    suspend fun togglePin(id: Long, pinned: Boolean)

    @Query("DELETE FROM memory_items WHERE id = :id")
    suspend fun deleteMemory(id: Long)
}

@Dao
interface ServerHealthDao {
    @Query("SELECT * FROM server_health ORDER BY serviceName ASC")
    fun getAllServerHealthFlow(): Flow<List<ServerHealthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServerHealth(servers: List<ServerHealthEntity>)

    @Query("UPDATE server_health SET status = :status, latencyMs = :latency, freeVramGb = :vram WHERE serviceName = :service")
    suspend fun updateServerStatus(service: String, status: String, latency: Int, vram: Float)
}

@Dao
interface OllamaInstanceDao {
    @Query("SELECT * FROM ollama_instances ORDER BY name ASC")
    fun getAllInstancesFlow(): Flow<List<OllamaInstanceEntity>>

    @Query("SELECT * FROM ollama_instances WHERE id = :id")
    suspend fun getInstanceById(id: String): OllamaInstanceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateInstance(instance: OllamaInstanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(instances: List<OllamaInstanceEntity>)

    @Query("DELETE FROM ollama_instances WHERE id = :id")
    suspend fun deleteInstance(id: String)

    @Query("UPDATE ollama_instances SET status = :status, latencyMs = :latency, availableModels = :models, lastPingMs = :lastPing WHERE id = :id")
    suspend fun updatePingResult(id: String, status: String, latency: Int, models: String, lastPing: Long = System.currentTimeMillis())
}
