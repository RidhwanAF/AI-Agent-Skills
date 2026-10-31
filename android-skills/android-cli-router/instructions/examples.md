# 🛠️ Android CLI Router Examples

Examples of command dispatching to the official Google Android CLI.

---

## 1. Running an Application on Device

```bash
# Builds, delta-deploys, and launches the app on a connected device
android run --debug
```

---

## 2. Inspecting Layout Tree

```bash
# Returns the hierarchical layout tree of the running activity in formatted JSON
android layout --pretty
```

---

## 3. Capturing Device Screen

```bash
# Captures current screen to a PNG file for UI verification
android screen capture
```

---

## 4. Searching Official Android Documentation

```bash
# Searches the Google Android Knowledge Base for Compose animations
android docs search "Jetpack Compose animateContentSize transition"
```

---

## 5. Initializing Official Skills into AI Agents

```bash
# Sets up official Android skills across all configured AI agents
android init
```

---

## 6. Active ADB Device Detection & Live App Logcat Capture

```bash
# 1. Check if an active device or emulator is connected
adb devices -l

# 2. Identify the currently focused foreground app & activity
adb shell dumpsys window | grep -E "mCurrentFocus|mFocusedApp"

# 3. Get the PID of the active app and dump its live logcat + crash buffer
APP_PID=$(adb shell pidof -s com.example.app)
adb logcat -d --pid="$APP_PID" -v threadtime *:I
adb logcat -d -b crash -v threadtime
```
