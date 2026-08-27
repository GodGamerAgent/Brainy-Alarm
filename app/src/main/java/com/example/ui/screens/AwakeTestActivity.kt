package com.example.ui.screens

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
import com.example.service.AlarmRingingService
import com.example.ui.theme.Theme
import com.example.ui.theme.ThemeManager
import com.example.util.AlarmScheduler

class AwakeTestActivity : ComponentActivity() {

    private var alarmId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ThemeManager.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }

        alarmId = intent.getLongExtra(EXTRA_ALARM_ID, -1L)

        setContent {
            Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AwakeTestScreen(
                        alarmId = alarmId,
                        onConfirmedAwake = {
                            AlarmScheduler.cancelAwakeTest(this, alarmId)
                            finish()
                        },
                        onFailedAwake = {
                            // Re-trigger the full ringing alarm!
                            val serviceIntent = Intent(this, AlarmRingingService::class.java).apply {
                                putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarmId)
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(serviceIntent)
                            } else {
                                startService(serviceIntent)
                            }
                            finish()
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }
}
