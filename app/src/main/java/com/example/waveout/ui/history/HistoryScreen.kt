package com.example.waveout.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.waveout.data.SessionStore
import com.example.waveout.data.model.SessionRecord
import com.example.waveout.model.AppLanguage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    appLanguage: AppLanguage = AppLanguage.FRENCH,
    modifier: Modifier = Modifier
) {
    val sessions by SessionStore.sessions.collectAsState(initial = emptyList())
    val isFr = appLanguage == AppLanguage.FRENCH

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (isFr) "Historique" else "History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (sessions.isNotEmpty()) {
                        IconButton(onClick = { SessionStore.clearAll() }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete all")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (sessions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.HistoryToggleOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    if (isFr) "Aucune session" else "No sessions yet",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (isFr) "Lancez un nettoyage pour le voir apparaître ici" else "Run a cleaning session to see it here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(modifier = Modifier.padding(padding)) {
                val mostUsed = sessions.groupBy { it.mode }
                    .maxByOrNull { it.value.size }?.key ?: "Water Eject"
                Text(
                    text = if (isFr) 
                        "Total sessions : ${sessions.size}  |  Favori : $mostUsed"
                    else 
                        "Total sessions: ${sessions.size}  |  Most used: $mostUsed",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(16.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(sessions.reversed()) { session ->
                        SessionCard(session = session)
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
    return sdf.format(Date(ts))
}

@Composable
fun SessionCard(session: SessionRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Speaker,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(session.mode, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${session.durationMs / 1000}s  \u2022  ${session.frequencyHz.toInt()} Hz",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatTimestamp(session.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Icon(
                    imageVector = if (session.completed) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
                    contentDescription = null,
                    tint = if (session.completed) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
