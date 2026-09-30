---
name: agent-audit-workflow
description: Standard operating procedure for pre-edit codebase analysis, changelog documentation, and static verification.
---

# Agent Operational Workflow

1. **Analyze First:** Read, analyze, and map existing code dependencies before editing. Never assume—if requirements are ambiguous, halt and ask for clarification.
2. **Heavy Workload Offloading:** Ensure all suspend functions are main-safe. Offload heavy CPU work to `Dispatchers.Default` and make long-running loops cooperative via `ensureActive()` or `yield()`.
3. **Changelog Logging:** Record every added feature, refactor, or architectural change as a Markdown log in `/docs/changelogs/YYYY-MM-DD-feature-name.md`.
4. **Verification Step:** Run static checks and compilation (`./gradlew spotlessCheck`, `./gradlew detekt`, or `./gradlew compileDebugKotlin`) to confirm zero build errors or warnings before completing a task.