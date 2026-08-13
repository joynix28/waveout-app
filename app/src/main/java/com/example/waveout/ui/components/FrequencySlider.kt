package com.example.waveout.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.exp
import kotlin.math.ln

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencySlider(
    frequency: Float,
    onFrequencyChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minHz: Float = 20f,
    maxHz: Float = 20000f
) {
    val minLog = ln(minHz)
    val maxLog = ln(maxHz)
    
    fun freqToLogPos(f: Float): Float {
        return ((ln(f.coerceIn(minHz, maxHz)) - minLog) / (maxLog - minLog)).coerceIn(0f, 1f)
    }
    
    fun logPosToFreq(p: Float): Float {
        return exp(minLog + p * (maxLog - minLog))
    }
    
    var sliderPosition by remember(frequency) { mutableStateOf(freqToLogPos(frequency)) }

    val formattedFreq = if (frequency >= 1000f) {
        String.format("%.1f kHz", frequency / 1000f)
    } else {
        "${frequency.toInt()} Hz"
    }

    val bandLabel = when {
        frequency < 60f -> "Sub Bass"
        frequency < 250f -> "Bass"
        frequency < 2000f -> "Midrange"
        frequency < 6000f -> "Presence"
        frequency < 10000f -> "Brilliance"
        else -> "Air"
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formattedFreq,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Slider(
            value = sliderPosition,
            onValueChange = { 
                sliderPosition = it
                onFrequencyChange(logPosToFreq(it))
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            track = { _ ->
                val trackGradient = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        Color(0xFFB026FF) // Purple accent
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(trackGradient, MaterialTheme.shapes.small)
                )
            }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = bandLabel,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
