package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AgentSessionEntity
import com.example.data.database.TerminalLogEntity
import com.example.ui.components.AgentTileCard
import com.example.ui.components.EmergencyStopBanner
import com.example.ui.theme.SleekBg
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCardActive
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleContainer
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekRedBg
import com.example.ui.theme.SleekRedBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MissionControlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GridScreen(
    viewModel: MissionControlViewModel,
    onNavigateToTTY: (Int) -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val isStopped = sessions.any { it.isEmergencyStopped }
    val activeCount = sessions.count { it.state == "RUNNING" }
    val approvalCount = sessions.count { it.state == "BLOCKED_APPROVAL" }

    var editingSession by remember { mutableStateOf<AgentSessionEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(12.dp)
    ) {
        // Top Orchestra Brand Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(SleekPurple, androidx.compose.foundation.shape.CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "O",
                        fontWeight = FontWeight.Bold,
                        color = SleekPurpleContainer,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ORCHESTRA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "MISSION CONTROL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = SleekPurple,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(
                        if (approvalCount > 0) SleekRedBg else SleekCardActive,
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        if (approvalCount > 0) SleekRedBorder else SleekCardBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (approvalCount > 0) "$approvalCount ALERT" else "$activeCount/6 ACTIVE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (approvalCount > 0) SleekRed else SleekGreen
                )
            }
        }

        // Emergency Stop Banner
        EmergencyStopBanner(
            isStopped = isStopped,
            onToggleEmergencyStop = { viewModel.triggerEmergencyStop(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ollama Serve Instance Node Filter Selector
        val ollamaNodes by viewModel.ollamaInstances.collectAsState()
        var selectedNodeFilter by remember { mutableStateOf<String?>(null) } // null = All Nodes

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (selectedNodeFilter == null) SleekPurple else SleekCard,
                        RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp,
                        if (selectedNodeFilter == null) SleekPurple else SleekCardBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { selectedNodeFilter = null }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "ALL OLLAMA NODES (${ollamaNodes.size})",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = if (selectedNodeFilter == null) SleekPurpleContainer else TextPrimary
                )
            }

            ollamaNodes.forEach { node ->
                val isSelected = selectedNodeFilter == node.id
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) SleekPurple else SleekCard,
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) SleekPurple else SleekCardBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedNodeFilter = node.id }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(if (node.status == "ONLINE") SleekGreen else SleekRed, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${node.name.take(18)} (6 AGENTS)",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (isSelected) SleekPurpleContainer else TextPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2x3 Grid View
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sessions, key = { it.ttySlot }) { session ->
                val logsFlow = remember(session.ttySlot) { viewModel.getLogsForSlot(session.ttySlot) }
                val logs: List<TerminalLogEntity> by logsFlow.collectAsState(initial = emptyList())
                val latestLog = logs.lastOrNull()?.logText

                AgentTileCard(
                    session = session,
                    latestLogText = latestLog,
                    onClick = {
                        viewModel.selectTTYSlot(session.ttySlot)
                        onNavigateToTTY(session.ttySlot)
                    },
                    onPauseClick = { viewModel.sendCommandToSlot(session.ttySlot, "pause") },
                    onInterruptClick = { viewModel.sendCommandToSlot(session.ttySlot, "SIGINT") },
                    onReassignClick = { editingSession = session }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick TTY Navigation Selector Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            for (slot in 1..6) {
                val sess = sessions.find { it.ttySlot == slot }
                val slotColor = when (sess?.state) {
                    "RUNNING" -> SleekGreen
                    "BLOCKED_APPROVAL" -> SleekRed
                    "WAITING_INPUT" -> SleekPurple
                    else -> TextSecondary
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .background(SleekCard, RoundedCornerShape(18.dp))
                        .border(1.dp, if (sess?.state == "RUNNING" || sess?.state == "BLOCKED_APPROVAL") slotColor else SleekCardBorder, RoundedCornerShape(18.dp))
                        .clickable {
                            viewModel.selectTTYSlot(slot)
                            onNavigateToTTY(slot)
                        }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TTY 0$slot",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = slotColor
                    )
                }
            }
        }
    }

    // Reassign Agent Config Dialog
    editingSession?.let { sess ->
        var roleInput by remember { mutableStateOf(sess.role) }
        var modelInput by remember { mutableStateOf(sess.modelName) }
        var hostInput by remember { mutableStateOf(sess.hostMachine) }

        AlertDialog(
            onDismissRequest = { editingSession = null },
            title = {
                Text(
                    "Configure TTY ${sess.ttySlot} Agent (${sess.agentName})",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = roleInput,
                        onValueChange = { roleInput = it },
                        label = { Text("Agent Role") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = modelInput,
                        onValueChange = { modelInput = it },
                        label = { Text("Model Route") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = hostInput,
                        onValueChange = { hostInput = it },
                        label = { Text("Host Machine") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateAgentConfig(sess.ttySlot, roleInput, modelInput, hostInput)
                        editingSession = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple)
                ) {
                    Text("Save Config", color = SleekPurpleContainer)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSession = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
