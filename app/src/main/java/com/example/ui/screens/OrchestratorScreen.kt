package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EmergencyStopBanner
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCardActive
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPillBg
import com.example.ui.theme.SleekPillText
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleContainer
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekTerminalBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTerminalGreen
import com.example.ui.viewmodel.MissionControlViewModel

@Composable
fun OrchestratorScreen(
    viewModel: MissionControlViewModel
) {
    val context = LocalContext.current
    val sessions by viewModel.sessions.collectAsState()
    val orchestrations by viewModel.orchestrations.collectAsState()
    val ollamaNodes by viewModel.ollamaInstances.collectAsState()
    val mdnsServices by viewModel.mdnsServices.collectAsState()
    val isMdnsDiscovering by viewModel.isMdnsDiscovering.collectAsState()
    val isStopped = sessions.any { it.isEmergencyStopped }

    var objectiveInput by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("MULTI_OLLAMA_ORCHESTRA") }
    var showAddNodeDialog by remember { mutableStateOf(false) }
    var newHostName by remember { mutableStateOf("") }
    var newHostUrl by remember { mutableStateOf("http://192.168.1.50:11434") }
    var subnetInput by remember { mutableStateOf("192.168.1") }
    var showSubnetScanDialog by remember { mutableStateOf(false) }

    val activeOrchestration = orchestrations.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(12.dp)
    ) {
        EmergencyStopBanner(
            isStopped = isStopped,
            onToggleEmergencyStop = { viewModel.triggerEmergencyStop(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Orchestra Header Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SleekCard)
                .border(1.dp, SleekCardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SleekPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Router, contentDescription = "Orchestra", tint = SleekPurpleContainer)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MULTI-OLLAMA CONDUCTOR",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${ollamaNodes.size} Instances Online • ${ollamaNodes.sumOf { it.activeAgentCount }} Workers Connected",
                        fontSize = 10.sp,
                        color = SleekPurple
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Button(
                    onClick = {
                        viewModel.executeSelfHealing { report ->
                            Toast.makeText(context, report, Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekCardActive),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.HealthAndSafety, contentDescription = "Self Healing", tint = SleekGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Self-Heal", fontSize = 10.sp, color = SleekGreen, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showSubnetScanDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekCardActive),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.NetworkCheck, contentDescription = "Scan Subnet", tint = SleekPurple, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Scan", fontSize = 10.sp, color = SleekPurple, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAddNodeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Node", tint = SleekPurpleContainer, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Node", fontSize = 10.sp, color = SleekPurpleContainer, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Conductor Command Input & Mode Tabs
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SleekCard)
                .border(1.dp, SleekCardBorder, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Text(
                text = "CONDUCTOR DISPATCH CONTROL",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = SleekPurple
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mode selector chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val modes = listOf(
                    "ROLE_BASED_6TTY" to "🎭 6-TTY ROLE FAN-OUT",
                    "BROADCAST_ALL" to "📡 BROADCAST GOAL",
                    "PIPELINE_CHAIN" to "⛓️ PIPELINE CHAIN"
                )
                modes.forEach { (modeKey, modeLabel) ->
                    val isSelected = selectedMode == modeKey
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) SleekPurple else SleekCardActive,
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) SleekPurple else SleekCardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedMode = modeKey }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = modeLabel,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (isSelected) SleekPurpleContainer else TextPrimary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = objectiveInput,
                    onValueChange = { objectiveInput = it },
                    placeholder = {
                        Text(
                            "Enter task (e.g. 'Build real-time WebSocket dashboard with unit tests')",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SleekPurple,
                        unfocusedBorderColor = SleekCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = {
                        if (objectiveInput.isNotBlank()) {
                            viewModel.conductMultiOllamaOrchestra(objectiveInput, mode = selectedMode)
                            Toast.makeText(context, "Orchestra dispatched to 6-TTY agents!", Toast.LENGTH_SHORT).show()
                            objectiveInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Conduct", tint = SleekPurpleContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main List: mDNS Discovered Services, Connected Ollama Nodes & Active Sessions
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // mDNS ZeroConf Auto-Discovery Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCard)
                        .border(1.dp, SleekPurple, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(SleekGreen, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "mDNS / ZEROCONF AUTO-DISCOVERY (${mdnsServices.size} DETECTED)",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SleekPurple
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.triggerMdnsScan()
                                Toast.makeText(context, "Scanning local network for mDNS ZeroConf Ollama services...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SleekCardActive),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Scan mDNS", tint = SleekPurple, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Rescan mDNS", fontSize = 9.sp, color = SleekPurple, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (mdnsServices.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            mdnsServices.forEach { service ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SleekTerminalBg, RoundedCornerShape(8.dp))
                                        .border(1.dp, SleekCardBorder, RoundedCornerShape(8.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = service.serviceName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = TextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(SleekPillBg, RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = service.serviceType,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontSize = 8.sp,
                                                        color = SleekPillText
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "${service.endpointUrl} • Latency: ${service.latencyMs}ms",
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                color = SleekPurple
                                            )

                                            if (service.availableModels.isNotEmpty()) {
                                                Text(
                                                    text = "Models: ${service.availableModels.take(3).joinToString(", ")}",
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 9.sp,
                                                    color = TextTerminalGreen
                                                )
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.connectMdnsService(service)
                                                Toast.makeText(context, "Connected ${service.serviceName} to Mission Control!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                                            shape = RoundedCornerShape(12.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Connect", fontSize = 10.sp, color = SleekPurpleContainer, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ACTIVE OLLAMA SERVE INSTANCES (${ollamaNodes.size} NODES)",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = SleekPurple
                )
            }

            // Cards for each connected Ollama Serve Instance
            items(ollamaNodes, key = { it.id }) { node ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCard)
                        .border(1.dp, SleekCardBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(if (node.status == "ONLINE") SleekGreen else SleekRed, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = node.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${node.endpointUrl} • ${node.latencyMs}ms",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = SleekPurple
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .background(SleekPillBg, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "6/6 AGENTS",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SleekPillText
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.pingOllamaInstance(node.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Ping", tint = SleekPurple, modifier = Modifier.size(16.dp))
                                }

                                IconButton(
                                    onClick = { viewModel.deleteOllamaInstance(node.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = SleekRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SleekTerminalBg, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Models: ${node.availableModels}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextTerminalGreen
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "LIVE ORCHESTRA AGENT WORKERS",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = SleekPurple
                )
            }

            items(sessions, key = { it.ttySlot }) { sess ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCard)
                        .border(1.dp, SleekCardBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountTree, contentDescription = "Tree", tint = SleekPurple, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "TTY 0${sess.ttySlot}: ${sess.agentName} (${sess.role})",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = sess.modelName,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SleekTerminalBg, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Text(
                                text = "Task: ${sess.currentTask}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextTerminalGreen
                            )
                        }
                    }
                }
            }

            // Consolidated Summary Card
            item {
                val clipboardManager = LocalClipboardManager.current
                val resultText = activeOrchestration?.consolidatedResult ?: "Enter a single task above and click 'Conduct' to decompose the objective across 6 TTY agents and generate a consolidated report."

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SleekCard)
                        .border(1.dp, SleekGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Compare, contentDescription = "Summary", tint = SleekGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONSOLIDATED ORCHESTRA SYNTHESIS REPORT",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekGreen,
                                    fontSize = 11.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(if (activeOrchestration?.status == "COMPLETED") SleekGreen else SleekPurple, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = activeOrchestration?.status ?: "READY",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekBg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 260.dp)
                                .background(SleekTerminalBg, RoundedCornerShape(8.dp))
                                .border(1.dp, SleekCardBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            LazyColumn {
                                item {
                                    Text(
                                        text = resultText,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = TextPrimary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(resultText))
                                    Toast.makeText(context, "Consolidated synthesis copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SleekGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = SleekBg, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Synthesis Report", color = SleekBg, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Ollama Instance Dialog
    if (showAddNodeDialog) {
        AlertDialog(
            onDismissRequest = { showAddNodeDialog = false },
            title = {
                Text(
                    text = "Add Ollama Serve Endpoint",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter name and Ollama serve endpoint URL (default port 11434):", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = newHostName,
                        onValueChange = { newHostName = it },
                        label = { Text("Node Name (e.g. Secondary GPU)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newHostUrl,
                        onValueChange = { newHostUrl = it },
                        label = { Text("Endpoint URL (e.g. http://192.168.1.50:11434)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newHostUrl.isNotBlank()) {
                            viewModel.addOllamaInstance(newHostName, newHostUrl)
                            showAddNodeDialog = false
                            newHostName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple)
                ) {
                    Text("Add & Connect", color = SleekPurpleContainer)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNodeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Subnet Scan Dialog
    if (showSubnetScanDialog) {
        AlertDialog(
            onDismissRequest = { showSubnetScanDialog = false },
            title = {
                Text(
                    text = "Scan Subnet for Ollama Serve",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter local network subnet prefix to discover running Ollama instances on port 11434:", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = subnetInput,
                        onValueChange = { subnetInput = it },
                        label = { Text("Subnet Prefix (e.g. 192.168.1)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.scanLocalNetworkForOllama(subnetInput)
                        Toast.makeText(context, "Scanning $subnetInput.X:11434...", Toast.LENGTH_SHORT).show()
                        showSubnetScanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple)
                ) {
                    Text("Start Scan", color = SleekPurpleContainer)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubnetScanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
