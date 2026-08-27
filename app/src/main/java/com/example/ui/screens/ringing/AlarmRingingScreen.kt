package com.example.ui.screens.ringing

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AlarmApplication
import com.example.data.AlarmEntity
import com.example.data.ChallengeType
import com.example.ui.screens.ringing.challenges.BarcodeChallenge
import com.example.ui.screens.ringing.challenges.EmergencyQuitDialog
import com.example.ui.screens.ringing.challenges.MathChallenge
import com.example.ui.screens.ringing.challenges.MemoryChallenge
import com.example.ui.screens.ringing.challenges.SequenceChallenge
import com.example.ui.screens.ringing.challenges.ShakeChallenge
import com.example.ui.screens.ringing.challenges.SimonChallenge
import com.example.ui.screens.ringing.challenges.StroopChallenge
import com.example.ui.screens.ringing.challenges.TypingChallenge
import com.example.ui.theme.LiquidGlassBadge
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCanvas
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.delay
import java.util.Date

@Composable
fun AlarmRingingScreen(
    alarmId: Long,
    onAlarmDismissed: (AlarmEntity?) -> Unit,
    onAlarmSnoozed: (AlarmEntity) -> Unit
) {
    val context = LocalContext.current
    var alarm by remember { mutableStateOf<AlarmEntity?>(null) }
    var currentChallengeIndex by remember { mutableIntStateOf(0) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var currentTimeString by remember { mutableStateOf("") }

    // Live clock ticker
    LaunchedEffect(Unit) {
        while (true) {
            val format = if (DateFormat.is24HourFormat(context)) "HH:mm:ss" else "hh:mm:ss a"
            currentTimeString = DateFormat.format(format, Date()).toString()
            delay(1000)
        }
    }

    // Load alarm
    LaunchedEffect(alarmId) {
        val loaded = AlarmApplication.instance.repository.getAlarmById(alarmId)
        alarm = loaded ?: AlarmEntity(
            id = alarmId,
            label = "Morning Alarm",
            challenges = listOf(ChallengeType.MATH, ChallengeType.SHAKE)
        )
    }

    val activeAlarm = alarm ?: return
    val challengeList = if (activeAlarm.challenges.isEmpty()) listOf(ChallengeType.MATH) else activeAlarm.challenges
    val totalChallenges = challengeList.size
    val currentChallengeType = challengeList.getOrNull(currentChallengeIndex)

    LiquidGlassCanvas(
        accentGlow = LiquidGlassTheme.CoralSiren
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Anti-Shutdown Badge, Emergency Quit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiquidGlassBadge(
                    text = "ANTI-SHUTDOWN GUARD ACTIVE",
                    accentColor = LiquidGlassTheme.EmeraldShield,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Anti-Shutdown Active",
                            modifier = Modifier.size(14.dp),
                            tint = LiquidGlassTheme.EmeraldShield
                        )
                    },
                    modifier = Modifier.testTag("anti_shutdown_active_pill")
                )

                if (activeAlarm.emergencyQuitAllowed) {
                    IconButton(
                        onClick = { showEmergencyDialog = true },
                        modifier = Modifier.testTag("emergency_quit_open_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Emergency Override",
                            tint = LiquidGlassTheme.CoralSiren
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alarm Clock Header Glass
            Text(
                text = currentTimeString,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = LiquidGlassTheme.TextPure,
                letterSpacing = (-1).sp
            )
            Text(
                text = activeAlarm.label,
                style = MaterialTheme.typography.titleMedium,
                color = LiquidGlassTheme.CyanLiquid,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Multi-task progress bar (if > 1 challenge)
            if (totalChallenges > 1) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Challenge ${currentChallengeIndex + 1} of $totalChallenges: ${currentChallengeType?.title}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LiquidGlassTheme.CyanLiquid
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (currentChallengeIndex).toFloat() / totalChallenges.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = LiquidGlassTheme.CyanLiquid,
                        trackColor = LiquidGlassTheme.GlassSurfaceSubtle
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Challenge View Area
            LiquidGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(26.dp),
                surfaceAlpha = 0.2f,
                borderAlpha = 0.4f,
                glowColor = LiquidGlassTheme.CyanGlow
            ) {
                AnimatedContent(
                    targetState = currentChallengeType,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "challenge_view"
                ) { targetType ->
                    when (targetType) {
                        ChallengeType.MATH -> MathChallenge(
                            difficulty = activeAlarm.mathDifficulty,
                            totalCount = activeAlarm.mathCount,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.MEMORY -> MemoryChallenge(
                            cardCount = activeAlarm.memoryCardCount,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.SEQUENCE -> SequenceChallenge(
                            tileCount = activeAlarm.sequenceLength,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.SIMON -> SimonChallenge(
                            targetRounds = activeAlarm.simonRounds,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.SHAKE -> ShakeChallenge(
                            targetShakes = activeAlarm.shakeCount,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.TYPING -> TypingChallenge(
                            difficulty = activeAlarm.typingDifficulty,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.STROOP -> StroopChallenge(
                            targetRounds = activeAlarm.stroopRounds,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        ChallengeType.BARCODE -> BarcodeChallenge(
                            targetBarcode = activeAlarm.barcodeValue,
                            onCompleted = {
                                if (currentChallengeIndex + 1 >= totalChallenges) {
                                    onAlarmDismissed(activeAlarm)
                                } else {
                                    currentChallengeIndex += 1
                                }
                            }
                        )
                        null -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                LiquidGlassButton(
                                    onClick = { onAlarmDismissed(activeAlarm) },
                                    accentColor = LiquidGlassTheme.CyanLiquid
                                ) {
                                    Text("Dismiss Alarm", fontWeight = FontWeight.Bold, color = LiquidGlassTheme.TextPure)
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Intelligent Snooze controls (if allowed)
            if (activeAlarm.maxSnoozeCount > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                val snoozesLeft = activeAlarm.maxSnoozeCount - activeAlarm.currentSnoozeCount
                if (snoozesLeft > 0) {
                    val dynamicMins = activeAlarm.computeDynamicSnoozeMinutes()
                    LiquidGlassButton(
                        onClick = { onAlarmSnoozed(activeAlarm) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("snooze_alarm_btn"),
                        accentColor = LiquidGlassTheme.CyanLiquid,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Snooze, contentDescription = null, tint = LiquidGlassTheme.TextPure)
                            Text(
                                text = "Intelligent Snooze ($dynamicMins min • $snoozesLeft left)",
                                fontWeight = FontWeight.Black,
                                color = LiquidGlassTheme.TextPure,
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = "🔒 No snoozes remaining! Complete wake-up challenges to stop alarm.",
                        style = MaterialTheme.typography.labelMedium,
                        color = LiquidGlassTheme.CoralSiren,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showEmergencyDialog) {
        EmergencyQuitDialog(
            correctPasscode = activeAlarm.emergencyPasscode,
            onDismiss = { showEmergencyDialog = false },
            onForceQuit = {
                showEmergencyDialog = false
                onAlarmDismissed(activeAlarm)
            }
        )
    }
}
