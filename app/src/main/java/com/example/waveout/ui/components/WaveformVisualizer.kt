package com.example.waveout.ui.components

import android.graphics.BlurMaskFilter
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    frequency: Float,
    amplitude: Float = 1f,
    modifier: Modifier = Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) (1000f / (frequency / 100f).coerceAtLeast(1f)).toInt() else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    val idleBreathing by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idleBreathing"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        
        val actualAmplitude = if (isPlaying) amplitude * (height / 3f) else 2f * idleBreathing
        val actualFrequency = if (isPlaying) (frequency / 200f).coerceAtLeast(1f) else 1f

        val layers = listOf(
            Triple(1f, 1f, 100), // Main wave (alpha 100%)
            Triple(0.6f, 1.2f, 60), // Secondary (alpha 60%, wider)
            Triple(0.3f, 1.5f, 30)  // Tertiary (alpha 30%, widest)
        )

        for ((opacity, spread, _) in layers) {
            val path = Path()
            val pointCount = 100
            for (i in 0..pointCount) {
                val x = width * (i.toFloat() / pointCount)
                val normalizedX = (i.toFloat() / pointCount) * 2f * Math.PI.toFloat() * actualFrequency
                
                // Add some envelope so it goes to zero at edges
                val envelope = Math.sin((i.toFloat() / pointCount) * Math.PI)
                
                val y = centerY + sin(normalizedX + time * spread) * actualAmplitude * envelope
                if (i == 0) {
                    path.moveTo(x, y.toFloat())
                } else {
                    path.lineTo(x, y.toFloat())
                }
            }

            drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    this.color = color.copy(alpha = opacity)
                    this.style = PaintingStyle.Stroke
                    this.strokeWidth = 6f
                    this.strokeCap = StrokeCap.Round
                    this.strokeJoin = StrokeJoin.Round
                }
                
                // Glow effect
                val frameworkPaint = paint.asFrameworkPaint()
                frameworkPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
                canvas.drawPath(path, paint)
                
                frameworkPaint.maskFilter = null
                canvas.drawPath(path, paint)
            }
        }
    }
}
