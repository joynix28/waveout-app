# 🌊 WaveOut — Open Source Android Speaker Cleaner & Water Ejector

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.xml" width="120" height="120" alt="WaveOut Logo" />
</p>

<p align="center">
  <b>100% Free, Open Source & Ad-Free Android application to clean phone speakers and eject water using PCM acoustic synthesis and haptic vibrations.</b><br/>
  <b>Application Android 100% Gratuite, Open Source et Sans Publicité pour nettoyer les haut-parleurs et expulser l'eau via synthèse acoustique PCM et vibrations haptiques.</b>
</p>

<p align="center">
  <a href="https://github.com/joynix28/waveout-app/releases"><img src="https://img.shields.io/badge/Release-v1.0.0-00D9FF.svg" alt="Release v1.0.0" /></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-4DFFB4.svg" alt="License MIT" /></a>
  <img src="https://img.shields.io/badge/Android-8.0%2B-blue.svg" alt="Android 8.0+" />
  <img src="https://img.shields.io/badge/Kotlin-Compose-purple.svg" alt="Jetpack Compose" />
</p>

---

## 📑 Language / Langue
- [🇫🇷 Version Française](#-version-française)
- [🇬🇧 English Version](#-english-version)

---

# 🇫🇷 Version Française

## 1. Présentation
**WaveOut** a été développé pour proposer une alternative moderne, transparente et gratuite aux applications payantes (comme *Clear Wave*), souvent remplies de traceurs et de publicités intrusives.

Elle combine :
- La génération en temps réel de **signaux audio sinusoïdaux PCM 16-bit** calibrés.
- Des **modèles de vibration haptiques** synchronisés pour maximiser le déplacement mécanique de l'eau.
- Une **interface Jetpack Compose Material 3** ultra-fluide (Sombre / Clair / Système).
- Un **Widget universel pour écran d'accueil** (compatible Xiaomi HyperOS/MIUI, Samsung One UI, Honor MagicOS, Google Pixel...).
- Un **tutoriel interactif avec sélection de langue au premier lancement**.

---

## 2. Modes de Nettoyage

| Mode | Fréquence / Signal | Durée | Rôle |
|---|---|---|---|
| 💧 **Water Eject** | 165 Hz (Fréquence de résonance) | 30s | Crée une pression acoustique continue et des vibrations pour expulser les gouttes d'eau accumulées dans la grille du haut-parleur. |
| 💨 **Dust Clean** | Sweep 300 Hz $\rightarrow$ 800 Hz | 20s | Balaye les fréquences pour décoller poussières et particules micro-agglomérées. |
| 🧹 **Deep Clean** | Multi-étapes (165 Hz $\rightarrow$ Sweep 200–2000 Hz $\rightarrow$ 440 Hz) | 60s | Nettoyage complet combinant résonance basse, balayage large bande et ton stabilisateur. |
| 🎛️ **Mode Manuel (Custom)** | 20 Hz à 20 000 Hz | 10s à 120s | Réglage libre de la fréquence et de la durée avec sélection des vibrations. |

---

## 3. Principe Acoustique & Technique

### Synthèse Audio PCM en temps réel (`AudioEngine.kt`)
Plutôt que de jouer de simples fichiers MP3 préenregistrés (qui subissent des artefacts de compression et une perte d'amplitude), WaveOut génère dynamiquement des **échantillons PCM 16-bit non compressés** à une fréquence d'échantillonnage de **44.1 kHz stéréo** via `android.media.AudioTrack` en mode `MODE_STREAM` :

$$\text{sample}(t) = \text{Short.MAX\_VALUE} \times \sin\left(\frac{2\pi \cdot f \cdot t}{\text{sampleRate}}\right)$$

L'amplitude est maintenue au maximum théorique pour générer le déplacement d'air maximal nécessaire à l'éjection.

### Moteur Haptique (`VibrationEngine.kt`)
Utilise l'API `VibrationEffect` d'Android (API 26+) pour alterner des ondes continues (pour l'eau) et des impulsions rapides (pour la poussière).

---

## 4. Arborescence du Projet

```text
waveout/
├── app/
│   ├── build.gradle.kts             # Configuration Gradle du module app
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Permissions, déclaration du widget & services
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
│           │   │   │   └── OnboardingTutorialDialog.kt # Dictacticiel & sélecteur de langue
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

## 5. Compilation & Installation

```bash
git clone https://github.com/joynix28/waveout-app.git
cd waveout-app

# Compilation du build Debug
./gradlew assembleDebug
```

---
---

# 🇬🇧 English Version

## 1. Overview
**WaveOut** was built to provide a modern, transparent, and completely free alternative to paid speaker cleaner apps (like *Clear Wave*), which are often plagued by ads and trackers.

Key highlights:
- Real-time generation of **calibrated PCM 16-bit sine wave signals**.
- Synchronized **haptic vibration patterns** to maximize mechanical fluid displacement.
- Ultra-smooth **Jetpack Compose Material 3 UI** (Dark / Light / System theme).
- **Universal Home Screen Widget** (compatible with Xiaomi HyperOS/MIUI, Samsung One UI, Honor MagicOS, Google Pixel Launcher...).
- **Interactive onboarding tutorial with language selection on first launch**.

---

## 2. Cleaning Modes

| Mode | Frequency / Pattern | Duration | Purpose |
|---|---|---|---|
| 💧 **Water Eject** | 165 Hz (Resonance Frequency) | 30s | Generates continuous acoustic air pressure and vibration to expel water droplets from speaker mesh. |
| 💨 **Dust Clean** | Sweep 300 Hz $\rightarrow$ 800 Hz | 20s | Sweeps through frequency bands to dislodge micro-dust and trapped debris. |
| 🧹 **Deep Clean** | Multi-stage (165 Hz $\rightarrow$ Sweep 200–2000 Hz $\rightarrow$ 440 Hz) | 60s | Full-spectrum cleaning combining low bass resonance, wide sweep and stabilizer tone. |
| 🎛️ **Custom Mode** | 20 Hz to 20,000 Hz | 10s to 120s | Customizable frequency and timer with optional vibration toggle. |

---

## 3. Acoustic & Technical Principles

### Real-Time PCM Audio Synthesis (`AudioEngine.kt`)
Instead of playing compressed audio files (which suffer from lossy artifacts and capped power), WaveOut synthesizes **uncompressed 16-bit PCM samples** at **44.1 kHz stereo** using Android's `AudioTrack` API in `MODE_STREAM`:

$$\text{sample}(t) = \text{Short.MAX\_VALUE} \times \sin\left(\frac{2\pi \cdot f \cdot t}{\text{sampleRate}}\right)$$

Amplitude is maintained at theoretical maximum to achieve peak cone excursion and air displacement.

### Haptic Engine (`VibrationEngine.kt`)
Leverages Android's `VibrationEffect` API (API 26+) for seamless continuous vibration or multi-frequency pulse waveforms.

---

## 4. Project Tree Structure

```text
waveout/
├── app/
│   ├── build.gradle.kts             # App module Gradle configuration
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # Permissions, widgets & service declarations
│           ├── java/com/example/waveout/
│           │   ├── MainActivity.kt          # App entrypoint, theme & onboarding controller
│           │   ├── Navigation.kt            # Native Compose transition navigation (Crossfade)
│           │   ├── audio/
│           │   │   ├── AudioEngine.kt       # Real-time PCM synthesis (AudioTrack)
│           │   │   ├── VibrationEngine.kt   # Haptic controller (VibrationEffect)
│           │   │   └── CleaningModes.kt     # Mode enums & presets
│           │   ├── data/
│           │   │   ├── DefaultDataRepository.kt # Reactive SessionStore (Kotlin Flow)
│           │   │   └── model/SessionRecord.kt   # Session history data entity
│           │   ├── ui/
│           │   │   ├── components/
│           │   │   │   ├── WaveformVisualizer.kt # Real-time Canvas sine wave visualizer
│           │   │   │   ├── TimerRing.kt          # Animated circular progress timer ring
│           │   │   │   ├── FrequencySlider.kt    # Logarithmic frequency slider (20Hz-20kHz)
│           │   │   │   └── ModeCard.kt           # Mode selector card
│           │   │   ├── history/
│           │   │   │   └── HistoryScreen.kt      # Session history list screen
│           │   │   ├── home/
│           │   │   │   ├── HomeScreen.kt         # Main dashboard with controls
│           │   │   │   └── HomeViewModel.kt      # ViewModel & cleaning coroutines
│           │   │   ├── onboarding/
│           │   │   │   └── OnboardingTutorialDialog.kt # Step-by-step onboarding & language picker
│           │   │   ├── settings/
│           │   │   │   └── SettingsScreen.kt     # App settings (Theme, Language, MIT, GitHub)
│           │   │   └── theme/
│           │   │       ├── WaveOutTheme.kt       # Material 3 Design System (Dark / Light)
│           │   │       └── Type.kt               # App typography
│           │   └── widget/
│           │       ├── WaveOutWidget.kt          # Universal AppWidgetProvider
│           │       └── WaveOutWidgetService.kt   # Foreground Service for widget actions
│           └── res/
│               ├── drawable/                     # Vector icons & WaveOut logo
│               ├── layout/                       # Widget & notification XML layouts
│               ├── values/                       # Strings & base styles
│               └── xml/                          # AppWidget provider info
├── gradle/
│   └── libs.versions.toml           # Dependency version catalog
├── README.md                        # Project documentation (Bilingual)
└── build.gradle.kts                 # Root Gradle build script
```

---

## 5. Build & Setup

```bash
git clone https://github.com/joynix28/waveout-app.git
cd waveout-app

# Build debug APK
./gradlew assembleDebug
```

---

## 📜 License

Distributed under the **MIT License**. See `LICENSE` for more information.
