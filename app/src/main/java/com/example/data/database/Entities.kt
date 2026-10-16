package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_sessions")
data class AgentSessionEntity(
    @PrimaryKey val ttySlot: Int, // 1 to 18 (up to 6 per Ollama serve instance)
    val agentName: String,
    val role: String,
    val hostMachine: String,
    val modelName: String,
    val state: String, // RUNNING, WAITING_INPUT, BLOCKED_APPROVAL, OFFLINE, COMPLETED, INTERRUPTED
    val autonomyLevel: String, // CHAT_ONLY, ASK_BEFORE_TOOLS, TRUSTED_TASKS, AUTONOMOUS
    val currentTask: String,
    val memoryUsageMb: Int,
    val contextTokensUsed: Int,
    val contextTokenLimit: Int,
    val workingDir: String,
    val lastHeartbeatMs: Long,
    val outputSeqNumber: Long,
    val isEmergencyStopped: Boolean = false,
    val lockedFile: String? = null,
    val ollamaInstanceId: String = "ollama-node-1",
    val endpointUrl: String = "http://192.168.1.10:11434"
)

@Entity(tableName = "ollama_instances")
data class OllamaInstanceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val endpointUrl: String, // e.g. "http://192.168.1.10:11434"
    val status: String, // ONLINE, LATENCY_LOW, OFFLINE, SCANNING
    val latencyMs: Int,
    val availableModels: String, // "llama3.3:70b, qwen2.5-coder:32b, mistral:7b"
    val activeAgentCount: Int = 6,
    val maxAgents: Int = 6,
    val freeVramGb: Float = 24.0f,
    val lastPingMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "terminal_logs")
data class TerminalLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ttySlot: Int,
    val timestampMs: Long,
    val logText: String,
    val logType: String, // STDOUT, STDERR, COMMAND_INPUT, SYSTEM_EVENT, APPROVAL_REQUIRED
    val command: String? = null,
    val requiresApproval: Boolean = false,
    val approvalCommand: String? = null,
    val isApproved: Boolean? = null
)

@Entity(tableName = "orchestrated_tasks")
data class OrchestratedTaskEntity(
    @PrimaryKey val id: String,
    val objective: String,
    val mode: String, // BROADCAST, SUBTASK_DELEGATION, COORDINATOR_LEADER
    val status: String, // PLANNING, IN_PROGRESS, CONSOLIDATING, COMPLETED, INTERRUPTED
    val createdAtMs: Long,
    val consolidatedResult: String? = null,
    val unresolvedDecisions: String = "" // Comma-separated or JSON list
)

@Entity(tableName = "job_automations")
data class JobAutomationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val triggerCondition: String, // Cron expression or event
    val command: String,
    val status: String, // QUEUED, EXECUTING, COMPLETED, PENDING_APPROVAL, FAILED
    val createdAtMs: Long,
    val diffPreview: String? = null,
    val approvalRequired: Boolean = false
)

@Entity(tableName = "memory_items")
data class MemoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workspace: String, // "Motel Lazarus", "Hyperion City", "Global Studio"
    val factText: String,
    val isPinned: Boolean = false,
    val beliefScore: Float = 0.95f,
    val sourceCitation: String? = null,
    val timestampMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "server_health")
data class ServerHealthEntity(
    @PrimaryKey val serviceName: String,
    val serviceType: String, // Ollama, MLX, ComfyUI, Tailscale, OpenClaw, GPU
    val ipAddress: String,
    val status: String, // ONLINE, LATENCY_LOW, DEGRADED, OFFLINE
    val latencyMs: Int,
    val freeVramGb: Float
)
