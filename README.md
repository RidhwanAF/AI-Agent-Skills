# 🤖 AI Agent Skills for Android Development

A curated collection of **AI coding agent skills** that enforce modern Android engineering best practices, clean architecture, and idiomatic Kotlin patterns across your entire team.

These skills work as instruction sets that AI coding assistants read and follow — ensuring consistent, production-quality code output regardless of which AI tool your team uses.

---

## 📦 What's Included

| Skill | Description |
|-------|-------------|
| **`android-expert`** | Comprehensive Android engineering guide: Jetpack Compose UI, Coroutines & Flow, MVVM/MVI, Hilt DI, shared element transitions with Coil, `PredictiveBackHandler`, `kotlin.time.Duration` enforcement, dead code cleanup, deprecation avoidance, DataStore over SharedPreferences, and Android 14/15 restrictions. |
| **`android-clean-architecture`** | Enforces Clean Architecture layer boundaries, multi-module dependency rules, Dagger Hilt DI scope management, and Navigation 3 type-safe routes. |
| **`android-data-layer`** | Best practices for Retrofit, Room Database, Jetpack DataStore, and `kotlinx.serialization` — prevents data layer leakage into domain/presentation. |
| **`android-testing`** | Automated unit test generation standards for ViewModels, UseCases, Repositories, and Compose UI components. |
| **`compose-ui-preview-design`** | Edge-to-edge window insets, Material 3 previews, Compose state stability (`@Immutable`/`@Stable`), haptic feedback patterns, and fake offline repository mocking. |
| **`compose-ui-maplibre`** | Jetpack Compose UI patterns with MapLibre Compose Material 3 integration and lifecycle safety. |
| **`gradle-build-hygiene`** | Version catalog (`libs.versions.toml`) management, R8/ProGuard keep rules, and Room database migration safety. |
| **`agent-audit-workflow`** | Pre-edit codebase analysis, changelog documentation, and static verification workflow. |
| **`android-project-onboarding-architect`** | Auto-triggers at task start to check project initialization, set up stack configs, and enforce global Android best practices. |
| **`android-cli`** | Instructions for using the `android` CLI tool — project creation, device deployment, AVD management, SDK inspection, and official docs lookup. |

---

## ⚡ Two Editions Available

| Edition | Directory | Best For | Description |
|---|---|---|---|
| **Standard Edition** | [`skills/`](file:///Users/raf/Downloads/AI-Agent-Skills/skills/) | Deep Architecture & Learning | In-depth documentation, full architectural explanations, exhaustive code patterns, and complete rationale. |
| **Token-Efficient Edition** | [`AI-Skills-Token-Efficient/`](file:///Users/raf/Downloads/AI-Agent-Skills/AI-Skills-Token-Efficient/) | Limited Company AI Credits / Fast Responses | Ultra-compact (~85% fewer tokens), straight-to-the-point rules, direct code answers, zero conversational filler, no unnecessary web searches. |

---

## 🚀 Installation

### Step 1: Clone this repo

```bash
git clone https://github.com/RidhwanAF/AI-Agent-Skills.git
cd AI-Agent-Skills
```

### Step 2: Choose your edition and copy to your AI agent

Select either **`skills/*`** (Standard) or **`AI-Skills-Token-Efficient/*`** (Token-Efficient for limited company credits), then copy to your AI agent's directory:

---

### Google Gemini CLI / Gemini Code Assist

```bash
# Create the skills directory if it doesn't exist
mkdir -p ~/.gemini/config/skills

# Copy all skills
cp -r AI-Agent-Skills/skills/* ~/.gemini/config/skills/
```

**Skills location:** `~/.gemini/config/skills/<skill-name>/SKILL.md`

---

### Claude Code (Anthropic)

```bash
mkdir -p ~/.claude/skills

cp -r AI-Agent-Skills/skills/* ~/.claude/skills/
```

**Skills location:** `~/.claude/skills/<skill-name>/SKILL.md`

---

### OpenAI Codex CLI

```bash
mkdir -p ~/.codex/skills

cp -r AI-Agent-Skills/skills/* ~/.codex/skills/
```

**Skills location:** `~/.codex/skills/<skill-name>/SKILL.md`

---

### GitHub Copilot (CLI / Agent Mode)

```bash
mkdir -p ~/.copilot/skills

cp -r AI-Agent-Skills/skills/* ~/.copilot/skills/
```

**Skills location:** `~/.copilot/skills/<skill-name>/SKILL.md`

---

### Cursor

```bash
mkdir -p ~/.cursor/skills

cp -r AI-Agent-Skills/skills/* ~/.cursor/skills/
```

**Skills location:** `~/.cursor/skills/<skill-name>/SKILL.md`

> **Tip:** In Cursor, you can also reference skills in your `.cursorrules` file at the project root.

---

### Windsurf (Codeium)

```bash
mkdir -p ~/.codeium/windsurf/skills

cp -r AI-Agent-Skills/skills/* ~/.codeium/windsurf/skills/
```

**Skills location:** `~/.codeium/windsurf/skills/<skill-name>/SKILL.md`

---

### Project-Level Installation (Any AI Agent)

You can also install skills at the **project level** so they only apply to a specific repo. This is useful for sharing skills with your team via version control.

```bash
# For Gemini
mkdir -p <project-root>/.gemini/skills
cp -r AI-Agent-Skills/skills/* <project-root>/.gemini/skills/

# For Claude
mkdir -p <project-root>/.claude/skills
cp -r AI-Agent-Skills/skills/* <project-root>/.claude/skills/

# For Codex
mkdir -p <project-root>/.codex/skills
cp -r AI-Agent-Skills/skills/* <project-root>/.codex/skills/
```

> **Note:** Project-level skills typically take precedence over global skills when both exist.

---

## 📝 Quick Install Script (All Agents at Once)

Run this one-liner to install skills to **all** supported AI agents globally.

### Option A: Token-Efficient Edition (Recommended for limited credits)
```bash
git clone https://github.com/RidhwanAF/AI-Agent-Skills.git
cd AI-Agent-Skills

for agent_dir in ~/.gemini/config/skills ~/.gemini/skills ~/.claude/skills ~/.codex/skills ~/.copilot/skills ~/.cursor/skills; do
  mkdir -p "$agent_dir"
  cp -r AI-Skills-Token-Efficient/* "$agent_dir/"
  echo "✅ Installed Token-Efficient skills to $agent_dir"
done
```

### Option B: Standard Edition (In-depth architecture & rationale)
```bash
git clone https://github.com/RidhwanAF/AI-Agent-Skills.git
cd AI-Agent-Skills

for agent_dir in ~/.gemini/config/skills ~/.gemini/skills ~/.claude/skills ~/.codex/skills ~/.copilot/skills ~/.cursor/skills; do
  mkdir -p "$agent_dir"
  cp -r skills/* "$agent_dir/"
  echo "✅ Installed Standard skills to $agent_dir"
done
```

---

## 🔄 Updating Skills

To pull the latest updates and re-sync:

```bash
cd AI-Agent-Skills
git pull origin main

# Re-sync to all agents
for agent_dir in ~/.gemini/config/skills ~/.gemini/skills ~/.claude/skills ~/.codex/skills ~/.copilot/skills ~/.cursor/skills; do
  mkdir -p "$agent_dir"
  cp -r skills/* "$agent_dir/"
  echo "✅ Updated $agent_dir"
done
```

---

## 📂 Folder Structure

```
AI-Agent-Skills/
├── README.md
├── skills/                             # Standard Edition (In-depth)
│   ├── agent-audit-workflow/
│   ├── android-clean-architecture/
│   ├── android-cli/
│   ├── android-data-layer/
│   ├── android-expert/
│   ├── android-project-onboarding-architect/
│   ├── android-testing/
│   ├── compose-ui-maplibre/
│   ├── compose-ui-preview-design/
│   └── gradle-build-hygiene/
└── AI-Skills-Token-Efficient/          # Token-Efficient Edition (~85% fewer tokens)
    ├── agent-audit-workflow/
    ├── android-clean-architecture/
    ├── android-cli/
    ├── android-data-layer/
    ├── android-expert/
    ├── android-project-onboarding-architect/
    ├── android-testing/
    ├── compose-ui-maplibre/
    ├── compose-ui-preview-design/
    └── gradle-build-hygiene/
```

---

## 🧠 How Skills Work

Each skill is a `SKILL.md` markdown file with:

1. **YAML Frontmatter** — `name` and `description` metadata
2. **Detailed Instructions** — Rules, patterns, code examples, and anti-patterns

When you use an AI coding agent (Gemini, Claude, Codex, etc.), it automatically reads the `SKILL.md` files from its skills directory and follows the instructions when generating or modifying code.

### Example: What the `android-expert` skill enforces

```kotlin
// ❌ Without skill — AI might generate this
val retryDelay = 3000L
delay(retryDelay)
Handler(Looper.getMainLooper()).postDelayed({ refresh() }, 5000)
val prefs = getSharedPreferences("settings", MODE_PRIVATE)

// ✅ With skill — AI generates this instead
val retryDelay = 3.seconds
delay(retryDelay)
lifecycleScope.launch { delay(5.seconds); refresh() }
val settings = context.settingsDataStore.data.first()
```

---

## 🧸 Runnable Demo Apps: Chiikawa Stretchy Character

Inside [`apps/`](file:///Users/raf/Downloads/AI-Agent-Skills/apps/) are two **fully runnable, independent Gradle Android projects** generated from the exact same user prompt to benchmark both skills editions:

- **[apps/chiikawa-character-standard/](file:///Users/raf/Downloads/AI-Agent-Skills/apps/chiikawa-character-standard/)** — Built using **Standard Edition** (modular Clean Architecture, pure domain physics, KDocs).
- **[apps/chiikawa-character-efficient/](file:///Users/raf/Downloads/AI-Agent-Skills/apps/chiikawa-character-efficient/)** — Built using **Token-Efficient Edition** (direct code first, zero fluff, ~81% fewer total tokens).

Read the detailed benchmark report: **[`CHIIKAWA_APPS_COMPARISON.md`](file:///Users/raf/Downloads/AI-Agent-Skills/CHIIKAWA_APPS_COMPARISON.md)**.

Both apps feature:
- 🌸 **Mochi Elastic Physics:** Squash-and-stretch with volume preservation on Canvas.
- 📐 **3D Perspective Tilt (Z-Axis):** Interactive spherical rotation and facial parallax.
- 📳 **Haptic Feedback:** `SegmentTick`, `GestureThresholdActivate`, and `Confirm` snap-back.
- 🍮 **Spring Return:** Jelly-like bouncy return when released.

To open and run:
Open either folder in **Android Studio** and click **Run**, or run `./gradlew assembleDebug`.

---

## 🏆 Built With These Skills — Published on Google Play

These skills aren't just theory — they power **real apps published on the Google Play Store**.

### 📱 Popup Anything

<a href="https://play.google.com/store/apps/details?id=com.raf.popupanything">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" width="200"/>
</a>

The **Find My Device** feature in this app was **completely built by [Google Antigravity](https://github.com/google-gemini/antigravity)** AI agent using these skills — from architecture to implementation. No manual coding involved for that feature.

### ⚔️ Gomi Samurai

<a href="https://play.google.com/store/apps/details?id=com.raf.gomisamurai">
  <img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" width="200"/>
</a>

This app was **100% built by AI from scratch** — including all the in-game images and assets. The video demo was created using public Gemini AI from the web.

> These apps demonstrate that with the right set of skills, AI coding agents can produce **production-quality, Play Store-ready Android applications**.

## 🤝 Contributing

1. Fork this repo
2. Add or modify skills in the `skills/` directory
3. Each skill must have a `SKILL.md` with YAML frontmatter (`name`, `description`)
4. Submit a Pull Request

---

## 📄 License

This project is open source. Feel free to use, modify, and share these skills with your team.
