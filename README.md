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

## 🚀 Installation

### Step 1: Clone this repo

```bash
git clone https://github.com/RidhwanAF/AI-Agent-Skills.git
```

### Step 2: Copy skills to your AI agent's global config

Each AI coding assistant reads skills from a specific directory. Copy the `skills/` folder contents to the correct location for your tool(s):

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

Run this one-liner to install skills to **all** supported AI agents globally:

```bash
# Clone the repo first
git clone https://github.com/RidhwanAF/AI-Agent-Skills.git
cd AI-Agent-Skills

# Install to all agents
for agent_dir in ~/.gemini/config/skills ~/.gemini/skills ~/.claude/skills ~/.codex/skills ~/.copilot/skills ~/.cursor/skills; do
  mkdir -p "$agent_dir"
  cp -r skills/* "$agent_dir/"
  echo "✅ Installed to $agent_dir"
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
└── skills/
    ├── agent-audit-workflow/
    │   └── SKILL.md
    ├── android-clean-architecture/
    │   └── SKILL.md
    ├── android-cli/
    │   ├── SKILL.md
    │   ├── interact.md
    │   ├── journeys.md
    │   └── references/
    ├── android-data-layer/
    │   └── SKILL.md
    ├── android-expert/
    │   └── SKILL.md
    ├── android-project-onboarding-architect/
    │   └── SKILL.md
    ├── android-testing/
    │   └── SKILL.md
    ├── compose-ui-maplibre/
    │   └── SKILL.md
    ├── compose-ui-preview-design/
    │   └── SKILL.md
    └── gradle-build-hygiene/
        └── SKILL.md
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

## 🤝 Contributing

1. Fork this repo
2. Add or modify skills in the `skills/` directory
3. Each skill must have a `SKILL.md` with YAML frontmatter (`name`, `description`)
4. Submit a Pull Request

---

## 📄 License

This project is open source. Feel free to use, modify, and share these skills with your team.
