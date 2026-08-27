package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun RoomQrToolDialog(
    initialLocation: String,
    initialBarcodeValue: String,
    onSave: (location: String, barcodeValue: String) -> Unit,
    onDismiss: () -> Unit
) {
    var locationName by remember { mutableStateOf(initialLocation.ifBlank { "Bathroom Mirror" }) }
    var barcodeValue by remember { mutableStateOf(initialBarcodeValue) }

    val presetLocations = listOf(
        "Bathroom Mirror" to "WAKEUP_BATHROOM_MIRROR_QR",
        "Kitchen Coffee Maker" to "WAKEUP_KITCHEN_COFFEE_QR",
        "Far Bedroom Desk" to "WAKEUP_BEDROOM_DESK_QR",
        "Any Household Barcode" to ""
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            glowColor = LiquidGlassTheme.CyanGlow,
            borderAlpha = 0.35f,
            surfaceAlpha = 0.22f
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(LiquidGlassTheme.CyanGlow),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = LiquidGlassTheme.CyanLiquid,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Room QR / Barcode Setup",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = LiquidGlassTheme.TextPure
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = LiquidGlassTheme.TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Force yourself to physically walk to another room to turn off the alarm!",
                    style = MaterialTheme.typography.bodySmall,
                    color = LiquidGlassTheme.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location Presets
                Text(
                    text = "QUICK ROOM PRESETS",
                    style = MaterialTheme.typography.labelSmall,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(8.dp))

                for ((name, value) in presetLocations) {
                    val isSelected = locationName == name
                    LiquidGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        surfaceAlpha = if (isSelected) 0.28f else 0.08f,
                        borderAlpha = if (isSelected) 0.5f else 0.15f,
                        glowColor = if (isSelected) LiquidGlassTheme.CyanGlow else null,
                        onClick = {
                            locationName = name
                            barcodeValue = value
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isSelected) LiquidGlassTheme.CyanLiquid else LiquidGlassTheme.TextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) LiquidGlassTheme.TextPure else LiquidGlassTheme.TextMuted,
                                    fontSize = 13.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = LiquidGlassTheme.CyanLiquid,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Barcode input
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Room Target Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("room_qr_name_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = barcodeValue,
                    onValueChange = { barcodeValue = it },
                    label = { Text("Target QR / Barcode Code (Optional)") },
                    placeholder = { Text("Leave blank to match any barcode") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("room_qr_code_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                LiquidGlassButton(
                    onClick = {
                        onSave(locationName.ifBlank { "Bathroom Mirror" }, barcodeValue.trim())
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_room_qr_btn"),
                    accentColor = LiquidGlassTheme.CyanLiquid
                ) {
                    Text(
                        text = "Save Room Location",
                        fontWeight = FontWeight.Bold,
                        color = LiquidGlassTheme.TextPure
                    )
                }
            }
        }
    }
}
