# 🧪 React & TypeScript Testing Guidelines

Instructions for writing unit, integration, and component tests using Vitest, React Testing Library, and MSW.

---

## 🎯 Testing Protocols

1. **User-Centric Assertions**:
   - Query by accessible role first (`getByRole`), label second (`getByLabelText`), text third (`getByText`).
   - Forbid arbitrary `getByTestId` unless no accessible landmark or role exists.
2. **User Interactions**:
   - Always use `@testing-library/user-event` instead of `fireEvent`.
   - Setup `const user = userEvent.setup()` before rendering.
3. **Network Isolation**:
   - Forbid mocking `fetch` or `axios` prototypes directly.
   - Use MSW (Mock Service Worker) handlers to intercept network traffic.
4. **Deterministic Behavior**:
   - Clean up state and mocks after each test (`beforeEach`, `afterEach`).
   - Use `vi.useFakeTimers()` for debounce, throttle, or delayed rendering tests.
