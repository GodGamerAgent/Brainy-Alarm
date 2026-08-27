package com.example.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.AlarmApplication
import com.example.MainActivity
import com.example.service.AlarmRingingService
import com.example.ui.screens.AwakeTestActivity
import com.example.util.AlarmScheduler
import com.example.util.AntiShutdownManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val alarmId = intent.getLongExtra(AlarmScheduler.EXTRA_ALARM_ID, -1L)
        val minutesLeft = intent.getIntExtra(AlarmScheduler.EXTRA_MINUTES_LEFT, 5)

        Log.d("AlarmReceiver", "Received action: $action for alarmId: $alarmId")

        when (action) {
            AlarmScheduler.ACTION_TRIGGER_ALARM -> {
                val serviceIntent = Intent(context, AlarmRingingService::class.java).apply {
                    putExtra(AlarmRingingService.EXTRA_ALARM_ID, alarmId)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }

            AlarmScheduler.ACTION_PRE_ALARM_LOCKDOWN -> {
                // Activate Pre-Alarm Anti-Shutdown Lockdown
                CoroutineScope(Dispatchers.IO).launch {
                    val alarm = AlarmApplication.instance.repository.getAlarmById(alarmId)
                    val label = alarm?.label ?: "Upcoming Alarm"
                    val passcode = alarm?.emergencyPasscode ?: "1234"
                    
                    AntiShutdownManager.setPreAlarmLockdown(
                        active = true,
                        alarmId = alarmId,
                        label = label,
                        minutesLeft = minutesLeft,
                        passcode = passcode
                    )

                    showPreAlarmLockdownNotification(context, alarmId, label, minutesLeft)
                }
            }

            AlarmScheduler.ACTION_AWAKE_TEST -> {
                showAwakeTestNotification(context, alarmId)
            }
        }
    }

    private fun showPreAlarmLockdownNotification(context: Context, alarmId: Long, label: String, minutes: Int) {
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            (alarmId + 40000).toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, AlarmApplication.CHANNEL_ANTI_SHUTDOWN)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("🛡️ Anti-Shutdown Shield Active")
            .setContentText("Alarm '$label' rings in $minutes min. Device power-off is locked down.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((alarmId + 40000).toInt(), notification)
    }

    private fun showAwakeTestNotification(context: Context, alarmId: Long) {
        val testIntent = Intent(context, AwakeTestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AwakeTestActivity.EXTRA_ALARM_ID, alarmId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            (alarmId + 60000).toInt(),
            testIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, AlarmApplication.CHANNEL_AWAKE_TEST)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("AWAKE TEST: Are you still awake?")
            .setContentText("Tap to confirm you are awake! If not confirmed, the alarm will ring again.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setFullScreenIntent(pendingIntent, true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((alarmId + 60000).toInt(), notification)

        try {
            context.startActivity(testIntent)
        } catch (e: Exception) {
            Log.e("AlarmReceiver", "Error launching AwakeTestActivity: ${e.message}")
        }
    }
}
