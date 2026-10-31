# 🚀 Engineer Mode Execution Guidelines

Operational instructions for running `/engineer-mode` as the primary adaptive entry point.

---

## 🎯 Master Orchestration Protocol

1. **Initial Interactive Triage**:
   - Present the 8-option interactive menu to the user.
   - Respect user selection and load the matching playbook from `playbooks/`.
2. **Context-Aware Dynamic Routing**:
   - Instead of trying to perform everything within a single massive prompt, route sub-tasks to the dedicated specialized skills (`/android-architecture`, `/compose-ui`, `/compose-maplibre`, `/error-detective`, `/adr-analyze`, etc.).
3. **Execution Gating & Mandatory Auto-Lint Fix**:
   - Gate each phase behind automated verification (compile checks, unit tests, lints).
   - **For Android Projects**: Always run `./gradlew lintDebug` (or `./gradlew :<module>:lintDebug` and `./gradlew compileDebugKotlin`) and automatically fix all lint warnings and compiler diagnostics before completing the turn—specifically enforcing KTX extensions (`toUri()` over `Uri.parse()`), top-level imports (`import kotlin.math.abs` + `abs()` over `kotlin.math.abs()`), functional loops (`forEach` over `for`), and zero unused imports, unused parameters, or redundant `@OptIn(Experimental...)` annotations.
   - Only advance when the current phase passes without errors or warnings.
4. **Anti-Slop 3-Strike Escalation & Self-Evolution Protocol**:
   - If the user or AI repeats a request/correction **2–3 times** due to AI slop (e.g., repeating a lint mistake, wrong import, wrong modifier placement, or incomplete logic fix):
     1. Immediately halt shallow single-file patching and perform a **workspace-wide audit** to eradicate every instance of the mistake across the entire project.
     2. Permanently upgrade the relevant skill rule (`SKILL.md` / `guidelines.md` in `~/.agents/skills/` and `~/.gemini/config/skills/`) with explicit `✅ CORRECT` vs `❌ FORBIDDEN` guardrails so the mistake never recurs.
5. **Mandatory Post-Execution Chat Summary with Flat README-Style Mermaid Diagram**:
   - At the end of every `/engineer-mode` execution, output a structured summary in the chat containing a top-down **Mermaid Flowchart (`flowchart TD`)** matching the Root README style:
     - Use a standalone root node `EM(["🚀 /engineer-mode<br/>(Master Orchestrator)"])`.
     - Use **flat, single-level `subgraph` containers** (never nest a `subgraph` inside another `subgraph` so the container boxes stay clean and slightly dark):
       - `subgraph PlaybooksUsed ["📚 Playbooks Executed"]`
       - `subgraph AndroidSuite ["📱 Modular Android Suite"]`
       - `subgraph ReactSuite ["⚛️ Modular React TS Suite"]`
       - `subgraph ArchitectureLayer ["📐 Architecture, Diagnostics & Docs"]`
       - `subgraph QualityAndShipping ["🔍 Verification, Auto-Lint & Shipping"]`
     - Use stadium-shaped nodes `(["..."])` inside each container and connect them with labeled directional arrows (`-->`).
6. **Permanent Artifact Creation**:
   - Every architectural change produces an ADR in `/docs/adr/`.
   - Every incident fix produces a post-mortem in `/docs/error-fix/`.
   - Every sprint or major refactor produces a timestamped changelog in `/docs/changelogs/`.
7. **Zero Auto-Push & Merge Confirmation Policy**:
   - The agent must **NEVER** push commits or merge branches automatically.
   - Always present the commit summary, branch, and destination, and explicitly ask the user whether they want to push to remote, push and merge/open a PR, or keep changes local only.
   - Only execute `git push` or branch merging upon receiving the user's explicit affirmative response.
