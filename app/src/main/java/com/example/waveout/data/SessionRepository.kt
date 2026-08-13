package com.example.waveout.data

import com.example.waveout.data.model.SessionRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class SessionRepository private constructor() {
    private val db = AppDatabase.getInstance()
    private val _sessionsFlow = MutableStateFlow<List<SessionRecord>>(emptyList())

    companion object {
        @Volatile
        private var instance: SessionRepository? = null

        fun getInstance(): SessionRepository {
            return instance ?: synchronized(this) {
                instance ?: SessionRepository().also { instance = it }
            }
        }
    }

    suspend fun insertSession(session: SessionRecord) {
        db.sessions.add(0, session)
        _sessionsFlow.update { db.sessions.toList() }
    }

    fun getAllSessions(): Flow<List<SessionRecord>> {
        return _sessionsFlow.asStateFlow()
    }

    fun getRecentSessions(limit: Int = 10): Flow<List<SessionRecord>> {
        return _sessionsFlow.map { list -> list.take(limit) }
    }

    suspend fun deleteAllSessions() {
        db.sessions.clear()
        _sessionsFlow.update { emptyList() }
    }

    fun exportToCsv(sessions: List<SessionRecord>): String {
        val builder = java.lang.StringBuilder()
        builder.append("Id,Mode,DurationMs,FrequencyHz,Timestamp,Completed\n")
        for (session in sessions) {
            builder.append("${session.id},${session.mode},${session.durationMs},${session.frequencyHz},${session.timestamp},${session.completed}\n")
        }
        return builder.toString()
    }
}
