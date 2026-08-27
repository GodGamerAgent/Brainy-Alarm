package com.example.ui.screens.ringing.challenges

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidGlassButton
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidGlassTheme
import kotlin.math.sqrt

@Composable
fun ShakeChallenge(
    targetShakes: Int = 25,
    onCompleted: () -> Unit
) {
    val context = LocalContext.current
    var shakeCount by remember { mutableIntStateOf(0) }
    var lastShakeTimestamp by remember { mutableLongStateOf(0L) }
    var currentForce by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]

                val gForce = sqrt((x * x + y * y + z * z).toDouble()).toFloat() / SensorManager.GRAVITY_EARTH
                currentForce = gForce

                if (gForce > 1.85f) { // Shake threshold
                    val now = System.currentTimeMillis()
                    if (now - lastShakeTimestamp > 250) { // 250ms debounce
                        lastShakeTimestamp = now
                        val next = shakeCount + 1
                        if (next >= targetShakes) {
                            shakeCount = targetShakes
                            onCompleted()
                        } else {
                            shakeCount = next
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val progress = (shakeCount.toFloat() / targetShakes.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "shake_progress")

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SHAKE PHONE",
                    style = MaterialTheme.typography.labelMedium,
                    color = LiquidGlassTheme.CyanLiquid,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "$shakeCount / $targetShakes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = LiquidGlassTheme.TextPure
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LiquidGlassTheme.CyanLiquid,
                trackColor = LiquidGlassTheme.GlassSurfaceSubtle
            )
        }

        // Circular Liquid Glass Shake Target
        LiquidGlassCard(
            modifier = Modifier.size(240.dp),
            shape = CircleShape,
            surfaceAlpha = 0.18f,
            borderAlpha = 0.4f,
            glowColor = LiquidGlassTheme.CyanGlow
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(200.dp),
                    strokeWidth = 10.dp,
                    color = LiquidGlassTheme.CyanLiquid,
                    trackColor = LiquidGlassTheme.GlassSurfaceSubtle
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = "Shake Phone",
                        modifier = Modifier.size(44.dp),
                        tint = LiquidGlassTheme.CyanLiquid
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = LiquidGlassTheme.TextPure
                    )
                }
            }
        }

        // Instructions & Simulation
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Shake your phone vigorously to wake up your body and mind!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = LiquidGlassTheme.TextMuted
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Manual simulator button for testing or emulator usage
            LiquidGlassButton(
                onClick = {
                    val next = shakeCount + 1
                    if (next >= targetShakes) {
                        shakeCount = targetShakes
                        onCompleted()
                    } else {
                        shakeCount = next
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shake_phone_simulate_button"),
                shape = RoundedCornerShape(16.dp),
                accentColor = LiquidGlassTheme.CyanLiquid
            ) {
                Text(
                    text = "Tap to Shake ($shakeCount / $targetShakes)",
                    fontWeight = FontWeight.Bold,
                    color = LiquidGlassTheme.TextPure
                )
            }
        }
    }
}
