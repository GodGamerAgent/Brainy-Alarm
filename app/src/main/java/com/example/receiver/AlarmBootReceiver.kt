package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.AlarmApplication
import com.example.util.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            Log.d("AlarmBootReceiver", "Device rebooted or app updated. Rescheduling all active alarms...")
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val repository = AlarmApplication.instance.repository
                    val enabledAlarms = repository.enabledAlarms.first()
                    for (alarm in enabledAlarms) {
                        AlarmScheduler.scheduleAlarm(context, alarm)
                        Log.d("AlarmBootReceiver", "Rescheduled alarm #${alarm.id} (${alarm.label})")
                    }
                } catch (e: Exception) {
                    Log.e("AlarmBootReceiver", "Error rescheduling alarms: ${e.message}")
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
