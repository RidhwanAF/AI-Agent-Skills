# 🏛️ React & TypeScript Clean Architecture Guidelines

These instructions guide the agent when establishing or refactoring React + TypeScript architecture.

---

## 🎯 Core Architectural Principles

1. **Separation of Concerns**:
   - **Presentation Layer**: React components, hooks, layout, styling.
   - **Domain Layer**: Pure TypeScript types, branded IDs, business validation, pure functions. Zero `react`, DOM, or framework imports.
   - **Data Layer**: API clients, DTOs, Zod schemas, DTO-to-Domain mappers, TanStack Query integration.
2. **Zero `any` Policy**:
   - Explicitly forbid `any`. Use `unknown` with runtime type guards, Zod validation, or discriminated unions.
   - Use `satisfies` operator to enforce interface compliance while preserving narrowed literal types.
3. **Data Immutability**:
   - Mark domain properties and array contents with `readonly` (e.g. `readonly items: readonly T[]`).
4. **Exhaustive Matching**:
   - Always enforce exhaustive switch checks using `assertNever(x: never): never` for discriminated unions.
5. **No Framework Leaks**:
   - Domain logic must remain portable across React, Node.js, CLI, or Next.js server components without modification.
