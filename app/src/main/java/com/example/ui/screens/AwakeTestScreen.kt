package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCanvas
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.delay

@Composable
fun AwakeTestScreen(
    alarmId: Long,
    onConfirmedAwake: () -> Unit,
    onFailedAwake: () -> Unit
) {
    var countdownSeconds by remember { mutableIntStateOf(120) } // 2 minutes to confirm

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds -= 1
        }
        onFailedAwake()
    }

    LiquidGlassCanvas(accentGlow = LiquidGlassTheme.CyanLiquid) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AWAKE CONFIRMATION TEST",
                    style = MaterialTheme.typography.labelLarge,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Are you still awake?",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Confirm within the countdown timer or the full alarm will sound again!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = LiquidGlassTheme.TextMuted
                )
            }

            // Circular Timer inside Liquid Glass Card
            LiquidGlassCard(
                modifier = Modifier.size(240.dp),
                shape = CircleShape,
                glowColor = if (countdownSeconds < 30) LiquidGlassTheme.CoralGlow else LiquidGlassTheme.CyanGlow,
                surfaceAlpha = 0.18f,
                borderAlpha = 0.4f
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { countdownSeconds.toFloat() / 120f },
                        modifier = Modifier.size(200.dp),
                        strokeWidth = 10.dp,
                        color = if (countdownSeconds < 30) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.CyanLiquid,
                        trackColor = LiquidGlassTheme.GlassSurfaceSubtle
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${countdownSeconds}s",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = if (countdownSeconds < 30) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextPure
                        )
                        Text(
                            text = "REMAINING",
                            style = MaterialTheme.typography.labelSmall,
                            color = LiquidGlassTheme.TextMuted,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            // Confirmation Button
            LiquidGlassButton(
                onClick = onConfirmedAwake,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("confirm_awake_btn"),
                accentColor = LiquidGlassTheme.EmeraldShield,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = LiquidGlassTheme.TextPure)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "I'm Awake! (Pass Test)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
        }
    }
}
