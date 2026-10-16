package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.ui.theme.MatrixBorder
import com.example.ui.theme.MonokaiCyan
import com.example.ui.theme.MonokaiGreen
import com.example.ui.theme.MonokaiOrange
import com.example.ui.theme.MonokaiPink
import com.example.ui.theme.MonokaiYellow
import com.example.ui.theme.SleekCardActive
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekPurpleContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTerminalGreen
import com.example.ui.theme.TextTerminalYellow
import com.example.ui.viewmodel.MissionControlViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobsScreen(
    viewModel: MissionControlViewModel
) {
    val context = LocalContext.current
    val jobs by viewModel.jobs.collectAsState()
    val sessions by viewModel.sessions.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "QUEUED", "EXECUTING", "COMPLETED"
    var showCreateJobDialog by remember { mutableStateOf(false) }

    val pendingCount = jobs.count { it.status == "QUEUED" }
    val activeCount = jobs.count { it.status == "EXECUTING" }
    val completedCount = jobs.count { it.status == "COMPLETED" }
    val runningAgents = sessions.count { it.state == "RUNNING" }

    val filteredJobs = when (selectedFilter) {
        "QUEUED" -> jobs.filter { it.status == "QUEUED" }
        "EXECUTING" -> jobs.filter { it.status == "EXECUTING" }
        "COMPLETED" -> jobs.filter { it.status == "COMPLETED" }
        else -> jobs
    }

    Scaffold(
        containerColor = CyberBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateJobDialog = true },
                containerColor = SleekPurple,
                contentColor = SleekPurpleContainer
            ) {
                Icon(Icons.Default.Add, contentDescription = "Enqueue Operation")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(12.dp)
        ) {
            // Task Queue Dashboard Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCard, RoundedCornerShape(12.dp))
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(SleekPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = "Queue", tint = SleekPurpleContainer, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "AI AGENT TASK QUEUE DASHBOARD",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Real-time dispatch, pending operations & agent capacity pipeline",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = {
                        Toast.makeText(context, "Task queue synchronized with 6-TTY agent network", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Sync", tint = SleekPurple)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Capacity & Metrics Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Pending Badge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCard, RoundedCornerShape(10.dp))
                        .border(1.dp, MonokaiOrange, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("PENDING", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = MonokaiOrange, fontWeight = FontWeight.Bold)
                        Text("$pendingCount Tasks", fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                // Active Badge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCard, RoundedCornerShape(10.dp))
                        .border(1.dp, MatrixBorder, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("ACTIVE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = MatrixBorder, fontWeight = FontWeight.Bold)
                        Text("$activeCount Executing", fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                // Completed Badge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(CyberCard, RoundedCornerShape(10.dp))
                        .border(1.dp, SleekPurple, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("COMPLETED", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = SleekPurple, fontWeight = FontWeight.Bold)
                        Text("$completedCount Finished", fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connected Agent Pipeline Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberCard, RoundedCornerShape(10.dp))
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SmartToy, contentDescription = "Agents", tint = MonokaiCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Connected Agent Capacity: $runningAgents/6 Active TTY Nodes",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(if (runningAgents > 0) SleekGreen else SleekCardActive, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (runningAgents > 0) "PIPELINE ONLINE" else "PIPELINE IDLE",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (runningAgents > 0) CyberBg else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val filterOptions = listOf(
                    "ALL" to "ALL (${jobs.size})",
                    "QUEUED" to "⏳ PENDING ($pendingCount)",
                    "EXECUTING" to "⚡ ACTIVE ($activeCount)",
                    "COMPLETED" to "✔ COMPLETED ($completedCount)"
                )

                filterOptions.forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) SleekPurple else CyberCard,
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) SleekPurple else CyberCardBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp,
                            color = if (isSelected) SleekPurpleContainer else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Task Queue Operations List
            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(CyberCard, RoundedCornerShape(12.dp))
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FormatListNumbered, contentDescription = "Empty", tint = TextSecondary, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No operations found in current filter view",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Click '+' below to enqueue a new operation for AI Agents.",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredJobs, key = { it.id }) { job ->
                        val statusBorder = when (job.status) {
                            "COMPLETED" -> SleekPurple
                            "EXECUTING" -> MatrixBorder
                            "QUEUED" -> MonokaiOrange
                            else -> CyberCardBorder
                        }

                        val statusBg = when (job.status) {
                            "COMPLETED" -> Color(0xFF2E1065)
                            "EXECUTING" -> Color(0xFF051308)
                            "QUEUED" -> Color(0xFF2A1B08)
                            else -> CyberCardBorder
                        }

                        val statusBadgeColor = when (job.status) {
                            "COMPLETED" -> SleekPurple
                            "EXECUTING" -> MatrixBorder
                            "QUEUED" -> MonokaiOrange
                            else -> TextSecondary
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberCard, RoundedCornerShape(10.dp))
                                .border(1.dp, statusBorder, RoundedCornerShape(10.dp))
                                .padding(12.dp)
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
                                                .background(statusBg, RoundedCornerShape(6.dp))
                                                .border(1.dp, statusBadgeColor, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = job.status,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = statusBadgeColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = job.title,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "ID: #${job.id}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = TextSecondary
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.deleteJob(job.id)
                                                Toast.makeText(context, "Operation removed from queue", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CyberRed, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Trigger", tint = MonokaiCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Schedule/Trigger: ${job.triggerCondition}",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Command block
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CyberTerminalBg, RoundedCornerShape(6.dp))
                                        .border(1.dp, CyberCardBorder, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "> ${job.command}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = TextTerminalYellow
                                    )
                                }

                                // Diff or output preview if completed
                                if (!job.diffPreview.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Difference, contentDescription = "Output", tint = SleekGreen, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Consolidated Output / Execution Log:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SleekGreen,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 2.dp)
                                            .background(Color(0xFF051308), RoundedCornerShape(6.dp))
                                            .border(1.dp, MatrixBorder, RoundedCornerShape(6.dp))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = job.diffPreview,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = TextTerminalGreen
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (job.status == "QUEUED") {
                                        Button(
                                            onClick = {
                                                viewModel.updateJobStatus(job.id, "EXECUTING")
                                                Toast.makeText(context, "Dispatched to next free TTY Agent!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MatrixBorder),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = CyberBg, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Dispatch Now", fontSize = 10.sp, color = CyberBg, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (job.status == "EXECUTING") {
                                        Button(
                                            onClick = {
                                                viewModel.updateJobStatus(job.id, "COMPLETED")
                                                Toast.makeText(context, "Marked operation as COMPLETED", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SleekPurple),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = "Complete", tint = SleekPurpleContainer, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Mark Complete", fontSize = 10.sp, color = SleekPurpleContainer, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Job Dialog
    if (showCreateJobDialog) {
        var jobTitle by remember { mutableStateOf("") }
        var jobTrigger by remember { mutableStateOf("Immediate Dispatch") }
        var jobCommand by remember { mutableStateOf("") }
        var requiresApproval by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCreateJobDialog = false },
            title = {
                Text("Enqueue Agent Operation", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = jobTitle,
                        onValueChange = { jobTitle = it },
                        label = { Text("Operation Title") },
                        placeholder = { Text("e.g. Audit security compliance across nodes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = jobTrigger,
                        onValueChange = { jobTrigger = it },
                        label = { Text("Trigger / Schedule") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = jobCommand,
                        onValueChange = { jobCommand = it },
                        label = { Text("Command / Agent Prompt") },
                        placeholder = { Text("e.g. run_audit --full-scan --output-report") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = requiresApproval,
                            onCheckedChange = { requiresApproval = it }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Require Operator Approval before execution", fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (jobTitle.isNotBlank() && jobCommand.isNotBlank()) {
                            viewModel.createJob(jobTitle, jobTrigger, jobCommand, requiresApproval)
                            showCreateJobDialog = false
                            Toast.makeText(context, "Operation enqueued successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SleekPurple)
                ) {
                    Text("Enqueue Operation", color = SleekPurpleContainer, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateJobDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

