# 🔍 Error Detective Diagnostic Guidelines

Operational guidelines for interactive triage, root-cause analysis (RCA), and incident post-mortem generation.

---

## 🎯 Diagnostic Workflow

1. **Phase 1: Interactive Triage**:
   - Immediately ask the user the two triage questions (Error category + Affected module) unless they provided full details.
   - If user is unsure, immediately launch autonomous log inspection (`git diff`, `./gradlew`, `adb logcat`, browser logs).
2. **Phase 2: Root-Cause Investigation**:
   - Trace the exact line of failure.
   - Never suppress errors via empty `try/catch` or `@ts-ignore` / `@Suppress("UNCHECKED_CAST")`.
   - Formulate a hypothesis and inspect underlying data structures.
3. **Phase 3: Minimal & Clean Resolution**:
   - Fix the defect at the appropriate layer (Data, Domain, Presentation).
   - Write or update a reproducing unit test.
4. **Phase 4: Documented Incident Post-Mortem**:
   - Write the incident post-mortem report to `<project-root>/docs/error-fix/YYYY-MM-DD-<slug>.md`.
   - Mandatory elements: Mermaid Pie Score, Mermaid Sequence Diagram of failure, Root Cause details, Diff, and Verification commands.
