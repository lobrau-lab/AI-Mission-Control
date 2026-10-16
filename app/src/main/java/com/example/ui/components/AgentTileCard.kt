package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.AgentSessionEntity
import com.example.ui.theme.MatrixBg
import com.example.ui.theme.MatrixBorder
import com.example.ui.theme.MatrixBorderDim
import com.example.ui.theme.MatrixCard
import com.example.ui.theme.MatrixCardActive
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.MatrixGreenDark
import com.example.ui.theme.MatrixGreenDim
import com.example.ui.theme.MonokaiBg
import com.example.ui.theme.MonokaiBorder
import com.example.ui.theme.MonokaiCard
import com.example.ui.theme.MonokaiCyan
import com.example.ui.theme.MonokaiGreen
import com.example.ui.theme.MonokaiOrange
import com.example.ui.theme.MonokaiPink
import com.example.ui.theme.MonokaiPurple
import com.example.ui.theme.MonokaiYellow
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCardActive
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekRedBg
import com.example.ui.theme.SleekRedBorder
import com.example.ui.theme.SleekTerminalBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AgentTileCard(
    session: AgentSessionEntity,
    latestLogText: String?,
    onClick: () -> Unit,
    onPauseClick: () -> Unit,
    onInterruptClick: () -> Unit,
    onReassignClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialTheme: String = "MATRIX" // "MATRIX", "MONOKAI", "SLEEK"
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(initialTheme) }

    val isMatrix = currentTheme == "MATRIX"
    val isMonokai = currentTheme == "MONOKAI"

    val cardBg = when {
        session.state == "BLOCKED_APPROVAL" -> if (isMatrix) MatrixCardActive else if (isMonokai) Color(0xFF38141F) else SleekCardActive
        isMatrix -> MatrixCard
        isMonokai -> MonokaiCard
        else -> SleekCard
    }

    val cardBorderColor = when {
        session.state == "BLOCKED_APPROVAL" -> SleekRed
        isMatrix -> MatrixBorder
        isMonokai -> MonokaiPink
        else -> SleekCardBorder
    }

    val terminalBlockBg = when {
        isMatrix -> MatrixBg
        isMonokai -> MonokaiBg
        else -> SleekTerminalBg
    }

    val terminalTextColor = when {
        isMatrix -> MatrixGreenBright
        isMonokai -> MonokaiGreen
        else -> TextPrimary
    }

    val titleColor = when {
        isMatrix -> MatrixGreenBright
        isMonokai -> MonokaiPink
        else -> TextPrimary
    }

    val leftAccentColor = when {
        session.state == "BLOCKED_APPROVAL" -> SleekRed
        isMatrix -> MatrixBorder
        isMonokai -> MonokaiCyan
        session.role.contains("COORDINATOR", ignoreCase = true) -> SleekPurple
        session.role.contains("IMPLEMENTER", ignoreCase = true) -> SleekPurple
        else -> SleekPurple
    }

    val statusDotColor = when (session.state) {
        "RUNNING" -> if (isMatrix) MatrixGreenBright else if (isMonokai) MonokaiGreen else SleekGreen
        "WAITING_INPUT" -> if (isMonokai) MonokaiYellow else Color(0xFFBFC8D4)
        "BLOCKED_APPROVAL" -> if (isMonokai) MonokaiPink else SleekRed
        "INTERRUPTED" -> SleekRed
        "COMPLETED" -> if (isMatrix) MatrixGreenDim else if (isMonokai) MonokaiPurple else SleekPurple
        else -> TextMuted
    }

    val transition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(cardBg)
            .border(
                width = if (isMatrix || session.state == "BLOCKED_APPROVAL") 1.5.dp else 1.dp,
                color = cardBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .combinedClickable(
                onClick = onClick,
                onLongClick = { menuExpanded = true }
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Left visual accent bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(leftAccentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Header Row: TTY Slot + Theme Pill + Agent Name + Heartbeat Badge + Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TTY-0${session.ttySlot}",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatrix) MatrixGreenDim else if (isMonokai) MonokaiCyan else TextSecondary,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "[$currentTheme]",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isMatrix) MatrixGreenBright else if (isMonokai) MonokaiYellow else SleekPurple
                            )
                        }
                        Text(
                            text = session.agentName.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = titleColor,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        HeartbeatMonitorBadge(
                            lastHeartbeatMs = session.lastHeartbeatMs,
                            isEmergencyStopped = session.isEmergencyStopped,
                            state = session.state,
                            alphaAnim = alphaAnim,
                            isMatrix = isMatrix,
                            isMonokai = isMonokai
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        if (session.state == "BLOCKED_APPROVAL") {
                            Box(
                                modifier = Modifier
                                    .background(SleekRedBg, RoundedCornerShape(4.dp))
                                    .border(1.dp, SleekRedBorder, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "ALERT",
                                    fontWeight = FontWeight.Bold,
                                    color = SleekRed,
                                    fontSize = 8.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = if (isMatrix) MatrixGreenBright else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Open Terminal View") },
                                onClick = { menuExpanded = false; onClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Theme: 🟢 MATRIX CRT") },
                                onClick = { currentTheme = "MATRIX"; menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Theme: 🎨 MONOKAI PRO") },
                                onClick = { currentTheme = "MONOKAI"; menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Theme: ⚡ SLEEK MODERN") },
                                onClick = { currentTheme = "SLEEK"; menuExpanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Pause Agent") },
                                onClick = { menuExpanded = false; onPauseClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Interrupt / Stop") },
                                onClick = { menuExpanded = false; onInterruptClick() }
                            )
                            DropdownMenuItem(
                                text = { Text("Reassign Model / Host") },
                                onClick = { menuExpanded = false; onReassignClick() }
                            )
                        }
                    }
                }

                // Current Task
                Text(
                    text = session.currentTask,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = if (isMatrix) MatrixGreenBright else if (isMonokai) TextPrimary else TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                // Latest stdout snippet in retro terminal code block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(terminalBlockBg)
                        .border(
                            1.dp,
                            if (isMatrix) MatrixBorderDim else if (isMonokai) MonokaiBorder else SleekCardBorder,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = latestLogText ?: "> ${session.currentTask.take(24)}...",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = terminalTextColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bottom Host Machine label & Progress Bar
                Column(modifier = Modifier.padding(top = 2.dp)) {
                    val progress = (session.contextTokensUsed.toFloat() / session.contextTokenLimit.coerceAtLeast(1)).coerceIn(0f, 1f)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "H: ${session.hostMachine}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (isMatrix) MatrixGreenDim else if (isMonokai) MonokaiCyan else SleekPurple
                        )

                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (isMatrix) MatrixGreenDark else Color(0xFF49454F))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .height(4.dp)
                                    .background(
                                        if (isMatrix) MatrixGreenBright
                                        else if (isMonokai) MonokaiYellow
                                        else if (session.state == "RUNNING") SleekGreen
                                        else SleekPurple
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeartbeatMonitorBadge(
    lastHeartbeatMs: Long,
    isEmergencyStopped: Boolean,
    state: String,
    alphaAnim: Float,
    isMatrix: Boolean,
    isMonokai: Boolean
) {
    val currentTime = System.currentTimeMillis()
    val elapsedMs = (currentTime - lastHeartbeatMs).coerceAtLeast(0)
    val elapsedSec = elapsedMs / 1000

    val (pulseColor, statusText, isLive) = when {
        isEmergencyStopped || state == "INTERRUPTED" || state == "OFFLINE" -> {
            Triple(SleekRed, "DEAD", false)
        }
        elapsedSec < 15 -> {
            Triple(
                if (isMatrix) MatrixGreenBright else if (isMonokai) MonokaiGreen else SleekGreen,
                if (elapsedSec < 2) "LIVE" else "${elapsedSec}s",
                true
            )
        }
        elapsedSec < 45 -> {
            Triple(
                if (isMonokai) MonokaiYellow else Color(0xFFF59E0B),
                "${elapsedSec}s STALE",
                true
            )
        }
        else -> {
            Triple(
                SleekRed,
                "${elapsedSec}s DEAD",
                false
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = pulseColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .border(
                width = 0.8.dp,
                color = pulseColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(if (isLive) alphaAnim else 1.0f)
                .background(pulseColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "💓 $statusText",
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = pulseColor
        )
    }
}


