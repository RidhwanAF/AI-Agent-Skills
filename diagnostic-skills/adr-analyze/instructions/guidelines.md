# 📜 ADR Analysis Guidelines

Guidelines for detecting architectural shifts, consulting official documentation, and authoring MADR records.

---

## 🎯 Architectural Assessment Principles

1. **Grounding in Official Standards**:
   - Every architectural recommendation must cite official documentation (Google Android, Jetpack, React.dev, TypeScript handbook, MDN).
   - Never recommend deprecated, unmaintained, or speculative patterns.
2. **Standardized MADR Structure**:
   - Save files in `<project-root>/docs/adr/ADR-<NUMBER>-<slug>.md`.
   - Include Status, Context, Decision Drivers, Considered Options, Pros & Cons, Decision Outcome, Official Citations, Mermaid Diagram, and Consequences.
3. **Registry Maintenance**:
   - Keep `<project-root>/docs/adr/README.md` updated with the tabular list of all ADRs.
4. **Immutability of Accepted Decisions**:
   - Once an ADR is marked `Accepted`, do not rewrite history. If circumstances change, create a new ADR and mark the old one as `Superseded by ADR-XXXX`.
