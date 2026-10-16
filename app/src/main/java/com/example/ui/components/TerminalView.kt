package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.TerminalLogEntity
import com.example.ui.theme.CyberAmber
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

import com.example.ui.theme.MatrixBg
import com.example.ui.theme.MatrixBorder
import com.example.ui.theme.MatrixGreenBright
import com.example.ui.theme.MatrixGreenDim
import com.example.ui.theme.MonokaiBg
import com.example.ui.theme.MonokaiBorder
import com.example.ui.theme.MonokaiCyan
import com.example.ui.theme.MonokaiGreen
import com.example.ui.theme.MonokaiPink
import com.example.ui.theme.MonokaiYellow

@Composable
fun TerminalView(
    logs: List<TerminalLogEntity>,
    modifier: Modifier = Modifier,
    onApprovalClick: ((Long, Boolean) -> Unit)? = null,
    terminalTheme: String = "MATRIX" // "MATRIX", "MONOKAI", "SLEEK"
) {
    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    val terminalBg = when (terminalTheme) {
        "MATRIX" -> MatrixBg
        "MONOKAI" -> MonokaiBg
        else -> CyberTerminalBg
    }

    val terminalBorder = when (terminalTheme) {
        "MATRIX" -> MatrixBorder
        "MONOKAI" -> MonokaiBorder
        else -> Color(0xFF1E293B)
    }

    Box(
        modifier = modifier
            .background(terminalBg, RoundedCornerShape(8.dp))
            .border(1.dp, terminalBorder, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        SelectionContainer {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs, key = { it.id }) { log ->
                    TerminalLogRow(log = log, onApprovalClick = onApprovalClick, theme = terminalTheme)
                }
            }
        }
    }
}

@Composable
fun TerminalLogRow(
    log: TerminalLogEntity,
    onApprovalClick: ((Long, Boolean) -> Unit)?,
    theme: String = "MATRIX"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        val annotatedText = parseAnsiAndMarkup(log.logText, log.logType, theme)

        val defaultTextColor = when (theme) {
            "MATRIX" -> MatrixGreenBright
            "MONOKAI" -> MonokaiGreen
            else -> TextPrimary
        }

        Text(
            text = annotatedText,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = defaultTextColor
        )

        if (log.requiresApproval && log.isApproved == null) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF321414), RoundedCornerShape(6.dp))
                    .border(1.dp, CyberRed, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Approval Required",
                            tint = CyberRed
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PRIVILEGED OPERATOR APPROVAL REQUIRED",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyberRed,
                            fontSize = 12.sp
                        )
                    }
                    if (!log.approvalCommand.isNullOrBlank()) {
                        Text(
                            text = log.approvalCommand,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = if (theme == "MONOKAI") MonokaiYellow else TextTerminalYellow,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        Button(
                            onClick = { onApprovalClick?.invoke(log.id, true) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberEmerald),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Approve", modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Approve", fontSize = 11.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = { onApprovalClick?.invoke(log.id, false) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberRed),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Deny", modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Deny", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

fun parseAnsiAndMarkup(raw: String, type: String, theme: String = "MATRIX") = buildAnnotatedString {
    val inputColor = when (theme) {
        "MATRIX" -> MatrixGreenBright
        "MONOKAI" -> MonokaiPink
        else -> CyberCyanGlow
    }

    val eventColor = when (theme) {
        "MATRIX" -> MatrixGreenDim
        "MONOKAI" -> MonokaiYellow
        else -> CyberAmber
    }

    when (type) {
        "COMMAND_INPUT" -> {
            withStyle(SpanStyle(color = inputColor, fontWeight = FontWeight.Bold)) {
                append(raw)
            }
        }
        "SYSTEM_EVENT" -> {
            withStyle(SpanStyle(color = eventColor, fontWeight = FontWeight.SemiBold)) {
                append(raw)
            }
        }
        "STDERR" -> {
            withStyle(SpanStyle(color = CyberRed)) {
                append(raw)
            }
        }
        else -> {
            var text = raw
            text = text.replace("\u001B[32m", "")
                .replace("\u001B[36m", "")
                .replace("\u001B[33m", "")
                .replace("\u001B[31m", "")
                .replace("\u001B[35m", "")
                .replace("\u001B[38;2;52;211;153m", "")
                .replace("\u001B[0m", "")

            if (theme == "MATRIX") {
                withStyle(SpanStyle(color = MatrixGreenBright)) { append(text) }
            } else if (theme == "MONOKAI") {
                if (raw.contains("[AGENT") || raw.contains("\u001B[36m")) {
                    withStyle(SpanStyle(color = MonokaiCyan)) { append(text) }
                } else if (raw.contains("[TASK]") || raw.contains("\u001B[33m")) {
                    withStyle(SpanStyle(color = MonokaiYellow)) { append(text) }
                } else {
                    withStyle(SpanStyle(color = MonokaiGreen)) { append(text) }
                }
            } else {
                if (raw.contains("\u001B[32m") || raw.contains("ping OK")) {
                    withStyle(SpanStyle(color = TextTerminalGreen)) { append(text) }
                } else if (raw.contains("\u001B[36m") || raw.contains("[AGENT")) {
                    withStyle(SpanStyle(color = TextTerminalCyan)) { append(text) }
                } else if (raw.contains("\u001B[33m") || raw.contains("[TASK]")) {
                    withStyle(SpanStyle(color = TextTerminalYellow)) { append(text) }
                } else if (raw.contains("\u001B[31m")) {
                    withStyle(SpanStyle(color = CyberRed)) { append(text) }
                } else {
                    append(text)
                }
            }
        }
    }
}

