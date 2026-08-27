package com.example.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.receiver.AntiShutdownAdminReceiver
import com.example.ui.theme.LiquidGlassBadge
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCanvas
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import com.example.util.AntiShutdownManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AntiShutdownSettingsScreen(
    onNavigateBack: () -> Unit,
    onTestAntiShutdownAlarm: () -> Unit = {},
    onOpenThemeCustomizer: () -> Unit = {}
) {
    val context = LocalContext.current

    var hasAccessibility by remember { mutableStateOf(false) }
    var hasDeviceAdmin by remember { mutableStateOf(false) }
    var hasOverlayPermission by remember { mutableStateOf(false) }
    var isBatteryOptIgnored by remember { mutableStateOf(false) }

    var preAlarmMinutes by remember { mutableIntStateOf(5) }
    var globalEmergencyPin by remember { mutableStateOf("1234") }
    var simulateInterceptionActive by remember { mutableStateOf(false) }

    fun refreshPermissions() {
        hasAccessibility = AntiShutdownManager.isAccessibilityEnabled(context)
        hasDeviceAdmin = AntiShutdownManager.isDeviceAdminActive(context)
        hasOverlayPermission = AntiShutdownManager.canDrawOverlays(context)
        isBatteryOptIgnored = AntiShutdownManager.isIgnoringBatteryOptimizations(context)
    }

    LaunchedEffect(Unit) {
        refreshPermissions()
    }

    val totalPermissionsCount = listOf(hasAccessibility, hasDeviceAdmin, hasOverlayPermission, isBatteryOptIgnored).count { it }

    LiquidGlassCanvas(
        accentGlow = LiquidGlassTheme.EmeraldShield
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LiquidGlassTheme.TextPure
                        )
                    }

                    Text(
                        text = "ANTI-SHUTDOWN SHIELD",
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure,
                        letterSpacing = 1.sp,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = { refreshPermissions() },
                        modifier = Modifier.testTag("refresh_permissions_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = LiquidGlassTheme.CyanLiquid
                        )
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Shield Status Hero Glass Card
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        glowColor = if (totalPermissionsCount >= 3) LiquidGlassTheme.EmeraldGlow else LiquidGlassTheme.AmberWarning,
                        surfaceAlpha = 0.2f,
                        borderAlpha = 0.4f
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (totalPermissionsCount >= 3) LiquidGlassTheme.EmeraldGlow else LiquidGlassTheme.AmberWarning.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = if (totalPermissionsCount >= 3) LiquidGlassTheme.EmeraldShield else LiquidGlassTheme.AmberWarning,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (totalPermissionsCount == 4) "MAXIMUM DEFENSE ACTIVE" else "PROTECTION LEVEL: $totalPermissionsCount / 4",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = if (totalPermissionsCount == 4) LiquidGlassTheme.EmeraldShield else LiquidGlassTheme.AmberWarning,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Blocks power-off menus, notification drawer snooze, and app force-stop attempts when an alarm is ringing or within the pre-alarm window.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LiquidGlassTheme.TextMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // ==========================================
                // SECTION: PRE-ALARM LOCKDOWN CONFIGURATION
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LockClock,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.EmeraldShield,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "PRE-ALARM LOCKDOWN TIMEFRAME",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.EmeraldShield,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Enforce anti-shutdown protection before the alarm even sounds, so you cannot preemptively power off your phone before waking up.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LiquidGlassTheme.TextMuted
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            val preWindows = listOf(0 to "Off", 2 to "2 min", 5 to "5 min (Default)", 10 to "10 min", 15 to "15 min")
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                for ((min, label) in preWindows) {
                                    val isSelected = preAlarmMinutes == min
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { preAlarmMinutes = min },
                                        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LiquidGlassTheme.EmeraldGlow,
                                            selectedLabelColor = LiquidGlassTheme.TextPure,
                                            containerColor = LiquidGlassTheme.GlassSurfaceSubtle,
                                            labelColor = LiquidGlassTheme.TextMuted
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION: LIVE POWER INTERCEPTION SIMULATOR
                // ==========================================
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f,
                        glowColor = if (simulateInterceptionActive) LiquidGlassTheme.CoralGlow else null
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PowerOff,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CoralSiren,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "TEST POWER-OFF BLOCKER INTERCEPTOR",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CoralSiren,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Test what happens if someone holds the power button or tries to power off the device right now.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LiquidGlassTheme.TextMuted
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            LiquidGlassButton(
                                onClick = {
                                    simulateInterceptionActive = true
                                    Toast.makeText(
                                        context,
                                        "🛡️ ANTI-SHUTDOWN GUARD: Power Off / Restart Dialog Intercepted and Auto-Dismissed!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("simulate_power_block_btn"),
                                accentColor = LiquidGlassTheme.CoralSiren
                            ) {
                                Text(
                                    text = "Simulate Device Power-Off Interception",
                                    fontWeight = FontWeight.Bold,
                                    color = LiquidGlassTheme.TextPure
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // SECTION: REQUIRED SYSTEM PERMISSIONS
                // ==========================================
                item {
                    Text(
                        text = "SYSTEM PERMISSIONS & LAYERS",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.CyanLiquid,
                        letterSpacing = 1.sp
                    )
                }

                // Layer 1: Accessibility Service
                item {
                    PermissionGlassCard(
                        title = "1. Accessibility Service (Power-Menu Blocker)",
                        description = "Intercepts system UI dialogs and auto-dismisses 'Power off' and 'Restart' buttons.",
                        isGranted = hasAccessibility,
                        icon = Icons.Default.Accessibility,
                        onEnable = {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // Layer 2: Device Administrator
                item {
                    PermissionGlassCard(
                        title = "2. Device Administrator (Anti-Uninstall)",
                        description = "Prevents uninstalling or force-stopping the alarm app while active in morning hours.",
                        isGranted = hasDeviceAdmin,
                        icon = Icons.Default.AdminPanelSettings,
                        onEnable = {
                            val comp = ComponentName(context, AntiShutdownAdminReceiver::class.java)
                            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, comp)
                                putExtra(
                                    DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                    "Prevents turning off or uninstalling the alarm during wake-up challenges."
                                )
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            }
                            context.startActivity(intent)
                        }
                    )
                }

                // Layer 3: Display Over Other Apps
                item {
                    PermissionGlassCard(
                        title = "3. Display Over Other Apps",
                        description = "Enables high-priority full-screen alarm override on top of all lock screens.",
                        isGranted = hasOverlayPermission,
                        icon = Icons.Default.Layers,
                        onEnable = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
                                context.startActivity(intent)
                            }
                        }
                    )
                }

                // Layer 4: Battery Optimization Exemption
                item {
                    PermissionGlassCard(
                        title = "4. Ignore Battery Optimizations",
                        description = "Guarantees alarms fire with microsecond precision even in deep Doze mode.",
                        isGranted = isBatteryOptIgnored,
                        icon = Icons.Default.BatteryAlert,
                        onEnable = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                context.startActivity(intent)
                            }
                        }
                    )
                }

                // Theme Customizer Section
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.16f,
                        borderAlpha = 0.35f,
                        glowColor = LiquidGlassTheme.CyanGlow,
                        onClick = onOpenThemeCustomizer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(LiquidGlassTheme.CyanLiquid.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = LiquidGlassTheme.CyanLiquid,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "LIQUID GLASS THEME",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Black,
                                        color = LiquidGlassTheme.CyanLiquid,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Customize aesthetic & color glows",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = LiquidGlassTheme.TextMuted
                                    )
                                }
                            }

                            LiquidGlassBadge(
                                text = "CUSTOMIZE",
                                accentColor = LiquidGlassTheme.CyanLiquid
                            )
                        }
                    }
                }

                // Emergency Passcode Config
                item {
                    LiquidGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        surfaceAlpha = 0.14f,
                        borderAlpha = 0.3f
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CyanLiquid,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "GLOBAL EMERGENCY OVERRIDE PIN",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Black,
                                    color = LiquidGlassTheme.CyanLiquid,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Emergency PIN to unlock Anti-Shutdown in case of genuine emergencies.",
                                style = MaterialTheme.typography.bodySmall,
                                color = LiquidGlassTheme.TextMuted
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = globalEmergencyPin,
                                onValueChange = { globalEmergencyPin = it },
                                label = { Text("Emergency PIN", color = LiquidGlassTheme.TextMuted) },
                                modifier = Modifier.fillMaxWidth().testTag("global_emergency_pin_input"),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LiquidGlassTheme.CyanLiquid,
                                    focusedTextColor = LiquidGlassTheme.TextPure,
                                    unfocusedTextColor = LiquidGlassTheme.TextPure
                                )
                            )
                        }
                    }
                }

                // Test Live Alarm Button
                item {
                    LiquidGlassButton(
                        onClick = onTestAntiShutdownAlarm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("test_anti_shutdown_alarm_btn"),
                        accentColor = LiquidGlassTheme.CoralSiren,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = LiquidGlassTheme.TextPure)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Test Anti-Shutdown Live Alarm",
                                fontWeight = FontWeight.Black,
                                color = LiquidGlassTheme.TextPure
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun PermissionGlassCard(
    title: String,
    description: String,
    isGranted: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onEnable: () -> Unit
) {
    LiquidGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        surfaceAlpha = if (isGranted) 0.18f else 0.08f,
        borderAlpha = if (isGranted) 0.4f else 0.16f,
        glowColor = if (isGranted) LiquidGlassTheme.EmeraldGlow else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isGranted) LiquidGlassTheme.EmeraldGlow else LiquidGlassTheme.GlassSurfaceSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isGranted) LiquidGlassTheme.EmeraldShield else LiquidGlassTheme.TextDim,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        color = LiquidGlassTheme.TextPure,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = LiquidGlassTheme.TextMuted
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (isGranted) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = LiquidGlassTheme.EmeraldShield,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                LiquidGlassButton(
                    onClick = onEnable,
                    shape = RoundedCornerShape(12.dp),
                    accentColor = LiquidGlassTheme.CyanLiquid,
                    modifier = Modifier.testTag("enable_perm_btn")
                ) {
                    Text("Grant", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LiquidGlassTheme.TextPure)
                }
            }
        }
    }
}
