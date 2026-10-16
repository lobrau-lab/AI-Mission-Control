package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.TextPrimary

@Composable
fun ShortcutKeyBar(
    onKeyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keys = listOf("Ctrl", "Alt", "Esc", "Tab", "↑", "↓", "←", "→", "|", "/", "SIGINT", "git status", "clear", "ls -l")

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SleekCard)
            .border(1.dp, SleekCardBorder, RoundedCornerShape(12.dp))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        keys.forEach { key ->
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SleekCardBorder)
                    .clickable { onKeyClick(key) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = key,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = SleekPurple
                )
            }
        }
    }
}

