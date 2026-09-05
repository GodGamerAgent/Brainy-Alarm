# ⏰ I Can't Wake Up! Alarm Clock

> **The ultimate anti-cheat wake-up system for heavy sleepers.**  
> Built with modern Android (Kotlin & Jetpack Compose), featuring cognitive wake-up challenges, anti-shutdown cheat protection, live audio sample preview, and custom sound support.

---

## 📌 Project Overview

**I Can't Wake Up!** is an extreme alarm clock application designed specifically for deep sleepers and habitual alarm snoozers. Traditional alarms allow you to turn them off half-asleep or simply power down the phone to escape the ringing. 

This application eliminates cheat methods by pairing cognitive wake-up challenges with multi-tier **Anti-Shutdown & Anti-Cheat Protection**, preventing power-menu pull-downs, forceful app termination, and unverified dismissals until your brain is fully awake.

---

## ✨ Key Features

### 🛡️ Anti-Shutdown & Anti-Cheat Defense Suite
- **Device Administrator Protection**: Prevents force-stopping or uninstalling the app while an alarm is ringing.
- **Accessibility Service Interception**: Detects and suppresses attempts to open the power/shutdown dialog or notification shade during an active alarm.
- **Full-Screen System Alert Overlay**: Rings over the lock screen and all running apps (`SYSTEM_ALERT_WINDOW`) with automatic screen wake-up (`FLAG_KEEP_SCREEN_ON`, `FLAG_TURN_SCREEN_ON`).
- **Battery Optimization Bypass**: Requests exemption from Android Doze mode to guarantee alarms trigger reliably down to the exact second.

### 🧠 Cognitive Wake-Up Challenges
To dismiss or snooze an alarm, you must solve one or more configurable cognitive tasks:
1. **Math Problems**: Configurable difficulty levels (easy, medium, hard) with arithmetic operations.
2. **Memory Match**: Tile matching card game testing visual recall.
3. **Sequence Order**: Tap numbers and patterns in exact ascending or descending order.
4. **Simon Says**: Repeat colored light and tone sequences.
5. **Shake Challenge**: Physical movement verification requiring intense shaking of the device.
6. **Stroop Effect**: Color-word psychological test to ensure active cognitive awareness.
7. **Typing Challenge**: Type randomized quotes or anti-sleep mantras accurately.
8. **Barcode / QR Code**: Requires getting out of bed to scan a physical code located in another room (e.g., bathroom mirror, kitchen fridge).

### 🎵 Sound & Audio Engine
- **Live Sound Sample Preview**: Audition preset alarm sounds, system ringtones, or custom tracks with instant play/stop buttons before setting them.
- **Custom Audio File Import**: Choose and play any local audio file (`.mp3`, `.wav`, `.m4a`, `.ogg`, `.flac`) directly from device storage.
- **System Ringtone Selector**: Integrate native device alarm and notification sounds via Android's ringtone picker.
- **Built-in Synthesized Alert Tones**: Extreme Siren, Digital Beep, Emergency Pulse, and Rooster alarm tones that cut through deep sleep.
- **Crescendo Volume**: Smoothly ramp volume from gentle chimes up to 100% maximum output over 10 to 60 seconds.

### ⏰ Smart Alarm Capabilities
- **Awake Test Verification**: Triggers a quiet check-in notification a few minutes after dismissal to verify you didn't fall back asleep. If unanswered, the alarm triggers again!
- **Quick Nap**: One-tap power nap timer (15, 30, 45, 60 minutes) with instant countdown.
- **Flexible Repeat & Snooze Caps**: Customize repeat schedules by day of week and restrict max snooze count to eliminate procrastination.

### 💎 Futuristic Liquid Glass UI
- Crafted with **Jetpack Compose** following Material 3 guidelines.
- Specular frosted glass cards, dynamic cyan/emerald glow borders, smooth motion transitions, and high-contrast dark theme.
- Responsive design tailored for compact phones, foldable devices, and tablets.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Toolkit**: Jetpack Compose + Material Design 3 (M3)
- **Architecture**: Clean Architecture / MVVM with Kotlin Coroutines & Flow
- **Local Persistence**: Room Database (SQLite)
- **Audio & Media**: Custom synthesized tone generator (`AudioTrack`) + `MediaPlayer` / `RingtoneManager`
- **System Integration**:
  - `DevicePolicyManager` (Device Administrator)
  - `AccessibilityService` (Shutdown & power dialog mitigation)
  - `AlarmManager` (`setExactAndAllowWhileIdle`)
  - `WindowManager` & Foreground Services for uninterrupted lockscreen playback

---

## 🔒 Required Permissions & Rationale

| Permission | Purpose |
|------------|---------|
| `USE_EXACT_ALARM` / `SCHEDULE_EXACT_ALARM` | Ensures alarms trigger at the exact scheduled second |
| `SYSTEM_ALERT_WINDOW` | Renders alarm ringing screen and challenges over the lock screen |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Reliable continuous background alarm sound playback |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Prevents the OS from putting the alarm process to sleep |
| `BIND_DEVICE_ADMIN` | Prevents premature force-stopping during wake-up tasks |
| `BIND_ACCESSIBILITY_SERVICE` | Detects power dialog events to prevent cheat shut-offs |
| `READ_MEDIA_AUDIO` / Storage Access | Imports custom user music and sound files |
| `CAMERA` | Scans QR codes / barcodes for the barcode wake-up challenge |
| `VIBRATE` | Drives custom vibration cadences and patterns |

---

## 🚀 Getting Started & Building

### Prerequisites
- **Android Studio Ladybug (2024.2+)** or newer
- **JDK 17** or higher
- **Android SDK Platform 35** (Min SDK: 26 / Android 8.0 Oreo)

### Build Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/i-cant-wake-up-alarm.git
   cd i-cant-wake-up-alarm
   ```

2. **Open in Android Studio:**
   - Select **File > Open** and select the repository root directory.
   - Wait for Gradle to complete dependency sync.

3. **Build the Debug APK via command line:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit & Screenshot Tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```

5. **Deploy to Device or Emulator:**
   - Connect your Android device or start an emulator.
   - Run the app from Android Studio or run:
     ```bash
     ./gradlew installDebug
     ```

---

## 📱 First-Time Setup Instructions

1. **Launch the App**: Grant required alarm and notification permissions when prompted.
2. **Enable Anti-Shutdown Protection (Optional but Recommended)**:
   - Navigate to the **Anti-Shutdown Protection** section from the menu or dashboard.
   - Grant **Device Administrator** to enable anti-uninstall defense.
   - Enable the **Accessibility Service** (`I Can't Wake Up Protection`) to block power-down screens during active ringing.
   - Allow **Ignore Battery Optimizations** so the OS doesn't sleep through your alarm.
3. **Configure Your First Alarm**:
   - Tap **+** to set time, repeat days, and label.
   - Select wake-up challenges (e.g., Math + QR Code).
   - Preview sounds and select your favorite ringtone or import custom audio.
   - Tap **Save Alarm**.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE) - feel free to modify and adapt it for personal or educational use.

---

# This Project is completely built using Google AI Studio
