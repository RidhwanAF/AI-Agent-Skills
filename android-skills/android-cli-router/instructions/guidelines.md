# 🛠️ Android CLI Router Guidelines

Operational protocol for routing developer requests to the official Google Android CLI.

---

## 🎯 Protocol & Dispatch Rules

1. **Discovery & Verification**:
   - Check if `android` is executable via PATH or standard locations (`/usr/local/bin/android`, `/opt/homebrew/bin/android`, `~/.local/bin/android`).
   - If missing, offer installation using Google's official curl installer.
2. **Dispatch Mapping**:
   - **Running Apps**: Execute `android run` instead of compiling and manually invoking `adb install` and `adb shell am start`.
   - **Inspecting UI**: Execute `android layout` to get the structured JSON layout tree.
   - **Capturing Screenshots**: Execute `android screen` to capture high-resolution device screens.
   - **Official Documentation**: Execute `android docs search "<topic>"` to query the authoritative Android Knowledge Base.
3. **Collaboration with Official Skill**:
   - This skill is an intelligent router within the custom AI-Engineer-Skills suite. It coexists with the official `android-cli` skill installed by `android init`.
   - Never attempt to overwrite or delete the official `android-cli` skill.
4. **Active ADB Device Logcat Telemetry**:
   - Always check `adb devices` when analyzing or verifying an Android app.
   - If an active device/emulator is connected, resolve the target app's PID (`adb shell pidof -s <package>`) or foreground package (`adb shell dumpsys activity activities | grep ResumedActivity`) and capture `adb logcat -d --pid=<PID>` and `adb logcat -d -b crash` to ground your analysis in real runtime telemetry.
