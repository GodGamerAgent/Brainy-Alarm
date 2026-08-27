package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import com.example.ui.theme.ThemeManager
import com.example.ui.theme.ThemePreset

@Composable
fun ThemeCustomizerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentPreset = ThemeManager.currentPreset

    Dialog(onDismissRequest = onDismiss) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(26.dp),
            surfaceAlpha = 0.28f,
            borderAlpha = 0.5f,
            glowColor = LiquidGlassTheme.CyanGlow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(LiquidGlassTheme.CyanLiquid.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = LiquidGlassTheme.CyanLiquid,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "CUSTOMIZE THEME",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = LiquidGlassTheme.TextPure,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Liquid Glass Aesthetic",
                                style = MaterialTheme.typography.labelSmall,
                                color = LiquidGlassTheme.TextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = LiquidGlassTheme.TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Presets list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(ThemePreset.entries) { preset ->
                        val isSelected = preset == currentPreset
                        ThemePresetCard(
                            preset = preset,
                            isSelected = isSelected,
                            onSelect = {
                                ThemeManager.setTheme(preset, context)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LiquidGlassButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("close_theme_dialog_btn"),
                    accentColor = LiquidGlassTheme.CyanLiquid,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Apply & Done",
                        fontWeight = FontWeight.Bold,
                        color = LiquidGlassTheme.TextPure
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemePresetCard(
    preset: ThemePreset,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        preset.previewBackground.copy(alpha = 0.9f),
                        preset.previewBackground.copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                brush = Brush.linearGradient(
                    if (isSelected) {
                        listOf(
                            preset.previewPrimary,
                            Color.White.copy(alpha = 0.8f),
                            preset.previewPrimary
                        )
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    }
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Color swatch sphere
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    preset.previewPrimary,
                                    preset.previewPrimary.copy(alpha = 0.6f),
                                    preset.previewBackground
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = if (preset == ThemePreset.MONOCHROME_MINIMAL) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = preset.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = preset.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 11.sp
                    )
                }
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(preset.previewPrimary.copy(alpha = 0.2f))
                        .border(1.dp, preset.previewPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ACTIVE",
                        color = preset.previewPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
