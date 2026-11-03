# 🌐 Gemini Live WebSocket Protocol

Giao tiếp giữa ứng dụng ALAD và đám mây Google sử dụng giao thức song công (full-duplex) WebSocket của Google Generative AI Multimodal Live API.

---

## 1. Kết nối & Endpoint

- **URL Endpoint:**  
  `wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1beta.GenerativeService.BidiGenerateContent?key={GEMINI_API_KEY}`
- **Client:** Sử dụng thư viện `OkHttpClient` với cấu hình tối ưu timeout.

---

## 2. Giai đoạn Khởi tạo (Handshake & Setup)

Ngay sau khi WebSocket mở (`onOpen`), ALAD gửi tin nhắn JSON `setup` để cấu hình model và các thông số dịch:

```json
{
  "setup": {
    "model": "models/gemini-3.5-live-translate-preview",
    "generationConfig": {
      "responseModalities": ["AUDIO"],
      "speechConfig": {
        "voiceConfig": {
          "prebuiltVoiceConfig": {
            "voiceName": "Puck"
          }
        }
      },
      "translationConfig": {
        "targetLanguageCode": "vi",
        "echoTargetLanguage": true
      }
    },
    "systemInstruction": {
      "parts": [
        {
          "text": "You are an expert real-time AI live audio dubber. Nói bằng tiếng Việt giọng Bắc (Hà Nội) tự nhiên, rõ ràng, phát âm chuẩn. Maintain natural pacing and high clarity."
        }
      ]
    },
    "inputAudioTranscription": {},
    "outputAudioTranscription": {},
    "sessionResumption": {
      "handle": null
    }
  }
}
```

> [!NOTE]
> `inputAudioTranscription` và `outputAudioTranscription` là trường trực tiếp của `setup` theo đặc tả mới của Google Gemini Live API. Nhờ đó, máy chủ đồng thời trả về văn bản nhận dạng tiếng gốc và văn bản bản dịch để hiển thị phụ đề song ngữ song song với audio.

---

## 3. Quá trình Stream âm thanh đi (Upstream Streaming)

Khi `setupComplete` được xác nhận từ phía server, ALAD đẩy liên tục các khối âm thanh PCM 16kHz vào WebSocket:

```json
{
  "realtimeInput": {
    "audio": {
      "mimeType": "audio/pcm;rate=16000",
      "data": "<BASE64_ENCODED_PCM_BYTES>"
    }
  }
}
```

- Nếu chưa hoàn tất `setup`, các audio chunk đầu tiên được lưu tạm vào hàng đợi `pendingAudio` (giới hạn tối đa 8 chunks) và gửi bù ngay khi server phản hồi `setupComplete`.

---

## 4. Quá trình Nhận dữ liệu về (Downstream Processing)

Server Gemini phản hồi qua tin nhắn `serverContent`:

```json
{
  "serverContent": {
    "outputTranscription": {
      "text": "Xin chào, hôm nay chúng ta sẽ tìm hiểu về..."
    },
    "inputTranscription": {
      "text": "Hello, today we are going to explore..."
    },
    "modelTurn": {
      "parts": [
        {
          "inlineData": {
            "mimeType": "audio/pcm;rate=24000",
            "data": "<BASE64_PCM_24KHZ_AUDIO>"
          }
        }
      ]
    },
    "turnComplete": false
  }
}
```

### Xử lý trong `ALADWebSocketManager.kt`:
1. **`outputTranscription.text`:** Đưa vào callback `onTextMessageReceived` -> chuyển tới `DubbingSessionRepository.appendSubtitleText()`.
2. **`inputTranscription.text`:** Đưa vào callback `onSourceTextReceived` -> chuyển tới `DubbingSessionRepository.appendSourceSubtitleText()`.
3. **`inlineData.data`:** Giải mã Base64 thành mảng `ByteArray` PCM 24kHz -> đưa vào `onBinaryMessageReceived` -> chuyển trực tiếp tới `AudioPlayerManager.playAudioData()`.
4. **`turnComplete: true`:** Báo hiệu hoàn tất một lượt nói (turn) -> reset trạng thái phụ đề cho câu tiếp theo.
