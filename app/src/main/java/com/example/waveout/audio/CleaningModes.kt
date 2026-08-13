package com.example.waveout.audio

enum class CleaningMode(val displayName: String, val description: String, val iconName: String) {
    WATER_EJECT("Water Eject", "Remove water from speaker", "water_drop"),
    DUST_CLEAN("Dust Clean", "Remove dust & debris", "air"),
    DEEP_CLEAN("Deep Clean", "Full spectrum clean", "cleaning_services"),
    CUSTOM("Custom", "Set your own frequency", "tune")
}

data class CleaningPreset(
    val mode: CleaningMode,
    val frequencies: List<Float> = emptyList(),
    val sweepStart: Float = 0f,
    val sweepEnd: Float = 0f,
    val durationMs: Long,
    val usePulse: Boolean = false,
    val useVibration: Boolean = true,
    val pulseIntervalMs: Long = 0L
)

object PresetLibrary {
    val WATER_EJECT_PRESET = CleaningPreset(
        mode = CleaningMode.WATER_EJECT,
        frequencies = listOf(165f),
        durationMs = 30000L,
        useVibration = true
    )

    val DUST_PRESET = CleaningPreset(
        mode = CleaningMode.DUST_CLEAN,
        sweepStart = 300f,
        sweepEnd = 800f,
        durationMs = 20000L,
        usePulse = true,
        pulseIntervalMs = 200L,
        useVibration = true
    )

    val DEEP_CLEAN_PRESET = CleaningPreset(
        mode = CleaningMode.DEEP_CLEAN,
        frequencies = listOf(165f, 440f), // simplified sequence
        sweepStart = 200f,
        sweepEnd = 2000f,
        durationMs = 60000L,
        useVibration = true
    )

    val presets = listOf(WATER_EJECT_PRESET, DUST_PRESET, DEEP_CLEAN_PRESET)
}
