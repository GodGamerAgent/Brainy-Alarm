package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.AppDatabase
import com.example.data.AlarmRepository
import com.example.ui.theme.ThemeManager

class AlarmApplication : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { AlarmRepository(database.alarmDao()) }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ThemeManager.init(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Critical Alarm Channel with high urgency sound and vibration
            val alarmChannel = NotificationChannel(
                CHANNEL_ALARM,
                "Active Alarm & Challenges",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows active ringing alarms and challenge progress"
                setBypassDnd(true)
                lockscreenVisibility = NotificationManager.IMPORTANCE_HIGH
                enableVibration(true)
            }

            // Awake Test Reminder Channel
            val awakeChannel = NotificationChannel(
                CHANNEL_AWAKE_TEST,
                "Awake Confirmation Tests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Verifies you did not fall back asleep after turning off alarm"
                enableVibration(true)
            }

            // Anti-Shutdown Status Channel
            val antiShutdownChannel = NotificationChannel(
                CHANNEL_ANTI_SHUTDOWN,
                "Anti-System Shutdown Protection",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Anti-shutdown foreground guardian protection status"
            }

            notificationManager.createNotificationChannels(listOf(alarmChannel, awakeChannel, antiShutdownChannel))
        }
    }

    companion object {
        const val CHANNEL_ALARM = "channel_cant_wake_up_alarm"
        const val CHANNEL_AWAKE_TEST = "channel_cant_wake_up_awake_test"
        const val CHANNEL_ANTI_SHUTDOWN = "channel_cant_wake_up_anti_shutdown"

        lateinit var instance: AlarmApplication
            private set
    }
}
