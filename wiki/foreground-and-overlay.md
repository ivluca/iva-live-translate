# 🪟 Dịch vụ Nền & Cửa sổ Nổi (Foreground & Floating Overlay)

Ứng dụng thuyết minh trực tiếp cần hoạt động liên tục trong khi người dùng chuyển sang xem YouTube, Netflix hoặc chơi game. Do đó, cơ chế Background Service và WindowManager Overlay là phần cốt tử.

---

## 1. Xin quyền thu âm hệ thống không làm gián đoạn người dùng

### File: `TransparentCaptureActivity.kt`
- Để sử dụng `MediaProjectionManager.createScreenCaptureIntent()`, Android bắt buộc phải có một `Activity` để hiển thị hộp thoại cảnh báo của hệ điều hành: *"ALAD will start capturing everything that's displayed on your screen or played from your device."*
- Nếu mở lại `MainActivity`, người dùng sẽ bị kéo ra khỏi ứng dụng họ đang xem (ví dụ đang xem dở video YouTube).
- **Giải pháp của ALAD:** Sử dụng một Activity hoàn toàn trong suốt (`Theme.Translucent.NoTitleBar`), gắn cờ `excludeFromRecents=true` và `taskAffinity=""`. Khi người dùng bấm nút bắt đầu từ Floating Widget, `TransparentCaptureActivity` xuất hiện vô hình, chỉ hiển thị đúng hộp thoại cho phép của Android, sau đó gửi kết quả cho Foreground Service và tự hủy (`finish()`) sau 200ms.

---

## 2. Dịch vụ nền giữ tiến trình sống (Foreground Service)

### File: `AudioDubbingForegroundService.kt`
- Đăng ký `foregroundServiceType="mediaProjection|mediaPlayback"`.
- Duy trì Notification liên tục trên thanh thông báo với ID `NOTIFICATION_ID = 101`.
- Lắng nghe sự thay đổi của cấu hình từ `UserPreferencesRepository` (ví dụ người dùng đổi target language từ Vi sang En, đổi giọng persona từ Puck sang Charon, đổi âm lượng mixer) để tự động điều chỉnh mà không cần ngắt phiên.

---

## 3. Cửa sổ nổi điều khiển & Phụ đề song ngữ (Floating Overlay)

### File: `OverlayWidgetService.kt`

Khác với các ứng dụng truyền thống viết giao diện Overlay bằng XML View cồng kềnh, ALAD sử dụng **Jetpack Compose** chạy trực tiếp trên WindowManager bằng cách bao bọc bên trong `ComposeView` kết hợp `LifecycleService`:

```kotlin
private val savedStateRegistryOwner by lazy { ServiceSavedStateRegistryOwner(this) }
private val viewModelStoreOwner by lazy { ServiceViewModelStoreOwner() }
```

### Thành phần giao diện của Overlay:
1. **Pill Controller (Thanh điều khiển thu gọn):**
   - Nút Start / Stop thuyết minh với hiệu ứng chuyển màu sống động.
   - Nút Mute / Unmute âm thanh thuyết minh AI.
   - Nút bật / tắt hiển thị khung phụ đề.
   - Khả năng kéo thả (Drag & Drop) di chuyển linh hoạt tự do tới bất kỳ vị trí nào trên màn hình.
2. **Subtitle Card (Khung phụ đề song ngữ nổi):**
   - Nằm ở cạnh dưới màn hình, thiết kế kính mờ (Frosted Glass) bán trong suốt không che nội dung video.
   - Hiển thị song song: dòng phụ đề ngôn ngữ gốc (ví dụ tiếng Anh) bên trên và dòng phụ đề bản dịch (tiếng Việt / tiếng Nhật) nổi bật bên dưới.
   - Tự động ẩn nhẹ nhàng khi hết câu nói hoặc tạm ngừng.
