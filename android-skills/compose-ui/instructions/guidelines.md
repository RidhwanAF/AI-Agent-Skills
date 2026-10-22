# Compose UI Architecture Guidelines

## 1. Multi-File Decomposition (Never 1 Giant File)
1. **Zero Monolithic Files**:
   - An entire screen component MUST NOT exceed 150-200 lines.
   - Decompose into distinct files:
     - `XScreen.kt`: Stateful container observing ViewModel.
     - `XContent.kt`: Stateless layout organizing UI elements.
     - `XUiState.kt`: Immutable UI state and event definitions.
     - `XPreview.kt`: MultiPreview annotations.
     - `components/XCard.kt`: Atomic presentation cards.
2. **Component Isolation**:
   - Every reusable child component resides in `components/`.
   - Child components must remain stateless, accepting immutable data and callback lambdas.

## 2. The Modifier Parameter & Consumption Rule
1. **In Declaration**:
   - `modifier: Modifier = Modifier` must be the FIRST optional parameter, placed immediately after required data and callbacks.
2. **At Call Site**:
   - Pass content and callback arguments first.
   - Place the `modifier` parameter at the VERY BOTTOM of the argument list.

## 3. State Stability & Recomposition Guardrails
1. **Annotate UI States**:
   - Annotate state classes with `@Immutable` or `@Stable`.
2. **Immutable Collections**:
   - Never use standard `List<T>` or `Map<K, V>` in state models. Use `kotlinx.collections.immutable.ImmutableList`.
3. **Lambda Stability**:
   - Use stable lambdas and method references to avoid breaking Strong Skipping mode.

## 4. Tactile Haptic Feedback
- Use `LocalHapticFeedback.current` with purposeful semantics:
  - `SegmentTick`: Sliders, tabs, toggles.
  - `Confirm`: Success, checkout, submission.
  - `Reject`: Errors, invalid input, boundary rejection.

## 5. Zero Hardcoded Strings
- Extract all text into `res/values/strings.xml`.
- Use descriptive semantic prefixes: `label_`, `msg_`, `title_`, `action_`, `error_`, `hint_`, `desc_`.
