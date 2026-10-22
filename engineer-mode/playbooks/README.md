# 📖 Engineer Mode Playbooks Registry

This directory contains the operational execution playbooks loaded dynamically by `/engineer-mode`. Each playbook guides the agent through an interactive, multi-step engineering workflow with concrete decision gates and connected skill routing.

---

## 🧭 Playbook Index

| # | File | Purpose | Primary Connected Skills |
| :---: | :--- | :--- | :--- |
| **01** | [`01-error-investigation.md`](./01-error-investigation.md) | Interactive triage, RCA, minimal fix, and post-mortem generation. | [`/error-detective`](../../diagnostic-skills/error-detective/README.md) |
| **02** | [`02-adr-decision-making.md`](./02-adr-decision-making.md) | Formal evaluation of architectural shifts grounded in official docs. | [`/adr-analyze`](../../diagnostic-skills/adr-analyze/README.md) |
| **03** | [`03-app-system-design.md`](./03-app-system-design.md) | Visual architecture blueprints, UML modeling, and use case specs. | [`/app-design`](../../diagnostic-skills/app-design/README.md) |
| **04** | [`04-android-deep-engineering.md`](./04-android-deep-engineering.md) | Clean Architecture, modular Compose UI, MapLibre, testing, Gradle. | [`/android-architecture`](../../android-skills/android-architecture/README.md), [`/compose-ui`](../../android-skills/compose-ui/README.md) |
| **05** | [`05-react-ts-web-engineering.md`](./05-react-ts-web-engineering.md) | React 19 / Next.js, zero-any TS, WCAG 2.2 AA, TanStack Query, Zustand. | [`/react-ts-architecture`](../../react-ts-skills/react-ts-architecture/README.md), [`/react-ui-components`](../../react-ts-skills/react-ui-components/README.md) |
| **06** | [`06-git-shipping-workflow.md`](./06-git-shipping-workflow.md) | Atomic commits, safe rebase, 3-way conflict resolution, PR briefings. | [`/git-assistant`](../../git-skills/git-assistant/README.md) |
| **07** | [`07-architecture-refactoring.md`](./07-architecture-refactoring.md) | Legacy code migration, layer extraction, dependency inversion. | [`/android-architecture`](../../android-skills/android-architecture/README.md), [`/react-ts-architecture`](../../react-ts-skills/react-ts-architecture/README.md) |
| **08** | [`08-changelog-documentation.md`](./08-changelog-documentation.md) | Timestamped auditable changelogs (`YYYY-MM-DD_HH-mm-ss_<title>.md`). | [`/changelog-recorder`](../../diagnostic-skills/changelog-recorder/README.md) |

---

## ⚡ Agent Loading Protocol
1. Match the user's intent to one of the 8 playbooks above.
2. Read the specific playbook markdown file.
3. Follow its step-by-step checklist sequentially without skipping verification gates.
