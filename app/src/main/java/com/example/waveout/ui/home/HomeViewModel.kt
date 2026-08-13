package com.example.waveout.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waveout.audio.AudioEngine
import com.example.waveout.audio.CleaningMode
import com.example.waveout.audio.VibrationEngine
import com.example.waveout.data.SessionStore
import com.example.waveout.data.model.SessionRecord
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _selectedMode = MutableStateFlow(CleaningMode.WATER_EJECT)
    val selectedMode: StateFlow<CleaningMode> = _selectedMode.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentFrequency = MutableStateFlow(165f)
    val currentFrequency: StateFlow<Float> = _currentFrequency.asStateFlow()

    private val _timerProgress = MutableStateFlow(0f)
    val timerProgress: StateFlow<Float> = _timerProgress.asStateFlow()

    private val _timeRemainingMs = MutableStateFlow(0L)
    val timeRemainingMs: StateFlow<Long> = _timeRemainingMs.asStateFlow()

    private val _totalDurationMs = MutableStateFlow(30_000L)
    val totalDurationMs: StateFlow<Long> = _totalDurationMs.asStateFlow()

    private val _useVibration = MutableStateFlow(true)
    val useVibration: StateFlow<Boolean> = _useVibration.asStateFlow()

    private val _customFrequency = MutableStateFlow(165f)
    val customFrequency: StateFlow<Float> = _customFrequency.asStateFlow()

    private val _customDuration = MutableStateFlow(30)
    val customDuration: StateFlow<Int> = _customDuration.asStateFlow()

    private var cleaningJob: Job? = null

    fun selectMode(mode: CleaningMode) {
        if (!_isPlaying.value) {
            _selectedMode.value = mode
        }
    }

    fun startCleaning(audioEngine: AudioEngine, vibrationEngine: VibrationEngine) {
        if (_isPlaying.value) return
        _isPlaying.value = true

        if (_useVibration.value) {
            vibrationEngine.vibratePattern(_selectedMode.value)
        }

        cleaningJob = viewModelScope.launch {
            val mode = _selectedMode.value
            val freq = when (mode) {
                CleaningMode.WATER_EJECT -> 165f
                CleaningMode.DUST_CLEAN -> 500f
                CleaningMode.DEEP_CLEAN -> 440f
                CleaningMode.CUSTOM -> _customFrequency.value
            }
            val durMs = when (mode) {
                CleaningMode.WATER_EJECT -> 30_000L
                CleaningMode.DUST_CLEAN -> 20_000L
                CleaningMode.DEEP_CLEAN -> 60_000L
                CleaningMode.CUSTOM -> _customDuration.value * 1000L
            }

            when (mode) {
                CleaningMode.WATER_EJECT -> playWaterEject(audioEngine)
                CleaningMode.DUST_CLEAN -> playDustClean(audioEngine)
                CleaningMode.DEEP_CLEAN -> playDeepClean(audioEngine)
                CleaningMode.CUSTOM -> playCustom(audioEngine)
            }

            // Save completed session to SessionStore
            SessionStore.addSession(
                SessionRecord(
                    mode = mode.displayName,
                    durationMs = durMs,
                    frequencyHz = freq,
                    completed = true
                )
            )

            // Auto-stop when done
            stopCleaning(audioEngine, vibrationEngine, completed = true)
        }
    }

    fun stopCleaning(audioEngine: AudioEngine, vibrationEngine: VibrationEngine, completed: Boolean = false) {
        val wasRunning = _isPlaying.value
        cleaningJob?.cancel()
        cleaningJob = null
        audioEngine.stopTone()
        vibrationEngine.stop()
        _isPlaying.value = false
        _timerProgress.value = 0f
        _timeRemainingMs.value = 0L

        if (wasRunning && !completed) {
            SessionStore.addSession(
                SessionRecord(
                    mode = _selectedMode.value.displayName,
                    durationMs = _totalDurationMs.value - _timeRemainingMs.value,
                    frequencyHz = _currentFrequency.value,
                    completed = false
                )
            )
        }
    }

    fun setCustomFrequency(hz: Float) { _customFrequency.value = hz }
    fun setCustomDuration(seconds: Int) { _customDuration.value = seconds }
    fun toggleVibration() { _useVibration.value = !_useVibration.value }

    private suspend fun playWaterEject(audioEngine: AudioEngine) {
        val durationMs = 30_000L
        _totalDurationMs.value = durationMs
        _currentFrequency.value = 165f
        audioEngine.startTone(165f)
        runTimer(durationMs)
        audioEngine.stopTone()
    }

    private suspend fun playDustClean(audioEngine: AudioEngine) {
        val durationMs = 20_000L
        _totalDurationMs.value = durationMs
        audioEngine.startSweep(300f, 800f, durationMs)
        runTimer(durationMs) { progress ->
            _currentFrequency.value = 300f + (500f * progress)
        }
        audioEngine.stopTone()
    }

    private suspend fun playDeepClean(audioEngine: AudioEngine) {
        val totalMs = 60_000L
        _totalDurationMs.value = totalMs
        _currentFrequency.value = 165f
        audioEngine.startTone(165f)
        runTimer(15_000L)
        audioEngine.startSweep(200f, 2000f, 30_000L)
        runTimer(30_000L) { progress ->
            _currentFrequency.value = 200f + (1800f * progress)
        }
        _currentFrequency.value = 440f
        audioEngine.startTone(440f)
        runTimer(15_000L)
        audioEngine.stopTone()
    }

    private suspend fun playCustom(audioEngine: AudioEngine) {
        val durationMs = _customDuration.value * 1000L
        _totalDurationMs.value = durationMs
        val freq = _customFrequency.value
        _currentFrequency.value = freq
        audioEngine.startTone(freq)
        runTimer(durationMs)
        audioEngine.stopTone()
    }

    private suspend fun runTimer(totalMs: Long, onTick: ((Float) -> Unit)? = null) {
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed >= totalMs) break
            val progress = (elapsed.toFloat() / totalMs).coerceIn(0f, 1f)
            _timerProgress.value = progress
            _timeRemainingMs.value = (totalMs - elapsed).coerceAtLeast(0L)
            onTick?.invoke(progress)
            delay(16L)
        }
    }
}
