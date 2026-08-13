# 🌊 WaveOut — Open Source Android Speaker Cleaner & Water Ejector

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="WaveOut Logo" />
</p>

<p align="center">
  <b>Application Android 100% Gratuite, Open Source et Sans Publicité pour nettoyer les haut-parleurs et expulser l'eau via synthèse acoustique PCM et vibrations haptiques.</b>
</p>

<p align="center">
  <a href="https://github.com/joynix28/waveout-app/releases"><img src="https://img.shields.io/badge/Release-v1.0.0-00D9FF.svg" alt="Release v1.0.0" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-4DFFB4.svg" alt="License MIT" /></a>
  <img src="https://img.shields.io/badge/Android-8.0%2B-blue.svg" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/Kotlin-Compose-purple.svg" alt="Jetpack Compose" />
</p>

---

## 📖 Table des matières
1. [Présentation](#-présentation)
2. [Fonctionnalités Clés](#-fonctionnalités-clés)
3. [Principe Acoustique & Technique](#-principe-acoustique--technique)
4. [Arborescence du Projet](#-arborescence-du-projet)
5. [Architecture & Composants](#-architecture--composants)
6. [Compilation & Installation](#-compilation--installation)
7. [Contribution & Licence](#-contribution--licence)

---

## 🌟 Présentation

**WaveOut** a été développé pour proposer une alternative moderne, transparente et gratuite aux applications payantes (comme *Clear Wave*), souvent remplies de traceurs et de publicités intrusives.

Elle combine :
- La génération en temps réel de **signaux audio sinusoïdaux PCM 16-bit** calibrés.
- Des **modèles de vibration haptiques** synchronisés pour maximiser le déplacement mécanique de l'eau.
- Une **interface Jetpack Compose Material 3** ultra-fluide (Sombre / Clair / Système).
- Un **Widget universel pour écran d'accueil** (compatible Xiaomi HyperOS/MIUI, Samsung One UI, Honor MagicOS, Google Pixel...).
- Un **tutoriel interactif au premier lancement**.

---

## ⚡ Fonctionnalités Clés

| Mode | Fréquence / Signal | Durée | Rôle |
|---|---|---|---|
| 💧 **Water Eject** | 165 Hz (Fréquence de résonance) | 30s | Crée une pression acoustique continue et des vibrations pour expulser les gouttes d'eau accumulées dans la grille du haut-parleur. |
| 💨 **Dust Clean** | Sweep 300 Hz $\rightarrow$ 800 Hz | 20s | Balaye les fréquences pour décoller poussières et particules micro-agglomérées. |
| 🧹 **Deep Clean** | Multi-étapes (165 Hz $\rightarrow$ Sweep 200–2000 Hz $\rightarrow$ 440 Hz) | 60s | Nettoyage complet combinant résonance basse, balayage large bande et ton stabilisateur. |
| 🎛️ **Mode Manuel (Custom)** | 20 Hz à 20 000 Hz | 10s à 120s | Réglage libre de la fréquence et de la durée avec sélection des vibrations. |

---

## 🔬 Principe Acoustique & Technique

### 1. Synthèse Audio PCM en temps réel (`AudioEngine.kt`)
Plutôt que de jouer de simples fichiers MP3 préenregistrés (qui subissent des artefacts de compression et une perte d'amplitude), WaveOut génère dynamiquement des **échantillons PCM 16-bit non compressés** à une fréquence d'échantillonnage de **44.1 kHz stéréo** via `android.media.AudioTrack` en mode `MODE_STREAM` :

$$\text{sample}(t) = \text{Short.MAX\_VALUE} \times \sin\left(\frac{2\pi \cdot f \cdot t}{\text{sampleRate}}\right)$$

L'amplitude est maintenue au maximum théorique pour générer le déplacement d'air maximal nécessaire à l'éjection.

### 2. Moteur Haptique (`VibrationEngine.kt`)
Utilise l'API `VibrationEffect` d'Android (API 26+) pour alterner des ondes continues (pour l'eau) et des impulsions rapides (pour la poussière).

---

## 📁 Arborescence du Projet

```text
waveout/
├── app/
│   ├── build.gradle.kts             # Configuration Gradle du module app
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Déclaration des permissions, widget & services
│           ├── java/com/example/waveout/
│           │   ├── MainActivity.kt          # Point d'entrée, gestion du thème & tutoriel
│           │   ├── Navigation.kt            # Navigation native fluide (Crossfade)
│           │   ├── audio/
│           │   │   ├── AudioEngine.kt       # Synthèse audio PCM temps réel (AudioTrack)
│           │   │   ├── VibrationEngine.kt   # Contrôleur haptique (VibrationEffect)
│           │   │   └── CleaningModes.kt     # Enum des modes & presets
│           │   ├── data/
│           │   │   ├── DefaultDataRepository.kt # SessionStore (Flow réactif)
│           │   │   └── model/SessionRecord.kt   # Modèle de données d'historique
│           │   ├── ui/
│           │   │   ├── components/
│           │   │   │   ├── WaveformVisualizer.kt # Canvas onde sinusoïdale animée
│           │   │   │   ├── TimerRing.kt          # Anneau circulaire animé de progression
│           │   │   │   ├── FrequencySlider.kt    # Slider logarithmique (20Hz-20kHz)
│           │   │   │   └── ModeCard.kt           # Carte de sélection de mode
│           │   │   ├── history/
│           │   │   │   └── HistoryScreen.kt      # Écran d'historique des sessions
│           │   │   ├── home/
│           │   │   │   ├── HomeScreen.kt         # Écran principal avec visualiseur & commandes
│           │   │   │   └── HomeViewModel.kt      # ViewModel & coroutines de nettoyage
│           │   │   ├── onboarding/
│           │   │   │   └── OnboardingTutorialDialog.kt # Dictacticiel de premier lancement
│           │   │   ├── settings/
│           │   │   │   └── SettingsScreen.kt     # Paramètres (Thème, Langue, Licence, GitHub)
│           │   │   └── theme/
│           │   │       ├── WaveOutTheme.kt       # Thème Material 3 (Dark / Light)
│           │   │       └── Type.kt               # Typographie de l'application
│           │   └── widget/
│           │       ├── WaveOutWidget.kt          # AppWidgetProvider universel
│           │       └── WaveOutWidgetService.kt   # Foreground Service pour exécution widget
│           └── res/
│               ├── drawable/                     # Icônes vectorielles & logo WaveOut
│               ├── layout/                       # Layout XML du widget & notifications
│               ├── values/                       # Chaînes de caractères & styles
│               └── xml/                          # Configuration du widget provider
├── gradle/
│   └── libs.versions.toml           # Version Catalog des dépendances
├── README.md                        # Ce fichier de documentation
└── build.gradle.kts                 # Configuration racine Gradle
```

---

## 🚀 Compilation & Installation

### Prérequis
- **JDK 17 ou 21**
- **Android SDK** (API 26 minimum, compileSdk 36)

### Cloner et compiler le projet
```bash
git clone https://github.com/joynix28/waveout-app.git
cd waveout-app

# Compilation du build Debug
./gradlew assembleDebug

# L'APK sera généré dans :
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📜 Licence

Ce projet est sous licence **MIT**. Vous êtes libre de l'utiliser, de le modifier et de le redistribuer librement.

*Développé pour la communauté open-source.*
