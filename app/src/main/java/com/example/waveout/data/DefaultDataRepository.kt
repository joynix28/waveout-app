package com.example.waveout.data

import com.example.waveout.data.model.SessionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SessionStore {
    private val _sessions = MutableStateFlow<List<SessionRecord>>(emptyList())
    val sessions: Flow<List<SessionRecord>> = _sessions.asStateFlow()
    
    fun addSession(session: SessionRecord) {
        _sessions.value = _sessions.value + session
    }
    
    fun clearAll() {
        _sessions.value = emptyList()
    }
    
    fun exportToCsv(): String {
        val header = "id,mode,durationMs,frequencyHz,timestamp,completed"
        val rows = _sessions.value.joinToString("\n") { s ->
            "${s.id},${s.mode},${s.durationMs},${s.frequencyHz},${s.timestamp},${s.completed}"
        }
        return "$header\n$rows"
    }
}
