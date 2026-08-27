package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class AlarmSoundPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private var rampJob: Job? = null
    private var vibrateJob: Job? = null
    private var isPlaying = false

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun playAlarm(
        soundUri: String,
        targetVolume: Float = 1.0f,
        isCrescendo: Boolean = true,
        crescendoDurationSec: Int = 30,
        vibratePattern: String = "PULSE",
        scope: CoroutineScope
    ) {
        stopAlarm()
        isPlaying = true

        // Ensure alarm stream is unmuted and set high
        try {
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVol, 0)
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Volume set error: ${e.message}")
        }

        // Start sound
        if (soundUri.startsWith("builtin:") || soundUri.isEmpty()) {
            startSynthesizer(soundUri, targetVolume, isCrescendo, crescendoDurationSec, scope)
        } else {
            startMediaPlayer(soundUri, targetVolume, isCrescendo, crescendoDurationSec, scope)
        }

        // Start vibration
        startVibration(vibratePattern, scope)
    }

    private fun startMediaPlayer(
        uriString: String,
        targetVolume: Float,
        isCrescendo: Boolean,
        crescendoSec: Int,
        scope: CoroutineScope
    ) {
        try {
            val uri = Uri.parse(uriString)
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )
                setDataSource(context, uri)
                isLooping = true
                val initialVol = if (isCrescendo) 0.05f else targetVolume
                setVolume(initialVol, initialVol)
                prepare()
                start()
            }

            if (isCrescendo) {
                rampVolume(initialVolume = 0.05f, targetVolume = targetVolume, durationSec = crescendoSec, scope = scope) { vol ->
                    mediaPlayer?.setVolume(vol, vol)
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "MediaPlayer fallback to synthesizer: ${e.message}")
            startSynthesizer("builtin:extreme_siren", targetVolume, isCrescendo, crescendoSec, scope)
        }
    }

    private fun startSynthesizer(
        soundKey: String,
        targetVolume: Float,
        isCrescendo: Boolean,
        crescendoSec: Int,
        scope: CoroutineScope
    ) {
        val sampleRate = 44100
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = maxOf(minBufferSize, sampleRate / 2)

        try {
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()

            var currentVol = if (isCrescendo) 0.1f else targetVolume
            if (isCrescendo) {
                rampVolume(0.1f, targetVolume, crescendoSec, scope) { vol ->
                    currentVol = vol
                }
            }

            synthJob = scope.launch(Dispatchers.Default) {
                val buffer = ShortArray(bufferSize)
                var phase = 0.0
                var sampleCounter = 0L

                while (isActive && isPlaying) {
                    val track = audioTrack ?: break
                    val soundType = soundKey.removePrefix("builtin:")

                    for (i in buffer.indices) {
                        val t = (sampleCounter + i).toDouble() / sampleRate
                        val freq = when (soundType) {
                            "digital_beep" -> {
                                val pulse = (t * 4).toInt() % 2
                                if (pulse == 0) 1200.0 else 0.0
                            }
                            "emergency_pulse" -> {
                                val step = (t * 3).toInt() % 2
                                if (step == 0) 880.0 else 1760.0
                            }
                            "classic_bell" -> {
                                val mod = sin(2.0 * Math.PI * 6.0 * t)
                                900.0 + mod * 200.0
                            }
                            "rooster" -> {
                                val cycle = t % 2.5
                                if (cycle < 0.6) 800.0 + cycle * 400.0 else if (cycle < 1.4) 1400.0 else 0.0
                            }
                            else -> { // extreme_siren
                                val sweep = sin(2.0 * Math.PI * 1.5 * t)
                                700.0 + (sweep + 1.0) * 600.0 // sweeps 700Hz to 1900Hz
                            }
                        }

                        if (freq <= 0.0) {
                            buffer[i] = 0
                        } else {
                            phase += 2.0 * Math.PI * freq / sampleRate
                            if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI
                            val rawSample = sin(phase)
                            val scaled = (rawSample * Short.MAX_VALUE * currentVol).toInt()
                            buffer[i] = scaled.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                        }
                    }

                    sampleCounter += buffer.size
                    track.write(buffer, 0, buffer.size)
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Synth error: ${e.message}")
        }
    }

    private fun rampVolume(
        initialVolume: Float,
        targetVolume: Float,
        durationSec: Int,
        scope: CoroutineScope,
        onVolumeUpdate: (Float) -> Unit
    ) {
        rampJob?.cancel()
        rampJob = scope.launch(Dispatchers.Default) {
            val steps = (durationSec * 10).coerceAtLeast(10)
            val stepDelay = (durationSec * 1000L) / steps
            val volumeDelta = (targetVolume - initialVolume) / steps
            var current = initialVolume

            for (i in 1..steps) {
                if (!isActive || !isPlaying) break
                current += volumeDelta
                onVolumeUpdate(current.coerceIn(0.0f, 1.0f))
                delay(stepDelay)
            }
            onVolumeUpdate(targetVolume)
        }
    }

    private fun startVibration(patternType: String, scope: CoroutineScope) {
        vibrateJob?.cancel()
        if (patternType == "OFF" || !vibrator.hasVibrator()) return

        vibrateJob = scope.launch(Dispatchers.Default) {
            val pattern = when (patternType) {
                "CONTINUOUS" -> longArrayOf(0, 1000, 100)
                "STACCATO" -> longArrayOf(0, 150, 100, 150, 100, 150, 400)
                "HEARTBEAT" -> longArrayOf(0, 100, 150, 150, 600)
                else -> longArrayOf(0, 500, 300) // PULSE
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val effect = VibrationEffect.createWaveform(pattern, 0)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern, 0)
                }
            } catch (e: Exception) {
                Log.e("AlarmSoundPlayer", "Vibration failed: ${e.message}")
            }
        }
    }

    fun stopAlarm() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        rampJob?.cancel()
        rampJob = null
        vibrateJob?.cancel()
        vibrateJob = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Error releasing MediaPlayer: ${e.message}")
        }

        try {
            audioTrack?.stop()
            audioTrack?.release()
            audioTrack = null
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Error releasing AudioTrack: ${e.message}")
        }

        try {
            vibrator.cancel()
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Error canceling vibrator: ${e.message}")
        }
    }
}
