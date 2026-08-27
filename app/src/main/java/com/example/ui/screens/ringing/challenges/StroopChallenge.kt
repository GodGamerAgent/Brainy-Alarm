package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassBadge
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlin.random.Random

data class ColorOption(val name: String, val color: Color)

val allStroopColors = listOf(
    ColorOption("RED", Color(0xFFEF4444)),
    ColorOption("BLUE", Color(0xFF38BDF8)),
    ColorOption("GREEN", Color(0xFF10B981)),
    ColorOption("YELLOW", Color(0xFFFBBF24)),
    ColorOption("PURPLE", Color(0xFFA855F7))
)

data class StroopQuestion(
    val word: String,
    val inkColor: ColorOption,
    val askForInk: Boolean // true = ask what color is the ink; false = ask what word is written
)

fun generateStroopQuestion(): StroopQuestion {
    val wordColor = allStroopColors.random()
    val inkColor = allStroopColors.filter { it != wordColor }.random()
    val askForInk = Random.nextBoolean()
    return StroopQuestion(wordColor.name, inkColor, askForInk)
}

@Composable
fun StroopChallenge(
    targetRounds: Int = 6,
    onCompleted: () -> Unit
) {
    var completedRounds by remember { mutableIntStateOf(0) }
    var currentQuestion by remember { mutableStateOf(generateStroopQuestion()) }
    var isMistake by remember { mutableStateOf(false) }

    val promptText = if (currentQuestion.askForInk) "SELECT THE INK COLOR" else "SELECT THE WORD TEXT"
    val correctAnswerName = if (currentQuestion.askForInk) currentQuestion.inkColor.name else currentQuestion.word

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
                    text = "STROOP COLOR MATCH",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "${completedRounds + 1} / $targetRounds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { completedRounds.toFloat() / targetRounds.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Display Word Glass Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            surfaceAlpha = if (isMistake) 0.3f else 0.16f,
            borderAlpha = 0.35f,
            glowColor = if (isMistake) LiquidGlassTheme.CoralGlow else currentQuestion.inkColor.color.copy(alpha = 0.5f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LiquidGlassBadge(
                    text = promptText,
                    accentColor = if (currentQuestion.askForInk) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.AmberWarning
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = currentQuestion.word,
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Black,
                    color = currentQuestion.inkColor.color
                )

                if (isMistake) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Oops! Read carefully.",
                        color = LiquidGlassTheme.CoralSiren,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Choice Buttons Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allStroopColors) { option ->
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("stroop_option_${option.name}"),
                    shape = RoundedCornerShape(16.dp),
                    surfaceAlpha = 0.16f,
                    borderAlpha = 0.3f,
                    glowColor = option.color.copy(alpha = 0.3f),
                    onClick = {
                        if (option.name == correctAnswerName) {
                            isMistake = false
                            val next = completedRounds + 1
                            if (next >= targetRounds) {
                                onCompleted()
                            } else {
                                completedRounds = next
                                currentQuestion = generateStroopQuestion()
                            }
                        } else {
                            isMistake = true
                        }
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = option.color
                        )
                    }
                }
            }
        }
    }
}
