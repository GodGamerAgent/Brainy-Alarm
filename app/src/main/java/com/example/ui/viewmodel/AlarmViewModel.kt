package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.AlarmApplication
import com.example.data.AlarmEntity
import com.example.data.ChallengeType
import com.example.service.AlarmRingingService
import com.example.util.AlarmScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as AlarmApplication).repository
    private val context: Context get() = getApplication()

    val allAlarms: StateFlow<List<AlarmEntity>> = repository.allAlarms
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Seed default alarm if database is empty on first run
        viewModelScope.launch {
            repository.allAlarms.collect { list ->
                if (list.isEmpty()) {
                    val defaultAlarm = AlarmEntity(
                        hour = 7,
                        minute = 30,
                        label = "Wake Up & Shine!",
                        isEnabled = true,
                        daysOfWeek = listOf(1, 2, 3, 4, 5),
                        challenges = listOf(ChallengeType.MATH, ChallengeType.SHAKE),
                        mathDifficulty = "MEDIUM",
                        mathCount = 2,
                        shakeCount = 20,
                        isAntiShutdownEnabled = true,
                        isAwakeTestEnabled = true
                    )
                    val id = repository.insert(defaultAlarm)
                    AlarmScheduler.scheduleAlarm(context, defaultAlarm.copy(id = id))
                }
            }
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.id == 0L) {
                val newId = repository.insert(alarm)
                val created = alarm.copy(id = newId)
                if (created.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, created)
                }
            } else {
                repository.update(alarm)
                if (alarm.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, alarm)
                } else {
                    AlarmScheduler.cancelAlarm(context, alarm.id)
                }
            }
        }
    }

    fun toggleAlarm(alarm: AlarmEntity, isEnabled: Boolean) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = isEnabled)
            repository.toggleAlarmState(alarm.id, isEnabled)
            if (isEnabled) {
                AlarmScheduler.scheduleAlarm(context, updated)
            } else {
                AlarmScheduler.cancelAlarm(context, alarm.id)
            }
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, alarm.id)
            AlarmScheduler.cancelAwakeTest(context, alarm.id)
            repository.delete(alarm)
        }
    }

    fun triggerTestAlarm(alarm: AlarmEntity) {
        val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
            putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarm.id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun startQuickNap(minutes: Int, label: String = "Power Nap") {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            now.add(Calendar.MINUTE, minutes)
            val napAlarm = AlarmEntity(
                hour = now.get(Calendar.HOUR_OF_DAY),
                minute = now.get(Calendar.MINUTE),
                label = "$label (${minutes}m)",
                isEnabled = true,
                daysOfWeek = emptyList(), // one-off
                challenges = listOf(ChallengeType.MATH),
                mathDifficulty = "EASY",
                mathCount = 1,
                isAntiShutdownEnabled = true,
                isAwakeTestEnabled = false
            )
            val id = repository.insert(napAlarm)
            AlarmScheduler.scheduleAlarm(context, napAlarm.copy(id = id))
        }
    }
}
