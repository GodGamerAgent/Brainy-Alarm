package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlin.random.Random

data class MathProblem(val expression: String, val answer: Int)

fun generateMathProblem(difficulty: String): MathProblem {
    val r = Random
    return when (difficulty) {
        "EASY" -> {
            val a = r.nextInt(10, 50)
            val b = r.nextInt(5, 30)
            if (r.nextBoolean()) MathProblem("$a + $b", a + b) else MathProblem("$a - $b", a - b)
        }
        "HARD" -> {
            val a = r.nextInt(12, 35)
            val b = r.nextInt(4, 9)
            val c = r.nextInt(10, 60)
            MathProblem("($a × $b) + $c", (a * b) + c)
        }
        "EXTREME" -> {
            val a = r.nextInt(20, 60)
            val b = r.nextInt(12, 25)
            val c = r.nextInt(15, 95)
            MathProblem("($a × $b) - $c", (a * b) - c)
        }
        else -> { // MEDIUM
            val op = r.nextInt(3)
            when (op) {
                0 -> {
                    val a = r.nextInt(25, 80)
                    val b = r.nextInt(15, 60)
                    MathProblem("$a + $b", a + b)
                }
                1 -> {
                    val a = r.nextInt(50, 100)
                    val b = r.nextInt(15, 45)
                    MathProblem("$a - $b", a - b)
                }
                else -> {
                    val a = r.nextInt(6, 14)
                    val b = r.nextInt(4, 9)
                    MathProblem("$a × $b", a * b)
                }
            }
        }
    }
}

@Composable
fun MathChallenge(
    difficulty: String = "MEDIUM",
    totalCount: Int = 2,
    onCompleted: () -> Unit
) {
    var solvedCount by remember { mutableIntStateOf(0) }
    var currentProblem by remember { mutableStateOf(generateMathProblem(difficulty)) }
    var inputStr by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun checkAnswer() {
        val entered = inputStr.toIntOrNull()
        if (entered == currentProblem.answer) {
            isError = false
            inputStr = ""
            val nextSolved = solvedCount + 1
            if (nextSolved >= totalCount) {
                onCompleted()
            } else {
                solvedCount = nextSolved
                currentProblem = generateMathProblem(difficulty)
            }
        } else {
            isError = true
            inputStr = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress header
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
                    text = "MATH CHALLENGE ($difficulty)",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "${solvedCount + 1} / $totalCount",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (solvedCount.toFloat()) / totalCount.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Problem Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            shape = RoundedCornerShape(24.dp),
            surfaceAlpha = if (isError) 0.35f else 0.16f,
            borderAlpha = if (isError) 0.6f else 0.3f,
            glowColor = if (isError) LiquidGlassTheme.CoralGlow else LiquidGlassTheme.CyanGlow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentProblem.expression,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = if (isError) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextPure,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                LiquidGlassCard(
                    shape = RoundedCornerShape(16.dp),
                    surfaceAlpha = 0.28f,
                    borderAlpha = 0.4f,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (inputStr.isEmpty()) if (isError) "Incorrect! Try again" else "= ?" else "= $inputStr",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = if (isError) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.CyanLiquid,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Numeric Keypad
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "DEL")
            )

            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (key in row) {
                        LiquidGlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .testTag("keypad_key_$key"),
                            shape = RoundedCornerShape(16.dp),
                            surfaceAlpha = if (key == "C" || key == "DEL") 0.12f else 0.2f,
                            borderAlpha = 0.3f,
                            glowColor = if (key != "C" && key != "DEL") LiquidGlassTheme.CyanGlow else null,
                            onClick = {
                                when (key) {
                                    "C" -> {
                                        inputStr = ""
                                        isError = false
                                    }
                                    "DEL" -> {
                                        if (inputStr.isNotEmpty()) inputStr = inputStr.dropLast(1)
                                        isError = false
                                    }
                                    else -> {
                                        if (inputStr.length < 5) {
                                            inputStr += key
                                            isError = false
                                            checkAnswer()
                                        }
                                    }
                                }
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (key == "DEL") {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Delete",
                                        tint = LiquidGlassTheme.TextMuted
                                    )
                                } else {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = if (key == "C") LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextPure
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
