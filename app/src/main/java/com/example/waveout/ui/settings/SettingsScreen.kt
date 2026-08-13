package com.example.waveout.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.waveout.data.SessionStore

enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

enum class AppLanguage {
    SYSTEM,
    FRENCH,
    ENGLISH
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var defaultDuration by remember { mutableFloatStateOf(30f) }
    var safeMode by remember { mutableStateOf(true) }
    var showLicenseDialog by remember { mutableStateOf(false) }
    var showHistoryClearedSnackbar by remember { mutableStateOf(false) }

    val isFr = appLanguage == AppLanguage.FRENCH

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (isFr) "Paramètres" else "Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // LANGUE / LANGUAGE
            SettingsSection(if (isFr) "LANGUE" else "LANGUAGE") {
                SettingsRow(
                    icon = Icons.Rounded.Language,
                    label = if (isFr) "Langue de l'application" else "App Language",
                    description = when (appLanguage) {
                        AppLanguage.FRENCH -> "Français"
                        AppLanguage.ENGLISH -> "English"
                        AppLanguage.SYSTEM -> if (isFr) "Système par défaut" else "System default"
                    },
                    control = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = appLanguage == AppLanguage.FRENCH,
                                onClick = { onLanguageChange(AppLanguage.FRENCH) },
                                label = { Text("FR") }
                            )
                            FilterChip(
                                selected = appLanguage == AppLanguage.ENGLISH,
                                onClick = { onLanguageChange(AppLanguage.ENGLISH) },
                                label = { Text("EN") }
                            )
                        }
                    }
                )
            }

            // APPARENCE / THEME
            SettingsSection(if (isFr) "APPARENCE" else "APPEARANCE") {
                SettingsRow(
                    icon = Icons.Rounded.SettingsBrightness,
                    label = if (isFr) "Thème système" else "System theme",
                    description = if (isFr) "Suit le réglage système" else "Follow system default",
                    control = {
                        Switch(
                            checked = themeMode == ThemeMode.SYSTEM,
                            onCheckedChange = { isChecked ->
                                onThemeModeChange(if (isChecked) ThemeMode.SYSTEM else ThemeMode.DARK)
                            }
                        )
                    }
                )

                if (themeMode != ThemeMode.SYSTEM) {
                    SettingsRow(
                        icon = if (themeMode == ThemeMode.DARK) Icons.Rounded.DarkMode else Icons.Rounded.LightMode,
                        label = if (isFr) "Thème sombre" else "Dark theme",
                        description = if (themeMode == ThemeMode.DARK) 
                            (if (isFr) "Mode sombre activé" else "Dark mode enabled") 
                        else 
                            (if (isFr) "Mode clair activé" else "Light mode enabled"),
                        control = {
                            Switch(
                                checked = themeMode == ThemeMode.DARK,
                                onCheckedChange = { isDark ->
                                    onThemeModeChange(if (isDark) ThemeMode.DARK else ThemeMode.LIGHT)
                                }
                            )
                        }
                    )
                }
            }

            // AUDIO
            SettingsSection("AUDIO") {
                SettingsRow(
                    icon = Icons.Rounded.Timer,
                    label = if (isFr) "Durée personnalisée" else "Default duration",
                    description = "${defaultDuration.toInt()}s",
                    control = {
                        Slider(
                            value = defaultDuration,
                            onValueChange = { defaultDuration = it },
                            valueRange = 10f..120f,
                            modifier = Modifier.width(120.dp)
                        )
                    }
                )
                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    label = if (isFr) "Mode sécurisé" else "Safe mode",
                    description = if (isFr) "Protection des haut-parleurs" else "Prevents over-driving speaker",
                    control = {
                        Switch(
                            checked = safeMode,
                            onCheckedChange = { safeMode = it }
                        )
                    }
                )
            }

            // DONNÉES / DATA
            SettingsSection(if (isFr) "DONNÉES" else "DATA") {
                SettingsRow(
                    icon = Icons.Rounded.DeleteForever,
                    label = if (isFr) "Effacer l'historique" else "Clear history",
                    description = if (isFr) "Supprimer toutes les sessions" else "Delete all past sessions",
                    control = {
                        Button(
                            onClick = {
                                SessionStore.clearAll()
                                showHistoryClearedSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(if (isFr) "Effacer" else "Clear")
                        }
                    }
                )
            }

            // À PROPOS / ABOUT
            SettingsSection(if (isFr) "À PROPOS" else "ABOUT") {
                SettingsRow(
                    icon = Icons.Rounded.Info,
                    label = if (isFr) "Version de l'application" else "App version",
                    description = "1.0.0 (Open Source)",
                    control = { }
                )
                SettingsRow(
                    icon = Icons.Rounded.Code,
                    label = if (isFr) "Licence Open Source" else "Open source license",
                    description = if (isFr) "Afficher la licence MIT" else "View MIT License",
                    modifier = Modifier.clickable { showLicenseDialog = true },
                    control = {
                        IconButton(onClick = { showLicenseDialog = true }) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null)
                        }
                    }
                )
                SettingsRow(
                    icon = Icons.AutoMirrored.Rounded.OpenInNew,
                    label = "GitHub Repository",
                    description = "github.com/waveout-app/waveout",
                    modifier = Modifier.clickable {
                        openUrl(context, "https://github.com/waveout-app/waveout")
                    },
                    control = {
                        IconButton(onClick = {
                            openUrl(context, "https://github.com/waveout-app/waveout")
                        }) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null)
                        }
                    }
                )
            }
        }
    }

    if (showLicenseDialog) {
        AlertDialog(
            onDismissRequest = { showLicenseDialog = false },
            title = { Text("MIT License") },
            text = {
                Text(
                    "WaveOut — Open Source Speaker Cleaner\n\n" +
                    "Copyright (c) 2026 WaveOut Contributors\n\n" +
                    "Permission is hereby granted, free of charge, to any person obtaining a copy " +
                    "of this software and associated documentation files, to deal in the Software " +
                    "without restriction, including without limitation the rights to use, copy, modify, " +
                    "merge, publish, distribute, sublicense, and/or sell copies of the Software.\n\n" +
                    "THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND."
                )
            },
            confirmButton = {
                TextButton(onClick = { showLicenseDialog = false }) {
                    Text(if (isFr) "Fermer" else "Close")
                }
            }
        )
    }

    if (showHistoryClearedSnackbar) {
        AlertDialog(
            onDismissRequest = { showHistoryClearedSnackbar = false },
            title = { Text(if (isFr) "Succès" else "Success") },
            text = { Text(if (isFr) "L'historique a été vidé !" else "Session history cleared!") },
            confirmButton = {
                TextButton(onClick = { showHistoryClearedSnackbar = false }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    label: String,
    description: String,
    modifier: Modifier = Modifier,
    control: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        control()
    }
}
