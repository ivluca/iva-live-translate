<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="iva Logo" width="120" />

  <h1>🎙️ iva - Live Translate & Audio Dubbing</h1>

  <p><strong>Real-time AI vocal dubbing & dual live subtitles for any Android app, powered by Google's Gemini 3.5 Live Translate.</strong></p>

  <p>
    <a href="https://github.com/navidseyedain/ALAD-Mobile"><img src="https://img.shields.io/badge/Forked%20From-ALAD--Mobile-blueviolet?style=for-the-badge&logo=github" alt="Forked from ALAD-Mobile"></a>
    <a href="https://developer.android.com/"><img src="https://img.shields.io/badge/Android-10%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"></a>
    <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"></a>
    <a href="https://aistudio.google.com/"><img src="https://img.shields.io/badge/Powered%20By-Gemini%203.5%20Live-00C896?style=for-the-badge&logo=google&logoColor=white" alt="Gemini"></a>
    <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License"></a>
  </p>

  <p>
    <b>Watch any foreign video or listen to any podcast. Hear and read it in your native language - instantly.</b><br/>
    No subscriptions. No third-party accounts. 100% free and open-source.
  </p>
</div>

---

> 📌 **Project Origin & Attribution:**
> This project is **customized and developed from the open-source project [ALAD-Mobile](https://github.com/navidseyedain/ALAD-Mobile)** created by **Navid Seyedain**. The user interface, control system, voice persona workflows, audio sliders, and translation pipelines have been refactored and tailored for real-time Vietnamese speech dubbing.

---

## 🌍 Overview

**iva (iva - Live Translate)** is a native Android application that provides real-time AI speech dubbing and live bilingual subtitles for any media playback on your device.

Working in the background, **iva** captures clean digital system audio directly from any active app (YouTube, Netflix, Spotify, TikTok, podcasts, university lectures, etc.) using Android's native `MediaProjection API`, streams it to Google's **Gemini 3.5 Live Translate** engine over a full-duplex bi-directional WebSocket, and plays back fluid, natural translated speech with sub-second latency while displaying floating live subtitles.

---

## ✨ Key Features & Enhancements

### 🇻🇳 1. Vietnamese-Focused Real-Time Translation
- Optimized for spoken Vietnamese with natural Northern phrasing (Hà Nội dialect prompt instruction).
- Direct Speech-to-Speech translation using the `gemini-3.5-live-translate-preview` model over Google Generative Language Live WebSocket.

### 🎙️ 2. Top 6 Curated AI Voice Personas (Main Screen Access)
Voice personas have been placed directly on the main dashboard for instant switching without navigating into Settings:
- **Female Voices:**
  - `Aoede`: Melodic, warm, and expressive
  - `Kore`: Gentle, serene, and calm
  - `Zephyr`: Crisp, clear, and modern
- **Male Voices:**
  - `Puck`: Energetic, friendly, and lively
  - `Charon`: Deep, resonant, and authoritative
  - `Fenrir`: Crisp, direct, and professional
- **Instant Reconnect:** Changing voices seamlessly re-establishes the active session with the selected voice configuration.

### 🎚️ 3. Dual Audio Mixer & Canvas Volume Sliders
- **SleekVolumeSlider:** Custom Canvas-rendered slider component that provides smooth touch feedback and eliminates thumb alignment issues found in standard Material 3 sliders.
- **Independent Controls:**
  - AI Dubbed Voice volume (0-100%) with instant **Mute** toggle.
  - Original Video/Media volume (0-100%) with instant **Mute** toggle.
- **Smart Audio Ducking:** Automatically attenuates original background audio while the AI voice is speaking.

### 💬 4. Floating Overlay Controller & Dual Live Subtitles
- **Compact Overlay Widget:** A floating pill widget allows full control outside the app.
- **Double-Tap Quick Control:** Double-tap anywhere on the floating widget to immediately pause or resume dubbing.
- **Dual Live Subtitles Capsule:** Translucent floating window showing original spoken words alongside live translated subtitles.
- **3 Dubbing Modes:**
  - `Voice + Subtitles (Hybrid)`: Spoken AI voice with simultaneous on-screen subtitles.
  - `Audio Only`: Dubbed audio only (optimized for background listening and screen-off usage).
  - `Subtitles Only`: Silent translation mode displaying only the live subtitle overlay.

### 🎨 5. Modern Dark Red UI
- Obsidian dark theme with distinct red accent branding (`#E72234`).
- Studio audio bars spectrum visualizer animating smoothly according to real-time audio amplitude.

---

## 🚀 Getting Started

### Prerequisites
- Android 10.0+ (API 29+)
- Free Gemini API Key from [Google AI Studio](https://aistudio.google.com/app/apikey)

### Quick Setup & Build
1. Open **iva**, tap ⚙️ **Settings**, and paste your **Gemini API Key**.
2. Select your **AI Voice Persona** and adjust volume in **Dual Audio Mixer**.
3. Tap **Start Translate** to begin live dubbing.

```bash
# Build Debug APK
./gradlew assembleDebug
```

---

## 📖 Documentation & Upstream Details

This project is a customized fork focused on Vietnamese live dubbing. For comprehensive architecture breakdowns, in-depth audio pipeline details, and troubleshooting guides, please refer to the original author's repository:

- 🔗 **Original Upstream Repository:** [navidseyedain/ALAD-Mobile](https://github.com/navidseyedain/ALAD-Mobile)
- 📚 **Developer Wiki:** Technical notes are also available in the local [`wiki/`](wiki/README.md) directory.

---

## 📜 License & Credits

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.

- **Original Creator:** [Navid Seyedain](https://github.com/navidseyedain) ([ALAD-Mobile](https://github.com/navidseyedain/ALAD-Mobile))
- **Powered by:** Google DeepMind & Google AI Studio (Gemini 3.5 Live Translate)
