package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ChallengeType
import com.example.ui.screens.ringing.challenges.BarcodeChallenge
import com.example.ui.screens.ringing.challenges.MathChallenge
import com.example.ui.screens.ringing.challenges.MemoryChallenge
import com.example.ui.screens.ringing.challenges.SequenceChallenge
import com.example.ui.screens.ringing.challenges.ShakeChallenge
import com.example.ui.screens.ringing.challenges.SimonChallenge
import com.example.ui.screens.ringing.challenges.StroopChallenge
import com.example.ui.screens.ringing.challenges.TypingChallenge
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme

@Composable
fun ChallengePreviewDialog(
    challengeType: ChallengeType,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .padding(12.dp),
            shape = RoundedCornerShape(26.dp),
            glowColor = LiquidGlassTheme.CyanGlow,
            surfaceAlpha = 0.22f,
            borderAlpha = 0.4f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = LiquidGlassTheme.CyanLiquid,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Practice: ${challengeType.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = LiquidGlassTheme.TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    when (challengeType) {
                        ChallengeType.MATH -> MathChallenge(
                            difficulty = "MEDIUM",
                            totalCount = 2,
                            onCompleted = onDismiss
                        )
                        ChallengeType.MEMORY -> MemoryChallenge(
                            cardCount = 6,
                            onCompleted = onDismiss
                        )
                        ChallengeType.SEQUENCE -> SequenceChallenge(
                            tileCount = 6,
                            onCompleted = onDismiss
                        )
                        ChallengeType.SIMON -> SimonChallenge(
                            targetRounds = 3,
                            onCompleted = onDismiss
                        )
                        ChallengeType.SHAKE -> ShakeChallenge(
                            targetShakes = 15,
                            onCompleted = onDismiss
                        )
                        ChallengeType.TYPING -> TypingChallenge(
                            difficulty = "MEDIUM",
                            onCompleted = onDismiss
                        )
                        ChallengeType.STROOP -> StroopChallenge(
                            targetRounds = 5,
                            onCompleted = onDismiss
                        )
                        ChallengeType.BARCODE -> BarcodeChallenge(
                            targetBarcode = "",
                            onCompleted = onDismiss
                        )
                    }
                }
            }
        }
    }
}
