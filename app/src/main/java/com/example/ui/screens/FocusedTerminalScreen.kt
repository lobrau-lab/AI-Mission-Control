package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Splitscreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.example.ui.components.ShortcutKeyBar
import com.example.ui.components.TerminalView
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
import com.example.ui.theme.TextTerminalCyan
import com.example.ui.theme.TextTerminalGreen
import com.example.ui.theme.TextTerminalYellow
import com.example.ui.viewmodel.MissionControlViewModel

@Composable
fun FocusedTerminalScreen(
    viewModel: MissionControlViewModel,
    initialSlot: Int = 1
) {
    val context = LocalContext.current
    val selectedSlot by viewModel.selectedTTYSlot.collectAsState()
    val session by viewModel.currentTTYSession.collectAsState()
    val logs by viewModel.currentTTYLogs.collectAsState()
    val isVoiceListening by viewModel.isVoiceListening.collectAsState()

    var commandInput by remember { mutableStateOf("") }
    var searchFilter by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var isSplitView by remember { mutableStateOf(false) }
    var autonomyMenuExpanded by remember { mutableStateOf(false) }
    var selectedTerminalTheme by remember { mutableStateOf("MATRIX") } // "MATRIX", "MONOKAI", "SLEEK"
    var themeMenuExpanded by remember { mutableStateOf(false) }


    val filteredLogs = remember(logs, searchFilter) {
        if (searchFilter.isBlank()) logs
        else logs.filter { it.logText.contains(searchFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SleekBg)
            .padding(8.dp)
    ) {
        // TTY 1..6 Top Switcher Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (slot in 1..6) {
                val isSelected = slot == selectedSlot
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(34.dp)
                        .background(
                            if (isSelected) SleekPillBg else SleekCard,
                            RoundedCornerShape(17.dp)
                        )
                        .border(
                            1.dp,
                            if (isSelected) SleekPillBg else SleekCardBorder,
                            RoundedCornerShape(17.dp)
                        )
                        .clickable { viewModel.selectTTYSlot(slot) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TTY 0$slot",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp,
                        color = if (isSelected) com.example.ui.theme.SleekPillText else TextSecondary
                    )
                }
            }
        }

        // Active Session Header Panel
        session?.let { sess ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SleekCard, RoundedCornerShape(12.dp))
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
                            Text(
                                text = "TTY 0${sess.ttySlot}: ${sess.agentName}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(SleekTerminalBg, RoundedCornerShape(4.dp))
                                    .border(1.dp, SleekGreen, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "tmux: session_${sess.ttySlot} [attached]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = SleekGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(SleekPillBg, RoundedCornerShape(4.dp))
                                    .border(1.dp, SleekPurple, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔐 BOSS SIGNED [HMAC-SHA256]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekPurple
                                )
                            }
                        }

                        // Autonomy Level Selector
                        Box {
                            Box(
                                modifier = Modifier
                                    .background(SleekCardActive, RoundedCornerShape(6.dp))
                                    .border(1.dp, SleekPurple, RoundedCornerShape(6.dp))
                                    .clickable { autonomyMenuExpanded = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Level: ${sess.autonomyLevel}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SleekPurple
                                )
                            }

                            DropdownMenu(
                                expanded = autonomyMenuExpanded,
                                onDismissRequest = { autonomyMenuExpanded = false }
                            ) {
                                listOf("CHAT_ONLY", "ASK_BEFORE_TOOLS", "TRUSTED_TASKS", "AUTONOMOUS").forEach { lvl ->
                                    DropdownMenuItem(
                                        text = { Text(lvl, fontFamily = FontFamily.Monospace, fontSize = 12.sp) },
                                        onClick = {
                                            viewModel.updateAutonomy(sess.ttySlot, lvl)
                                            autonomyMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${sess.role} • ${sess.modelName}",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Ollama Endpoint: ${sess.endpointUrl}",
                                fontSize = 10.sp,
                                color = SleekPurple,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Theme scheme dropdown selector
                            Box {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when (selectedTerminalTheme) {
                                                "MATRIX" -> Color(0xFF051308)
                                                "MONOKAI" -> Color(0xFF272822)
                                                else -> SleekCardActive
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                        .border(
                                            1.dp,
                                            when (selectedTerminalTheme) {
                                                "MATRIX" -> com.example.ui.theme.MatrixBorder
                                                "MONOKAI" -> com.example.ui.theme.MonokaiPink
                                                else -> SleekCardBorder
                                            },
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable { themeMenuExpanded = true }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = when (selectedTerminalTheme) {
                                            "MATRIX" -> "🟢 MATRIX"
                                            "MONOKAI" -> "🎨 MONOKAI"
                                            else -> "⚡ SLEEK"
                                        },
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (selectedTerminalTheme) {
                                            "MATRIX" -> com.example.ui.theme.MatrixGreenBright
                                            "MONOKAI" -> com.example.ui.theme.MonokaiPink
                                            else -> SleekPurple
                                        }
                                    )
                                }

                                DropdownMenu(
                                    expanded = themeMenuExpanded,
                                    onDismissRequest = { themeMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("🟢 MATRIX CRT (Phosphor Green)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                                        onClick = { selectedTerminalTheme = "MATRIX"; themeMenuExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("🎨 MONOKAI PRO (Vibrant Palette)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                                        onClick = { selectedTerminalTheme = "MONOKAI"; themeMenuExpanded = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("⚡ SLEEK MODERN (Dark Minimal)", fontFamily = FontFamily.Monospace, fontSize = 11.sp) },
                                        onClick = { selectedTerminalTheme = "SLEEK"; themeMenuExpanded = false }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { showSearch = !showSearch },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search Log", tint = TextSecondary, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = { isSplitView = !isSplitView },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Splitscreen,
                                    contentDescription = "Split View",
                                    tint = if (isSplitView) SleekPurple else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Dir: ${sess.workingDir}",
                        fontSize = 10.sp,
                        color = SleekPurple,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Search Filter Bar (if toggled)
        AnimatedVisibility(visible = showSearch) {
            OutlinedTextField(
                value = searchFilter,
                onValueChange = { searchFilter = it },
                placeholder = { Text("Filter terminal output...", fontSize = 11.sp, color = TextSecondary) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SleekPurple,
                    unfocusedBorderColor = SleekCardBorder
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Terminal Body / Split View
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TerminalView(
                logs = filteredLogs,
                modifier = Modifier.weight(1f),
                onApprovalClick = { logId, approved ->
                    viewModel.resolveApproval(logId, selectedSlot, approved)
                },
                terminalTheme = selectedTerminalTheme
            )


            if (isSplitView) {
                Spacer(modifier = Modifier.width(6.dp))
                // Side Preview Panel (File diff / Shader preview / Output viewer)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(SleekCard, RoundedCornerShape(8.dp))
                        .border(1.dp, SleekCardBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text(
                            text = "FILE & SHADER PREVIEW PANEL",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = SleekPurple
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SleekTerminalBg, RoundedCornerShape(6.dp))
                                .border(1.dp, SleekCardBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "// Active file lock: ${session?.lockedFile ?: "src/graphics/ShaderPipeline.kt"}\n" +
                                        "void main() {\n" +
                                        "    vec2 uv = gl_FragCoord.xy / u_resolution;\n" +
                                        "    vec3 purpleGlow = vec3(0.81, 0.73, 1.0) * sin(u_time * 2.0);\n" +
                                        "    gl_FragColor = vec4(purpleGlow, 1.0);\n" +
                                        "}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = TextTerminalGreen
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Attachments Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { Toast.makeText(context, "File attached to TTY $selectedSlot", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.size(32.dp)
                ) { Icon(Icons.Default.AttachFile, contentDescription = "Attach File", tint = SleekPurple, modifier = Modifier.size(18.dp)) }

                IconButton(
                    onClick = { Toast.makeText(context, "Image selected for agent analysis", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.size(32.dp)
                ) { Icon(Icons.Default.Image, contentDescription = "Attach Image", tint = SleekPurple, modifier = Modifier.size(18.dp)) }

                IconButton(
                    onClick = { Toast.makeText(context, "Camera capture ready", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.size(32.dp)
                ) { Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = SleekPurple, modifier = Modifier.size(18.dp)) }

                IconButton(
                    onClick = { Toast.makeText(context, "Clipboard contents appended", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.size(32.dp)
                ) { Icon(Icons.Default.ContentCopy, contentDescription = "Clipboard", tint = SleekPurple, modifier = Modifier.size(18.dp)) }

                IconButton(
                    onClick = { Toast.makeText(context, "Doc imported", Toast.LENGTH_SHORT).show() },
                    modifier = Modifier.size(32.dp)
                ) { Icon(Icons.Default.Description, contentDescription = "Doc", tint = SleekPurple, modifier = Modifier.size(18.dp)) }
            }

            // Voice PTT Button
            Button(
                onClick = { viewModel.toggleVoiceListening() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isVoiceListening) SleekRed else SleekGreen
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(
                    imageVector = if (isVoiceListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Voice Input",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isVoiceListening) "Listening..." else "Voice PTT",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Shortcut Keys Bar
        ShortcutKeyBar(
            onKeyClick = { key ->
                if (key == "SIGINT") {
                    viewModel.sendCommandToSelectedTTY("SIGINT")
                } else if (key == "clear") {
                    viewModel.sendCommandToSelectedTTY("clear")
                } else {
                    commandInput = "$commandInput $key".trim()
                }
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Input Line with Send Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                placeholder = {
                    Text(
                        "Send command or objective to TTY $selectedSlot...",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                },
                singleLine = true,
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
                    if (commandInput.isNotBlank()) {
                        viewModel.sendCommandToSelectedTTY(commandInput)
                        commandInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Command",
                    tint = SleekPurpleContainer
                )
            }
        }
    }
}
