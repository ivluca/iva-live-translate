# 🔊 Audio Pipeline — Thu âm, Phát & Xử lý DSP

Pipeline âm thanh là xương sống của ALAD Mobile, đảm bảo độ trễ gần như bằng 0 (ultra-low latency), chất lượng âm thanh rõ ràng và khả năng kiểm soát âm lượng linh hoạt.

---

## 1. Thu âm nội bộ (Internal Audio Capture)

### File: `app/src/main/kotlin/com/iva/translate/app/core/audio/AudioCaptureManager.kt`

Khác với các ứng dụng dịch thông thường phải sử dụng Microphone vật lý (dễ bị nhiễu tiếng ồn môi trường và phòng vọng), ALAD bắt luồng tín hiệu âm thanh kỹ thuật số sạch trực tiếp từ hệ thống Android:

```kotlin
val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
    .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
    .addMatchingUsage(AudioAttributes.USAGE_GAME)
    .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
    .excludeUid(appUid) // Ngăn bắt âm thanh từ chính ALAD để chống vòng lặp phản hồi âm thanh (Acoustic Echo)
    .build()
```

### Thông số kỹ thuật thu âm:
- **Tần số lấy mẫu (Sample Rate):** `16,000 Hz` (16kHz) — Chuẩn tối ưu của Google Speech & Gemini Live API.
- **Kênh (Channel):** Mono (`AudioFormat.CHANNEL_IN_MONO`).
- **Định dạng mẫu (Encoding):** `AudioFormat.ENCODING_PCM_16BIT` (16-bit signed integer, Little-Endian).
- **Kích thước Chunk (Chunk Size):** `1280 bytes` tương đương `640 samples` = đúng `40ms` âm thanh.
- **Thread ưu tiên cao:** `android.os.Process.THREAD_PRIORITY_URGENT_AUDIO` đảm bảo không bị giật (drop audio frame).

---

## 2. Phát âm thanh AI (Audio Playback)

### File: `app/src/main/kotlin/com/iva/translate/app/core/audio/AudioPlayerManager.kt`

Âm thanh thuyết minh nhận về từ Gemini API là PCM 24kHz. `AudioPlayerManager` nhận dữ liệu qua Kotlin Coroutine Channel (`Channel<ByteArray>(Channel.UNLIMITED)`), chạy trên luồng `Dispatchers.IO` với cấu hình sau:

- **Tần số lấy mẫu:** `24,000 Hz` (24kHz).
- **Chế độ phát:** `AudioTrack.MODE_STREAM`, `AudioTrack.PERFORMANCE_MODE_LOW_LATENCY`.
- **Mục đích âm thanh (Usage):** `AudioAttributes.USAGE_ASSISTANT`, Content type: `CONTENT_TYPE_SPEECH`.
- **Audio Focus:** Sử dụng `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK` cho phép giảm nhẹ âm lượng ứng dụng khác khi cần.

---

## 3. Bộ lọc định hình chất giọng (DSP Voice Persona Shaping)

Để giúp từng chất giọng nhân vật nghe sống động và đặc trưng hơn, tín hiệu PCM 16-bit được giải mã thành mẫu dạng số thực (Float) và đưa qua bộ lọc DSP tức thời trước khi gửi ra `AudioTrack`:

| Nhân vật (Persona) | Đặc tính giọng | Thuật toán DSP áp dụng |
| :--- | :--- | :--- |
| **Charon** | Trầm ấm & Vang | Tăng cường dải tần thấp (Low-end boost), giảm tần số chói: `(sample * 0.70f) + (prevSample * 0.30f) * 1.08f` |
| **Puck** | Năng động & Sắc nét | Tăng dải tần cao (Presence high-shelf boost): `(sample * 0.85f) + ((sample - prevSample) * 0.28f)` |
| **Aoede** | Ngọt ngào & Ấm áp | Làm mượt hài âm (Balanced harmonic warmth): `(sample * 0.82f) + (prevSample * 0.18f)` |
| **Fenrir** | Uy lực & Rõ ràng | Tăng tính động lực (Firm dynamic presence): `(sample * 0.90f) + ((sample - prevSample) * 0.18f)` |
| **Kore** | Nhẹ nhàng & Điềm tĩnh | Giảm nhẹ dải cao (Gentle softening): `(sample * 0.75f) + (prevSample * 0.25f)` |

### Soft Limiter & Logarithmic Gain:
- Độ lớn âm lượng áp dụng hàm Logarithmic: `gain = (vol * vol * 1.35f)`.
- Mẫu sau khi khuếch đại được kẹp giới hạn: `.coerceIn(-32767f, 32767f)` để triệt tiêu hoàn toàn hiện tượng vỡ âm thanh (digital clipping).

---

## 4. Bộ trộn âm thanh kép (Dual Audio Mixer)

ALAD cho phép người dùng vừa nghe được âm thanh gốc của video (nhạc nền, tiếng động môi trường) vừa nghe rõ giọng thuyết minh tiếng Việt/Anh/Nhật của AI:

1. **Âm lượng giọng AI Thuyết minh (`dubbingVolume`):**
   - Điều khiển trực tiếp bằng phần mềm nhân biên độ PCM và gọi `audioTrack.setVolume()`.
   - Hỗ trợ nút Mute giọng AI nhanh chóng.
2. **Âm lượng Video gốc (`originalMediaVolume`):**
   - Điều khiển kênh `AudioManager.STREAM_MUSIC` của hệ điều hành.
   - Khi khởi động phiên dịch, ALAD lưu lại mức âm lượng gốc ban đầu (`initialMusicVolume`).
   - Khi điều chỉnh thanh trượt, ALAD tự tính tỷ lệ phần trăm so với mức ban đầu.
   - Khi người dùng bấm Stop, ALAD khôi phục nguyên vẹn âm lượng hệ thống ban đầu.
