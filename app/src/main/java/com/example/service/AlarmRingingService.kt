package com.example.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.AlarmApplication
import com.example.MainActivity
import com.example.audio.AlarmSoundPlayer
import com.example.data.AlarmEntity
import com.example.ui.screens.ringing.AlarmRingingActivity
import com.example.util.AntiShutdownManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AlarmRingingService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var soundPlayer: AlarmSoundPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var volumeWatchdogJob: Job? = null
    private var currentAlarm: AlarmEntity? = null

    override fun onCreate() {
        super.onCreate()
        soundPlayer = AlarmSoundPlayer(this)
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP_ALARM) {
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(EXTRA_ALARM_ID, -1L) ?: -1L

        serviceScope.launch {
            val app = application as? AlarmApplication ?: AlarmApplication.instance
            val alarm = if (alarmId != -1L) app.repository.getAlarmById(alarmId) else null

            val activeAlarm = alarm ?: AlarmEntity(
                id = if (alarmId != -1L) alarmId else 999999L,
                label = "Wake Up!",
                ringtoneName = "Extreme Siren",
                ringtoneUri = "builtin:extreme_siren",
                volume = 1.0f,
                isCrescendo = true,
                crescendoDurationSec = 20,
                vibratePattern = "PULSE"
            )
            currentAlarm = activeAlarm

            // Set Anti-Shutdown global state
            AntiShutdownManager.setRinging(true, activeAlarm.id, activeAlarm.label)

            // Start Foreground Notification
            val notification = buildForegroundNotification(activeAlarm)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK or ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                    )
                } catch (e: Exception) {
                    startForeground(NOTIFICATION_ID, notification)
                }
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }

            // Start Sound & Vibration
            soundPlayer?.playAlarm(
                soundUri = activeAlarm.ringtoneUri,
                targetVolume = activeAlarm.volume,
                isCrescendo = activeAlarm.isCrescendo,
                crescendoDurationSec = activeAlarm.crescendoDurationSec,
                vibratePattern = activeAlarm.vibratePattern,
                scope = serviceScope
            )

            // Start Anti-Shutdown Volume Enforcer (Prevents lowering volume buttons)
            startVolumeWatchdog()

            // Dismiss any open system power dialogs
            AntiShutdownManager.dismissSystemDialogs(this@AlarmRingingService)

            // Launch Full-Screen Ringing Activity
            val ringActivityIntent = Intent(this@AlarmRingingService, AlarmRingingActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(AlarmRingingActivity.EXTRA_ALARM_ID, activeAlarm.id)
            }
            startActivity(ringActivityIntent)
        }

        return START_STICKY
    }

    private fun startVolumeWatchdog() {
        volumeWatchdogJob?.cancel()
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return

        volumeWatchdogJob = serviceScope.launch(Dispatchers.Default) {
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            while (isActive) {
                try {
                    val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                    if (currentVol < maxVol) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVol, 0)
                    }
                } catch (e: Exception) {
                    Log.e("AlarmRingingService", "Volume watchdog error: ${e.message}")
                }
                delay(500)
            }
        }
    }

    private fun buildForegroundNotification(alarm: AlarmEntity): Notification {
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmRingingActivity.EXTRA_ALARM_ID, alarm.id)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this,
            alarm.id.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, AlarmApplication.CHANNEL_ALARM)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("ALARM RINGING: ${alarm.label}")
            .setContentText("Solve the awake challenges to turn off. Anti-shutdown active!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setContentIntent(fullScreenPendingIntent)
            .build()
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = pm?.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "CantWakeUp:AlarmWakeLock"
        )?.apply {
            acquire(15 * 60 * 1000L) // 15 mins timeout
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AntiShutdownManager.setRinging(false)
        soundPlayer?.stopAlarm()
        volumeWatchdogJob?.cancel()
        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
            }
        } catch (e: Exception) {
            Log.e("AlarmRingingService", "WakeLock release error: ${e.message}")
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val NOTIFICATION_ID = 2026
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val ACTION_START_ALARM = "com.example.ACTION_START_ALARM"
        const val ACTION_STOP_ALARM = "com.example.ACTION_STOP_ALARM"
    }
}
