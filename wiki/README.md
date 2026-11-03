# 📚 ALAD Mobile — Developer Wiki

Chào mừng đến với hệ thống tài liệu kiến trúc kỹ thuật của **ALAD Mobile** (AI Live Audio Dubbing). Tài liệu này được thiết kế nhằm giúp các lập trình viên và các AI coding agents nhanh chóng nắm bắt bức tranh toàn cảnh, nguyên lý vận hành và chi tiết từng tầng kỹ thuật của dự án mà không tốn thời gian đọc lại từng dòng code.

---

## 📑 Mục lục tài liệu

1. [**Kiến trúc tổng thể (System Architecture)**](architecture.md)
   - Luồng hoạt động từ MediaProjection đến Gemini Live và phát ngược lại loa.
   - Sơ đồ quan hệ giữa các thành phần cốt lõi.
2. [**Audio Pipeline (Thu âm, Phát & Xử lý DSP)**](audio-pipeline.md)
   - `AudioCaptureManager`: Bắt âm thanh hệ thống qua `AudioPlaybackCaptureConfiguration`.
   - `AudioPlayerManager`: Phát PCM 24kHz độ trễ cực thấp, DSP lọc chất giọng AI (Persona), cơ chế Dual Audio Mixer.
   - `AudioUtils`: Chuẩn hóa biên độ sóng âm RMS.
3. [**Gemini Live WebSocket Protocol**](gemini-live-websocket.md)
   - Cấu trúc giao thức BidiGenerateContent WebSocket.
   - JSON Setup Payload: Modality, SpeechConfig, TranslationConfig, Dialect System Prompt.
   - Chu kỳ truyền nhận: Gửi PCM 16kHz RealtimeInput và nhận Audio PCM 24kHz + Transcript phụ đề.
4. [**Dịch vụ nền & Cửa sổ nổi (Foreground & Floating Overlay)**](foreground-and-overlay.md)
   - `AudioDubbingForegroundService`: Quản lý vòng đời dịch vụ nền `mediaProjection` + `mediaPlayback`.
   - `TransparentCaptureActivity`: Cơ chế xin cấp quyền MediaProjection vô hình.
   - `OverlayWidgetService`: Jetpack Compose chạy trên WindowManager Overlay (Widget Pill & Phụ đề song ngữ).
5. [**Quản lý trạng thái & Dữ liệu (State & Data Management)**](data-and-state.md)
   - `DubbingSessionRepository`: Single Source of Truth cho phiên dịch theo thời gian thực.
   - `UserPreferencesRepository`: Jetpack DataStore lưu trữ cài đặt.
   - `Languages.kt`: Danh mục ngôn ngữ đích và ngữ điệu (Dialects).

---

## ⚡ Bảng tra cứu thông số kỹ thuật nhanh

| Hạng mục | Giá trị kỹ thuật | Ghi chú |
| :--- | :--- | :--- |
| **Android Target / Min SDK** | SDK 34 / SDK 29 (Android 10+) | Yêu cầu tối thiểu Android 10 để bắt AudioPlaybackCapture |
| **Ngôn ngữ & UI** | Kotlin 1.9+, Jetpack Compose, Material 3 | Single Activity + Compose View trên Overlay |
| **Capture Audio Format** | 16,000 Hz, 16-bit Mono PCM, 1280 bytes/chunk | 40ms / chunk, truyền thẳng qua WebSocket |
| **Playback Audio Format** | 24,000 Hz, 16-bit Mono PCM | AudioTrack Stream mode, Low Latency |
| **AI Model** | `gemini-3.5-live-translate-preview` | Google Generative Multimodal Live Bidi WebSocket |
| **WebSocket Endpoint** | `wss://generativelanguage.googleapis.com/ws/...` | Giao tiếp 2 chiều toàn phần (Full-Duplex) |
| **Target Languages** | Tiếng Việt (`vi`), Tiếng Anh (`en`), Tiếng Nhật (`ja`) | Tối ưu hóa cho người dùng Việt Nam & học ngoại ngữ |
