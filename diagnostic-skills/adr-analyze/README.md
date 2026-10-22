# 📜 Architecture Decision Record (ADR) Skill

Captures, evaluates, and documents significant architectural decisions across Android and Web projects. Ensures technical choices are systematically reasoned with trade-offs, grounded in official documentation (Google, React, W3C, MDN), and presented with an immediate **executive summary**, **visual Mermaid pie chart scoring**, and a **detailed multi-criteria decision matrix** before the in-depth technical analysis in `/docs/adr/`.

---

## 🧭 Connected Skills Architecture

```mermaid
flowchart TD
    ADR["📜 /adr-analyze"]
    
    EM["🚀 /engineer-mode"]
    AD["📐 /app-design"]
    AA["🏛️ /android-architecture"]
    RTA["🏛️ /react-ts-architecture"]
    GA["🌿 /git-assistant"]

    EM -->|Architecture Evaluation| ADR
    AD -->|Supplies System Blueprint to| ADR
    ADR -->|Enforces Standards in| AA
    ADR -->|Enforces Standards in| RTA
    ADR -->|Commits Decisions via| GA
```

---

## 📂 Subfolder Structure

| Path | Purpose | Description |
| :--- | :--- | :--- |
| [`SKILL.md`](./SKILL.md) | Agent Skill Definition | Core triggers, executive summary, Mermaid pie chart scoring, and MADR template. |
| [`instructions/guidelines.md`](./instructions/guidelines.md) | Agent Execution Protocol | Executive summary first rule, weighted scoring rubric, and official docs grounding. |
| [`instructions/examples.md`](./instructions/examples.md) | Production Documentation Example | Real-world MADR record featuring executive summary, Mermaid pie chart, and scoring matrix. |

---

## 🚀 How to Use

### 1. Direct Skill Activation
Trigger this skill whenever making technology choices, major refactors, or layer restructuring:
```text
/adr-analyze
```

### 2. Guided via Engineer Mode
Start through the adaptive interactive questionnaire:
```text
/engineer-mode
```
Select **Playbook 01 (Architecture Blueprint & System Design)**.

---

## 🔗 Related Skills

- [📐 App Design](../app-design/README.md)
- [🏛️ Android Architecture](../../android-skills/android-architecture/README.md)
- [🏛️ React Clean Architecture](../../react-ts-skills/react-ts-architecture/README.md)
- [🌿 Git Assistant](../../git-skills/git-assistant/README.md)
- [🏠 Master Suite README](../../README.md)
