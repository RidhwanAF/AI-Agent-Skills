# 🔍 Error Detective Skill

Interactive diagnostic and root-cause resolution specialist for Android and Web. Interactively questions error category and affected module (or autonomously inspects logs/diffs if unsure), generates clean non-invasive fixes, and writes comprehensive incident post-mortems in `/docs/error-fix/` with Mermaid sequence diagrams and pie chart scoring.

---

## 🧭 Connected Skills Architecture

```mermaid
flowchart TD
    ED["🔍 /error-detective"]
    
    EM["🚀 /engineer-mode"]
    AA["🏛️ /android-architecture"]
    CUI["🎨 /compose-ui"]
    RTA["🏛️ /react-ts-architecture"]
    RUI["🎨 /react-ui-components"]
    GA["🌿 /git-assistant"]

    EM -->|Incident Triage Request| ED
    ED -->|Android Code Fix| AA
    ED -->|Compose UI Fix| CUI
    ED -->|Web Code Fix| RTA
    ED -->|React UI Fix| RUI
    ED -->|Fix Commits & Branches| GA
```

---

## 📂 Subfolder Structure

| Path | Purpose | Description |
| :--- | :--- | :--- |
| [`SKILL.md`](./SKILL.md) | Agent Skill Definition | Core instructions and triage prompt questionnaire. |
| [`instructions/guidelines.md`](./instructions/guidelines.md) | Agent Execution Protocol | Step-by-step triage, root-cause investigation, and documentation rules. |
| [`instructions/examples.md`](./instructions/examples.md) | Production Documentation Example | Real-world incident report in `/docs/error-fix/` with Mermaid diagrams. |

---

## 🚀 How to Use

### 1. Direct Skill Activation
Trigger this skill whenever you encounter crashes, build errors, memory leaks, or runtime exceptions:
```text
/error-detective
```
*(Alias: `/error-detection`)*

### 2. Guided via Engineer Mode
Start through the adaptive interactive questionnaire:
```text
/engineer-mode
```
Select **Playbook 06 (Diagnostic & Build Remediation)**.

---

## 🔗 Related Skills

- [📱 Android Architecture](../../android-skills/android-architecture/README.md)
- [🎨 Jetpack Compose UI](../../android-skills/compose-ui/README.md)
- [🏛️ React Clean Architecture](../../react-ts-skills/react-ts-architecture/README.md)
- [🌿 Git Assistant](../../git-skills/git-assistant/README.md)
- [🏠 Master Suite README](../../README.md)
