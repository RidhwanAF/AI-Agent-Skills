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

## 2. The Modifier Parameter & Consumption Rule (Strict Dual-Rule)
1. **In Composable Function Declaration (`@Composable fun ...`) — VERY TOP**:
   - `modifier: Modifier = Modifier` MUST be placed at the **very first top parameter (`#1`)** of the `@Composable fun` parameter list.
   - **NEVER** place `modifier: Modifier = Modifier` at the bottom of a Composable function declaration! Even for simple two-parameter or three-parameter Composables (e.g., `Screen`, `Content`, `Surface`), `modifier: Modifier = Modifier` belongs at the very top of the declaration signature.
2. **At Composable Call Site (Consumption) — VERY BOTTOM**:
   - When calling/consuming any Composable (custom or built-in like `Row`, `LazyColumn`, `ElevatedCard`, `GoogleMap`), pass all content data and callback arguments first.
   - Place the `modifier = Modifier...` argument at the **VERY BOTTOM** of the call-site argument list (immediately preceding any trailing `{ ... }` content lambda).
3. **Hilt ViewModel Compose Import (Strict Non-Deprecated Package)**:
   - **ALWAYS** import `hiltViewModel` from:
     `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`
   - **NEVER** use the deprecated import `import androidx.hilt.navigation.compose.hiltViewModel`.
   - In screen declarations, always keep `modifier: Modifier = Modifier` at the **very top (`#1`)** before `viewModel: MyViewModel = hiltViewModel()`.

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
