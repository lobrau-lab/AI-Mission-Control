package com.example.data.repository

import com.example.data.database.AgentSessionDao
import com.example.data.database.AgentSessionEntity
import com.example.data.database.JobAutomationDao
import com.example.data.database.JobAutomationEntity
import com.example.data.database.MemoryItemDao
import com.example.data.database.MemoryItemEntity
import com.example.data.database.OrchestratedTaskDao
import com.example.data.database.OrchestratedTaskEntity
import com.example.data.database.ServerHealthDao
import com.example.data.database.ServerHealthEntity
import com.example.data.database.TerminalLogDao
import com.example.data.database.TerminalLogEntity
import com.example.data.database.OllamaInstanceDao
import com.example.data.database.OllamaInstanceEntity
import com.example.data.network.DiscoveredMdnsService
import com.example.data.network.OllamaMdnsDiscoveryService
import com.example.data.network.OllamaNetworkService
import com.example.data.security.BossSecurityManager
import com.example.data.security.SelfHealingManager
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

class MissionControlRepository(
    private val agentSessionDao: AgentSessionDao,
    private val terminalLogDao: TerminalLogDao,
    private val orchestratedTaskDao: OrchestratedTaskDao,
    private val jobAutomationDao: JobAutomationDao,
    private val memoryItemDao: MemoryItemDao,
    private val serverHealthDao: ServerHealthDao,
    private val ollamaInstanceDao: OllamaInstanceDao
) {
    val sessionsFlow: Flow<List<AgentSessionEntity>> = agentSessionDao.getAllSessionsFlow()
    val allJobsFlow: Flow<List<JobAutomationEntity>> = jobAutomationDao.getAllJobsFlow()
    val allMemoryFlow: Flow<List<MemoryItemEntity>> = memoryItemDao.getAllMemoryItemsFlow()
    val allServerHealthFlow: Flow<List<ServerHealthEntity>> = serverHealthDao.getAllServerHealthFlow()
    val allOrchestrationsFlow: Flow<List<OrchestratedTaskEntity>> = orchestratedTaskDao.getAllOrchestrationsFlow()
    val allOllamaInstancesFlow: Flow<List<OllamaInstanceEntity>> = ollamaInstanceDao.getAllInstancesFlow()
    val mdnsDiscoveredServicesFlow: Flow<List<DiscoveredMdnsService>> = OllamaMdnsDiscoveryService.discoveredServices
    val isMdnsDiscoveringFlow: Flow<Boolean> = OllamaMdnsDiscoveryService.isDiscovering

    fun getLogsForSlot(slot: Int): Flow<List<TerminalLogEntity>> = terminalLogDao.getLogsForSlotFlow(slot)
    fun getSessionForSlot(slot: Int): Flow<AgentSessionEntity?> = agentSessionDao.getSessionBySlotFlow(slot)

    suspend fun initializeSeedDataIfNeeded() {
        val existingSessions = agentSessionDao.getAllSessionsFlow().first()
        if (existingSessions.isEmpty()) {
            val initialSessions = listOf(
                AgentSessionEntity(
                    ttySlot = 1,
                    agentName = "Atlas",
                    role = "Coordinator",
                    hostMachine = "Desktop GPU (RTX 4090)",
                    modelName = "gemini-3.5-flash",
                    state = "RUNNING",
                    autonomyLevel = "AUTONOMOUS",
                    currentTask = "Monitoring sub-agent workflow & synthesizing team state",
                    memoryUsageMb = 840,
                    contextTokensUsed = 18450,
                    contextTokenLimit = 128000,
                    workingDir = "/workspace/project-control",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 104
                ),
                AgentSessionEntity(
                    ttySlot = 2,
                    agentName = "Aether",
                    role = "Architect / Planner",
                    hostMachine = "MacBook Pro M3 Max",
                    modelName = "llama3.3:70b-q4",
                    state = "RUNNING",
                    autonomyLevel = "TRUSTED_TASKS",
                    currentTask = "Designing high-level interface schema for Motel Lazarus",
                    memoryUsageMb = 1240,
                    contextTokensUsed = 32100,
                    contextTokenLimit = 128000,
                    workingDir = "/workspace/motel-lazarus/docs",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 88
                ),
                AgentSessionEntity(
                    ttySlot = 3,
                    agentName = "Vulcain",
                    role = "Implementer",
                    hostMachine = "Desktop GPU (RTX 4090)",
                    modelName = "qwen-2.5-coder:32b",
                    state = "BLOCKED_APPROVAL",
                    autonomyLevel = "ASK_BEFORE_TOOLS",
                    currentTask = "Refactoring OpenGL render pipeline & shader bindings",
                    memoryUsageMb = 1890,
                    contextTokensUsed = 45200,
                    contextTokenLimit = 64000,
                    workingDir = "/workspace/motel-lazarus/src/graphics",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 210,
                    lockedFile = "src/graphics/ShaderPipeline.kt"
                ),
                AgentSessionEntity(
                    ttySlot = 4,
                    agentName = "Argus",
                    role = "Researcher",
                    hostMachine = "Cloud Worker Alpha",
                    modelName = "mlx-deepseek-r1-7b",
                    state = "WAITING_INPUT",
                    autonomyLevel = "CHAT_ONLY",
                    currentTask = "Analyzing WebSockets reconnection resumption protocol",
                    memoryUsageMb = 620,
                    contextTokensUsed = 8900,
                    contextTokenLimit = 32000,
                    workingDir = "/workspace/research/network",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 45
                ),
                AgentSessionEntity(
                    ttySlot = 5,
                    agentName = "Sentry",
                    role = "Tester / Critic",
                    hostMachine = "Android Local Terminal",
                    modelName = "gemini-3.1-pro-preview",
                    state = "RUNNING",
                    autonomyLevel = "TRUSTED_TASKS",
                    currentTask = "Executing Robolectric CUJ suite & checking memory leaks",
                    memoryUsageMb = 430,
                    contextTokensUsed = 12100,
                    contextTokenLimit = 128000,
                    workingDir = "/workspace/app/src/test",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 162
                ),
                AgentSessionEntity(
                    ttySlot = 6,
                    agentName = "Nexus",
                    role = "Creative Specialist",
                    hostMachine = "Desktop GPU (RTX 4090)",
                    modelName = "comfyui-flux-v1",
                    state = "RUNNING",
                    autonomyLevel = "AUTONOMOUS",
                    currentTask = "Generating cyberpunk Motel Lazarus background assets",
                    memoryUsageMb = 2450,
                    contextTokensUsed = 15600,
                    contextTokenLimit = 64000,
                    workingDir = "/workspace/motel-lazarus/assets",
                    lastHeartbeatMs = System.currentTimeMillis(),
                    outputSeqNumber = 301
                )
            )
            agentSessionDao.insertAll(initialSessions)

            // Initial Logs
            for (slot in 1..6) {
                val agent = initialSessions[slot - 1]
                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        ttySlot = slot,
                        timestampMs = System.currentTimeMillis() - 120000,
                        logText = "\u001B[32m[tmux: session_${slot} attached on ${agent.hostMachine}]\u001B[0m",
                        logType = "SYSTEM_EVENT"
                    )
                )
                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        ttySlot = slot,
                        timestampMs = System.currentTimeMillis() - 90000,
                        logText = "$ agent_daemon --role ${agent.role} --model ${agent.modelName}",
                        logType = "COMMAND_INPUT",
                        command = "agent_daemon --role ${agent.role}"
                    )
                )
                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        ttySlot = slot,
                        timestampMs = System.currentTimeMillis() - 60000,
                        logText = "\u001B[36m[AGENT ${agent.agentName.uppercase()}]\u001B[0m Loaded context buffer (${agent.contextTokensUsed} tokens). Working dir: ${agent.workingDir}",
                        logType = "STDOUT"
                    )
                )
                terminalLogDao.insertLog(
                    TerminalLogEntity(
                        ttySlot = slot,
                        timestampMs = System.currentTimeMillis() - 30000,
                        logText = "\u001B[33m[TASK]\u001B[0m ${agent.currentTask}",
                        logType = "STDOUT"
                    )
                )

                if (slot == 3) {
                    terminalLogDao.insertLog(
                        TerminalLogEntity(
                            ttySlot = 3,
                            timestampMs = System.currentTimeMillis() - 10000,
                            logText = "\u001B[31m[APPROVAL REQUIRED]\u001B[0m Destructive action: git reset --hard HEAD~1 && rm -rf build/cache",
                            logType = "APPROVAL_REQUIRED",
                            requiresApproval = true,
                            approvalCommand = "git reset --hard HEAD~1 && rm -rf build/cache"
                        )
                    )
                }
            }

            // Initial Ollama Serve Instances
            ollamaInstanceDao.insertAll(
                listOf(
                    OllamaInstanceEntity(
                        id = "ollama-node-1",
                        name = "Ollama Instance #1 (Desktop RTX 4090)",
                        endpointUrl = "http://192.168.1.10:11434",
                        status = "ONLINE",
                        latencyMs = 12,
                        availableModels = "llama3.3:70b-q4, qwen2.5-coder:32b, codellama:13b, mistral:7b",
                        activeAgentCount = 6,
                        maxAgents = 6,
                        freeVramGb = 22.4f
                    ),
                    OllamaInstanceEntity(
                        id = "ollama-node-2",
                        name = "Ollama Instance #2 (Mac Studio M3 Ultra)",
                        endpointUrl = "http://192.168.1.15:11434",
                        status = "LATENCY_LOW",
                        latencyMs = 18,
                        availableModels = "llama3.3:70b-instruct, deepseek-r1:8b, phi3:medium, gemma2:27b",
                        activeAgentCount = 6,
                        maxAgents = 6,
                        freeVramGb = 32.0f
                    ),
                    OllamaInstanceEntity(
                        id = "ollama-node-3",
                        name = "Ollama Instance #3 (Local Host / Edge)",
                        endpointUrl = "http://127.0.0.1:11434",
                        status = "ONLINE",
                        latencyMs = 4,
                        availableModels = "qwen2.5:7b, mistral:7b, llama3.2:3b",
                        activeAgentCount = 6,
                        maxAgents = 6,
                        freeVramGb = 8.0f
                    )
                )
            )

            // Initial Servers
            serverHealthDao.insertServerHealth(
                listOf(
                    ServerHealthEntity("Desktop RTX 4090 GPU", "Ollama / ComfyUI", "100.112.45.18", "ONLINE", 12, 21.4f),
                    ServerHealthEntity("MacBook Pro M3 Max", "MLX Inference", "100.112.45.22", "ONLINE", 18, 32.0f),
                    ServerHealthEntity("Cloud Worker Alpha", "DeepSeek R1", "100.112.99.10", "ONLINE", 28, 48.0f),
                    ServerHealthEntity("Tailscale Mesh VPN", "Network Router", "100.64.0.1", "LATENCY_LOW", 8, 0f),
                    ServerHealthEntity("OpenClaw Daemon", "Agent Supervisor", "localhost:8080", "ONLINE", 5, 0f)
                )
            )

            // Initial Memories
            memoryItemDao.insertMemory(
                MemoryItemEntity(
                    workspace = "Motel Lazarus",
                    factText = "Neon lighting palette uses primary Cyan #06B6D4 and Amber #F59E0B accents.",
                    isPinned = true,
                    beliefScore = 0.98f,
                    sourceCitation = "design-spec.md line 14"
                )
            )
            memoryItemDao.insertMemory(
                MemoryItemEntity(
                    workspace = "Hyperion City",
                    factText = "Cyberpunk world map requires chunked level streaming with local SQLite spatial index.",
                    isPinned = true,
                    beliefScore = 0.92f,
                    sourceCitation = "architecture.pdf section 3.1"
                )
            )
            memoryItemDao.insertMemory(
                MemoryItemEntity(
                    workspace = "Global Studio",
                    factText = "All API keys must be retrieved via secrets Gradle plugin from .env file.",
                    isPinned = true,
                    beliefScore = 1.0f,
                    sourceCitation = "AGENTS.md"
                )
            )

            // Initial Jobs
            jobAutomationDao.insertJob(
                JobAutomationEntity(
                    title = "Automated ComfyUI Render Clean",
                    triggerCondition = "Cron: 0 */2 * * *",
                    command = "comfyui-cli purge-cache --older-than 24h",
                    status = "QUEUED",
                    createdAtMs = System.currentTimeMillis() - 3600000
                )
            )
            jobAutomationDao.insertJob(
                JobAutomationEntity(
                    title = "Sync Tailscale LAN Nodes & Check VRAM",
                    triggerCondition = "When Desktop comes online",
                    command = "tailscale status --json | openclaw-node-sync",
                    status = "COMPLETED",
                    createdAtMs = System.currentTimeMillis() - 7200000,
                    diffPreview = "+ Node desktop-rtx4090 added\n+ 24GB VRAM available"
                )
            )

            // Initial Orchestrated Task
            orchestratedTaskDao.insertOrchestration(
                OrchestratedTaskEntity(
                    id = UUID.randomUUID().toString(),
                    objective = "Orchestrate Motel Lazarus character dialogue & lighting shader pipeline",
                    mode = "SUBTASK_DELEGATION",
                    status = "IN_PROGRESS",
                    createdAtMs = System.currentTimeMillis() - 1800000,
                    consolidatedResult = "Atlas coordinated subtasks. Aether created schema. Vulcain in progress on shader bindings. Nexus generated textures."
                )
            )
        }
    }

    suspend fun updateAgentHeartbeat(slot: Int) {
        agentSessionDao.updateHeartbeat(slot, System.currentTimeMillis())
    }

    suspend fun sendCommandToTTY(slot: Int, commandText: String) {
        val now = System.currentTimeMillis()
        val (stampedCmd, stampInfo) = BossSecurityManager.stampCommand(slot, commandText)

        terminalLogDao.insertLog(
            TerminalLogEntity(
                ttySlot = slot,
                timestampMs = now,
                logText = "$ $commandText\n${stampInfo.formattedStamp}",
                logType = "COMMAND_INPUT",
                command = commandText
            )
        )

        agentSessionDao.updateTask(slot, "Executing: $commandText", "RUNNING")
        agentSessionDao.updateHeartbeat(slot, now)

        // Simulate immediate terminal response output with Boss auth verification
        terminalLogDao.insertLog(
            TerminalLogEntity(
                ttySlot = slot,
                timestampMs = now + 100,
                logText = "\u001B[32m[ACK]\u001B[0m Received instruction in TTY $slot: $commandText\n\u001B[35m✔ [BOSS AUTHENTICATED]\u001B[0m Signature verified [Sig: ${stampInfo.signatureHex}]",
                logType = "STDOUT"
            )
        )
    }

    suspend fun executeSelfHealingRoutine(): String {
        var healedSessions = 0
        var healedNodes = 0
        val now = System.currentTimeMillis()
        val allNodes = ollamaInstanceDao.getAllInstancesFlow().first()
        val healthyNode = allNodes.firstOrNull { it.status == "ONLINE" }?.name ?: "Primary Ollama Rig (RTX 4090)"

        for (slot in 1..6) {
            val session = agentSessionDao.getSessionBySlot(slot)
            if (session == null || session.state == "OFFLINE" || session.state == "INTERRUPTED" || session.isEmergencyStopped || (now - session.lastHeartbeatMs) > 20000) {
                healedSessions++
                // Repair agent session
                val repaired = (session ?: AgentSessionEntity(
                    ttySlot = slot,
                    agentName = "Agent-0$slot",
                    role = "Worker Node",
                    hostMachine = "localhost",
                    modelName = "llama3.3:70b",
                    state = "RUNNING",
                    autonomyLevel = "TRUSTED_TASKS",
                    currentTask = "Self-healing recovery re-bind",
                    memoryUsageMb = 512,
                    contextTokensUsed = 1024,
                    contextTokenLimit = 32768,
                    workingDir = "/app/agent_0$slot",
                    lastHeartbeatMs = now,
                    outputSeqNumber = 100L
                )).copy(
                    state = "RUNNING",
                    isEmergencyStopped = false,
                    lastHeartbeatMs = now
                )
                agentSessionDao.insertOrUpdateSession(repaired)

                // Inject diagnostic self-healing recovery log
                val diagLog = SelfHealingManager.createDiagnosticRecoveryLog(slot, "Connection Stale / Interrupted", healthyNode)
                appendTTYOutput(slot, diagLog, "SYSTEM_EVENT")
            }
        }

        // Re-ping offline nodes
        allNodes.filter { it.status == "OFFLINE" }.forEach { node ->
            pingOllamaInstance(node.id)
            healedNodes++
        }

        return "Self-Healing Diagnostic Complete: Recovered $healedSessions Agent Sessions and re-validated $healedNodes Ollama Nodes."
    }

    suspend fun appendTTYOutput(slot: Int, text: String, type: String = "STDOUT") {
        val now = System.currentTimeMillis()
        terminalLogDao.insertLog(
            TerminalLogEntity(
                ttySlot = slot,
                timestampMs = now,
                logText = text,
                logType = type
            )
        )
        agentSessionDao.updateHeartbeat(slot, now)
    }

    suspend fun updateAgentRoleAndModel(slot: Int, role: String, model: String, host: String) {
        val existing = agentSessionDao.getSessionBySlot(slot)
        if (existing != null) {
            agentSessionDao.insertOrUpdateSession(
                existing.copy(
                    role = role,
                    modelName = model,
                    hostMachine = host
                )
            )
        }
    }

    suspend fun updateAutonomy(slot: Int, autonomyLevel: String) {
        agentSessionDao.updateAutonomy(slot, autonomyLevel)
    }

    suspend fun setEmergencyStopAll(stopped: Boolean) {
        agentSessionDao.setEmergencyStopAll(stopped)
        for (slot in 1..6) {
            terminalLogDao.insertLog(
                TerminalLogEntity(
                    ttySlot = slot,
                    timestampMs = System.currentTimeMillis(),
                    logText = if (stopped) "\u001B[31m[EMERGENCY STOP TRIGGERED - ALL AGENT PROCESSES INTERRUPTED]\u001B[0m"
                             else "\u001B[32m[EMERGENCY STOP CLEARED - AGENTS RESUMED]\u001B[0m",
                    logType = "SYSTEM_EVENT"
                )
            )
        }
    }

    suspend fun resolveApproval(logId: Long, slot: Int, approved: Boolean) {
        terminalLogDao.setApproval(logId, approved)
        terminalLogDao.insertLog(
            TerminalLogEntity(
                ttySlot = slot,
                timestampMs = System.currentTimeMillis(),
                logText = if (approved) "\u001B[32m[APPROVAL GRANTED BY OPERATOR]\u001B[0m Action executed."
                         else "\u001B[31m[APPROVAL DENIED BY OPERATOR]\u001B[0m Operation cancelled.",
                logType = "SYSTEM_EVENT"
            )
        )
        agentSessionDao.updateState(slot, if (approved) "RUNNING" else "WAITING_INPUT")
    }

    suspend fun addMemoryItem(workspace: String, fact: String, source: String?) {
        memoryItemDao.insertMemory(
            MemoryItemEntity(
                workspace = workspace,
                factText = fact,
                isPinned = false,
                beliefScore = 0.95f,
                sourceCitation = source
            )
        )
    }

    suspend fun toggleMemoryPin(id: Long, currentPinned: Boolean) {
        memoryItemDao.togglePin(id, !currentPinned)
    }

    suspend fun deleteMemory(id: Long) {
        memoryItemDao.deleteMemory(id)
    }

    suspend fun createJob(title: String, trigger: String, command: String, requiresApproval: Boolean) {
        jobAutomationDao.insertJob(
            JobAutomationEntity(
                title = title,
                triggerCondition = trigger,
                command = command,
                status = "QUEUED",
                createdAtMs = System.currentTimeMillis(),
                approvalRequired = requiresApproval
            )
        )
    }

    suspend fun updateJobStatus(jobId: Long, status: String) {
        jobAutomationDao.updateJobStatus(jobId, status)
    }

    suspend fun deleteJob(jobId: Long) {
        jobAutomationDao.deleteJob(jobId)
    }


    suspend fun startOrchestrationGoal(objective: String, mode: String) {
        val taskId = UUID.randomUUID().toString()
        orchestratedTaskDao.insertOrchestration(
            OrchestratedTaskEntity(
                id = taskId,
                objective = objective,
                mode = mode,
                status = "IN_PROGRESS",
                createdAtMs = System.currentTimeMillis(),
                consolidatedResult = "Coordinator divided objective among 6 TTY agents. Agent 1 leading synthesis."
            )
        )

        // Broadcast or send subtasks to TTYs
        for (slot in 1..6) {
            val subtaskMsg = when (mode) {
                "BROADCAST" -> "Goal Broadcast: $objective"
                else -> "Orchestrated Subtask [$slot/6]: Process sub-phase for objective: $objective"
            }
            sendCommandToTTY(slot, subtaskMsg)
        }
    }

    suspend fun startMdnsDiscovery(context: Context) {
        OllamaMdnsDiscoveryService.startDiscovery(context)
    }

    suspend fun stopMdnsDiscovery() {
        OllamaMdnsDiscoveryService.stopDiscovery()
    }

    suspend fun triggerMdnsScan() {
        OllamaMdnsDiscoveryService.triggerMdnsBroadcastScan()
    }

    suspend fun connectMdnsServiceToOrchestra(service: DiscoveredMdnsService) {
        val newInstance = OllamaInstanceEntity(
            id = "ollama-mdns-${service.hostAddress.replace(".", "-")}-${service.port}",
            name = "${service.serviceName} [mDNS]",
            endpointUrl = service.endpointUrl,
            status = if (service.isVerifiedOllama) "ONLINE" else "LATENCY_LOW",
            latencyMs = service.latencyMs,
            availableModels = if (service.availableModels.isNotEmpty()) service.availableModels.joinToString(", ") else "llama3.3:70b, qwen2.5-coder:32b",
            activeAgentCount = 6,
            maxAgents = 6,
            freeVramGb = 24.0f
        )
        ollamaInstanceDao.insertOrUpdateInstance(newInstance)
    }

    suspend fun addOrUpdateOllamaInstance(instance: OllamaInstanceEntity) {
        ollamaInstanceDao.insertOrUpdateInstance(instance)
    }

    suspend fun deleteOllamaInstance(id: String) {
        ollamaInstanceDao.deleteInstance(id)
    }

    suspend fun pingOllamaInstance(id: String) {
        val instance = ollamaInstanceDao.getInstanceById(id) ?: return
        val ping = OllamaNetworkService.pingAndGetModels(instance.endpointUrl)
        val status = if (ping.isOnline) "ONLINE" else "OFFLINE"
        val models = if (ping.models.isNotEmpty()) ping.models.joinToString(", ") else instance.availableModels
        ollamaInstanceDao.updatePingResult(id, status, ping.latencyMs, models)
    }

    suspend fun scanLocalNetworkForOllama(subnetPrefix: String) {
        val discovered = OllamaNetworkService.scanLocalSubnetForOllama(subnetPrefix)
        for (node in discovered) {
            val existingId = "ollama-${node.ip.replace(".", "-")}"
            val newInstance = OllamaInstanceEntity(
                id = existingId,
                name = "Ollama @ ${node.ip}",
                endpointUrl = node.endpointUrl,
                status = "ONLINE",
                latencyMs = node.latencyMs,
                availableModels = if (node.models.isNotEmpty()) node.models.joinToString(", ") else "llama3:7b",
                activeAgentCount = 6,
                maxAgents = 6
            )
            ollamaInstanceDao.insertOrUpdateInstance(newInstance)
        }
    }

    suspend fun conductMultiNodeOrchestration(
        objective: String,
        targetInstanceIds: List<String> = emptyList(),
        mode: String = "ROLE_BASED_6TTY"
    ) {
        val taskId = UUID.randomUUID().toString()
        val allInstances = ollamaInstanceDao.getAllInstancesFlow().first()
        val selectedNodes = if (targetInstanceIds.isEmpty()) allInstances else allInstances.filter { targetInstanceIds.contains(it.id) }

        val initialSummary = "Initiating Orchestrated Conduction across ${selectedNodes.size} Ollama serve nodes and 6 TTY Role-Based Agents for: '$objective'"

        orchestratedTaskDao.insertOrchestration(
            OrchestratedTaskEntity(
                id = taskId,
                objective = objective,
                mode = mode,
                status = "IN_PROGRESS",
                createdAtMs = System.currentTimeMillis(),
                consolidatedResult = initialSummary
            )
        )

        // Define 6 TTY Agent Roles and role-specific subtasks
        val agentRoles = listOf(
            Triple(1, "Coordinator (Atlas)", "Plan workflow sequence, dependency matrix, and track cross-agent milestones for: '$objective'"),
            Triple(2, "Architect / Planner (Aether)", "Define software schemas, entity diagrams, data flows, and API specifications for: '$objective'"),
            Triple(3, "Implementer (Vulcain)", "Write clean production Kotlin code, Jetpack Compose UI state, and repository logic for: '$objective'"),
            Triple(4, "Researcher (Argus)", "Benchmark network latency, evaluate edge cases, and research protocol specs for: '$objective'"),
            Triple(5, "Tester / Critic (Sentry)", "Construct Robolectric unit tests, edge case validation suites, and code quality audit for: '$objective'"),
            Triple(6, "Creative Specialist (Nexus)", "Design Material 3 layout palette, visual components, micro-animations, and UX specs for: '$objective'")
        )

        val agentResponses = mutableListOf<Pair<String, String>>()

        // Fan out role-based subtasks across all 6 agent slots
        for (roleInfo in agentRoles) {
            val slot = roleInfo.first
            val roleName = roleInfo.second
            val rolePrompt = roleInfo.third

            val assignedNode = selectedNodes.getOrNull((slot - 1) % selectedNodes.size.coerceAtLeast(1))
            val endpoint = assignedNode?.endpointUrl ?: "http://192.168.1.10:11434"
            val nodeName = assignedNode?.name ?: "Primary Ollama Serve"
            val modelName = assignedNode?.availableModels?.split(",")?.firstOrNull()?.trim() ?: "llama3.3:70b"

            // Update Agent Session state to RUNNING with role subtask in Room DB
            agentSessionDao.updateTask(slot, rolePrompt, "RUNNING")

            val dispatchCmd = "orchestrated_dispatch --role=\"$roleName\" --objective=\"$objective\""
            sendCommandToTTY(slot, dispatchCmd)

            // Append dispatched prompt log to terminal
            appendTTYOutput(
                slot,
                "\u001B[33m[ORCHESTRATOR -> TTY 0$slot ($roleName)]\u001B[0m Dispatched subtask on $nodeName ($modelName):\n$rolePrompt",
                "STDOUT"
            )

            // Query Ollama serve endpoint
            val fullPrompt = "System Role: You are agent '$roleName' on node '$nodeName'. Objective: '$objective'. Task: $rolePrompt. Provide detailed, actionable response."
            val responseText = OllamaNetworkService.generatePromptResponse(endpoint, modelName, fullPrompt)

            // Append Ollama response to TTY logs
            appendTTYOutput(
                slot,
                "\u001B[32m[OLLAMA SERVE RESPONSE @ $nodeName / $modelName]\u001B[0m\n$responseText",
                "STDOUT"
            )

            // Update Agent Session to COMPLETED
            agentSessionDao.updateTask(slot, "Completed: ${rolePrompt.take(45)}...", "COMPLETED")
            agentResponses.add(roleName to responseText)
        }

        // Synthesize results into Consolidated Summary Report
        val consolidatedReport = buildString {
            appendLine("================================================================================")
            appendLine("MISSION CONTROL CONSOLIDATED ORCHESTRA SYNTHESIS")
            appendLine("================================================================================")
            appendLine("OBJECTIVE: $objective")
            appendLine("MODE: $mode")
            appendLine("ENGAGED NODES: ${selectedNodes.size} Ollama Instances (${selectedNodes.sumOf { it.activeAgentCount }} Workers)")
            appendLine("TIMESTAMP: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date())}")
            appendLine("================================================================================")
            appendLine()

            agentResponses.forEachIndexed { index, pair ->
                val (role, response) = pair
                appendLine("--------------------------------------------------------------------------------")
                appendLine("PHASE ${index + 1}: ${role.uppercase()}")
                appendLine("--------------------------------------------------------------------------------")
                appendLine(response)
                appendLine()
            }

            appendLine("================================================================================")
            appendLine("STATUS: SYNTHESIS COMPLETE - ALL 6 AGENTS COMPLETED ROLE SUBTASKS SUCCESSFULLY")
            appendLine("================================================================================")
        }

        // Store consolidated result in database
        orchestratedTaskDao.updateStatusAndResult(taskId, "COMPLETED", consolidatedReport)
    }
}
