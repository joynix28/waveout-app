package com.example.waveout.data

import com.example.waveout.data.model.SessionRecord

class AppDatabase private constructor() {
    val sessions = mutableListOf<SessionRecord>()

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: AppDatabase().also { instance = it }
            }
        }
    }
}
