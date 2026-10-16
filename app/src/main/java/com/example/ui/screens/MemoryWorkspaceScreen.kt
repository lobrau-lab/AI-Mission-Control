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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBg
import com.example.ui.theme.CyberCard
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.CyberEmerald
import com.example.ui.theme.CyberRed
import com.example.ui.theme.CyberTerminalBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTerminalCyan
import com.example.ui.theme.TextTerminalGreen
import com.example.ui.theme.TextTerminalYellow
import com.example.ui.viewmodel.MissionControlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryWorkspaceScreen(
    viewModel: MissionControlViewModel
) {
    val context = LocalContext.current
    val memories by viewModel.memories.collectAsState()
    val serverHealth by viewModel.serverHealth.collectAsState()

    var selectedWorkspace by remember { mutableStateOf("Motel Lazarus") }
    var showAddMemoryDialog by remember { mutableStateOf(false) }

    // Prompt Composer State
    var promptSubject by remember { mutableStateOf("Neon rain-slicked Motel Lazarus entrance") }
    var selectedStyle by remember { mutableStateOf("Cyberpunk Cinema") }
    var selectedCamera by remember { mutableStateOf("35mm Anamorphic, Low Angle") }
    var selectedLighting by remember { mutableStateOf("Volumetric Cyan & Amber Neon") }
    var loraTrigger by remember { mutableStateOf("<lora:motel_lazarus_v2:0.85>") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBg)
            .padding(12.dp)
    ) {
        // Workspace Tab Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Motel Lazarus", "Hyperion City", "Global Studio").forEach { ws ->
                val isSelected = selectedWorkspace == ws
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .background(if (isSelected) CyberCard else CyberBg, RoundedCornerShape(6.dp))
                        .border(1.dp, if (isSelected) CyberCyan else CyberCardBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedWorkspace = ws }
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ws,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) CyberCyanGlow else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Creative Prompt Composer Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCard, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = "Composer", tint = CyberCyanGlow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CREATIVE PROMPT COMPOSER & COMFYUI",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = promptSubject,
                            onValueChange = { promptSubject = it },
                            label = { Text("Prompt / Subject") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberCyan,
                                unfocusedBorderColor = CyberCardBorder
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedStyle,
                                onValueChange = { selectedStyle = it },
                                label = { Text("Style") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = selectedCamera,
                                onValueChange = { selectedCamera = it },
                                label = { Text("Camera") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = selectedLighting,
                                onValueChange = { selectedLighting = it },
                                label = { Text("Lighting") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = loraTrigger,
                                onValueChange = { loraTrigger = it },
                                label = { Text("LoRA Trigger") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                Toast.makeText(context, "Render job submitted to ComfyUI on Desktop RTX 4090", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "Render", tint = CyberBg)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Submit to ComfyUI Render Queue", color = CyberBg, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Project Memory Inspector ("What the agent currently believes")
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, contentDescription = "Memory", tint = CyberEmerald)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PROJECT MEMORY & BELIEFS INSPECTOR",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CyberEmerald
                        )
                    }

                    IconButton(
                        onClick = { showAddMemoryDialog = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Memory", tint = CyberCyan)
                    }
                }
            }

            val filteredMemories = memories.filter { it.workspace == selectedWorkspace || it.workspace == "Global Studio" }

            items(filteredMemories, key = { it.id }) { mem ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCard, RoundedCornerShape(8.dp))
                        .border(1.dp, if (mem.isPinned) CyberEmerald else CyberCardBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Fact [Belief: ${(mem.beliefScore * 100).toInt()}%]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyanGlow
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.toggleMemoryPin(mem.id, mem.isPinned) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (mem.isPinned) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Pin Memory",
                                        tint = if (mem.isPinned) CyberEmerald else TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.deleteMemory(mem.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Memory",
                                        tint = CyberRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = mem.factText,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )

                        if (!mem.sourceCitation.isNullOrBlank()) {
                            Text(
                                text = "Source: ${mem.sourceCitation}",
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextTerminalCyan
                            )
                        }
                    }
                }
            }

            // Local Network & Server Health Status Dashboard
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCard, RoundedCornerShape(8.dp))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Router, contentDescription = "Server Health", tint = CyberCyanGlow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TAILSCALE LAN & SERVER HEALTH",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        serverHealth.forEach { srv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                if (srv.status == "ONLINE") CyberEmerald else CyberRed,
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = srv.serviceName,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = TextPrimary
                                    )
                                }

                                Text(
                                    text = "${srv.latencyMs}ms • ${srv.ipAddress} ${if (srv.freeVramGb > 0) "(${srv.freeVramGb}GB VRAM)" else ""}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Memory Dialog
    if (showAddMemoryDialog) {
        var factInput by remember { mutableStateOf("") }
        var sourceInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddMemoryDialog = false },
            title = {
                Text("Pin Fact to $selectedWorkspace Memory", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = factInput,
                        onValueChange = { factInput = it },
                        label = { Text("Fact / Rule / Preference") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = sourceInput,
                        onValueChange = { sourceInput = it },
                        label = { Text("Source Citation (Optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (factInput.isNotBlank()) {
                            viewModel.addMemory(selectedWorkspace, factInput, sourceInput.ifBlank { null })
                            showAddMemoryDialog = false
                            Toast.makeText(context, "Memory pinned", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                ) {
                    Text("Pin Memory", color = CyberBg)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemoryDialog = false }) { Text("Cancel") }
            }
        )
    }
}
