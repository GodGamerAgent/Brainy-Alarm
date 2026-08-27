package com.example.ui.screens.ringing.challenges

import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme

@Composable
fun BarcodeChallenge(
    targetBarcode: String = "",
    onCompleted: () -> Unit
) {
    var manualCodeInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ROOM BARCODE / QR SCANNER",
                style = MaterialTheme.typography.labelMedium,
                color = LiquidGlassTheme.CyanLiquid,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (targetBarcode.isEmpty()) "Scan any household barcode to confirm awake!" else "Walk to room location & scan barcode: $targetBarcode",
                style = MaterialTheme.typography.bodyMedium,
                color = LiquidGlassTheme.TextMuted,
                textAlign = TextAlign.Center
            )
        }

        // Scanner viewfinder card
        LiquidGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            surfaceAlpha = 0.16f,
            borderAlpha = 0.35f,
            glowColor = LiquidGlassTheme.CyanGlow
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Viewfinder frame
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .border(2.dp, LiquidGlassTheme.CyanLiquid, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scanner Viewfinder",
                            modifier = Modifier.size(64.dp),
                            tint = LiquidGlassTheme.CyanLiquid
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Point camera at QR / Barcode",
                            color = LiquidGlassTheme.TextPure,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Actions & Fallback test scanner button
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LiquidGlassButton(
                onClick = onCompleted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("barcode_scan_success_btn"),
                accentColor = LiquidGlassTheme.EmeraldShield,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Scan / Match QR Code (Success)", fontWeight = FontWeight.Black, color = LiquidGlassTheme.TextPure)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualCodeInput,
                    onValueChange = { manualCodeInput = it },
                    placeholder = { Text("Or manual digits", color = LiquidGlassTheme.TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("manual_barcode_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LiquidGlassTheme.CyanLiquid,
                        focusedTextColor = LiquidGlassTheme.TextPure,
                        unfocusedTextColor = LiquidGlassTheme.TextPure
                    )
                )
                LiquidGlassButton(
                    onClick = {
                        if (targetBarcode.isEmpty() || manualCodeInput.trim() == targetBarcode.trim()) {
                            onCompleted()
                        }
                    },
                    enabled = manualCodeInput.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.height(52.dp),
                    accentColor = LiquidGlassTheme.CyanLiquid
                ) {
                    Text("Verify", color = LiquidGlassTheme.TextPure, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
