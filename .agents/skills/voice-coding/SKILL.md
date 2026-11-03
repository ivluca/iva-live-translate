---
name: voice-coding
description: >-
  Chuyên môn phát triển tính năng âm thanh, xử lý DSP, kết nối WebSocket Gemini Live và Android Audio Pipeline cho ALAD-Mobile.
  Kích hoạt khi cần sửa đổi code AudioRecord, AudioTrack, WebSocket, Dubbing Service, Overlay Widget hoặc thêm tính năng liên quan đến voice AI.
---

# 🎙️ Voice-Coding Skill — ALAD Mobile Audio & Live AI Expert

Skill này cung cấp toàn bộ bối cảnh kiến trúc, thông số kỹ thuật chuẩn và quy ước lập trình cho các AI Agent (Gemini / Antigravity) khi phát triển, mở rộng hoặc sửa lỗi dự án **ALAD-Mobile** mà không cần phải quét hay đọc lại toàn bộ mã nguồn.

---

## 🗺️ Bản đồ Codebase Nhanh (Cheat Sheet)

```text
app/src/main/kotlin/com/iva/translate/app/
├── core/
│   ├── audio/
│   │   ├── AudioCaptureManager.kt      # Thu âm nội bộ hệ thống (16kHz PCM mono, 40ms/chunk)
│   │   ├── AudioPlayerManager.kt       # Phát âm thanh AI (24kHz PCM mono), DSP lọc chất giọng
│   │   └── AudioUtils.kt               # Tính RMS amplitude trực quan hóa sóng âm
│   ├── network/
│   │   └── ALADWebSocketManager.kt     # WebSocket 2 chiều với Gemini Live API
│   └── service/
│       ├── AudioDubbingForegroundService.kt # Foreground service (mediaProjection + mediaPlayback)
│       ├── OverlayWidgetService.kt     # Floating Widget & phụ đề song ngữ bằng Compose
│       └── TransparentCaptureActivity.kt# Activity vô hình xin quyền MediaProjection
├── data/repository/
│   ├── DubbingSessionRepository.kt     # In-memory Singleton StateFlow cho toàn phiên dịch
│   └── UserPreferencesRepository.kt    # Jetpack DataStore lưu trữ API key, volume, cài đặt
└── presentation/
    ├── main/
    │   ├── Languages.kt                # Danh sách ngôn ngữ đích (vi, en, ja) & Dialects
    │   ├── MainScreen.kt               # Bento Dark UI, visualizer, mixer controls
    │   └── MainViewModel.kt            # ViewModel kết nối SessionRepo & Preferences
    └── settings/                       # Màn hình cấu hình API Key, Persona, Dubbing mode
```

---

## ⚙️ Các thông số kỹ thuật bất biến (Crucial Invariants)

Bất kỳ thay đổi nào liên quan đến Audio hoặc Network đều **BẮT BUỘC** phải tuân thủ các hằng số sau:

1. **Audio Capture (Đầu vào từ hệ thống Android):**
   - Tần số: `16,000 Hz` (16kHz).
   - Kênh: `AudioFormat.CHANNEL_IN_MONO` (Mono).
   - Mã hóa: `AudioFormat.ENCODING_PCM_16BIT`.
   - Kích thước Chunk: `1280 bytes` (đúng 640 mẫu = 40ms âm thanh ở 16kHz). Không tăng buffer quá lớn vì sẽ gây delay thuyết minh.
   - Luôn gọi `.excludeUid(appUid)` trên `AudioPlaybackCaptureConfiguration.Builder` để tránh ALAD tự thu âm lại giọng đọc của chính nó (gây hú/vòng lặp phản hồi âm).

2. **Audio Playback (Đầu ra thuyết minh từ Gemini):**
   - Tần số: `24,000 Hz` (24kHz).
   - Kênh: `AudioFormat.CHANNEL_OUT_MONO`.
   - AudioTrack: `MODE_STREAM` kết hợp `PERFORMANCE_MODE_LOW_LATENCY`.
   - Thuật toán Gain: Áp dụng đường cong logarithmic `gain = (vol * vol * 1.35f)` và kẹp bằng `.coerceIn(-32767f, 32767f)` để chống vỡ tiếng.

3. **Gemini Live WebSocket:**
   - Model: `models/gemini-3.5-live-translate-preview`.
   - URL: `wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent?key=...`.
   - Format dữ liệu gửi lên: JSON bọc `realtimeInput.audio` với MIME `audio/pcm;rate=16000`, dữ liệu Base64.
   - Nhận về: `serverContent.modelTurn.parts[].inlineData.data` là Base64 PCM 24kHz.
   - Subtitles: Đọc từ `outputTranscription.text` (bản dịch) và `inputTranscription.text` (giọng gốc).

---

## 💡 Hướng dẫn phát triển các tác vụ thường gặp

### 1. Thay đổi hoặc thêm Dialect / Ngữ điệu
Sửa file [`Languages.kt`](file:///D:/Workspace/ALAD-Mobile/app/src/main/kotlin/com/iva/translate/app/presentation/main/Languages.kt):
- Thêm đối tượng `DialectOption(code, displayName, promptInstruction)`.
- `promptInstruction` sẽ được đưa tự động vào `systemInstruction` của WebSocket setup payload để hướng dẫn Gemini nói đúng phương ngữ mong muốn.

### 2. Thêm Voice Persona hoặc tinh chỉnh hiệu ứng âm thanh DSP
Sửa file [`AudioPlayerManager.kt`](file:///D:/Workspace/ALAD-Mobile/app/src/main/kotlin/com/iva/translate/app/core/audio/AudioPlayerManager.kt):
- Tinh chỉnh thuật toán lọc IIR trong vòng lặp `for (i in 0 until numSamples)`.
- Luôn giữ biến `prevSample` để duy trì bộ lọc liên tục qua các chunk âm thanh liền kề.

### 3. Đồng bộ trạng thái phiên dịch với UI mới
- **KHÔNG ĐƯỢC** gọi trực tiếp vào Service từ UI.
- Luôn đọc và ghi qua [`DubbingSessionRepository`](file:///D:/Workspace/ALAD-Mobile/app/src/main/kotlin/com/iva/translate/app/data/repository/DubbingSessionRepository.kt).
- Mọi thành phần UI quan sát `DubbingSessionRepository.*` dưới dạng Kotlin `StateFlow`.

---

## 🚨 Các cạm bẫy cần tránh (Pitfalls to Avoid)

1. **Tuyệt đối không chạy lệnh gradle build khi thiết bị thật chưa kết nối:**
   - Dự án dùng `compileSdk = 34` và gradle wrapper. Chỉ build APK hoặc install khi người dùng đã kết nối adb device thật và có yêu cầu rõ ràng.
2. **Không tự ý git commit / push:**
   - Tuyệt đối không tự ý chạy `git commit` hay `git push` trừ khi người dùng ra lệnh rõ ràng.
3. **Không chặn (block) thread `AudioCaptureThread`:**
   - Thread thu âm chạy ở mức ưu tiên `THREAD_PRIORITY_URGENT_AUDIO`. Mọi thao tác I/O nặng hoặc xử lý mạng phải đẩy sang Coroutine Channel hoặc Coroutine riêng biệt.
3. **Quản lý WindowManager trên Service:**
   - `OverlayWidgetService` sử dụng Jetpack Compose trên WindowManager. Bắt buộc phải gắn `ServiceLifecycleOwner`, `ServiceViewModelStoreOwner`, và `ServiceSavedStateRegistryOwner` trước khi set content cho `ComposeView`.
