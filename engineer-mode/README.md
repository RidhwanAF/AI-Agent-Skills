# 🚀 Engineer Mode (/engineer-mode)

Welcome to **Engineer Mode**, the central command post and intelligent orchestrator of the **AI-Engineer-Skills** suite. Inspired by the rigor and multi-agent parallelism of *pstack (potato-mode)*, Engineer Mode transforms an AI coding assistant into a structured, proactive senior software engineering team.

---

## 🧭 Navigation & Connected Skills Hub

| Hub Directory | Domain | Direct Link |
|---|---|---|
| 🏠 **Root Hub** | Full Suite Overview & Universal Installer | [Root README](../README.md) |
| 📱 **Android Skills** | Clean Arch, Compose, MapLibre, Testing, Gradle | [Android README](../android-skills/README.md) |
| ⚛️ **React TS Skills** | Next.js, Vite, TypeScript, Tailwind, Web Vitals | [React TS README](../react-ts-skills/README.md) |
| 🔍 **Diagnostic Skills** | Error Detective, ADR Detection, App Design, Changelogs | [Diagnostic README](../diagnostic-skills/README.md) |
| 🌿 **Git Skills** | Branching, Safe Rebase, Conflict Resolution, PRs | [Git README](../git-skills/README.md) |

---

## 📂 Subfolder Structure

| Path | Purpose | Description |
| :--- | :--- | :--- |
| [`SKILL.md`](./SKILL.md) | Agent Skill Definition | Master prompt instructions, questionnaire, and routing table. |
| [`playbooks/README.md`](./playbooks/README.md) | Playbook Catalog Registry | Complete index and overview of the 8 execution playbooks. |
| [`instructions/guidelines.md`](./instructions/guidelines.md) | Agent Execution Protocol | Master orchestration protocols and gating rules. |
| [`instructions/examples.md`](./instructions/examples.md) | Production Workflow Examples | Interactive triage dialogue and multi-skill routing chains. |

---

## 💡 How It Works

Instead of guessing or generating boilerplate code, `/engineer-mode` performs an **adaptive assessment**. It matches your goal against curated engineering playbooks, interviews you on critical constraints, and routes work dynamically across specialized modular skills:

```mermaid
flowchart TD
    UserPrompt(["💬 User invokes /engineer-mode"]) --> Assess{"Intent clearly specified<br/>in prompt?"}
    
    Assess -->|No| InteractiveMenu["Show Interactive Playbook Menu<br/>(Options 1 - 8)"]
    InteractiveMenu --> UserChoice["User picks playbook or describes goal"]
    UserChoice --> RoutePlaybook
    
    Assess -->|Yes| RoutePlaybook["Match to Playbook"]
    
    subgraph PlaybookRouter ["🎯 Playbook Execution & Connected Skills Routing"]
        RoutePlaybook --> PB1["Playbook 01: Error Investigation"]
        RoutePlaybook --> PB2["Playbook 02: ADR Decisions"]
        RoutePlaybook --> PB3["Playbook 03: App System Design"]
        RoutePlaybook --> PB4["Playbook 04: Android Engineering"]
        RoutePlaybook --> PB5["Playbook 05: React TS Engineering"]
        RoutePlaybook --> PB6["Playbook 06: Git Shipping"]
        RoutePlaybook --> PB7["Playbook 07: Architecture Refactoring"]
        RoutePlaybook --> PB8["Playbook 08: Changelog Tracking"]

        PB1 --> SK_ED["🔍 /error-detective"]
        PB2 --> SK_ADR["📜 /adr-analyze"]
        PB3 --> SK_AD["📐 /app-design"]
        PB4 --> SK_AND["📱 Android Suite (/compose-ui, /android-architecture)"]
        PB5 --> SK_REACT["⚛️ React TS Suite (/react-ui-components, /react-ts-architecture)"]
        PB6 --> SK_GA["🌿 /git-assistant"]
        PB7 --> SK_MESH["🔄 Composite Multi-Skill Mesh"]
        PB8 --> SK_CR["📝 /changelog-recorder"]
    end

    SK_AND -.->|Device Ops| GoogleCLI[("🛠️ Google android-cli")]
    SK_ED -.->|Report| ErrorDocs["/docs/error-fix/*.md"]
    SK_ADR -.->|Record| ADRDocs["/docs/adr/*.md"]
    SK_AD -.->|Architecture| ArchDocs["/docs/architecture/*.md"]
    SK_CR -.->|Changelog| ChangeDocs["/docs/changelogs/*.md"]
```

---

## ⚡ How to Use

### 1. Interactive Entry (Default)
Simply type:
```text
/engineer-mode
```
The agent will immediately present the interactive menu, asking you to choose from the playbook catalog or describe your project goal.

### 2. Direct Task Trigger
```text
/engineer-mode diagnose and fix the NullPointerException in OrderRepository
```
```text
/engineer-mode design a modular offline-first sync engine for Android with KMP domain
```
```text
/engineer-mode build an accessible checkout screen in React 19 with TanStack Query
```

---

## 📋 Playbook Catalog

| # | Playbook | Key Responsibilities | Output Location |
|---|---|---|---|
| **01** | [Error Investigation](playbooks/01-error-investigation.md) | Asks error type & location, root-causes bug, generates fix, and logs scoring | `/docs/error-fix/` |
| **02** | [ADR Decision Making](playbooks/02-adr-decision-making.md) | Formalizes architectural decisions grounded in official documentation | `/docs/adr/` |
| **03** | [App System Design](playbooks/03-app-system-design.md) | Generates UML class, sequence, and state diagrams with use case specs in English | `/docs/architecture/` |
| **04** | [Android Engineering](playbooks/04-android-deep-engineering.md) | Modular Clean Architecture, Compose UI decomposition, testing, Gradle | `app/src/` |
| **05** | [React TS Web Engineering](playbooks/05-react-ts-web-engineering.md) | Frontend Clean Architecture, accessible modular UI, TanStack Query, Vitest | `src/` |
| **06** | [Git Shipping Workflow](playbooks/06-git-shipping-workflow.md) | Atomic staging, conventional commits, rebase/merge safety, PR briefs | Git / PR |
| **07** | [Architecture Refactoring](playbooks/07-architecture-refactoring.md) | Eliminates code debt, cleans domain boundaries, verifies zero regressions | Codebase |
| **08** | [Changelog Documentation](playbooks/08-changelog-documentation.md) | Records chronological changes with strict timestamp naming conventions | `/docs/changelogs/` |
