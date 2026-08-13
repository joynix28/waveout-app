package com.example.waveout.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TimerRing(
    progress: Float,
    timeRemainingMs: Long,
    totalMs: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    color: Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "progress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.width * 0.05f
            val radius = (size.width - strokeWidth) / 2f
            
            // Background track
            drawArc(
                color = color.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc with glow
            val sweepAngle = 360f * animatedProgress
            
            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    this.color = color.copy(alpha = if (isRunning) pulseAlpha else 1f)
                    this.style = PaintingStyle.Stroke
                    this.strokeWidth = strokeWidth
                    this.strokeCap = StrokeCap.Round
                }
                
                if (isRunning) {
                    val frameworkPaint = paint.asFrameworkPaint()
                    frameworkPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
                    canvas.drawArc(
                        left = strokeWidth / 2f,
                        top = strokeWidth / 2f,
                        right = size.width - strokeWidth / 2f,
                        bottom = size.height - strokeWidth / 2f,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        paint = paint
                    )
                    frameworkPaint.maskFilter = null
                }
                
                canvas.drawArc(
                    left = strokeWidth / 2f,
                    top = strokeWidth / 2f,
                    right = size.width - strokeWidth / 2f,
                    bottom = size.height - strokeWidth / 2f,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    paint = paint
                )
            }
        }
        
        val seconds = (timeRemainingMs / 1000) % 60
        val minutes = (timeRemainingMs / 1000) / 60
        val timeString = String.format("%02d:%02d", minutes, seconds)
        
        Text(
            text = timeString,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
