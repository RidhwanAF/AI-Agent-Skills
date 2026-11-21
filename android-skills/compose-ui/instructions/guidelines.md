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
4. **Modifier Chaining Order (`clip` ➔ `background`/`border` ➔ `clickable` ➔ `padding`)**:
   - **DO**: Place `clip(shape)` and `clickable { ... }` **before** inner `padding(...)` on clickable `Column`/`Row`/`Box` containers so the touch target and ripple span the full container.
   - **DON'T**: Place `padding(...)` before `clickable { ... }` when full-container ripple is intended.
5. **Zero Deprecated Compose APIs**:
   - Use `androidx.lifecycle.compose.LocalLifecycleOwner`, `collectAsStateWithLifecycle()`, `HorizontalDivider()`, `PullToRefreshBox`, `Icons.AutoMirrored.Filled.*`, and `enableEdgeToEdge()`. Never use Accompanist wrappers or `androidx.compose.ui.platform.LocalLifecycleOwner`.

## 3. Expandable Containers & Anti-Misclick Architecture
1. **Full-Card Clickable When Collapsed**:
   - The clickable region and ripple MUST span the entire card edge-to-edge (`Modifier.clip(cardShape).clickable { onToggle() }`).
2. **Isolated Body When Expanded**:
   - The expanded content MUST reside outside the toggle click target so child buttons, text fields, and switches never accidentally collapse the card.
3. **Animated Chevron / Arrow Rotation**:
   - Always animate chevron and dropdown arrow rotations (`0° <-> 180°`) with `animateFloatAsState` and `.graphicsLayer { rotationZ = rotation }`.

## 4. Responsive FlowRow & Sub-Pixel Rounding Guard
1. **Never use naive `maxWidth / 2`**:
   - Compute columns dynamically: `((maxWidth + horizontalSpacing) / (minItemWidth + horizontalSpacing)).toInt().coerceIn(1, maxColumns)`.
   - Subtract `1.dp` sub-pixel rounding guard to prevent premature wrapping: `((maxWidth - totalGap - 1.dp) / columns.toFloat()).coerceAtLeast(1.dp)`.

## 5. Scroll Viewports & Adaptive Top Shadows
1. **Clearance**: Provide explicit `12.dp` - `16.dp` spacing between sticky headers/mockups and scroll viewports.
2. **Rounded Clip**: Clip the scroll container with `RoundedCornerShape(topStart = ..., topEnd = ...)`.
3. **Adaptive Shadow**: Use `derivedStateOf { scrollState.value > 0 }` to fade in a subtle top gradient shadow only during active scroll.

## 6. Modern Compose 1.13+ Text Field State Migration
- **FORBIDDEN**: `BasicTextField(value: String, onValueChange: (String) -> Unit)` (Deprecated in Compose 1.13.0+).
- **MANDATORY**: `BasicTextField(state = remember { TextFieldState() }, lineLimits = TextFieldLineLimits.SingleLine, ...)` with `state.clearText()` and `snapshotFlow { state.text.toString() }`.

## 7. State Stability & Recomposition Guardrails
1. **Annotate UI States**:
   - Annotate state classes with `@Immutable` or `@Stable`.
2. **Immutable Collections**:
   - Never use standard `List<T>` or `Map<K, V>` in state models. Use `kotlinx.collections.immutable.ImmutableList`.
3. **Lambda Stability**:
   - Use stable lambdas and method references to avoid breaking Strong Skipping mode.

## 4. Tactile Haptic Feedback
- Use `LocalHapticFeedback.current` with purposeful semantics:
  - `SegmentFrequentTick`: Sliders, continuous dragging, scrubbing, seek bar tracking.
  - `SegmentTick`: Discrete segmented buttons, tab switches, expandable card toggles.
  - `Confirm`: Success, checkout, submission, gesture dismiss commit.
  - `Reject`: Errors, invalid input, boundary rejection.

## 5. Zero Hardcoded Strings
- Extract all text into `res/values/strings.xml`.
- Use descriptive semantic prefixes: `label_`, `msg_`, `title_`, `action_`, `error_`, `hint_`, `desc_`.
