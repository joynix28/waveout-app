package com.example.waveout.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class AudioEngine private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var instance: AudioEngine? = null

        fun getInstance(context: Context): AudioEngine {
            return instance ?: synchronized(this) {
                instance ?: AudioEngine(context.applicationContext).also { instance = it }
            }
        }
    }

    private var audioTrack: AudioTrack? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var originalVolume: Int = 0

    var isPlaying: Boolean = false
        private set

    private val sampleRate = 44100
    private val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private fun initAudioTrack() {
        audioTrack?.release()
        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()
    }

    private fun maximizeVolume() {
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
    }

    private fun restoreVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
    }

    fun startTone(frequency: Float, durationMs: Long = -1) {
        stopTone()
        initAudioTrack()
        maximizeVolume()
        isPlaying = true
        audioTrack?.play()

        job = scope.launch {
            val buffer = ShortArray(minBufferSize)
            var angle = 0.0
            val increment = 2.0 * Math.PI * frequency / sampleRate
            val startTime = System.currentTimeMillis()

            while (isActive && isPlaying) {
                if (durationMs > 0 && System.currentTimeMillis() - startTime >= durationMs) {
                    break
                }
                for (i in buffer.indices step 2) {
                    val sample = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
                    buffer[i] = sample // Left channel
                    buffer[i + 1] = sample // Right channel
                    angle += increment
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
            stopToneInternal()
        }
    }

    fun startSweep(startHz: Float, endHz: Float, durationMs: Long) {
        stopTone()
        initAudioTrack()
        maximizeVolume()
        isPlaying = true
        audioTrack?.play()

        job = scope.launch {
            val buffer = ShortArray(minBufferSize)
            var angle = 0.0
            val startTime = System.currentTimeMillis()

            while (isActive && isPlaying) {
                val elapsed = System.currentTimeMillis() - startTime
                if (elapsed >= durationMs) {
                    break
                }
                val progress = elapsed.toFloat() / durationMs
                val currentHz = startHz + (endHz - startHz) * progress
                val increment = 2.0 * Math.PI * currentHz / sampleRate

                for (i in buffer.indices step 2) {
                    val sample = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
                    buffer[i] = sample
                    buffer[i + 1] = sample
                    angle += increment
                }
                audioTrack?.write(buffer, 0, buffer.size)
            }
            stopToneInternal()
        }
    }

    fun startPulse(frequency: Float, pulseIntervalMs: Long) {
        stopTone()
        initAudioTrack()
        maximizeVolume()
        isPlaying = true
        audioTrack?.play()

        job = scope.launch {
            val bufferSize = (sampleRate * pulseIntervalMs / 1000).toInt() * 2 // stereo
            val buffer = ShortArray(bufferSize)
            val increment = 2.0 * Math.PI * frequency / sampleRate
            
            while (isActive && isPlaying) {
                var angle = 0.0
                for (i in buffer.indices step 2) {
                    val sample = (sin(angle) * Short.MAX_VALUE).toInt().toShort()
                    buffer[i] = sample
                    buffer[i + 1] = sample
                    angle += increment
                }
                audioTrack?.write(buffer, 0, buffer.size)
                delay(pulseIntervalMs)
            }
            stopToneInternal()
        }
    }

    fun stopTone() {
        job?.cancel()
        stopToneInternal()
    }

    private fun stopToneInternal() {
        if (!isPlaying) return
        isPlaying = false
        audioTrack?.let {
            if (it.state == AudioTrack.STATE_INITIALIZED) {
                it.stop()
                it.flush()
            }
            it.release()
        }
        audioTrack = null
        restoreVolume()
    }
}
