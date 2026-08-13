package com.example.waveout

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.waveout.ui.onboarding.OnboardingTutorialDialog
import com.example.waveout.ui.settings.AppLanguage
import com.example.waveout.ui.settings.ThemeMode
import com.example.waveout.ui.theme.WaveOutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val prefs = getSharedPreferences("waveout_prefs", Context.MODE_PRIVATE)
        val hasSeenTutorial = prefs.getBoolean("has_seen_tutorial", false)

        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            var appLanguage by remember { mutableStateOf(AppLanguage.FRENCH) }
            var showTutorial by remember { mutableStateOf(!hasSeenTutorial) }

            val isSystemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemDark
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
            }

            WaveOutTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(
                        themeMode = themeMode,
                        onThemeModeChange = { themeMode = it },
                        appLanguage = appLanguage,
                        onLanguageChange = { appLanguage = it }
                    )

                    if (showTutorial) {
                        OnboardingTutorialDialog(
                            appLanguage = appLanguage,
                            onDismiss = {
                                showTutorial = false
                                prefs.edit().putBoolean("has_seen_tutorial", true).apply()
                            }
                        )
                    }
                }
            }
        }
    }
}
