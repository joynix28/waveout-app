package com.example.waveout

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.example.waveout.ui.history.HistoryScreen
import com.example.waveout.ui.home.HomeScreen
import com.example.waveout.ui.settings.AppLanguage
import com.example.waveout.ui.settings.SettingsScreen
import com.example.waveout.ui.settings.ThemeMode

enum class AppScreen {
    HOME,
    HISTORY,
    SETTINGS
}

@Composable
fun MainNavigation(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            AppScreen.HOME -> {
                HomeScreen(
                    onNavigateToHistory = { currentScreen = AppScreen.HISTORY },
                    onNavigateToSettings = { currentScreen = AppScreen.SETTINGS },
                    appLanguage = appLanguage
                )
            }
            AppScreen.HISTORY -> {
                HistoryScreen(
                    onBack = { currentScreen = AppScreen.HOME },
                    appLanguage = appLanguage
                )
            }
            AppScreen.SETTINGS -> {
                SettingsScreen(
                    onBack = { currentScreen = AppScreen.HOME },
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    appLanguage = appLanguage,
                    onLanguageChange = onLanguageChange
                )
            }
        }
    }
}
