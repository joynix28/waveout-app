package com.example.waveout.data.model

data class SessionRecord(
    val id: Long = System.currentTimeMillis(),
    val mode: String,
    val durationMs: Long,
    val frequencyHz: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val completed: Boolean = true
)
