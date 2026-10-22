# 🚀 Engineer Mode Execution Guidelines

Operational instructions for running `/engineer-mode` as the primary adaptive entry point.

---

## 🎯 Master Orchestration Protocol

1. **Initial Interactive Triage**:
   - Present the 8-option interactive menu to the user.
   - Respect user selection and load the matching playbook from `playbooks/`.
2. **Context-Aware Dynamic Routing**:
   - Instead of trying to perform everything within a single massive prompt, route sub-tasks to the dedicated specialized skills (`/android-architecture`, `/compose-ui`, `/compose-maplibre`, `/error-detective`, `/adr-analyze`, etc.).
3. **Execution Gating**:
   - Gate each phase behind automated verification (compile checks, unit tests, lints).
   - Only advance when the current phase passes without errors or warnings.
4. **Permanent Artifact Creation**:
   - Every architectural change produces an ADR in `/docs/adr/`.
   - Every incident fix produces a post-mortem in `/docs/error-fix/`.
   - Every sprint or major refactor produces a timestamped changelog in `/docs/changelogs/`.
