# 📜 ADR Analysis Guidelines

Guidelines for detecting architectural shifts, consulting official documentation, performing quantitative multi-criteria evaluations, and authoring MADR records.

---

## 🎯 Architectural Assessment Principles

1. **Executive Summary First (TL;DR Clarity)**:
   - Every ADR must start with a high-level `## 📋 Executive Summary` directly below the metadata header.
   - Summarize the business/technical problem, the chosen architecture, and the primary rationale in 2-4 concise sentences so stakeholders understand the decision immediately without reading the entire document.

2. **Visual & Quantitative Scoring Before Deep Explanations**:
   - Immediately beneath the Executive Summary, provide the `## 📊 Architectural Evaluation & Scoring` section containing:
     - **Mermaid Pie Chart**: Visual breakdown of the winning option's fit across key architectural dimensions (e.g. Official Standards, Architectural Integrity, Reactive Flow, Dev Velocity, Maintenance).
     - **Detailed Multi-Criteria Scoring Table**: Quantitative score comparison across all candidate options (e.g. out of 10) with explicit weights totaling 100%, calculated overall score, and rationale per criterion.
   - This visual scorecard must appear **before** the deep technical deep-dive sections.

3. **Grounding in Official Standards (Zero Speculation)**:
   - Every architectural recommendation must cite official vendor documentation (Google Android Docs, Jetpack Compose, React.dev, TypeScript Handbook, MDN, W3C).
   - Never recommend deprecated, unmaintained, or speculative patterns. Include verbatim quotes or exact links in the Citations section.

4. **Standardized MADR Structure**:
   - Save files in `<project-root>/docs/adr/ADR-<NUMBER>-<slug>.md`.
   - Order of sections: Metadata Header ➔ Executive Summary ➔ Mermaid Pie Chart & Scoring Matrix ➔ In-Depth Context & Problem Statement ➔ Decision Drivers ➔ Considered Options ➔ Decision Outcome & Citations ➔ Pros and Cons ➔ Architectural Blueprint Diagram ➔ Consequences.

5. **Registry Maintenance**:
   - Keep `<project-root>/docs/adr/README.md` updated with the tabular list of all ADRs, status badges, dates, and summaries.

6. **Immutability of Accepted Decisions**:
   - Once an ADR is marked `Accepted`, do not rewrite history. If circumstances change, create a new ADR and mark the old one as `Superseded by ADR-XXXX`.
