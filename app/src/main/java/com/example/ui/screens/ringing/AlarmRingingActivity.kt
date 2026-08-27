package com.example.ui.screens.ringing

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.AlarmApplication
import com.example.service.AlarmRingingService
import com.example.ui.theme.Theme
import com.example.ui.theme.ThemeManager
import com.example.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmRingingActivity : ComponentActivity() {

    private var alarmId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ThemeManager.init(this)

        // Turn on screen and show over lockscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
            keyguardManager?.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)

        setContent {
            Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmRingingScreen(
                        alarmId = alarmId,
                        onAlarmDismissed = { alarm ->
                            dismissAlarm(alarm)
                        },
                        onAlarmSnoozed = { alarm ->
                            snoozeAlarm(alarm)
                        }
                    )
                }
            }
        }
    }

    private fun dismissAlarm(alarm: com.example.data.AlarmEntity?) {
        // Stop foreground ringing service
        val stopServiceIntent = Intent(this, AlarmRingingService::class.java).apply {
            action = AlarmRingingService.ACTION_STOP_ALARM
        }
        startService(stopServiceIntent)

        // If awake test is enabled, schedule awake test in background
        if (alarm != null && alarm.isAwakeTestEnabled) {
            AlarmScheduler.scheduleAwakeTest(this, alarm.id, alarm.awakeTestDelayMin)
        }

        // Reschedule recurring alarm or disable one-off alarm
        if (alarm != null) {
            CoroutineScope(Dispatchers.IO).launch {
                val repo = AlarmApplication.instance.repository
                if (alarm.daysOfWeek.isEmpty()) {
                    repo.toggleAlarmState(alarm.id, false)
                } else {
                    AlarmScheduler.scheduleAlarm(this@AlarmRingingActivity, alarm)
                }
            }
        }

        finish()
    }

    private fun snoozeAlarm(alarm: com.example.data.AlarmEntity) {
        val stopServiceIntent = Intent(this, AlarmRingingService::class.java).apply {
            action = AlarmRingingService.ACTION_STOP_ALARM
        }
        startService(stopServiceIntent)

        AlarmScheduler.scheduleSnooze(this, alarm.id, alarm.snoozeDurationMin)
        finish()
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Anti-Cheat: Intercept back press while alarm is ringing!
        // Do not allow dismissing by back button unless completed.
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }
}
