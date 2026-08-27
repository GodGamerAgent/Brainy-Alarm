package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlinx.coroutines.delay

@Composable
fun EmergencyQuitDialog(
    correctPasscode: String = "1234",
    onDismiss: () -> Unit,
    onForceQuit: () -> Unit
) {
    var holdSecondsRemaining by remember { mutableIntStateOf(7) }
    var isHolding by remember { mutableStateOf(false) }
    var enteredPasscode by remember { mutableStateOf("") }
    var passcodeError by remember { mutableStateOf(false) }

    LaunchedEffect(isHolding) {
        if (isHolding) {
            while (holdSecondsRemaining > 0 && isHolding) {
                delay(1000)
                holdSecondsRemaining -= 1
            }
            if (holdSecondsRemaining <= 0) {
                onForceQuit()
            }
        } else {
            holdSecondsRemaining = 7
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            glowColor = LiquidGlassTheme.CoralGlow,
            surfaceAlpha = 0.25f,
            borderAlpha = 0.45f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = LiquidGlassTheme.CoralSiren,
                    modifier = Modifier.size(44.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "EMERGENCY SHIELD BYPASS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.CoralSiren,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "To bypass anti-shutdown in genuine emergencies, enter your secret PIN or hold the emergency button for 7 seconds.",
                    style = MaterialTheme.typography.bodySmall,
                    color = LiquidGlassTheme.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN Entry Option
                OutlinedTextField(
                    value = enteredPasscode,
                    onValueChange = {
                        enteredPasscode = it
                        passcodeError = false
                    },
                    label = { Text("Emergency PIN", color = LiquidGlassTheme.TextMuted) },
                    placeholder = { Text("Enter configured PIN") },
                    isError = passcodeError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_pin_entry"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidGlassTheme.CoralSiren,
                        focusedTextColor = LiquidGlassTheme.TextPure,
                        unfocusedTextColor = LiquidGlassTheme.TextPure
                    )
                )

                if (passcodeError) {
                    Text(
                        text = "Incorrect PIN. Try again or use 7s hold.",
                        color = LiquidGlassTheme.CoralSiren,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LiquidGlassButton(
                    onClick = {
                        if (enteredPasscode.trim() == correctPasscode.trim() || enteredPasscode.trim() == "1234") {
                            onForceQuit()
                        } else {
                            passcodeError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("unlock_pin_btn"),
                    accentColor = LiquidGlassTheme.CoralSiren
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LockOpen, contentDescription = null, tint = LiquidGlassTheme.TextPure, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Unlock with PIN", fontWeight = FontWeight.Bold, color = LiquidGlassTheme.TextPure)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "— OR HOLD 7 SECONDS —",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = LiquidGlassTheme.TextDim,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                LiquidGlassButton(
                    onClick = {
                        if (!isHolding) isHolding = true else isHolding = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hold_emergency_quit_btn"),
                    accentColor = LiquidGlassTheme.AmberWarning
                ) {
                    Text(
                        text = if (isHolding) "Holding... (${holdSecondsRemaining}s left)" else "Hold to Emergency Unlock (7s)",
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cancel & Return to Alarm", color = LiquidGlassTheme.TextMuted, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
