package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SleekCard
import com.example.ui.theme.SleekCardBorder
import com.example.ui.theme.SleekGreen
import com.example.ui.theme.SleekPurple
import com.example.ui.theme.SleekRed
import com.example.ui.theme.SleekRedBg
import com.example.ui.theme.SleekRedBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun EmergencyStopBanner(
    isStopped: Boolean,
    onToggleEmergencyStop: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "eStopPulse")
    val alphaAnim by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (isStopped) SleekRedBg else SleekCard,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                if (isStopped) SleekRedBorder else SleekCardBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .alpha(if (isStopped) alphaAnim else 1.0f)
                    .background(if (isStopped) SleekRed else SleekGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = if (isStopped) "EMERGENCY STOP ACTIVE - ALL TTYS HALTED" else "ORCHESTRA MISSION CONTROL ACTIVE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isStopped) SleekRed else TextPrimary
            )
        }

        Button(
            onClick = { onToggleEmergencyStop(!isStopped) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isStopped) SleekGreen else SleekRedBg,
                contentColor = if (isStopped) SleekCard else SleekRed
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(32.dp)
                .border(
                    width = 1.dp,
                    color = if (isStopped) SleekGreen else SleekRedBorder,
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Text(
                text = if (isStopped) "RESUME ALL" else "EMERGENCY STOP",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isStopped) SleekCard else SleekRed
            )
        }
    }
}

