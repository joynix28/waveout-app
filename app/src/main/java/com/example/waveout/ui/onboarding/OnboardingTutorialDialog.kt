package com.example.waveout.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.waveout.ui.settings.AppLanguage

data class TutorialStep(
    val icon: ImageVector,
    val titleFr: String,
    val titleEn: String,
    val descFr: String,
    val descEn: String
)

@Composable
fun OnboardingTutorialDialog(
    appLanguage: AppLanguage,
    onDismiss: () -> Unit
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val isFr = appLanguage == AppLanguage.FRENCH

    val steps = listOf(
        TutorialStep(
            icon = Icons.Rounded.WaterDrop,
            titleFr = "Bienvenue sur WaveOut !",
            titleEn = "Welcome to WaveOut!",
            descFr = "WaveOut utilise des ondes acoustiques sinusoïdales et des vibrations pour expulser l'eau et déloger la poussière de vos haut-parleurs.",
            descEn = "WaveOut uses pure sine wave acoustic frequencies and targeted vibrations to push out water and clear dust from your speakers."
        ),
        TutorialStep(
            icon = Icons.Rounded.PhoneAndroid,
            titleFr = "Position recommandée",
            titleEn = "Recommended Position",
            descFr = "Pour une efficacité maximale, tenez votre téléphone le haut-parleur orienté vers le bas afin que l'eau s'écoule par gravité.",
            descEn = "For maximum efficiency, hold your phone with the speaker facing downward so gravity helps eject the water."
        ),
        TutorialStep(
            icon = Icons.Rounded.Tune,
            titleFr = "Modes multiples & Widget",
            titleEn = "Multiple Modes & Widget",
            descFr = "Profitez de l'éjection d'eau à 165 Hz, du balayage anti-poussière, ou ajoutez le Widget sur votre écran d'accueil pour un accès en 1 clic !",
            descEn = "Enjoy 165 Hz water eject, dust frequency sweep, or add our home screen widget for 1-tap instant cleaning!"
        )
    )

    val step = steps[currentStepIndex]

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isFr) step.titleFr else step.titleEn,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isFr) step.descFr else step.descEn,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == currentStepIndex) 10.dp else 8.dp)
                                .background(
                                    color = if (index == currentStepIndex)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant,
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentStepIndex < steps.size - 1) {
                        currentStepIndex++
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentStepIndex < steps.size - 1) 
                        (if (isFr) "Suivant" else "Next")
                    else 
                        (if (isFr) "Commencer" else "Get Started")
                )
            }
        },
        dismissButton = {
            if (currentStepIndex > 0) {
                TextButton(onClick = { currentStepIndex-- }) {
                    Text(if (isFr) "Précédent" else "Previous")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(if (isFr) "Passer" else "Skip")
                }
            }
        }
    )
}
