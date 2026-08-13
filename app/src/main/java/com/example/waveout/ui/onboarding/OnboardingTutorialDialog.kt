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
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    var stepIndex by remember { mutableIntStateOf(0) }
    val isFr = currentLanguage == AppLanguage.FRENCH

    val steps = listOf(
        TutorialStep(
            icon = Icons.Rounded.Language,
            titleFr = "Choisissez votre langue",
            titleEn = "Choose your language",
            descFr = "Sélectionnez la langue d'affichage de l'application.",
            descEn = "Select your preferred application display language."
        ),
        TutorialStep(
            icon = Icons.Rounded.WaterDrop,
            titleFr = "Bienvenue sur WaveOut !",
            titleEn = "Welcome to WaveOut!",
            descFr = "WaveOut utilise des ondes acoustiques sinusoïdales pures et des vibrations pour expulser l'eau et déloger la poussière de vos haut-parleurs.",
            descEn = "WaveOut uses pure sine acoustic waves and targeted vibrations to push out trapped water and clear dust from your speakers."
        ),
        TutorialStep(
            icon = Icons.Rounded.PhoneAndroid,
            titleFr = "Position recommandée",
            titleEn = "Recommended Position",
            descFr = "Pour une efficacité optimale, maintenez votre téléphone haut-parleur vers le bas afin que la gravité aide à l'expulsion.",
            descEn = "For maximum efficiency, hold your phone with the speaker facing downward so gravity assists ejection."
        ),
        TutorialStep(
            icon = Icons.Rounded.Tune,
            titleFr = "Modes multiples & Widget",
            titleEn = "Multiple Modes & Widget",
            descFr = "Profitez de l'éjection d'eau à 165 Hz, du sweep anti-poussière, ou ajoutez le Widget sur votre écran d'accueil pour nettoyer en 1 clic !",
            descEn = "Enjoy 165 Hz water eject, dust frequency sweep, or add our home screen widget for instant 1-tap cleaning!"
        )
    )

    val step = steps[stepIndex]

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
                        .size(68.dp)
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
                Spacer(modifier = Modifier.height(14.dp))
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

                // Step 0: Language selection buttons
                if (stepIndex == 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onLanguageSelected(AppLanguage.FRENCH) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isFr) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Français", fontWeight = if (isFr) FontWeight.Bold else FontWeight.Normal)
                        }

                        Button(
                            onClick = { onLanguageSelected(AppLanguage.ENGLISH) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isFr) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (!isFr) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("English", fontWeight = if (!isFr) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == stepIndex) 10.dp else 7.dp)
                                .background(
                                    color = if (index == stepIndex)
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
                    if (stepIndex < steps.size - 1) {
                        stepIndex++
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (stepIndex < steps.size - 1)
                        (if (isFr) "Suivant" else "Next")
                    else
                        (if (isFr) "Commencer" else "Get Started")
                )
            }
        },
        dismissButton = {
            if (stepIndex > 0) {
                TextButton(onClick = { stepIndex-- }) {
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
