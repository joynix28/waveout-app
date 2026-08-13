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
import com.example.waveout.model.AppLanguage
import com.example.waveout.model.ThemeMode
import com.example.waveout.ui.onboarding.OnboardingTutorialDialog
import com.example.waveout.ui.theme.WaveOutTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val prefs = getSharedPreferences("waveout_prefs", Context.MODE_PRIVATE)
        val hasSeenTutorial = prefs.getBoolean("has_seen_tutorial", false)
        val savedLang = prefs.getString("app_language", "FR")
        val initialLanguage = if (savedLang == "EN") AppLanguage.ENGLISH else AppLanguage.FRENCH

        setContent {
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }
            var appLanguage by remember { mutableStateOf(initialLanguage) }
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
                        onLanguageChange = { newLang ->
                            appLanguage = newLang
                            prefs.edit().putString("app_language", if (newLang == AppLanguage.ENGLISH) "EN" else "FR").apply()
                        }
                    )

                    if (showTutorial) {
                        OnboardingTutorialDialog(
                            currentLanguage = appLanguage,
                            onLanguageSelected = { selectedLang ->
                                appLanguage = selectedLang
                                prefs.edit().putString("app_language", if (selectedLang == AppLanguage.ENGLISH) "EN" else "FR").apply()
                            },
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
