package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int = 7,
    val minute: Int = 0,
    val label: String = "Wake Up!",
    val isEnabled: Boolean = true,

    // Advanced Recurrence Settings
    val recurrenceType: String = "DAYS_OF_WEEK", // "DAYS_OF_WEEK", "EVERY_N_DAYS", "ROTATING_SHIFT", "ONCE"
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5), // 1=Mon .. 7=Sun
    val intervalDays: Int = 2, // For EVERY_N_DAYS (e.g. Every 2 days, Every 3 days)
    val intervalAnchorDateMillis: Long = System.currentTimeMillis(),
    val shiftDaysOn: Int = 4, // For ROTATING_SHIFT (e.g. 4 days on, 2 days off)
    val shiftDaysOff: Int = 2,
    val shiftAnchorDateMillis: Long = System.currentTimeMillis(),

    // Sound & Ramp-up
    val ringtoneName: String = "Extreme Siren",
    val ringtoneUri: String = "builtin:extreme_siren",
    val volume: Float = 1.0f,
    val isCrescendo: Boolean = true,
    val crescendoDurationSec: Int = 30,
    val vibratePattern: String = "PULSE", // CONTINUOUS, PULSE, STACCATO, OFF

    // Intelligent Snooze Functionality
    val snoozeMode: String = "CHALLENGE_ADAPTIVE", // "FIXED", "DECREASING_PENALTY", "CHALLENGE_ADAPTIVE"
    val snoozeDurationMin: Int = 5,
    val maxSnoozeCount: Int = 3,
    val currentSnoozeCount: Int = 0,

    // Diverse Challenges
    val challenges: List<ChallengeType> = listOf(ChallengeType.MATH, ChallengeType.SHAKE),
    val mathDifficulty: String = "MEDIUM", // EASY, MEDIUM, HARD, EXTREME
    val mathCount: Int = 2,
    val memoryCardCount: Int = 6, // 6, 8, 12
    val sequenceLength: Int = 6,
    val simonRounds: Int = 4,
    val shakeCount: Int = 25,
    val typingDifficulty: String = "MEDIUM", // EASY, MEDIUM, HARD
    val stroopRounds: Int = 6,
    val barcodeValue: String = "", // empty means any room barcode or default match
    val qrLocationName: String = "Bathroom Mirror", // Room location hint for QR challenge

    // Anti-System Shutdown Features
    val isAntiShutdownEnabled: Boolean = true,
    val preAlarmLockdownMin: Int = 5, // 0 = disabled, 2, 5, 10, 15 min pre-alarm window
    val isAwakeTestEnabled: Boolean = true,
    val awakeTestDelayMin: Int = 5,
    val emergencyQuitAllowed: Boolean = true,
    val emergencyPasscode: String = "1234", // User-controllable emergency pin
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val h = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        val m = String.format("%02d", minute)
        val amPm = if (hour < 12) "AM" else "PM"
        return "$h:$m $amPm"
    }

    fun getRecurrenceSummary(): String {
        return when (recurrenceType) {
            "ONCE" -> "Once"
            "EVERY_N_DAYS" -> "Every $intervalDays days"
            "ROTATING_SHIFT" -> "Rotating ($shiftDaysOn on / $shiftDaysOff off)"
            else -> {
                if (daysOfWeek.isEmpty()) return "Once"
                if (daysOfWeek.size == 7) return "Every day"
                if (daysOfWeek.containsAll(listOf(1, 2, 3, 4, 5)) && daysOfWeek.size == 5) return "Weekdays"
                if (daysOfWeek.containsAll(listOf(6, 7)) && daysOfWeek.size == 2) return "Weekends"
                val names = mapOf(1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu", 5 to "Fri", 6 to "Sat", 7 to "Sun")
                daysOfWeek.sorted().mapNotNull { names[it] }.joinToString(", ")
            }
        }
    }

    /**
     * Calculates the intelligent dynamic snooze duration in minutes
     */
    fun computeDynamicSnoozeMinutes(): Int {
        return when (snoozeMode) {
            "FIXED" -> snoozeDurationMin
            "DECREASING_PENALTY" -> {
                // Cascading snooze penalty: 1st snooze = 9m, 2nd = 5m, 3rd = 2m, etc.
                when (currentSnoozeCount) {
                    0 -> snoozeDurationMin.coerceAtLeast(8)
                    1 -> (snoozeDurationMin * 0.6f).toInt().coerceAtLeast(4)
                    2 -> 2
                    else -> 1
                }
            }
            "CHALLENGE_ADAPTIVE" -> {
                // Harder challenges enforce strictly shorter snooze to reduce sleep inertia
                val hasHardChallenge = mathDifficulty == "HARD" || mathDifficulty == "EXTREME" ||
                        challenges.contains(ChallengeType.BARCODE) || challenges.size >= 3
                if (hasHardChallenge) {
                    when (currentSnoozeCount) {
                        0 -> 4
                        1 -> 2
                        else -> 1
                    }
                } else {
                    when (currentSnoozeCount) {
                        0 -> 6
                        1 -> 4
                        else -> 2
                    }
                }
            }
            else -> snoozeDurationMin
        }
    }

    /**
     * Computes the exact timestamp for the next alarm trigger
     */
    fun getNextTriggerMillis(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        when (recurrenceType) {
            "ONCE" -> {
                if (target.before(now)) {
                    target.add(Calendar.DAY_OF_YEAR, 1)
                }
                return target.timeInMillis
            }

            "EVERY_N_DAYS" -> {
                val safeInterval = intervalDays.coerceAtLeast(1)
                val anchorCal = Calendar.getInstance().apply {
                    timeInMillis = intervalAnchorDateMillis
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                // Check upcoming days until we find a match on the interval grid
                for (i in 0..60) {
                    val candidate = Calendar.getInstance().apply {
                        timeInMillis = target.timeInMillis
                        add(Calendar.DAY_OF_YEAR, i)
                    }
                    if (candidate.after(now)) {
                        val diffDays = TimeUnit.MILLISECONDS.toDays(
                            candidate.timeInMillis - anchorCal.timeInMillis
                        ).toInt()
                        if (diffDays >= 0 && diffDays % safeInterval == 0) {
                            return candidate.timeInMillis
                        }
                    }
                }
                return target.timeInMillis + TimeUnit.DAYS.toMillis(safeInterval.toLong())
            }

            "ROTATING_SHIFT" -> {
                val cycleLength = (shiftDaysOn + shiftDaysOff).coerceAtLeast(2)
                val anchorCal = Calendar.getInstance().apply {
                    timeInMillis = shiftAnchorDateMillis
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                for (i in 0..60) {
                    val candidate = Calendar.getInstance().apply {
                        timeInMillis = target.timeInMillis
                        add(Calendar.DAY_OF_YEAR, i)
                    }
                    if (candidate.after(now)) {
                        val diffDays = TimeUnit.MILLISECONDS.toDays(
                            candidate.timeInMillis - anchorCal.timeInMillis
                        ).toInt()
                        if (diffDays >= 0) {
                            val dayInCycle = diffDays % cycleLength
                            if (dayInCycle < shiftDaysOn) {
                                return candidate.timeInMillis
                            }
                        }
                    }
                }
                return target.timeInMillis + TimeUnit.DAYS.toMillis(1)
            }

            else -> { // "DAYS_OF_WEEK"
                if (daysOfWeek.isEmpty()) {
                    if (target.before(now)) {
                        target.add(Calendar.DAY_OF_YEAR, 1)
                    }
                    return target.timeInMillis
                }

                val currentIsoDay = when (now.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 1
                    Calendar.TUESDAY -> 2
                    Calendar.WEDNESDAY -> 3
                    Calendar.THURSDAY -> 4
                    Calendar.FRIDAY -> 5
                    Calendar.SATURDAY -> 6
                    Calendar.SUNDAY -> 7
                    else -> 1
                }

                var daysToAdd = -1
                for (i in 0..7) {
                    val checkDay = ((currentIsoDay - 1 + i) % 7) + 1
                    if (daysOfWeek.contains(checkDay)) {
                        if (i == 0) {
                            if (target.after(now)) {
                                daysToAdd = 0
                                break
                            }
                        } else {
                            daysToAdd = i
                            break
                        }
                    }
                }

                if (daysToAdd >= 0) {
                    target.add(Calendar.DAY_OF_YEAR, daysToAdd)
                } else {
                    target.add(Calendar.DAY_OF_YEAR, 7)
                }

                return target.timeInMillis
            }
        }
    }
}
