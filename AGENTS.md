# AGENTS.md — Quy tắc hỗ trợ lập trình cho ALAD Mobile

Tệp này được tự động đọc bởi Antigravity / Gemini CLI khi làm việc trong thư mục dự án `ALAD-Mobile`.

## 📌 Nguyên tắc cốt lõi
1. **Kiến trúc & Wiki kỹ thuật:**
   - Xem toàn bộ sơ đồ kiến trúc, Audio Pipeline và giao thức Gemini Live trong thư mục [**`wiki/`**](file:///D:/Workspace/ALAD-Mobile/wiki/README.md).
   - Trước khi sửa logic liên quan đến Audio, WebSocket hoặc Service, hãy kích hoạt hoặc tham chiếu đến Skill [**`voice-coding`**](file:///D:/Workspace/ALAD-Mobile/.agents/skills/voice-coding/SKILL.md).

2. **Quy tắc Build & Chạy thử:**
   - **Không tự ý chạy lệnh Gradle build APK/bundle** khi thiết bị Android thật chưa được người dùng cắm và yêu cầu rõ ràng.
   - Khi người dùng cắm máy thật (`adb devices` hiển thị thiết bị), sử dụng `./gradlew installDebug` hoặc build APK theo hướng dẫn.

3. **Ngôn ngữ mục tiêu hỗ trợ:**
   - Dự án đã được tinh gọn và chỉ tập trung vào 3 ngôn ngữ: **Tiếng Việt (`vi`)**, **Tiếng Anh (`en`)**, **Tiếng Nhật (`ja`)**.
   - Mặc định khởi tạo cho người dùng là `vi`.

4. **Bảo toàn Invariants âm thanh:**
   - Thu âm: 16kHz PCM Mono 16-bit, chunk size 1280 bytes.
   - Phát âm: 24kHz PCM Mono 16-bit qua AudioTrack STREAM mode.
   - State: Mọi giao tiếp giữa Service và UI phải đi qua `DubbingSessionRepository`.

5. **Quy tắc Git & Phiên bản:**
   - **Tuyệt đối không tự động chạy lệnh `git commit` hay `git push`** cho tới khi có yêu cầu hoặc mệnh lệnh rõ ràng từ người dùng.
   - Giữ nguyên các thay đổi trong Working Directory để người dùng chủ động kiểm tra và quyết định việc commit.

6. **Quy tắc điều phối Subagent & Xử lý song song:**
   - Được phép chia nhỏ nhiệm vụ và kích hoạt các subagent xử lý song song các tác vụ độc lập (ví dụ: nghiên cứu độc lập, refactor module riêng biệt, chuẩn bị assets/tài nguyên) nhằm tối ưu thời gian.
   - Giới hạn tối đa **3 subagent** chạy cùng một lúc để tránh xung đột tài nguyên và phân mảnh ngữ cảnh.
   - Agent chính giữ vai trò điều phối tổng thể, tổng hợp kết quả, quản lý build và tương tác trực tiếp với người dùng.
