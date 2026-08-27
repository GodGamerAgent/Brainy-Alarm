package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.MainActivity
import com.example.data.AlarmEntity
import com.example.receiver.AlarmReceiver

object AlarmScheduler {

    const val ACTION_TRIGGER_ALARM = "com.example.ACTION_TRIGGER_ALARM"
    const val ACTION_PRE_ALARM_LOCKDOWN = "com.example.ACTION_PRE_ALARM_LOCKDOWN"
    const val ACTION_AWAKE_TEST = "com.example.ACTION_AWAKE_TEST"
    const val EXTRA_ALARM_ID = "extra_alarm_id"
    const val EXTRA_MINUTES_LEFT = "extra_minutes_left"

    fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
        if (!alarm.isEnabled) {
            cancelAlarm(context, alarm.id)
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = alarm.getNextTriggerMillis()

        // 1. Schedule Primary Alarm Intent
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_ALARM_ID, alarm.id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Show Intent for AlarmClockInfo
        val showIntent = PendingIntent.getActivity(
            context,
            alarm.id.toInt() + 10000,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTime, showIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Log.d("AlarmScheduler", "Alarm #${alarm.id} scheduled for $triggerTime (${alarm.getRecurrenceSummary()})")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to set alarm clock: ${e.message}")
            try {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } catch (ex: Exception) {
                Log.e("AlarmScheduler", "Fallback alarm failed: ${ex.message}")
            }
        }

        // 2. Schedule Pre-Alarm Lockdown Window if enabled
        if (alarm.isAntiShutdownEnabled && alarm.preAlarmLockdownMin > 0) {
            val preLockdownTime = triggerTime - (alarm.preAlarmLockdownMin * 60 * 1000L)
            if (preLockdownTime > System.currentTimeMillis()) {
                val preIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = ACTION_PRE_ALARM_LOCKDOWN
                    putExtra(EXTRA_ALARM_ID, alarm.id)
                    putExtra(EXTRA_MINUTES_LEFT, alarm.preAlarmLockdownMin)
                }
                val prePendingIntent = PendingIntent.getBroadcast(
                    context,
                    (alarm.id + 20000).toInt(),
                    preIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, preLockdownTime, prePendingIntent)
                    Log.d("AlarmScheduler", "Pre-alarm lockdown scheduled ${alarm.preAlarmLockdownMin}m prior")
                } catch (e: Exception) {
                    Log.e("AlarmScheduler", "Failed to schedule pre-alarm lockdown: ${e.message}")
                }
            }
        }
    }

    fun cancelAlarm(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel main alarm
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        // Cancel pre-alarm lockdown
        val preIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_PRE_ALARM_LOCKDOWN
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val prePendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 20000).toInt(),
            preIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(prePendingIntent)
    }

    fun scheduleAwakeTest(context: Context, alarmId: Long, delayMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (delayMinutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_AWAKE_TEST
            putExtra(EXTRA_ALARM_ID, alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 50000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            Log.d("AlarmScheduler", "Awake test scheduled for in $delayMinutes minutes")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error scheduling awake test: ${e.message}")
        }
    }

    fun cancelAwakeTest(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_AWAKE_TEST
            putExtra(EXTRA_ALARM_ID, alarmId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (alarmId + 50000).toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleSnooze(context: Context, alarmId: Long, snoozeMinutes: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val triggerTime = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_TRIGGER_ALARM
            putExtra(EXTRA_ALARM_ID, alarmId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val showIntent = PendingIntent.getActivity(
                    context,
                    alarmId.toInt() + 10000,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val clockInfo = AlarmManager.AlarmClockInfo(triggerTime, showIntent)
                alarmManager.setAlarmClock(clockInfo, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule snooze: ${e.message}")
        }
    }
}
