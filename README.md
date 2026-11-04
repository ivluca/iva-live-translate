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

## 🧠 System Architecture & Workflow

```mermaid
flowchart TD
    subgraph Capture["📱 1. Internal Audio Capture"]
        APP["Active Media App<br/><i>(YouTube / Netflix / Spotify / Web)</i>"]
        MP["MediaProjection API<br/><code>AudioPlaybackCaptureConfiguration</code>"]
        REC["AudioRecord Engine<br/><code>16kHz Mono 16-bit PCM (1280 bytes/chunk)</code>"]
        APP -->|"Raw System Audio"| MP
        MP -->|"PCM Bytes"| REC
    end

    subgraph Service["⚡ 2. Core Service & Streaming Engine"]
        FS["AudioDubbingForegroundService<br/><i>(Persistent Lifecycle & State Sync)</i>"]
        WS["OkHttp WebSocket Client<br/><code>BidiGenerateContent Protocol</code>"]
        MIX["Dual Audio Mixer & Ducking<br/><i>(Software Logarithmic Scaling)</i>"]
        REC -->|"16kHz PCM Chunks"| FS
        FS -->|"RealtimeInput (Upstream)"| WS
        FS -.->|"Volume Control"| MIX
    end

    subgraph Cloud["☁️ 3. Google Gemini Live AI"]
        GEMINI["<b>Gemini Live Translate Engine</b><br/><code>gemini-3.5-live-translate-preview</code><br/><i>Direct Speech-to-Speech & Transcripts</i>"]
        WS <-->|"Bi-directional Full-Duplex WebSocket"| GEMINI
    end

    subgraph Playback["🔊 4. Low-Latency Audio Playback"]
        AT["Low-Latency AudioTrack (24kHz Mono 16-bit PCM)<br/><i>STREAM Mode</i>"]
        OUT["Device Speaker / Earphones 🎧"]
        WS -->|"Downstream 24kHz PCM"| AT
        AT -->|"Natural Dubbed Voice"| OUT
    end

    subgraph Controls["🎛️ 5. Modern UI & Overlays"]
        UI["MainScreen Dashboard<br/><i>(Voice Personas & Dual Mixer)</i>"]
        WIDGET["Floating Overlay Widget<br/><i>(👆 Double-Tap Gesture & Haptics)</i>"]
        SUB["Dual Live Subtitles Overlay<br/><i>(Bilingual Subtitle Capsule)</i>"]
        WS -->|"Transcript Events"| SUB
        UI <-->|"StateFlow Repository"| FS
        WIDGET <-->|"Pause/Resume Control"| FS
    end

    classDef capture fill:#1a1a2e,stroke:#0f3460,stroke-width:2px,color:#ffffff;
    classDef service fill:#16213e,stroke:#e94560,stroke-width:2px,color:#ffffff;
    classDef cloud fill:#0f3460,stroke:#533483,stroke-width:2px,color:#ffffff;
    classDef playback fill:#2c003e,stroke:#c70039,stroke-width:2px,color:#ffffff;
    classDef controls fill:#1a1c23,stroke:#e72234,stroke-width:2px,color:#ffffff;

    class APP,MP,REC capture;
    class FS,WS,MIX service;
    class GEMINI cloud;
    class AT,OUT playback;
    class UI,WIDGET,SUB controls;
```

### Core Audio Invariants:
- **Audio Capture:** 16,000 Hz, 16-bit Mono PCM, 1280 bytes per chunk (~40ms).
- **Audio Playback:** 24,000 Hz, 16-bit Mono PCM via `AudioTrack` STREAM mode.
- **AI Model:** `gemini-3.5-live-translate-preview` via Google Live Bidi WebSocket endpoint.
- **Minimum OS:** Android 10.0 (API level 29) required for internal audio playback capture.

---

## 🚀 Getting Started

### Prerequisites
1. **Android Device:** Android 10.0 (API 29) or newer.
2. **Gemini API Key:** A free API key from [Google AI Studio](https://aistudio.google.com/app/apikey).

### Configuration & Usage
1. Open **iva**, tap the ⚙️ **Settings** icon in the top corner.
2. Enter your **Gemini API Key** and tap **Save Settings**.
3. On the Main Screen:
   - Select your preferred narrator in **AI Voice Persona**.
   - Balance the AI dubbed voice and original video audio with the **Dual Audio Mixer**.
   - Enable the **Floating Widget** switch if you want multitasking overlay controls.
4. Tap **Start Translate** and grant the screen capture/audio permission prompt.

---

## 🛠️ Build from Source

### Requirements:
- JDK 17 or higher
- Android SDK (API 34, Android Build Tools)

### Build Commands:
```bash
# Clone repository
git clone https://github.com/navidseyedain/ALAD-Mobile.git
cd ALAD-Mobile

# Or clone from your custom repository fork:
# git clone <your-repo-url>

# Build Debug APK
./gradlew assembleDebug

# Install directly to a connected Android device via ADB
./gradlew installDebug
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📚 Technical Documentation (Developer Wiki)

For in-depth architectural and implementation details, refer to the [`wiki/`](wiki/README.md) directory:
- [**System Architecture**](wiki/architecture.md) - Comprehensive layer breakdown and component lifecycle.
- [**Audio Pipeline**](wiki/audio-pipeline.md) - Capture mechanics, AudioTrack playback, and DSP mixing.
- [**Gemini Live WebSocket Protocol**](wiki/gemini-live-websocket.md) - BidiGenerateContent frames, payloads, and reconnect strategies.
- [**Foreground Service & Floating Overlay**](wiki/foreground-and-overlay.md) - Background execution, MediaProjection capture activity, and WindowManager overlay compose views.
- [**State & Data Management**](wiki/data-and-state.md) - DubbingSessionRepository StateFlows and DataStore preferences.

---

## 🔧 Troubleshooting

| Issue | Cause | Solution |
|---|---|---|
| **No translation audio** | Media app is not currently playing sound or Gemini API quota is exhausted. | Ensure media is playing in the background app and verify API quota in Google AI Studio. |
| **WebSocket connection fails** | Invalid API Key or network blocks WebSockets. | Double-check API Key in Settings or check internet connection / VPN. |
| **Dubbing stops when switching apps** | Android battery optimization terminated the background service. | Go to *App Info > Battery > Unrestricted*. |
| **Audio not capturing in apps like Netflix** | Protected by DRM or system phone call policy. | Android OS strictly blocks recording for DRM-protected streams and active phone calls. |

---

## 📜 Credits & License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for details.

### Acknowledgments:
- **Upstream Project:** [**ALAD-Mobile**](https://github.com/navidseyedain/ALAD-Mobile) created by [**Navid Seyedain**](https://github.com/navidseyedain).
- **Google DeepMind & Google AI Studio:** For Gemini Live streaming architecture and API access.
