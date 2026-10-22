# Android Automated Testing Guidelines

## 1. Unit Testing Guidelines (Coroutines & Flows)
1. **Turbine for StateFlows**:
   - Always verify cold and hot flows using `app.cash.turbine`.
   - Never extract flow values into mutable list accumulators manually.
2. **Deterministic Dispatchers**:
   - Use `StandardTestDispatcher` and `runTest`.
   - Call `Dispatchers.setMain(testDispatcher)` in `@Before` and `Dispatchers.resetMain()` in `@After`.
   - Use `advanceUntilIdle()` to progress virtual time; NEVER use `Thread.sleep()`.
3. **Fake Repositories over Excessive Mocks**:
   - For pure Domain UseCases, prefer in-memory test fakes (`FakeUserRepository`) over brittle MockK chains.

## 2. Compose UI Testing Guidelines
1. **Semantic Matching**:
   - Query nodes using accessibility semantics: `onNodeWithText`, `onNodeWithContentDescription`.
   - Avoid matching by internal test tags unless no accessible label exists.
2. **State Injection**:
   - Test stateless `XContent` composables directly by passing controlled test state and verifying callback invocations.
