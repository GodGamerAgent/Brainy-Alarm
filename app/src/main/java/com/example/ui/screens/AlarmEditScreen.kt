package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.AlarmEntity
import com.example.data.ChallengeType
import com.example.ui.theme.LiquidGlassBadge
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCanvas
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlarmEditScreen(
    initialAlarm: AlarmEntity?,
    onSave: (AlarmEntity) -> Unit,
    onDelete: (AlarmEntity) -> Unit,
    onNavigateBack: () -> Unit
) {
    val alarm = initialAlarm ?: AlarmEntity(
        hour = 7,
        minute = 30,
        label = "Wake Up & Move!",
        challenges = listOf(ChallengeType.MATH, ChallengeType.SHAKE)
    )

    val timePickerState = rememberTimePickerState(
        initialHour = alarm.hour,
        initialMinute = alarm.minute,
        is24Hour = false
    )

    var label by remember { mutableStateOf(alarm.label) }

    // Advanced Recurrence
    var recurrenceType by remember { mutableStateOf(alarm.recurrenceType) } // "DAYS_OF_WEEK", "EVERY_N_DAYS", "ROTATING_SHIFT", "ONCE"
    val selectedDays = remember { mutableStateListOf(*alarm.daysOfWeek.toTypedArray()) }
    var intervalDays by remember { mutableIntStateOf(alarm.intervalDays) }
    var shiftDaysOn by remember { mutableIntStateOf(alarm.shiftDaysOn) }
    var shiftDaysOff by remember { mutableIntStateOf(alarm.shiftDaysOff) }

    // Intelligent Snooze
    var snoozeMode by remember { mutableStateOf(alarm.snoozeMode) } // "CHALLENGE_ADAPTIVE", "DECREASING_PENALTY", "FIXED"
    var snoozeDurationMin by remember { mutableIntStateOf(alarm.snoozeDurationMin) }
    var maxSnoozeCount by remember { mutableIntStateOf(alarm.maxSnoozeCount) }

    // Challenges Selection
    val selectedChallenges = remember { mutableStateListOf(*alarm.challenges.toTypedArray()) }
    var mathDifficulty by remember { mutableStateOf(alarm.mathDifficulty) }
    var mathCount by remember { mutableIntStateOf(alarm.mathCount) }
    var memoryCardCount by remember { mutableIntStateOf(alarm.memoryCardCount) }
    var sequenceLength by remember { mutableIntStateOf(alarm.sequenceLength) }
    var simonRounds by remember { mutableIntStateOf(alarm.simonRounds) }
    var shakeCount by remember { mutableIntStateOf(alarm.shakeCount) }
    var typingDifficulty by remember { mutableStateOf(alarm.typingDifficulty) }
    var stroopRounds by remember { mutableIntStateOf(alarm.stroopRounds) }
    var qrLocationName by remember { mutableStateOf(alarm.qrLocationName) }
    var barcodeValue by remember { mutableStateOf(alarm.barcodeValue) }

    // Anti-Shutdown & Security
    var isAntiShutdownEnabled by remember { mutableStateOf(alarm.isAntiShutdownEnabled) }
    var preAlarmLockdownMin by remember { mutableIntStateOf(alarm.preAlarmLockdownMin) }
    var isAwakeTestEnabled by remember { mutableStateOf(alarm.isAwakeTestEnabled) }
    var awakeTestDelayMin by remember { mutableIntStateOf(alarm.awakeTestDelayMin) }
    var emergencyQuitAllowed by remember { mutableStateOf(alarm.emergencyQuitAllowed) }
    var emergencyPasscode by remember { mutableStateOf(alarm.emergencyPasscode) }

    // Sound & Ramp-up
    var ringtoneName by remember { mutableStateOf(alarm.ringtoneName) }
    var ringtoneUri by remember { mutableStateOf(alarm.ringtoneUri) }
    var isCrescendo by remember { mutableStateOf(alarm.isCrescendo) }
    var crescendoDurationSec by remember { mutableIntStateOf(alarm.crescendoDurationSec) }

    var previewChallengeType by remember { mutableStateOf<ChallengeType?>(null) }
    var showRoomQrDialog by remember { mutableStateOf(false) }

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
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LiquidGlassTheme.TextPure
                        )
                    }

                    Text(
                        text = if (initialAlarm == null) "NEW ALARM" else "EDIT ALARM",
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure,
                        letterSpacing = 1.sp,
                        fontSize = 16.sp
                    )

                    if (initialAlarm != null) {
                        IconButton(
                            onClick = {
                                onDelete(initialAlarm)
                                onNavigateBack()
                            },
                            modifier = Modifier.testTag("delete_alarm_action")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Alarm",
                                tint = LiquidGlassTheme.CoralSiren
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Time Picker Glass Card
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = LiquidGlassTheme.CyanGlow,
                        surfaceAlpha = 0.16f,
                        borderAlpha = 0.35f
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TimePicker(
                                state = timePickerState,
                                colors = TimePickerDefaults.colors(
                                    clockDialColor = LiquidGlassTheme.CanvasDeep,
                                    clockDialSelectedContentColor = LiquidGlassTheme.TextPure,
                                    clockDialUnselectedContentColor = LiquidGlassTheme.TextMuted,
                                    selectorColor = LiquidGlassTheme.CyanLiquid,
                                    periodSelectorSelectedContainerColor = LiquidGlassTheme.CyanGlow,
                                    periodSelectorUnselectedContainerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                    periodSelectorSelectedContentColor = LiquidGlassTheme.CyanLiquid,
                                    periodSelectorUnselectedContentColor = LiquidGlassTheme.TextMuted,
                                    timeSelectorSelectedContainerColor = LiquidGlassTheme.CyanGlow,
                                    timeSelectorUnselectedContainerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                    timeSelectorSelectedContentColor = LiquidGlassTheme.CyanLiquid,
                                    timeSelectorUnselectedContentColor = LiquidGlassTheme.TextPure
                                )
                            )
                        }
                    }
                }

                // Alarm Label Glass Input
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.12f,
                        borderAlpha = 0.25f
                    ) {
                        OutlinedTextField(
                            value = label,
                            onValueChange = { label = it },
                            label = { Text("Alarm Label", color = LiquidGlassTheme.TextMuted) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .testTag("alarm_label_input"),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LiquidGlassTheme.CyanLiquid,
                                unfocusedBorderColor = LiquidGlassTheme.GlassBorderSubtle,
                                focusedTextColor = LiquidGlassTheme.TextPure,
                                unfocusedTextColor = LiquidGlassTheme.TextPure,
                                cursorColor = LiquidGlassTheme.CyanLiquid
                            )
                        )
                    }
                }

                // ==========================================
                // SECTION: ADVANCED RECURRING SCHEDULE
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CyanLiquid,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ADVANCED RECURRING SCHEDULE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CyanLiquid,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Recurrence Type Tabs
                            val recurrenceOptions = listOf(
                                "DAYS_OF_WEEK" to "Days of Week",
                                "EVERY_N_DAYS" to "Interval (N Days)",
                                "ROTATING_SHIFT" to "Rotating Shifts",
                                "ONCE" to "Once"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for ((key, title) in recurrenceOptions) {
                                    val isSelected = recurrenceType == key
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { recurrenceType = key },
                                        label = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LiquidGlassTheme.CyanGlow,
                                            selectedLabelColor = LiquidGlassTheme.TextPure,
                                            containerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                            labelColor = LiquidGlassTheme.TextMuted
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.GlassBorderSubtle
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            when (recurrenceType) {
                                "DAYS_OF_WEEK" -> {
                                    val dayNames = listOf(
                                        1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun"
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        for ((dayNum, dayLabel) in dayNames) {
                                            val isSelected = selectedDays.contains(dayNum)
                                            Surface(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        if (isSelected) selectedDays.remove(dayNum) else selectedDays.add(dayNum)
                                                    },
                                                shape = CircleShape,
                                                color = if (isSelected) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.GlassSurfaceSubtle
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = dayLabel.first().toString(),
                                                        fontWeight = FontWeight.Black,
                                                        color = if (isSelected) LiquidGlassTheme.CanvasBase else LiquidGlassTheme.TextMuted
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        FilterChip(
                                            selected = selectedDays.containsAll(listOf(1, 2, 3, 4, 5)) && selectedDays.size == 5,
                                            onClick = {
                                                selectedDays.clear()
                                                selectedDays.addAll(listOf(1, 2, 3, 4, 5))
                                            },
                                            label = { Text("Weekdays", fontSize = 11.sp) }
                                        )
                                        FilterChip(
                                            selected = selectedDays.containsAll(listOf(6, 7)) && selectedDays.size == 2,
                                            onClick = {
                                                selectedDays.clear()
                                                selectedDays.addAll(listOf(6, 7))
                                            },
                                            label = { Text("Weekends", fontSize = 11.sp) }
                                        )
                                        FilterChip(
                                            selected = selectedDays.size == 7,
                                            onClick = {
                                                selectedDays.clear()
                                                selectedDays.addAll(listOf(1, 2, 3, 4, 5, 6, 7))
                                            },
                                            label = { Text("Daily", fontSize = 11.sp) }
                                        )
                                    }
                                }

                                "EVERY_N_DAYS" -> {
                                    Column {
                                        Text(
                                            text = "Repeat frequency: Every $intervalDays days",
                                            fontWeight = FontWeight.Bold,
                                            color = LiquidGlassTheme.TextPure
                                        )
                                        Slider(
                                            value = intervalDays.toFloat(),
                                            onValueChange = { intervalDays = it.toInt() },
                                            valueRange = 2f..14f,
                                            steps = 11,
                                            colors = SliderDefaults.colors(
                                                thumbColor = LiquidGlassTheme.CyanLiquid,
                                                activeTrackColor = LiquidGlassTheme.CyanLiquid
                                            )
                                        )
                                        Text(
                                            text = "Ideal for alternate-day schedules, gym routines, or multi-day recurring shifts.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LiquidGlassTheme.TextMuted
                                        )
                                    }
                                }

                                "ROTATING_SHIFT" -> {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = "Rotating Cycle: $shiftDaysOn Days ON / $shiftDaysOff Days OFF",
                                            fontWeight = FontWeight.Bold,
                                            color = LiquidGlassTheme.TextPure
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Days ON ($shiftDaysOn)", style = MaterialTheme.typography.labelSmall, color = LiquidGlassTheme.TextMuted)
                                                Slider(
                                                    value = shiftDaysOn.toFloat(),
                                                    onValueChange = { shiftDaysOn = it.toInt() },
                                                    valueRange = 1f..7f,
                                                    steps = 5,
                                                    colors = SliderDefaults.colors(thumbColor = LiquidGlassTheme.CyanLiquid, activeTrackColor = LiquidGlassTheme.CyanLiquid)
                                                )
                                            }
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Days OFF ($shiftDaysOff)", style = MaterialTheme.typography.labelSmall, color = LiquidGlassTheme.TextMuted)
                                                Slider(
                                                    value = shiftDaysOff.toFloat(),
                                                    onValueChange = { shiftDaysOff = it.toInt() },
                                                    valueRange = 1f..7f,
                                                    steps = 5,
                                                    colors = SliderDefaults.colors(thumbColor = LiquidGlassTheme.TextMuted, activeTrackColor = LiquidGlassTheme.TextMuted)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Automatic shift rotation calculated seamlessly from your starting anchor date.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LiquidGlassTheme.TextMuted
                                        )
                                    }
                                }

                                "ONCE" -> {
                                    Text(
                                        text = "Will ring once at the next upcoming occurrence, then automatically turn off.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION: INTELLIGENT SNOOZE FUNCTIONALITY
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Snooze,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CyanLiquid,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "INTELLIGENT SNOOZE ENGINE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CyanLiquid,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Snooze Modes
                            val snoozeModes = listOf(
                                "CHALLENGE_ADAPTIVE" to "Adaptive Snooze",
                                "DECREASING_PENALTY" to "Penalty Snooze",
                                "FIXED" to "Custom Fixed"
                            )

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for ((key, title) in snoozeModes) {
                                    val isSelected = snoozeMode == key
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { snoozeMode = key },
                                        label = { Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LiquidGlassTheme.CyanGlow,
                                            selectedLabelColor = LiquidGlassTheme.TextPure,
                                            containerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                            labelColor = LiquidGlassTheme.TextMuted
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = isSelected,
                                            borderColor = if (isSelected) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.GlassBorderSubtle
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            when (snoozeMode) {
                                "CHALLENGE_ADAPTIVE" -> {
                                    Text(
                                        text = "⚡ Automatically shortens snooze duration according to challenge severity. Hard/Extreme tasks grant only 2-4 min snoozes to eliminate sleep inertia.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }
                                "DECREASING_PENALTY" -> {
                                    Text(
                                        text = "⏱️ Cascading penalty: 1st snooze = 9 min, 2nd = 5 min, 3rd = 2 min, then snooze is strictly locked out.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }
                                "FIXED" -> {
                                    Column {
                                        Text(
                                            text = "Fixed Duration: $snoozeDurationMin minutes",
                                            fontWeight = FontWeight.Bold,
                                            color = LiquidGlassTheme.TextPure
                                        )
                                        Slider(
                                            value = snoozeDurationMin.toFloat(),
                                            onValueChange = { snoozeDurationMin = it.toInt() },
                                            valueRange = 1f..20f,
                                            steps = 18,
                                            colors = SliderDefaults.colors(thumbColor = LiquidGlassTheme.CyanLiquid, activeTrackColor = LiquidGlassTheme.CyanLiquid)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Max Snooze Limit
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Max Snooze Count Limit", fontWeight = FontWeight.SemiBold, color = LiquidGlassTheme.TextPure)
                                Text(
                                    text = if (maxSnoozeCount == 0) "No Snooze Allowed" else "$maxSnoozeCount times",
                                    fontWeight = FontWeight.Bold,
                                    color = if (maxSnoozeCount == 0) LiquidGlassTheme.CoralSiren else LiquidGlassTheme.CyanLiquid
                                )
                            }
                            Slider(
                                value = maxSnoozeCount.toFloat(),
                                onValueChange = { maxSnoozeCount = it.toInt() },
                                valueRange = 0f..5f,
                                steps = 4,
                                colors = SliderDefaults.colors(thumbColor = LiquidGlassTheme.CyanLiquid, activeTrackColor = LiquidGlassTheme.CyanLiquid)
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION: DIVERSE WAKE-UP CHALLENGES
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "WAKE-UP TASKS / CHALLENGES",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = LiquidGlassTheme.CyanLiquid,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Select one or more tasks in sequence:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }

                                if (selectedChallenges.contains(ChallengeType.BARCODE)) {
                                    LiquidGlassBadge(
                                        text = qrLocationName,
                                        accentColor = LiquidGlassTheme.CyanLiquid,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = null,
                                                modifier = Modifier.size(12.dp),
                                                tint = LiquidGlassTheme.CyanLiquid
                                            )
                                        }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            for (challenge in ChallengeType.values()) {
                                val isChecked = selectedChallenges.contains(challenge)
                                val taskIndex = selectedChallenges.indexOf(challenge)

                                LiquidGlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    surfaceAlpha = if (isChecked) 0.22f else 0.06f,
                                    borderAlpha = if (isChecked) 0.45f else 0.12f,
                                    glowColor = if (isChecked) LiquidGlassTheme.CyanGlow else null,
                                    onClick = {
                                        if (isChecked) {
                                            if (selectedChallenges.size > 1) selectedChallenges.remove(challenge)
                                        } else {
                                            selectedChallenges.add(challenge)
                                        }
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = { checked ->
                                                if (checked) selectedChallenges.add(challenge)
                                                else if (selectedChallenges.size > 1) selectedChallenges.remove(challenge)
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = LiquidGlassTheme.CyanLiquid,
                                                checkmarkColor = LiquidGlassTheme.CanvasBase,
                                                uncheckedColor = LiquidGlassTheme.TextMuted
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = challenge.title,
                                                    fontWeight = FontWeight.Bold,
                                                    color = LiquidGlassTheme.TextPure,
                                                    fontSize = 14.sp
                                                )
                                                if (isChecked) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    LiquidGlassBadge(
                                                        text = "Step ${taskIndex + 1}",
                                                        accentColor = LiquidGlassTheme.CyanLiquid
                                                    )
                                                }
                                            }
                                            Text(
                                                text = if (challenge == ChallengeType.BARCODE && isChecked) "Target location: $qrLocationName" else challenge.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = LiquidGlassTheme.TextMuted
                                            )
                                        }

                                        // Special Room QR setup button for barcode
                                        if (challenge == ChallengeType.BARCODE) {
                                            IconButton(
                                                onClick = { showRoomQrDialog = true },
                                                modifier = Modifier.testTag("setup_room_qr_btn")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Configure Room QR",
                                                    tint = LiquidGlassTheme.CyanLiquid,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        // Practice Button
                                        IconButton(
                                            onClick = { previewChallengeType = challenge },
                                            modifier = Modifier.testTag("preview_challenge_${challenge.name}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Practice",
                                                tint = LiquidGlassTheme.CyanLiquid,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION: ANTI-SYSTEM SHUTDOWN PROTECTION
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f,
                        glowColor = if (isAntiShutdownEnabled) LiquidGlassTheme.EmeraldGlow else null
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = LiquidGlassTheme.EmeraldShield,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Anti-System Shutdown Guard",
                                            fontWeight = FontWeight.Bold,
                                            color = LiquidGlassTheme.TextPure,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Text(
                                        text = "Prevents powering off device while ringing or in pre-alarm window.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }
                                Switch(
                                    checked = isAntiShutdownEnabled,
                                    onCheckedChange = { isAntiShutdownEnabled = it },
                                    modifier = Modifier.testTag("anti_shutdown_toggle"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = LiquidGlassTheme.TextPure,
                                        checkedTrackColor = LiquidGlassTheme.EmeraldShield,
                                        uncheckedThumbColor = LiquidGlassTheme.TextMuted,
                                        uncheckedTrackColor = LiquidGlassTheme.CanvasDeep
                                    )
                                )
                            }

                            if (isAntiShutdownEnabled) {
                                Spacer(modifier = Modifier.height(14.dp))

                                // Pre-Alarm Lockdown Window Selector
                                Text(
                                    text = "Pre-Alarm Lockdown Window",
                                    fontWeight = FontWeight.SemiBold,
                                    color = LiquidGlassTheme.TextPure,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (preAlarmLockdownMin > 0) "Locks down power menu $preAlarmLockdownMin minutes before alarm starts ringing." else "Disabled (only locks down when ringing starts)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LiquidGlassTheme.TextMuted
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val windowOptions = listOf(0 to "Off", 2 to "2 min", 5 to "5 min", 10 to "10 min", 15 to "15 min")
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    for ((mins, text) in windowOptions) {
                                        val isSelected = preAlarmLockdownMin == mins
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { preAlarmLockdownMin = mins },
                                            label = { Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = LiquidGlassTheme.EmeraldGlow,
                                                selectedLabelColor = LiquidGlassTheme.TextPure,
                                                containerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                                labelColor = LiquidGlassTheme.TextMuted
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Awake Test Confirmation
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Awake Test Confirmation", fontWeight = FontWeight.SemiBold, color = LiquidGlassTheme.TextPure)
                                        Text("Re-checks 5m after dismissal to verify you didn't fall back asleep.", style = MaterialTheme.typography.bodySmall, color = LiquidGlassTheme.TextMuted)
                                    }
                                    Switch(
                                        checked = isAwakeTestEnabled,
                                        onCheckedChange = { isAwakeTestEnabled = it },
                                        modifier = Modifier.testTag("awake_test_toggle"),
                                        colors = SwitchDefaults.colors(checkedTrackColor = LiquidGlassTheme.CyanLiquid)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Emergency Passcode Override
                                OutlinedTextField(
                                    value = emergencyPasscode,
                                    onValueChange = { emergencyPasscode = it },
                                    label = { Text("Emergency Override PIN", color = LiquidGlassTheme.TextMuted) },
                                    placeholder = { Text("e.g. 1234") },
                                    modifier = Modifier.fillMaxWidth().testTag("emergency_pin_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = LiquidGlassTheme.CyanLiquid,
                                        focusedTextColor = LiquidGlassTheme.TextPure,
                                        unfocusedTextColor = LiquidGlassTheme.TextPure
                                    )
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION: SOUND & CRESCENDO
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CyanLiquid,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RINGTONE & SOUNDS",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CyanLiquid,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val ringtones = listOf(
                                "Extreme Siren" to "builtin:extreme_siren",
                                "Digital Beep" to "builtin:digital_beep",
                                "Emergency Pulse" to "builtin:emergency_pulse",
                                "Classic Twin Bell" to "builtin:classic_bell",
                                "Wake Up Rooster" to "builtin:rooster"
                            )

                            for ((name, uri) in ringtones) {
                                val isSelected = ringtoneUri == uri
                                LiquidGlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    surfaceAlpha = if (isSelected) 0.22f else 0.06f,
                                    borderAlpha = if (isSelected) 0.45f else 0.12f,
                                    onClick = {
                                        ringtoneName = name
                                        ringtoneUri = uri
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = name, fontWeight = FontWeight.SemiBold, color = LiquidGlassTheme.TextPure)
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = LiquidGlassTheme.CyanLiquid,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Crescendo Ramp-Up", fontWeight = FontWeight.SemiBold, color = LiquidGlassTheme.TextPure)
                                    Text("Ramps volume smoothly over $crescendoDurationSec seconds", style = MaterialTheme.typography.bodySmall, color = LiquidGlassTheme.TextMuted)
                                }
                                Switch(
                                    checked = isCrescendo,
                                    onCheckedChange = { isCrescendo = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = LiquidGlassTheme.CyanLiquid)
                                )
                            }
                        }
                    }
                }

                // Save Action Button
                item {
                    LiquidGlassButton(
                        onClick = {
                            val updated = alarm.copy(
                                hour = timePickerState.hour,
                                minute = timePickerState.minute,
                                label = label.ifBlank { "Wake Up" },
                                recurrenceType = recurrenceType,
                                daysOfWeek = selectedDays.toList(),
                                intervalDays = intervalDays,
                                shiftDaysOn = shiftDaysOn,
                                shiftDaysOff = shiftDaysOff,
                                snoozeMode = snoozeMode,
                                snoozeDurationMin = snoozeDurationMin,
                                maxSnoozeCount = maxSnoozeCount,
                                challenges = selectedChallenges.toList(),
                                mathDifficulty = mathDifficulty,
                                mathCount = mathCount,
                                memoryCardCount = memoryCardCount,
                                sequenceLength = sequenceLength,
                                simonRounds = simonRounds,
                                shakeCount = shakeCount,
                                typingDifficulty = typingDifficulty,
                                stroopRounds = stroopRounds,
                                qrLocationName = qrLocationName,
                                barcodeValue = barcodeValue,
                                isAntiShutdownEnabled = isAntiShutdownEnabled,
                                preAlarmLockdownMin = preAlarmLockdownMin,
                                isAwakeTestEnabled = isAwakeTestEnabled,
                                awakeTestDelayMin = awakeTestDelayMin,
                                emergencyQuitAllowed = emergencyQuitAllowed,
                                emergencyPasscode = emergencyPasscode.ifBlank { "1234" },
                                ringtoneName = ringtoneName,
                                ringtoneUri = ringtoneUri,
                                isCrescendo = isCrescendo,
                                crescendoDurationSec = crescendoDurationSec,
                                isEnabled = true
                            )
                            onSave(updated)
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .testTag("save_alarm_button"),
                        accentColor = LiquidGlassTheme.CyanLiquid,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Save Alarm Configuration",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = LiquidGlassTheme.TextPure
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    if (showRoomQrDialog) {
        RoomQrToolDialog(
            initialLocation = qrLocationName,
            initialBarcodeValue = barcodeValue,
            onSave = { loc, code ->
                qrLocationName = loc
                barcodeValue = code
            },
            onDismiss = { showRoomQrDialog = false }
        )
    }

    previewChallengeType?.let { challenge ->
        ChallengePreviewDialog(
            challengeType = challenge,
            onDismiss = { previewChallengeType = null }
        )
    }
}
