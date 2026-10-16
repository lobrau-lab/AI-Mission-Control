package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AgentSessionEntity
import com.example.data.database.AppDatabase
import com.example.data.database.JobAutomationEntity
import com.example.data.database.MemoryItemEntity
import com.example.data.database.OrchestratedTaskEntity
import com.example.data.database.ServerHealthEntity
import com.example.data.database.TerminalLogEntity
import com.example.data.repository.MissionControlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.data.database.OllamaInstanceEntity
import com.example.data.network.DiscoveredMdnsService

class MissionControlViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = MissionControlRepository(
        agentSessionDao = db.agentSessionDao(),
        terminalLogDao = db.terminalLogDao(),
        orchestratedTaskDao = db.orchestratedTaskDao(),
        jobAutomationDao = db.jobAutomationDao(),
        memoryItemDao = db.memoryItemDao(),
        serverHealthDao = db.serverHealthDao(),
        ollamaInstanceDao = db.ollamaInstanceDao()
    )

    val sessions: StateFlow<List<AgentSessionEntity>> = repository.sessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val jobs: StateFlow<List<JobAutomationEntity>> = repository.allJobsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memories: StateFlow<List<MemoryItemEntity>> = repository.allMemoryFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serverHealth: StateFlow<List<ServerHealthEntity>> = repository.allServerHealthFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orchestrations: StateFlow<List<OrchestratedTaskEntity>> = repository.allOrchestrationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ollamaInstances: StateFlow<List<OllamaInstanceEntity>> = repository.allOllamaInstancesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mdnsServices: StateFlow<List<DiscoveredMdnsService>> = repository.mdnsDiscoveredServicesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMdnsDiscovering: StateFlow<Boolean> = repository.isMdnsDiscoveringFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _selectedTTYSlot = MutableStateFlow(1)
    val selectedTTYSlot: StateFlow<Int> = _selectedTTYSlot.asStateFlow()

    val currentTTYLogs: StateFlow<List<TerminalLogEntity>> = _selectedTTYSlot
        .flatMapLatest { slot -> repository.getLogsForSlot(slot) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentTTYSession: StateFlow<AgentSessionEntity?> = _selectedTTYSlot
        .flatMapLatest { slot -> repository.getSessionForSlot(slot) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getLogsForSlot(slot: Int): Flow<List<TerminalLogEntity>> = repository.getLogsForSlot(slot)

    private val _isVoiceListening = MutableStateFlow(false)
    val isVoiceListening: StateFlow<Boolean> = _isVoiceListening.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.initializeSeedDataIfNeeded()
            repository.startMdnsDiscovery(application)
            startAgentBackgroundSimulation()
        }
    }

    fun selectTTYSlot(slot: Int) {
        _selectedTTYSlot.value = slot
    }

    fun sendCommandToSelectedTTY(command: String) {
        if (command.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            val slot = _selectedTTYSlot.value
            repository.sendCommandToTTY(slot, command)
        }
    }

    fun sendCommandToSlot(slot: Int, command: String) {
        if (command.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.sendCommandToTTY(slot, command)
        }
    }

    fun updateAgentConfig(slot: Int, role: String, model: String, hostMachine: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAgentRoleAndModel(slot, role, model, hostMachine)
        }
    }

    fun updateAutonomy(slot: Int, autonomyLevel: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateAutonomy(slot, autonomyLevel)
        }
    }

    fun triggerEmergencyStop(stopped: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setEmergencyStopAll(stopped)
        }
    }

    fun resolveApproval(logId: Long, slot: Int, approved: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resolveApproval(logId, slot, approved)
        }
    }

    fun addMemory(workspace: String, factText: String, source: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMemoryItem(workspace, factText, source)
        }
    }

    fun toggleMemoryPin(id: Long, currentPinned: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleMemoryPin(id, currentPinned)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMemory(id)
        }
    }

    fun createJob(title: String, trigger: String, command: String, requiresApproval: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createJob(title, trigger, command, requiresApproval)
        }
    }

    fun updateJobStatus(id: Long, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateJobStatus(id, status)
        }
    }

    fun deleteJob(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteJob(id)
        }
    }


    fun startOrchestrationGoal(objective: String, mode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startOrchestrationGoal(objective, mode)
        }
    }

    fun addOllamaInstance(name: String, endpointUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = "ollama-${System.currentTimeMillis()}"
            val instance = OllamaInstanceEntity(
                id = id,
                name = if (name.isBlank()) "Ollama Node ($endpointUrl)" else name,
                endpointUrl = endpointUrl,
                status = "TESTING",
                latencyMs = 0,
                availableModels = "llama3.3, qwen2.5-coder, mistral",
                activeAgentCount = 6,
                maxAgents = 6
            )
            repository.addOrUpdateOllamaInstance(instance)
            repository.pingOllamaInstance(id)
        }
    }

    fun pingOllamaInstance(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.pingOllamaInstance(id)
        }
    }

    fun deleteOllamaInstance(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteOllamaInstance(id)
        }
    }

    fun startMdnsDiscovery() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.startMdnsDiscovery(getApplication())
        }
    }

    fun triggerMdnsScan() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.triggerMdnsScan()
        }
    }

    fun connectMdnsService(service: DiscoveredMdnsService) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.connectMdnsServiceToOrchestra(service)
        }
    }

    fun scanLocalNetworkForOllama(subnetPrefix: String = "192.168.1") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.scanLocalNetworkForOllama(subnetPrefix)
        }
    }

    fun conductMultiOllamaOrchestra(
        objective: String,
        targetInstanceIds: List<String> = emptyList(),
        mode: String = "ROLE_BASED_6TTY"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.conductMultiNodeOrchestration(objective, targetInstanceIds, mode)
        }
    }

    fun executeSelfHealing(onResult: ((String) -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.executeSelfHealingRoutine()
            withContext(Dispatchers.Main) {
                onResult?.invoke(result)
            }
        }
    }

    fun toggleVoiceListening() {
        _isVoiceListening.value = !_isVoiceListening.value
    }

    private fun startAgentBackgroundSimulation() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                delay(3000)
                val currentSessions = sessions.value
                if (currentSessions.isNotEmpty()) {
                    // Update heartbeats for all running, non-emergency-stopped agents
                    currentSessions.filter { !it.isEmergencyStopped && (it.state == "RUNNING" || it.state == "COMPLETED") }.forEach { session ->
                        repository.updateAgentHeartbeat(session.ttySlot)
                    }

                    val activeSession = currentSessions.filter { !it.isEmergencyStopped && it.state == "RUNNING" }.randomOrNull()
                    if (activeSession != null) {
                        val slot = activeSession.ttySlot
                        val simOutputs = listOf(
                            "\u001B[32m[EXEC]\u001B[0m Compiled target binary in 1.42s (0 warnings)",
                            "\u001B[36m[NET]\u001B[0m Synced chunk 104 with Tailscale node ${activeSession.hostMachine}",
                            "\u001B[33m[GPU]\u001B[0m VRAM allocation: ${activeSession.memoryUsageMb + Random.nextInt(10, 50)}MB",
                            "\u001B[38;2;52;211;153m[HEARTBEAT]\u001B[0m Agent ${activeSession.agentName} heartbeat ping OK (${Random.nextInt(5, 18)}ms)",
                            "\u001B[35m[CONTEXT]\u001B[0m Sliding window retained ${activeSession.contextTokensUsed} tokens",
                            "Parsing AST for module: ${activeSession.workingDir}"
                        )
                        repository.appendTTYOutput(slot, simOutputs.random())
                    }
                }
            }
        }
    }
}
