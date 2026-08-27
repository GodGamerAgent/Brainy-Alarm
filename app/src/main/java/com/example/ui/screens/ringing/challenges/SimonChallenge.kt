package com.example.ui.screens.ringing.challenges

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun SimonChallenge(
    targetRounds: Int = 4,
    onCompleted: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val fullSequence = remember {
        val list = mutableListOf<Int>()
        for (i in 0 until targetRounds + 1) {
            list.add(Random.nextInt(4))
        }
        list
    }

    var currentRound by remember { mutableIntStateOf(1) }
    var flashingButton by remember { mutableStateOf<Int?>(null) }
    var isShowingSequence by remember { mutableStateOf(true) }
    val userInputs = remember { mutableStateListOf<Int>() }
    var isMistake by remember { mutableStateOf(false) }

    fun playRoundSequence(round: Int) {
        scope.launch {
            isShowingSequence = true
            userInputs.clear()
            delay(500)
            for (step in 0 until round) {
                val btn = fullSequence[step]
                flashingButton = btn
                delay(400)
                flashingButton = null
                delay(200)
            }
            isShowingSequence = false
        }
    }

    LaunchedEffect(currentRound) {
        playRoundSequence(currentRound)
    }

    val buttonColors = listOf(
        Color(0xFFE53935) to Color(0xFFFF8A80), // Red
        Color(0xFF1E88E5) to Color(0xFF82B1FF), // Blue
        Color(0xFF43A047) to Color(0xFFB9F6CA), // Green
        Color(0xFFFB8C00) to Color(0xFFFFE57F)  // Yellow/Orange
    )

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
                    text = "SIMON SAYS",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Round $currentRound / $targetRounds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentRound - 1).toFloat() / targetRounds.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        Text(
            text = if (isShowingSequence) "Watch pattern..." else if (isMistake) "Wrong sequence! Repeating..." else "Your turn: Repeat sequence",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isMistake) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.CyanLiquid
        )

        // 2x2 Simon Matrix
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                for (btnIndex in 0..1) {
                    SimonPad(
                        index = btnIndex,
                        isLit = flashingButton == btnIndex,
                        baseColor = buttonColors[btnIndex].first,
                        activeColor = buttonColors[btnIndex].second,
                        enabled = !isShowingSequence,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (!isShowingSequence) {
                                userInputs.add(btnIndex)
                                val checkIndex = userInputs.size - 1
                                if (userInputs[checkIndex] != fullSequence[checkIndex]) {
                                    isMistake = true
                                    scope.launch {
                                        delay(400)
                                        isMistake = false
                                        playRoundSequence(currentRound)
                                    }
                                } else if (userInputs.size == currentRound) {
                                    if (currentRound >= targetRounds) {
                                        onCompleted()
                                    } else {
                                        currentRound += 1
                                    }
                                }
                            }
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                for (btnIndex in 2..3) {
                    SimonPad(
                        index = btnIndex,
                        isLit = flashingButton == btnIndex,
                        baseColor = buttonColors[btnIndex].first,
                        activeColor = buttonColors[btnIndex].second,
                        enabled = !isShowingSequence,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (!isShowingSequence) {
                                userInputs.add(btnIndex)
                                val checkIndex = userInputs.size - 1
                                if (userInputs[checkIndex] != fullSequence[checkIndex]) {
                                    isMistake = true
                                    scope.launch {
                                        delay(400)
                                        isMistake = false
                                        playRoundSequence(currentRound)
                                    }
                                } else if (userInputs.size == currentRound) {
                                    if (currentRound >= targetRounds) {
                                        onCompleted()
                                    } else {
                                        currentRound += 1
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun SimonPad(
    index: Int,
    isLit: Boolean,
    baseColor: Color,
    activeColor: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isLit) activeColor else baseColor,
        animationSpec = tween(150),
        label = "simon_color"
    )

    LiquidGlassCard(
        modifier = modifier
            .fillMaxSize()
            .testTag("simon_pad_$index"),
        shape = RoundedCornerShape(24.dp),
        surfaceAlpha = if (isLit) 0.5f else 0.22f,
        borderAlpha = if (isLit) 0.8f else 0.35f,
        glowColor = if (isLit) animatedColor else null,
        onClick = if (enabled) onClick else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(0.4f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(animatedColor.copy(alpha = if (isLit) 0.85f else 0.35f))
            )
        }
    }
}
