package com.example.util

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import com.example.receiver.AntiShutdownAdminReceiver
import com.example.service.AntiShutdownAccessibilityService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object AntiShutdownManager {

    private val _isRinging = MutableStateFlow(false)
    val isRinging = _isRinging.asStateFlow()

    private val _isPreAlarmLockdown = MutableStateFlow(false)
    val isPreAlarmLockdown = _isPreAlarmLockdown.asStateFlow()

    var activeAlarmId: Long? = null
    var activeAlarmLabel: String = "Alarm"
    var activeEmergencyPasscode: String = "1234"
    var preAlarmMinutesLeft: Int = 0

    fun setRinging(ringing: Boolean, alarmId: Long? = null, label: String = "Alarm", passcode: String = "1234") {
        _isRinging.value = ringing
        activeAlarmId = if (ringing) alarmId else null
        activeAlarmLabel = label
        activeEmergencyPasscode = passcode
        if (ringing) {
            _isPreAlarmLockdown.value = false
        }
    }

    fun setPreAlarmLockdown(active: Boolean, alarmId: Long? = null, label: String = "Upcoming Alarm", minutesLeft: Int = 5, passcode: String = "1234") {
        _isPreAlarmLockdown.value = active
        if (active) {
            activeAlarmId = alarmId
            activeAlarmLabel = label
            preAlarmMinutesLeft = minutesLeft
            activeEmergencyPasscode = passcode
        }
    }

    /**
     * Checks if Anti-Shutdown protection should currently prevent device power off/restart
     */
    fun isProtectionActive(): Boolean {
        return _isRinging.value || _isPreAlarmLockdown.value
    }

    fun isAccessibilityEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val expectedService = ComponentName(context, AntiShutdownAccessibilityService::class.java).flattenToString()
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabledServices.contains(expectedService)
    }

    fun isDeviceAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager ?: return false
        val comp = ComponentName(context, AntiShutdownAdminReceiver::class.java)
        return dpm.isAdminActive(comp)
    }

    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true
    }

    fun isIgnoringBatteryOptimizations(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            pm?.isIgnoringBatteryOptimizations(context.packageName) ?: true
        } else true
    }

    fun dismissSystemDialogs(context: Context) {
        try {
            @Suppress("DEPRECATION")
            context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        } catch (e: Exception) {
            // Ignored on newer Android when restricted
        }
    }
}
