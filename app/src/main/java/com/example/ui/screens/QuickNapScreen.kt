package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

data class NapPreset(val minutes: Int, val title: String, val desc: String)

val napPresets = listOf(
    NapPreset(15, "Power Nap", "Quick alertness boost without grogginess"),
    NapPreset(20, "Cat Nap", "Motor memory & executive focus recovery"),
    NapPreset(30, "Refresh Nap", "Mid-day cognitive fatigue reset"),
    NapPreset(45, "Deep Rest", "Short restorative slow-wave sleep"),
    NapPreset(60, "Full Cycle", "REM cognitive restoration"),
    NapPreset(90, "Master Cycle", "Full 90-min natural sleep architecture")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickNapScreen(
    onNavigateBack: () -> Unit,
    onStartNap: (Int, String) -> Unit
) {
    LiquidGlassCanvas(accentGlow = LiquidGlassTheme.CyanLiquid) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LiquidGlassTheme.TextPure
                        )
                    }
                    Text(
                        text = "QUICK NAP TIMER",
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure,
                        letterSpacing = 1.sp,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Glass Card
                LiquidGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    glowColor = LiquidGlassTheme.CyanGlow,
                    surfaceAlpha = 0.16f,
                    borderAlpha = 0.35f
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            modifier = Modifier.size(34.dp),
                            tint = LiquidGlassTheme.CyanLiquid
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Anti-Shutdown Power Nap",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = LiquidGlassTheme.TextPure
                            )
                            Text(
                                text = "Protected by wake-up challenges & anti-shutdown guard so you never oversleep.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LiquidGlassTheme.TextMuted
                            )
                        }
                    }
                }

                // Grid of nap presets
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(napPresets) { preset ->
                        LiquidGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("nap_preset_${preset.minutes}"),
                            shape = RoundedCornerShape(22.dp),
                            surfaceAlpha = 0.12f,
                            borderAlpha = 0.28f,
                            glowColor = LiquidGlassTheme.CyanGlow
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${preset.minutes}",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CyanLiquid
                                )
                                Text(
                                    text = "MINUTES",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.CyanLiquid,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.TextPure,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = preset.desc,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LiquidGlassTheme.TextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                LiquidGlassButton(
                                    onClick = {
                                        onStartNap(preset.minutes, preset.title)
                                        onNavigateBack()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    accentColor = LiquidGlassTheme.CyanLiquid
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = LiquidGlassTheme.TextPure
                                        )
                                        Text("Start Nap", fontWeight = FontWeight.Bold, color = LiquidGlassTheme.TextPure)
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
