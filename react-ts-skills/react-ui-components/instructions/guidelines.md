# 🎨 Modular React UI Components Guidelines

These instructions guide the agent when creating, modifying, or refactoring React UI components.

---

## 🎯 Component Architecture Rules

1. **Modular File Decomposition**:
   - **Never write monolithic components**: Every feature must separate root pages, state containers, UI child components, custom hooks, and types into distinct files.
   - Max file size target: < 150 lines per component.
2. **Prop Parameter Ordering Protocol**:
   - In interface declaration:
     1. Mandatory data models (`item`, `user`, `total`).
     2. Mandatory callback handlers (`onSelect`, `onSubmit`).
     3. Optional configuration flags (`disabled`, `variant`).
     4. Optional styling and slot props (`className`, `children`) at the bottom.
   - At consumption call site:
     - Always place layout/styling classes (`className="..."`) at the very bottom of the JSX tag.
3. **Accessibility First (WCAG 2.2 AA)**:
   - Use semantic elements (`<button>`, `<header>`, `<main>`, `<dialog>`).
   - Interactive elements must be keyboard accessible with clear `focus-visible` styling.
   - Dynamic alerts must use `role="status"` or `aria-live="polite"`.
4. **Tailwind CSS Hygiene**:
   - Use standard design tokens and utilities.
   - Wrap dynamic classes with `clsx` or `tailwind-merge` (`cn(...)`) utility for robust style overrides.
