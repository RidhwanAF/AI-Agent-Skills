# 🚀 Engineer Mode Workflow Examples

Example interaction traces demonstrating the adaptive question-and-route protocol.

---

## 1. Interactive Session Example: Diagnostic Routing

```text
User: /engineer-mode

Agent:
🛠️ AI Engineering Mode Active
What would you like to achieve today?

[1] 🔍 Investigate & Fix an Error (Crash, ANR, build failure, hydration bug)
[2] 📜 Make / Document an Architecture Decision (ADR)
[3] 📐 System Design & Visual Modeling (UML, Sequence, Use Cases)
[4] 📱 Deep Android Engineering (Clean Arch, Compose, MapLibre, Gradle)
[5] ⚛️ Deep React + TypeScript Engineering (Clean Arch, Components, State)
[6] 🌿 Git & Shipping Assistant (Commits, Rebase, PR Briefing)
[7] 🏗️ Refactor Legacy Codebase
[8] 📝 Record Timestamped Changelog

User: 1

Agent:
Loading Playbook 01: Error Investigation & Triage.
Routing to 🔍 /error-detective...

To help me pinpoint and resolve the issue quickly:
1. What kind of error are you encountering? (Crash / Build / ANR / Runtime / Unsure)
2. In which feature, screen, or file did you observe this?
```

---

## 2. Multi-Skill Routing Chain

```mermaid
sequenceDiagram
    actor Developer
    participant EM as /engineer-mode
    participant ED as /error-detective
    participant AA as /android-architecture
    participant GA as /git-assistant
    participant CR as /changelog-recorder

    Developer->>EM: Select Option [1]
    EM->>ED: Route to Error Detective
    ED->>Developer: Triage & Root Cause Identified
    ED->>AA: Apply Clean Architecture Fix
    AA->>Developer: Fix verified via ./gradlew test
    ED->>Developer: Generate /docs/error-fix/ report
    EM->>CR: Generate timestamped /docs/changelogs/ record
    EM->>GA: Stage atomic commit & create PR briefing
```

---

## 3. Post-Execution Chat Report Summary (README-Style Slightly Dark Container Mermaid Diagram)

At the completion of every `/engineer-mode` task, the agent renders a clean, top-down `flowchart TD` Mermaid diagram in the chat response using flat, single-level category `subgraph` containers (matching the Root README style, without nested subgraphs) and stadium nodes `(["..."])`:

````markdown
### 📊 `/engineer-mode` Execution Report Summary

```mermaid
flowchart TD
    EM(["🚀 /engineer-mode<br/>(Master Orchestrator)"])

    subgraph PlaybooksUsed ["📚 Playbooks Executed"]
        PB1(["📘 Playbook 01: Error Investigation"])
        PB4(["📘 Playbook 04: Android Deep Engineering"])
    end

    subgraph AndroidSuite ["📱 Modular Android Suite"]
        AA(["🏛️ /android-architecture<br/>(Enforce KTX & Clean Arch)"])
        CUI(["🎨 /compose-ui<br/>(Modifier & Hilt Rules)"])
        GBH(["🐘 /gradle-build-hygiene<br/>(Version Catalog Check)"])
    end

    subgraph ArchitectureLayer ["📐 Architecture, Diagnostics & Docs"]
        ED(["🔍 /error-detective<br/>(3-Strike Anti-Slop Fix)"])
        CR(["📝 /changelog-recorder<br/>(Record Artifacts)"])
    end

    subgraph QualityAndShipping ["🔍 Verification, Auto-Lint & Shipping"]
        LINT(["🧹 Auto-Lint & Compiler Gate<br/>(./gradlew lintDebug)"])
        GA(["🌿 /git-assistant<br/>(Stage Safe Commit)"])
    end

    EM -->|1. Triage & Route| PB1
    EM -->|2. Domain Execution| PB4
    PB1 -->|Root-Cause Audit| ED
    PB4 -->|KTX, Imports & forEach| AA
    AA -->|UI & Modifier Hygiene| CUI
    CUI -->|Catalog Verification| GBH
    ED -->|3-Strike Self-Upgrade| AA
    GBH -->|Verify 0 Warnings| LINT
    LINT -->|Log Changes| CR
    CR -->|Ready to Ship| GA
```
````
