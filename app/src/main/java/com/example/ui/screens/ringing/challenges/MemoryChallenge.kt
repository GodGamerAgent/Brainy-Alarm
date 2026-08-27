package com.example.ui.screens.ringing.challenges

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class MemoryCard(
    val id: Int,
    val icon: ImageVector,
    var isRevealed: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoryChallenge(
    cardCount: Int = 6, // 6, 8, or 12
    onCompleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val allIcons = listOf(
        Icons.Default.Favorite,
        Icons.Default.Star,
        Icons.Default.Bolt,
        Icons.Default.Pets,
        Icons.Default.Headphones,
        Icons.Default.DirectionsCar,
        Icons.Default.Brightness7,
        Icons.Default.Bedtime
    )

    val pairsNeeded = (cardCount / 2).coerceIn(2, allIcons.size)
    val cards = remember {
        val selected = allIcons.shuffled().take(pairsNeeded)
        val deck = (selected + selected).shuffled().mapIndexed { index, icon ->
            MemoryCard(id = index, icon = icon)
        }
        mutableStateListOf(*deck.toTypedArray())
    }

    var firstSelectedIndex by remember { mutableStateOf<Int?>(null) }
    var isBusy by remember { mutableStateOf(false) }

    val matchedPairsCount = cards.count { it.isMatched } / 2
    val totalPairs = cards.size / 2

    LaunchedEffect(matchedPairsCount) {
        if (matchedPairsCount == totalPairs && totalPairs > 0) {
            delay(500)
            onCompleted()
        }
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
                    text = "MEMORY MATCH",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "$matchedPairsCount / $totalPairs Pairs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (totalPairs == 0) 0f else matchedPairsCount.toFloat() / totalPairs.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Cards Grid
        val columns = if (cards.size > 8) 3 else 2
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                val rotation by animateFloatAsState(
                    targetValue = if (card.isRevealed || card.isMatched) 180f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "card_flip"
                )

                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .testTag("memory_card_$index")
                        .graphicsLayer {
                            rotationY = rotation
                            cameraDistance = 12f * density
                        },
                    shape = RoundedCornerShape(18.dp),
                    surfaceAlpha = if (card.isMatched) 0.35f else if (card.isRevealed) 0.25f else 0.12f,
                    borderAlpha = if (card.isMatched) 0.6f else 0.28f,
                    glowColor = if (card.isMatched) LiquidGlassTheme.EmeraldGlow else if (card.isRevealed) LiquidGlassTheme.CyanGlow else null,
                    onClick = if (!card.isRevealed && !card.isMatched && !isBusy) {
                        {
                            val currentFirst = firstSelectedIndex
                            if (currentFirst == null) {
                                cards[index] = card.copy(isRevealed = true)
                                firstSelectedIndex = index
                            } else if (currentFirst != index) {
                                cards[index] = card.copy(isRevealed = true)
                                isBusy = true
                                scope.launch {
                                    delay(400)
                                    val firstCard = cards[currentFirst]
                                    if (firstCard.icon == card.icon) {
                                        cards[currentFirst] = firstCard.copy(isMatched = true)
                                        cards[index] = card.copy(isMatched = true)
                                    } else {
                                        delay(300)
                                        cards[currentFirst] = firstCard.copy(isRevealed = false)
                                        cards[index] = card.copy(isRevealed = false)
                                    }
                                    firstSelectedIndex = null
                                    isBusy = false
                                }
                            }
                        }
                    } else null
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (rotation > 90f) {
                            Icon(
                                imageVector = card.icon,
                                contentDescription = "Matched icon",
                                modifier = Modifier
                                    .size(36.dp)
                                    .graphicsLayer { rotationY = 180f },
                                tint = if (card.isMatched) LiquidGlassTheme.EmeraldShield else LiquidGlassTheme.CyanLiquid
                            )
                        } else {
                            Text(
                                text = "?",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = LiquidGlassTheme.TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
