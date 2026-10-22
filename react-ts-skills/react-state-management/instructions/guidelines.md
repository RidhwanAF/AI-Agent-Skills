# ⚡ React State Management Guidelines

Guidelines for managing server and client state with TanStack Query and Zustand.

---

## 🎯 State Partitioning Rules

1. **Clear Division of State**:
   - **Server State (TanStack Query)**: Any data fetched from an API, cached, shared across sessions, or mutated asynchronously.
   - **Client State (Zustand)**: Ephemeral UI state (modals, active tabs, sidebars, wizard steps).
   - **Local State (`useState` / `useReducer`)**: Component-isolated state (text input draft, hover dropdown).
2. **TanStack Query Discipline**:
   - Centralize query keys in typed query key factories.
   - Set meaningful `staleTime` (e.g. 1-5 minutes) to avoid request stampedes.
   - Implement optimistic updates with explicit rollback (`onError`) when mutating frequently interacted state.
3. **Zustand Discipline**:
   - Create focused, atomic stores.
   - Never duplicate server-fetched data into Zustand.
   - Always use atomic selector hooks (`useStore(state => state.property)`) to prevent unnecessary component re-renders.
