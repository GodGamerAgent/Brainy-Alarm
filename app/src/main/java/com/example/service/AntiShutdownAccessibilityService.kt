package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.ui.screens.ringing.AlarmRingingActivity
import com.example.util.AntiShutdownManager

class AntiShutdownAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // Intercept if alarm is actively ringing OR within pre-alarm lockdown window
        if (!AntiShutdownManager.isProtectionActive()) return

        val pkgName = event.packageName?.toString() ?: ""
        val className = event.className?.toString() ?: ""
        val textContent = event.text?.joinToString(" ") ?: ""

        val isPowerMenuEvent = pkgName.contains("systemui", ignoreCase = true) &&
                (className.contains("GlobalActions", ignoreCase = true) ||
                 className.contains("PowerMenu", ignoreCase = true) ||
                 className.contains("Shutdown", ignoreCase = true) ||
                 className.contains("Dialog", ignoreCase = true))

        val containsShutdownText = textContent.contains("Power off", ignoreCase = true) ||
                textContent.contains("Restart", ignoreCase = true) ||
                textContent.contains("Shut down", ignoreCase = true) ||
                textContent.contains("Turn off", ignoreCase = true) ||
                textContent.contains("Emergency", ignoreCase = true)

        if (isPowerMenuEvent || containsShutdownText) {
            val isPreAlarm = AntiShutdownManager.isPreAlarmLockdown.value
            Log.d("AntiShutdownService", "Intercepted power menu attempt (PreAlarm=$isPreAlarm)! Dismissing...")

            // Auto-dismiss the power/shutdown dialog immediately
            performGlobalAction(GLOBAL_ACTION_BACK)
            performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)

            val toastMessage = if (isPreAlarm) {
                "ANTI-SHUTDOWN GUARD: Phone cannot be powered off within ${AntiShutdownManager.preAlarmMinutesLeft}m of your alarm (${AntiShutdownManager.activeAlarmLabel})!"
            } else {
                "ANTI-SHUTDOWN GUARD: Power off disabled while alarm is ringing! Complete your wake-up challenges to dismiss."
            }

            Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show()

            // If actively ringing, bring the challenge activity back to front
            if (AntiShutdownManager.isRinging.value) {
                val ringIntent = Intent(this, AlarmRingingActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                    AntiShutdownManager.activeAlarmId?.let { putExtra(AlarmRingingActivity.EXTRA_ALARM_ID, it) }
                }
                startActivity(ringIntent)
            }
        }
    }

    override fun onInterrupt() {
        Log.d("AntiShutdownService", "Service interrupted")
    }
}
