# 📐 App Design & UML Modeling Guidelines

Guidelines for generating architecture blueprints, UML models, and use case specifications.

---

## 🎯 Visual Modeling Protocol

1. **System Modeling Standards**:
   - Every system architecture document must specify:
     - High-level C4 container / component architecture.
     - UML Class diagram (`classDiagram`) illustrating layer dependencies and inversion.
     - UML Sequence diagram (`sequenceDiagram`) detailing asynchronous message flow.
     - State machine diagram (`stateDiagram-v2`) for screen and entity states.
     - Formal Use Case specification table.
2. **Mermaid Validation**:
   - Ensure all Mermaid diagrams are syntax-error free and use standard types (`classDiagram`, `sequenceDiagram`, `stateDiagram-v2`, `erDiagram`).
   - Quote special characters in labels.
3. **Storage Location**:
   - Save all blueprints to `<project-root>/docs/architecture/<feature-slug>-design.md`.
4. **Language & Clarity**:
   - All documentation, actor labels, and scenario descriptions must be in English.
