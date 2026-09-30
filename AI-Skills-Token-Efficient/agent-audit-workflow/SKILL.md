---
name: agent-audit-workflow
description: "Token-efficient agent workflow: Analyze first, main-safe execution, and compilation verification."
---

# Agent Audit Workflow (Token-Efficient)

1. **Analyze First:** Inspect code context before editing. If requirements are ambiguous, clarify concisely.
2. **Main-Safe Suspend:** Keep suspend functions main-safe. Offload CPU to `Dispatchers.Default` and check `ensureActive()` in loops.
3. **Verification:** Validate code edits using Gradle static checks (`./gradlew compileDebugKotlin` or `./gradlew spotlessCheck`).
4. **Changelog:** Log architectural changes to `/docs/changelogs/YYYY-MM-DD-<topic>.md`.
