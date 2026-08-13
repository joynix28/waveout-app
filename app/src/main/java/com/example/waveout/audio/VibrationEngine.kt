package com.example.waveout.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class VibrationEngine private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: VibrationEngine? = null

        fun getInstance(context: Context): VibrationEngine {
            return instance ?: synchronized(this) {
                instance ?: VibrationEngine(context.applicationContext).also { instance = it }
            }
        }
    }

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibratePattern(mode: CleaningMode) {
        if (!vibrator.hasVibrator()) return
        stop()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val effect = when (mode) {
                CleaningMode.WATER_EJECT -> {
                    // Long continuous vibration
                    VibrationEffect.createOneShot(30000, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                CleaningMode.DUST_CLEAN -> {
                    // Repeated short pulses
                    val timings = longArrayOf(0, 100, 100, 100, 100)
                    val amplitudes = intArrayOf(0, 255, 0, 255, 0)
                    VibrationEffect.createWaveform(timings, amplitudes, 0)
                }
                CleaningMode.DEEP_CLEAN -> {
                    // Complex waveform pattern
                    val timings = longArrayOf(0, 500, 200, 500, 200, 1000)
                    val amplitudes = intArrayOf(0, 128, 0, 255, 0, 255)
                    VibrationEffect.createWaveform(timings, amplitudes, 0)
                }
                CleaningMode.CUSTOM -> {
                    VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
                }
            }
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            when (mode) {
                CleaningMode.WATER_EJECT -> vibrator.vibrate(30000)
                CleaningMode.DUST_CLEAN -> vibrator.vibrate(longArrayOf(0, 100, 100, 100, 100), 0)
                CleaningMode.DEEP_CLEAN -> vibrator.vibrate(longArrayOf(0, 500, 200, 500, 200, 1000), 0)
                CleaningMode.CUSTOM -> vibrator.vibrate(500)
            }
        }
    }

    fun vibrateOnce(durationMs: Long, amplitude: Int = 128) {
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    fun stop() {
        vibrator.cancel()
    }
}
