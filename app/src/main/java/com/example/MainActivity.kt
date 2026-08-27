package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AlarmEntity
import com.example.data.ChallengeType
import com.example.ui.screens.AlarmEditScreen
import com.example.ui.screens.AlarmListScreen
import com.example.ui.screens.AntiShutdownSettingsScreen
import com.example.ui.screens.ChallengePreviewDialog
import com.example.ui.screens.QuickNapScreen
import com.example.ui.screens.ThemeCustomizerDialog
import com.example.ui.theme.Theme
import com.example.ui.theme.ThemeManager
import com.example.ui.viewmodel.AlarmViewModel

sealed class AppScreen(val route: String) {
    data object AlarmList : AppScreen("alarm_list")
    data object AlarmEdit : AppScreen("alarm_edit")
    data object AntiShutdownSettings : AppScreen("anti_shutdown_settings")
    data object QuickNap : AppScreen("quick_nap")
}

class MainActivity : ComponentActivity() {

    private val viewModel: AlarmViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ThemeManager.init(this)

        // Request notification permission if needed (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val alarms by viewModel.allAlarms.collectAsState()
                    var editingAlarm by remember { mutableStateOf<AlarmEntity?>(null) }
                    var practiceChallengeType by remember { mutableStateOf<ChallengeType?>(null) }
                    var showThemeCustomizer by remember { mutableStateOf(false) }

                    NavHost(
                        navController = navController,
                        startDestination = AppScreen.AlarmList.route
                    ) {
                        composable(AppScreen.AlarmList.route) {
                            AlarmListScreen(
                                alarms = alarms,
                                onAddAlarm = {
                                    editingAlarm = null
                                    navController.navigate(AppScreen.AlarmEdit.route)
                                },
                                onEditAlarm = { alarm ->
                                    editingAlarm = alarm
                                    navController.navigate(AppScreen.AlarmEdit.route)
                                },
                                onToggleAlarm = { alarm, enabled ->
                                    viewModel.toggleAlarm(alarm, enabled)
                                },
                                onTestAlarm = { alarm ->
                                    viewModel.triggerTestAlarm(alarm)
                                },
                                onOpenAntiShutdownSettings = {
                                    navController.navigate(AppScreen.AntiShutdownSettings.route)
                                },
                                onOpenQuickNap = {
                                    navController.navigate(AppScreen.QuickNap.route)
                                },
                                onOpenPracticeChallenges = {
                                    practiceChallengeType = ChallengeType.MATH
                                },
                                onOpenThemeCustomizer = {
                                    showThemeCustomizer = true
                                }
                            )
                        }

                        composable(AppScreen.AlarmEdit.route) {
                            AlarmEditScreen(
                                initialAlarm = editingAlarm,
                                onSave = { alarm ->
                                    viewModel.saveAlarm(alarm)
                                },
                                onDelete = { alarm ->
                                    viewModel.deleteAlarm(alarm)
                                },
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(AppScreen.AntiShutdownSettings.route) {
                            AntiShutdownSettingsScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onTestAntiShutdownAlarm = {
                                    val defaultAlarm = alarms.firstOrNull() ?: AlarmEntity(
                                        label = "Anti-Shutdown Test Alarm",
                                        isAntiShutdownEnabled = true
                                    )
                                    viewModel.triggerTestAlarm(defaultAlarm)
                                },
                                onOpenThemeCustomizer = {
                                    showThemeCustomizer = true
                                }
                            )
                        }

                        composable(AppScreen.QuickNap.route) {
                            QuickNapScreen(
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onStartNap = { minutes, title ->
                                    viewModel.startQuickNap(minutes, title)
                                }
                            )
                        }
                    }

                    practiceChallengeType?.let { challenge ->
                        ChallengePreviewDialog(
                            challengeType = challenge,
                            onDismiss = { practiceChallengeType = null }
                        )
                    }

                    if (showThemeCustomizer) {
                        ThemeCustomizerDialog(
                            onDismiss = { showThemeCustomizer = false }
                        )
                    }
                }
            }
        }
    }
}
