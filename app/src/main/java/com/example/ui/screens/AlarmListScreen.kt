package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AlarmEntity
import com.example.ui.theme.LiquidGlassBadge
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCanvas
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlarmListScreen(
    alarms: List<AlarmEntity>,
    onAddAlarm: () -> Unit,
    onEditAlarm: (AlarmEntity) -> Unit,
    onToggleAlarm: (AlarmEntity, Boolean) -> Unit,
    onTestAlarm: (AlarmEntity) -> Unit,
    onOpenAntiShutdownSettings: () -> Unit,
    onOpenQuickNap: () -> Unit,
    onOpenPracticeChallenges: () -> Unit,
    onOpenThemeCustomizer: () -> Unit = {}
) {
    val context = LocalContext.current
    val is24Hour = DateFormat.is24HourFormat(context)

    // Calculate next active alarm time string & remaining countdown
    val nextAlarmInfo = remember(alarms) {
        val activeAlarms = alarms.filter { it.isEnabled }
        if (activeAlarms.isEmpty()) null
        else {
            val now = Calendar.getInstance()
            val nextAlarm = activeAlarms.minByOrNull { it.getNextTriggerMillis() - now.timeInMillis }
            nextAlarm?.let {
                val triggerMillis = it.getNextTriggerMillis()
                val diffMillis = (triggerMillis - now.timeInMillis).coerceAtLeast(0)
                val hours = (diffMillis / (1000 * 60 * 60)).toInt()
                val mins = ((diffMillis / (1000 * 60)) % 60).toInt()

                val formattedHour = if (is24Hour) "%02d".format(it.hour) else "%02d".format(if (it.hour % 12 == 0) 12 else it.hour % 12)
                val formattedMin = "%02d".format(it.minute)
                val amPm = if (is24Hour) "" else if (it.hour < 12) " AM" else " PM"
                val countdown = if (hours > 0) "in ${hours}h ${mins}m" else "in ${mins}m"
                Triple("$formattedHour:$formattedMin$amPm", it.label, countdown)
            }
        }
    }

    LiquidGlassCanvas(
        accentGlow = LiquidGlassTheme.CyanLiquid
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "I CAN'T WAKE UP!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = LiquidGlassTheme.TextPure,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Anti-Shutdown • Smart Snooze • Challenges",
                            style = MaterialTheme.typography.labelSmall,
                            color = LiquidGlassTheme.TextMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = onOpenThemeCustomizer,
                            modifier = Modifier.testTag("nav_theme_customizer")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Customize Theme",
                                tint = LiquidGlassTheme.CyanLiquid
                            )
                        }
                        IconButton(
                            onClick = onOpenQuickNap,
                            modifier = Modifier.testTag("nav_quick_nap")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bedtime,
                                contentDescription = "Quick Nap",
                                tint = LiquidGlassTheme.TextPure
                            )
                        }
                        IconButton(
                            onClick = onOpenPracticeChallenges,
                            modifier = Modifier.testTag("nav_practice")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = "Practice",
                                tint = LiquidGlassTheme.TextPure
                            )
                        }
                        IconButton(
                            onClick = onOpenAntiShutdownSettings,
                            modifier = Modifier.testTag("nav_anti_shutdown")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Anti-Shutdown Shield",
                                tint = LiquidGlassTheme.EmeraldShield
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                LiquidGlassButton(
                    onClick = onAddAlarm,
                    modifier = Modifier.testTag("add_alarm_fab"),
                    accentColor = LiquidGlassTheme.CyanLiquid,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = LiquidGlassTheme.TextPure
                        )
                        Text(
                            text = "New Alarm",
                            fontWeight = FontWeight.Black,
                            color = LiquidGlassTheme.TextPure,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Next Alarm Liquid Glass Hero Banner
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = if (nextAlarmInfo != null) LiquidGlassTheme.CyanGlow else null,
                        borderAlpha = 0.35f,
                        surfaceAlpha = 0.18f
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (nextAlarmInfo != null) LiquidGlassTheme.CyanGlow else LiquidGlassTheme.GlassSurfaceSubtle
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = if (nextAlarmInfo != null) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.TextDim,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (nextAlarmInfo != null) "NEXT WAKE-UP (${nextAlarmInfo.third})" else "ALL ALARMS SLEEPING",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (nextAlarmInfo != null) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.TextDim,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (nextAlarmInfo != null) "${nextAlarmInfo.first} — ${nextAlarmInfo.second}" else "Tap '+' below to schedule your morning",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.TextPure
                                )
                            }
                        }
                    }
                }

                // Quick Action Strip
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LiquidGlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onOpenAntiShutdownSettings,
                            surfaceAlpha = 0.12f,
                            borderAlpha = 0.25f,
                            glowColor = LiquidGlassTheme.EmeraldGlow
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = LiquidGlassTheme.EmeraldShield
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Anti-Shutdown",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.TextPure
                                )
                            }
                        }

                        LiquidGlassCard(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            onClick = onOpenQuickNap,
                            surfaceAlpha = 0.12f,
                            borderAlpha = 0.25f,
                            glowColor = LiquidGlassTheme.CyanGlow
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bedtime,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = LiquidGlassTheme.CyanLiquid
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Quick Nap",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.TextPure
                                )
                            }
                        }
                    }
                }

                // Alarms Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "YOUR ALARMS (${alarms.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = LiquidGlassTheme.CyanLiquid,
                            letterSpacing = 1.2.sp
                        )
                    }
                }

                // Alarm Cards List
                items(alarms, key = { it.id }) { alarm ->
                    AlarmGlassItemCard(
                        alarm = alarm,
                        is24Hour = is24Hour,
                        onClick = { onEditAlarm(alarm) },
                        onToggle = { onToggleAlarm(alarm, it) },
                        onTest = { onTestAlarm(alarm) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AlarmGlassItemCard(
    alarm: AlarmEntity,
    is24Hour: Boolean,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onTest: () -> Unit
) {
    val displayHour = if (is24Hour) "%02d".format(alarm.hour) else "%02d".format(if (alarm.hour % 12 == 0) 12 else alarm.hour % 12)
    val displayMin = "%02d".format(alarm.minute)
    val amPm = if (is24Hour) "" else if (alarm.hour < 12) "AM" else "PM"

    val dayLetters = listOf(1 to "M", 2 to "T", 3 to "W", 4 to "T", 5 to "F", 6 to "S", 7 to "S")

    LiquidGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alarm_item_${alarm.id}"),
        shape = RoundedCornerShape(26.dp),
        surfaceAlpha = if (alarm.isEnabled) 0.16f else 0.05f,
        borderAlpha = if (alarm.isEnabled) 0.35f else 0.12f,
        glowColor = if (alarm.isEnabled) LiquidGlassTheme.CyanGlow else null,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Row 1: Time, AM/PM, Toggle Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$displayHour:$displayMin",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = if (alarm.isEnabled) LiquidGlassTheme.TextPure else LiquidGlassTheme.TextDim,
                        letterSpacing = (-1).sp
                    )
                    if (amPm.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = amPm,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (alarm.isEnabled) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.TextDim,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = onToggle,
                    modifier = Modifier.testTag("alarm_switch_${alarm.id}"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LiquidGlassTheme.TextPure,
                        checkedTrackColor = LiquidGlassTheme.CyanLiquid,
                        uncheckedThumbColor = LiquidGlassTheme.TextMuted,
                        uncheckedTrackColor = LiquidGlassTheme.CanvasDeep
                    )
                )
            }

            // Row 2: Label & Recurrence Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alarm.label,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (alarm.isEnabled) LiquidGlassTheme.TextMuted else LiquidGlassTheme.TextDim
                )

                // Recurrence Display
                if (alarm.recurrenceType == "DAYS_OF_WEEK") {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for ((dayNum, letter) in dayLetters) {
                            val isDayActive = alarm.daysOfWeek.contains(dayNum)
                            Surface(
                                modifier = Modifier.size(22.dp),
                                shape = CircleShape,
                                color = if (isDayActive && alarm.isEnabled) LiquidGlassTheme.CyanGlow else LiquidGlassTheme.GlassSurfaceSubtle
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = letter,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isDayActive && alarm.isEnabled) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.TextDim
                                    )
                                }
                            }
                        }
                    }
                } else {
                    LiquidGlassBadge(
                        text = alarm.getRecurrenceSummary(),
                        accentColor = LiquidGlassTheme.CyanLiquid,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = LiquidGlassTheme.CyanLiquid
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 3: Feature Badges (Anti-Shutdown, Pre-Lockdown, Smart Snooze, Challenges) + Test Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (alarm.isAntiShutdownEnabled) {
                        LiquidGlassBadge(
                            text = if (alarm.preAlarmLockdownMin > 0) "Lockdown (${alarm.preAlarmLockdownMin}m pre)" else "Anti-Shutdown",
                            accentColor = LiquidGlassTheme.EmeraldShield,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = LiquidGlassTheme.EmeraldShield
                                )
                            }
                        )
                    }

                    if (alarm.maxSnoozeCount > 0) {
                        LiquidGlassBadge(
                            text = when (alarm.snoozeMode) {
                                "CHALLENGE_ADAPTIVE" -> "Adaptive Snooze"
                                "DECREASING_PENALTY" -> "Penalty Snooze"
                                else -> "${alarm.snoozeDurationMin}m Snooze"
                            },
                            accentColor = LiquidGlassTheme.CyanLiquid,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Snooze,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = LiquidGlassTheme.CyanLiquid
                                )
                            }
                        )
                    }

                    for (ch in alarm.challenges) {
                        LiquidGlassBadge(
                            text = ch.title,
                            accentColor = LiquidGlassTheme.TextMuted
                        )
                    }
                }

                // Test Button
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(LiquidGlassTheme.GlassSurfaceSubtle)
                        .clickable(onClick = onTest)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Test Alarm",
                            modifier = Modifier.size(14.dp),
                            tint = LiquidGlassTheme.CyanLiquid
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Test",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LiquidGlassTheme.TextPure
                        )
                    }
                }
            }
        }
    }
}
