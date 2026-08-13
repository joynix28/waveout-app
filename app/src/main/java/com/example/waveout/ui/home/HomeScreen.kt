package com.example.waveout.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waveout.audio.AudioEngine
import com.example.waveout.audio.CleaningMode
import com.example.waveout.audio.VibrationEngine
import com.example.waveout.ui.components.ModeCard
import com.example.waveout.ui.components.TimerRing
import com.example.waveout.ui.components.WaveformVisualizer
import com.example.waveout.ui.settings.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    appLanguage: AppLanguage = AppLanguage.FRENCH,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val audioEngine = remember { AudioEngine.getInstance(context) }
    val vibrationEngine = remember { VibrationEngine.getInstance(context) }
    val viewModel: HomeViewModel = viewModel()

    val selectedMode by viewModel.selectedMode.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val timerProgress by viewModel.timerProgress.collectAsState()
    val timeRemainingMs by viewModel.timeRemainingMs.collectAsState()
    val totalDurationMs by viewModel.totalDurationMs.collectAsState()
    val useVibration by viewModel.useVibration.collectAsState()
    val currentFreq by viewModel.currentFrequency.collectAsState()
    val customDuration by viewModel.customDuration.collectAsState()

    val isFr = appLanguage == AppLanguage.FRENCH
    
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopCleaning(audioEngine, vibrationEngine)
        }
    }
    
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text("WaveOut", color = MaterialTheme.colorScheme.onBackground)
                },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            Icons.Rounded.History, 
                            contentDescription = if (isFr) "Historique" else "History", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            Icons.Rounded.Settings, 
                            contentDescription = if (isFr) "Paramètres" else "Settings", 
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaveformVisualizer(
                isPlaying = isPlaying,
                frequency = currentFreq,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 16.dp),
                color = if (isPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modes = listOf(
                    CleaningMode.WATER_EJECT to Icons.Rounded.WaterDrop,
                    CleaningMode.DUST_CLEAN to Icons.Rounded.Air,
                    CleaningMode.DEEP_CLEAN to Icons.Rounded.AutoAwesome,
                    CleaningMode.CUSTOM to Icons.Rounded.Tune
                )
                
                modes.forEach { (mode, icon) ->
                    val localizedName = when (mode) {
                        CleaningMode.WATER_EJECT -> if (isFr) "Éjecter Eau" else "Water Eject"
                        CleaningMode.DUST_CLEAN -> if (isFr) "Poussière" else "Dust Clean"
                        CleaningMode.DEEP_CLEAN -> if (isFr) "Profond" else "Deep Clean"
                        CleaningMode.CUSTOM -> if (isFr) "Manuel" else "Custom"
                    }
                    ModeCard(
                        mode = mode,
                        displayName = localizedName,
                        icon = icon,
                        isSelected = selectedMode == mode,
                        onClick = { viewModel.selectMode(mode) },
                        modifier = Modifier.weight(1f).padding(4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                TimerRing(
                    progress = timerProgress,
                    timeRemainingMs = timeRemainingMs,
                    totalMs = totalDurationMs,
                    isRunning = isPlaying,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxSize()
                )
                
                Button(
                    onClick = {
                        if (isPlaying) {
                            viewModel.stopCleaning(audioEngine, vibrationEngine)
                        } else {
                            viewModel.startCleaning(audioEngine, vibrationEngine)
                        }
                    },
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Stop else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "Stop" else "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = useVibration,
                    onClick = { viewModel.toggleVibration() },
                    label = { Text(if (isFr) "Vibrations" else "Vibration") },
                    leadingIcon = { Icon(Icons.Rounded.Vibration, contentDescription = null) }
                )
                
                if (selectedMode == CleaningMode.CUSTOM) {
                    Slider(
                        value = customDuration.toFloat(),
                        onValueChange = { viewModel.setCustomDuration(it.toInt()) },
                        valueRange = 10f..120f,
                        modifier = Modifier.width(140.dp)
                    )
                    Text("${customDuration}s", color = MaterialTheme.colorScheme.onBackground)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = if (isFr) 
                    "Conseil : Placez le téléphone haut-parleur vers le bas"
                else 
                    "Tip: Place phone speaker-down for best results",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}
