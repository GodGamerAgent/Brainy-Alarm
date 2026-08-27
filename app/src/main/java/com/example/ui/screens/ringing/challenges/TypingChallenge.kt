package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme

val wakeQuotes = listOf(
    "Rise and shine, the early bird gets the worm!",
    "Today is a fresh opportunity to achieve great things.",
    "No snooze today! I am awake, alert, and ready.",
    "Discipline is choosing between what you want now and what you want most.",
    "Energy flows where attention goes, get up and start!"
)

@Composable
fun TypingChallenge(
    difficulty: String = "MEDIUM",
    onCompleted: () -> Unit
) {
    val targetQuote = remember { wakeQuotes.random() }
    var typedText by remember { mutableStateOf("") }

    val matchedLength = typedText.zip(targetQuote).takeWhile { (a, b) -> a == b }.size

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
                    text = "TEXT REWRITE CHALLENGE",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "${matchedLength}/${targetQuote.length}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { matchedLength.toFloat() / targetQuote.length.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Quote prompt in Liquid Glass Card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(22.dp),
            surfaceAlpha = 0.16f,
            borderAlpha = 0.35f,
            glowColor = LiquidGlassTheme.CyanGlow
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "Type the sentence exactly as shown:",
                    style = MaterialTheme.typography.labelSmall,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = targetQuote,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 26.sp,
                    fontFamily = FontFamily.Serif,
                    color = LiquidGlassTheme.TextPure
                )
            }
        }

        // Input text field
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = typedText,
                onValueChange = { newText ->
                    typedText = newText
                    if (newText.trim() == targetQuote.trim()) {
                        onCompleted()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("typing_challenge_input"),
                placeholder = { Text("Start typing quote here...", color = LiquidGlassTheme.TextMuted) },
                singleLine = false,
                maxLines = 4,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LiquidGlassTheme.CyanLiquid,
                    focusedTextColor = LiquidGlassTheme.TextPure,
                    unfocusedTextColor = LiquidGlassTheme.TextPure
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (typedText.isNotEmpty() && !targetQuote.startsWith(typedText)) "Typing mismatch detected! Check spelling & punctuation." else "Keep typing accurately to dismiss.",
                style = MaterialTheme.typography.bodySmall,
                color = if (typedText.isNotEmpty() && !targetQuote.startsWith(typedText)) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.TextMuted
            )
        }
    }
}
