---
name: engineer_mode
description: "Master engineering orchestrator and adaptive entry point (inspired by pstack). Interactively interviews the user, matches requests to specialized playbooks, and dynamically coordinates interconnected skills across Android, React/TS Web, Error Triage, ADR logging, System Design, Changelogs, and Git operations."
---

# 🚀 Engineer Mode (/engineer-mode)

**Engineer Mode** is the unified starting point and chief orchestrator for end-to-end software engineering. Built on the principle that AI coding should be rigorous, verifiable, and free of slop, `engineer-mode` adapts to any user goal, determines the optimal path through curated playbooks, and coordinates specialized skills into an interconnected execution mesh.

---

## 🧭 The Connected Skills Mesh

No skill operates in a silo. Every skill in the suite connects with its peers:

```mermaid
flowchart TD
    EM(["🚀 /engineer-mode<br/>(Master Orchestrator)"])

    subgraph ArchitectureLayer ["📐 Architecture & Design"]
        AD(["📐 /app-design"])
        ADR(["📜 /adr-analyze"])
        CR(["📝 /changelog-recorder"])
    end

    subgraph AndroidSuite ["📱 Modular Android Suite"]
        AA(["🏛️ /android-architecture"])
        CUI(["🎨 /compose-ui"])
        ML(["🗺️ /compose-maplibre"])
        AT(["🧪 /android-testing"])
        GBH(["🐘 /gradle-build-hygiene"])
    end

    subgraph ReactSuite ["⚛️ Modular React TS Suite"]
        RTA(["🏛️ /react-ts-architecture"])
        RUI(["🎨 /react-ui-components"])
        RSM(["⚡ /react-state-management"])
        RT(["🧪 /react-testing"])
    end

    subgraph QualityAndShipping ["🔍 Quality & Version Control"]
        ED(["🔍 /error-detective"])
        GA(["🌿 /git-assistant"])
    end

    GoogleCLI[("🛠️ Google android-cli")]

    %% Orchestrator routing
    EM -->|System Modeling| AD
    EM -->|Document Decisions| ADR
    EM -->|Record Progress| CR
    EM -->|Android Architecture & Domain| AA
    EM -->|Compose UI Components| CUI
    EM -->|Web Architecture & Domain| RTA
    EM -->|Web UI Components| RUI
    EM -->|Investigate Errors| ED
    EM -->|Commit & Ship| GA

    %% Inter-skill collaboration
    AA -->|StateFlow to| CUI
    AA -->|Domain Entities to| AT
    CUI -->|Tested via| AT
    CUI -->|Map Integrations| ML
    GBH -->|Grounded Dependencies to| AA

    RTA -->|Domain Models to| RUI
    RSM -->|Server/Client State to| RUI
    RUI -->|Component Tested via| RT

    AA -->|Device Ops & Doctor| GoogleCLI
    CUI -->|Render Crashes to| ED
    RUI -->|Hydration Bugs to| ED
    ED -->|Verified Fixes Logged in| CR
    CR -->|Staged for Commit by| GA
```

---

## ⚡ Adaptive Entry Point Protocol

When `/engineer-mode` is triggered:

### 1. Assess User Input
- **Scenario A: Explicit Directive Provided**
  *(e.g., `/engineer-mode fix the crash in CartViewModel when payment token expires`)*
  - Extract target platform, scope, and intent.
  - Match immediately to the corresponding playbook (e.g. `01-error-investigation` or `04-android-deep-engineering`).
  - Summarize the execution plan and confirm before proceeding.
- **Scenario B: Broad, Ambiguous, or Empty Prompt**
  *(e.g., `/engineer-mode` or `/engineer-mode I want to build a new feature`)*
  - Initiate the **Interactive Adaptive Questionnaire** below. Do not guess or make unverified assumptions.

### 2. Mandatory Post-Edit Lint & Auto-Fix Gate (Android & Web)
Whenever `/engineer-mode` executes a task on an Android project (or when invoked in an Android workspace), you **MUST** always run a Lint & Compiler check and fix all warnings/errors before finishing:
1. **Run Lint & Kotlin Compilation**: Execute `./gradlew lintDebug` (or `./gradlew :<module>:lintDebug` + `./gradlew compileDebugKotlin` / `detekt` / `ktlintCheck` if configured).
2. **Audit & Auto-Fix Idiomatic Kotlin / Lint Issues**:
   - **KTX Extensions (`UseKtx`)**: Replace `Uri.parse(...)` with `import androidx.core.net.toUri` + `.toUri()`, and `Color.parseColor(...)` with `import androidx.core.graphics.toColorInt` + `.toColorInt()`.
   - **Clean Top-Level Imports**: Replace inline fully-qualified calls like `kotlin.math.abs(...)` or `kotlin.math.max(...)` with top-level `import kotlin.math.abs` and short call `abs(...)`.
   - **Functional Iteration**: Replace verbose `for (...)` loops with idiomatic `.forEach { ... }`, `.forEachIndexed { ... }`, `.map { ... }`, or `.filter { ... }`.
   - **Zero Dead/Redundant Code**: Remove all **unused imports**, **unused parameters** (use `_` in lambdas), and **unnecessary `@OptIn(Experimental...)`** annotations.
   - **Compose Modifier & Hilt Rules**: Verify `modifier: Modifier = Modifier` is `#1` at the very top of `@Composable fun` declarations, `modifier = ...` is at the very bottom of call sites, and `hiltViewModel` is imported from `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`.
3. **Active ADB Device & Live App Logcat Analysis (When Device/Emulator is Connected)**:
   - Run `adb devices -l` to detect if an Android device or emulator is active.
   - Whenever an ADB device is connected, automatically capture and analyze the active app's runtime logs (`adb shell pidof -s <applicationId>` ➔ `adb logcat -d --pid=<PID> -v threadtime` + `adb logcat -d -b crash` + foreground check `adb shell dumpsys window | grep -E 'mCurrentFocus|mFocusedApp'`) to inspect real-time exceptions, network/API logs, coroutine flows, and UI state issues.

### 3. Anti-Slop Self-Learning & 3-Strike Escalation Protocol (Smart Self-Correction)
To prevent repetitive AI mistakes ("AI slop") across sessions or within a conversation:
1. **Track Repeated Corrections (1st, 2nd, 3rd Strike)**:
   - Monitor conversation history and user feedback for repeated requests or corrections (e.g., if the user has to ask **2 or 3 times** for the same fix, pattern, modifier ordering, import path, lint rule, or business logic correction).
2. **Trigger Deep Root-Cause & Skill Self-Upgrade on Repeat**:
   - **Stop Blind Patching**: Never apply a superficial one-line band-aid when an issue repeats.
   - **Workspace-Wide Sweep**: Grep/scan the entire codebase to find and fix **every single occurrence** of that anti-pattern across all files so the user never encounters it in another file.
   - **Permanent Skill Hardening (Self-Evolution)**: If the repeated mistake came from a gap or ambiguity in the AI agent skills (`~/.agents/skills/` or `~/.gemini/config/skills/` or the repository skills), **immediately codify the new rule** with explicit `✅ CORRECT` vs `❌ FORBIDDEN` examples into the relevant `SKILL.md` and `instructions/guidelines.md` files (and recommend `/learn` if appropriate) so the AI becomes permanently smarter and never repeats the mistake!

### 4. Mandatory Post-Execution Visual Summary & Grouped Mermaid Flowchart (In Chat)
Whenever `/engineer-mode` finishes executing any task, you **MUST** conclude your chat response with an **Execution Report Summary** featuring a clean, top-down **Mermaid Flowchart (`flowchart TD`)** styled exactly like the Root README mesh (with flat, single-level slightly dark category `subgraph` containers—**NEVER** nest a `subgraph` inside another `subgraph`, and always use rounded stadium nodes `(["..."])` for every node):
- **Top-Level Root Node**: `EM(["🚀 /engineer-mode<br/>(Master Orchestrator)"])`
- **Flat Top-Level Category Subgraphs** (only include the categories actually used in the task):
  - `subgraph PlaybooksUsed ["📚 Playbooks Executed"]`
  - `subgraph AndroidSuite ["📱 Modular Android Suite"]`
  - `subgraph ReactSuite ["⚛️ Modular React TS Suite"]`
  - `subgraph ArchitectureLayer ["📐 Architecture, Diagnostics & Docs"]`
  - `subgraph QualityAndShipping ["🔍 Verification, Auto-Lint & Shipping"]`
- **Clear Directional Flow Arrows (`-->`)** with concise action labels connecting `/engineer-mode` ➔ Playbooks ➔ Categorized Skills ➔ Quality & Shipping.

#### Example Post-Execution Chat Report Template:
````markdown
### 📊 `/engineer-mode` Execution Summary

```mermaid
flowchart TD
    EM(["🚀 /engineer-mode<br/>(Master Orchestrator)"])

    subgraph PlaybooksUsed ["📚 Playbooks Executed"]
        PB4(["📘 Playbook 04: Android Deep Engineering"])
    end

    subgraph AndroidSuite ["📱 Modular Android Suite"]
        AA(["🏛️ /android-architecture<br/>(KTX, Top Imports, forEach)"])
        CUI(["🎨 /compose-ui<br/>(Modifier Rule, Zero Unused)"])
    end

    subgraph ArchitectureLayer ["📐 Architecture, Diagnostics & Docs"]
        ED(["🔍 /error-detective<br/>(3-Strike Anti-Slop Upgrade)"])
    end

    subgraph QualityAndShipping ["🔍 Verification, Auto-Lint & Shipping"]
        LINT(["🧹 Auto-Lint & Compiler Gate<br/>(./gradlew lintDebug)"])
        GA(["🌿 /git-assistant<br/>(Safe Commit & PR)"])
    end

    EM -->|1. Matched Intent| PB4
    PB4 -->|2. Clean Arch & KTX| AA
    AA -->|3. Compose & Lint Rules| CUI
    PB4 -->|4. Self-Evolution Rule| ED
    CUI -->|5. Verify 0 Warnings| LINT
    ED -->|6. Hardened SKILL.md| LINT
    LINT -->|7. Staged Commit| GA
```
````

---

## 📋 Interactive Playbook Questionnaire

Present the user with clear, structured options to select their desired path:

```markdown
### 🛠️ Engineer Mode: Select an Engineering Playbook

What would you like to accomplish today?

1. 🔍 **[1] Error Detection & Root Cause Analysis** (`/error-detective`)
   - Triage runtime crashes, compilation failures, ANRs, hydration bugs, or memory leaks.
   - Generates automated root-cause diagnosis, code fix, and scoring report in `/docs/error-fix/`.

2. 📐 **[2] Architecture & App System Design** (`/app-design`)
   - Model new modules, domain workflows, UML class diagrams, sequence flows, and use cases.
   - Generates structured documentation in `/docs/architecture/` in English.

3. 📜 **[3] Architectural Decision Record (ADR)** (`/adr-analyze`)
   - Evaluate architecture choices, ground against official Google/W3C docs, and generate MADR entries in `/docs/adr/`.

4. 📱 **[4] Android Engineering Suite**
   - Clean Architecture (`/android-architecture`), Modular Compose UI (`/compose-ui`), Google Maps (`/compose-google-maps`), MapLibre (`/compose-maplibre`), Koin DI (`/koin-di`), Testing (`/android-testing`), Gradle hygiene (`/gradle-build-hygiene`), and Android CLI routing (`/android-cli-router`).

5. ⚛️ **[5] React + TypeScript Modern Web Suite**
   - Frontend Clean Architecture (`/react-ts-architecture`), Accessible Modular UI (`/react-ui-components`), State Sync (`/react-state-management`), and Vitest Testing (`/react-testing`).

6. 📝 **[6] Changelog Documentation** (`/changelog-recorder`)
   - Document technical changes into permanent timestamped records in `/docs/changelogs/YYYY-MM-DD_HH-mm-ss_<title>.md`.

7. 🌿 **[7] Git Assistant & Safe Shipping** (`/git-assistant`)
   - Branching strategies, rebase/merge safety, 3-way conflict resolution, conventional commits, and PR briefings.

8. 🔄 **[8] End-to-End Autonomous Feature Delivery**
   - Full lifecycle: Design (`/app-design`) ➔ ADR (`/adr-analyze`) ➔ Modular Code ➔ Testing ➔ Changelog (`/changelog-recorder`) ➔ Safe Commit (`/git-assistant`).

*Reply with a number (1-8) or describe your goal in detail.*
```

---

## 🗂️ Playbook Execution Matrix

| # | Playbook | Entry Trigger | Primary Skills Involved | Deliverables / Artifacts |
|---|---|---|---|---|
| **1** | [Error Investigation](playbooks/01-error-investigation.md) | `/error-detective`, crash, bug, ANR, 500 | `error-detective`, domain skills | `/docs/error-fix/YYYY-MM-DD-<slug>.md` with Mermaid Pie Score |
| **2** | [ADR Decision Making](playbooks/02-adr-decision-making.md) | `/adr-analyze`, ADR, decision, trade-off | `adr-analyze`, official documentation | `/docs/adr/ADR-XXXX-<title>.md` & index |
| **3** | [App System Design](playbooks/03-app-system-design.md) | `/app-design`, UML, sequence, architecture | `app-design`, domain skills | `/docs/architecture/` with Mermaid UML class & sequence |
| **4** | [Android Engineering](playbooks/04-android-deep-engineering.md) | Android, Compose, Kotlin, Room, Gradle | `android-architecture`, `compose-ui`, `android-testing`, `gradle-build-hygiene` | Modular source files, KDocs, unit tests |
| **5** | [React TS Engineering](playbooks/05-react-ts-web-engineering.md) | React, Next.js, Vite, TypeScript | `react-ts-architecture`, `react-ui-components`, `react-state-management`, `react-testing` | Strict TS components, hooks, tests |
| **6** | [Git Shipping Workflow](playbooks/06-git-shipping-workflow.md) | `/git-assistant`, commit, push, PR, merge | `git-assistant` | Conventional commits, PR brief (Mandatory user confirmation before push/merge) |
| **7** | [Architecture Refactoring](playbooks/07-architecture-refactoring.md) | `/engineer-mode refactor`, technical debt | `engineer-mode`, `app-design`, `adr-analyze` | Refactored modules, ADR records, tests |
| **8** | [Changelog Documentation](playbooks/08-changelog-documentation.md) | `/changelog-recorder`, `/changelog` | `changelog-recorder`, `git-assistant` | `/docs/changelogs/YYYY-MM-DD_HH-mm-ss_*.md` |
