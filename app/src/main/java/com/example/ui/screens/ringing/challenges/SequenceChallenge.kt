package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme

@Composable
fun SequenceChallenge(
    tileCount: Int = 6, // 6 to 9
    onCompleted: () -> Unit
) {
    val count = tileCount.coerceIn(4, 9)
    var nextExpected by remember { mutableIntStateOf(1) }
    var isMistake by remember { mutableStateOf(false) }

    // Shuffled tiles
    val tiles = remember {
        val list = (1..count).toList().shuffled()
        mutableStateListOf(*list.toTypedArray())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TILE SEQUENCE",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Tap next: #$nextExpected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isMistake) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (nextExpected - 1).toFloat() / count.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Subtitle instructions
        Text(
            text = if (isMistake) "Wrong tile! Reshuffled, start from 1" else "Tap tiles in numerical order from 1 to $count",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isMistake) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextMuted
        )

        // Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(tiles) { index, num ->
                val isCompleted = num < nextExpected

                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .testTag("tile_sequence_item_$num"),
                    shape = RoundedCornerShape(18.dp),
                    surfaceAlpha = if (isCompleted) 0.06f else if (isMistake) 0.3f else 0.18f,
                    borderAlpha = if (isCompleted) 0.12f else 0.35f,
                    glowColor = if (isMistake) LiquidGlassTheme.CoralGlow else if (!isCompleted) LiquidGlassTheme.CyanGlow else null,
                    onClick = if (!isCompleted) {
                        {
                            if (num == nextExpected) {
                                isMistake = false
                                if (nextExpected == count) {
                                    onCompleted()
                                } else {
                                    nextExpected += 1
                                }
                            } else {
                                isMistake = true
                                nextExpected = 1
                                val reshuffled = (1..count).toList().shuffled()
                                tiles.clear()
                                tiles.addAll(reshuffled)
                            }
                        }
                    } else null
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$num",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = if (isCompleted) {
                                LiquidGlassTheme.TextDim
                            } else if (isMistake) {
                                LiquidGlassTheme.CoralSiren
                            } else {
                                LiquidGlassTheme.TextPure
                            }
                        )
                    }
                }
            }
        }
    }
}
