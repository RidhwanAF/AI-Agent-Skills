---
name: compose_ui
description: "Master modern Jetpack Compose UI engineering. Enforces modular component file separation (never 1 giant file), strict modifier parameter positioning (very first top parameter in @Composable fun declarations, very bottom at call sites), state stability (@Immutable/@Stable), edge-to-edge insets, purposeful haptics, and KDocs."
---

# 🎨 Jetpack Compose UI Engineering (/compose-ui)

The **Compose UI Skill** establishes production standards for declarative Android UI. It enforces modular component decomposition into dedicated files, strict modifier ordering (top in declarations, bottom at call sites), Compose compiler state stability, edge-to-edge window insets, purposeful tactile feedback, and zero hardcoded strings.

---

## 🧭 Connected Skills & Routing

```mermaid
flowchart LR
    CUI(["🎨 /compose-ui"])
    
    AA(["🏛️ /android-architecture"])
    AT(["🧪 /android-testing"])
    ED(["🔍 /error-detective"])
    CR(["📝 /changelog-recorder"])

    AA -->|StateFlow / Events| CUI
    CUI -->|Tested via ComposeTestRule| AT
    CUI -->|Recomposition / Jank Triage| ED
    CUI -->|UI Updates Logged in| CR
```

---

## 📂 1. Modular File Separation (Never in a Single File)

Never bundle a complete screen, its multiple child subcomponents, its state models, and its previews into a single monolithic 800-line file. Always decompose each screen component into cohesive, single-responsibility files:

```
feature/cart/presentation/
├── CartScreen.kt             # Stateful screen container (ViewModel binding, lifecycle observation)
├── CartContent.kt            # Stateless top-level content layout
├── CartUiState.kt            # Immutable UI state and UI event definitions
├── CartPreview.kt            # MultiPreview annotations (Dark, Light, Font Scale)
└── components/
    ├── CartItemCard.kt       # Individual item card component
    ├── CartItemThumbnail.kt  # Cached image with fallback
    └── CartSummaryBar.kt     # Sticky bottom action bar with checkout CTA
```

---

## 📐 2. The Modifier Parameter & Consumption Rule (Mandatory)

To maintain strict API consistency across the Android Jetpack Compose ecosystem, you MUST distinguish between **declaring** a `@Composable` function and **consuming (calling)** a `@Composable` function:

| Context | Where `modifier` Goes | Rule Summary |
|:---|:---|:---|
| **1. Declaring `@Composable fun`** | **VERY TOP (`#1 First Parameter`)** | Always place `modifier: Modifier = Modifier` at the **very first top parameter** of the function signature—**NEVER** at the bottom of the parameter list! |
| **2. Consuming / Calling a Composable** | **VERY BOTTOM (`Last Argument`)** | Always pass `modifier = Modifier...` at the **very bottom** of the call-site arguments (just before any trailing `{ ... }` content lambda). |

### Rule A: In the Composable Function Declaration (`modifier` at the VERY TOP)
When **declaring** any `@Composable fun` (whether a Screen container, Content layout, or custom child component), `modifier: Modifier = Modifier` MUST be placed at the **very top of the parameter list**—**NEVER** at the bottom of the function parameters!

```kotlin
// ✅ CORRECT DECLARATION: modifier is placed at the VERY TOP (#1 parameter)
/**
 * Renders an individual cart item with quantity adjustment controls.
 *
 * @param modifier Applied to the outer card container. MUST be at the very top of parameters.
 * @param item The immutable cart item entity.
 * @param onQuantityChange Fired when the user increments or decrements quantity.
 * @param onDeleteClick Fired when the user removes the item.
 * @param isHighlighted Whether to render an accent border indicating pending checkout.
 * @param elevation Elevation level of the card container.
 */
@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,                        // 1. VERY TOP PARAMETER (Mandatory!)
    item: CartItemUiModel,                                // 2. Required data parameters
    onQuantityChange: (itemId: String, qty: Int) -> Unit, // 3. Required callback parameters
    onDeleteClick: (itemId: String) -> Unit,
    isHighlighted: Boolean = false,                       // 4. Subsequent optional configs
    elevation: Dp = 2.dp,                                 // 5. Subsequent optional styling
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = modifier, // Consuming Card -> modifier at the bottom of arguments
    ) {
        // Implementation
    }
}

// ❌ WRONG DECLARATION: NEVER place modifier at the bottom of a @Composable fun signature!
@Composable
fun CartItemCardWrong(
    item: CartItemUiModel,
    onQuantityChange: (itemId: String, qty: Int) -> Unit,
    onDeleteClick: (itemId: String) -> Unit,
    modifier: Modifier = Modifier, // ❌ VIOLATION! Never put modifier at the bottom of a declaration!
)
```

### Rule B: At the Call Site / Consumption (`modifier` at the VERY BOTTOM)
When **calling / consuming** any Composable (both built-in Material/Foundation composables like `Row`, `Column`, `Card`, `LazyColumn`, `GoogleMap` and your own custom Composables), pass the `modifier = Modifier...` argument at the **very bottom of the argument list**. This ensures business data and callbacks remain prominent at the top while long layout modifier chains sit cleanly at the bottom:

```kotlin
// ✅ CORRECT CALL SITE: Content & callbacks at the top, modifier chain at the VERY BOTTOM
CartItemCard(
    item = cartItem,
    onQuantityChange = { id, qty -> viewModel.onEvent(CartEvent.UpdateQuantity(id, qty)) },
    onDeleteClick = { id -> viewModel.onEvent(CartEvent.RemoveItem(id)) },
    isHighlighted = cartItem.isPending,
    modifier = Modifier                                   // ✅ VERY BOTTOM of call-site arguments!
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clickable { viewModel.onEvent(CartEvent.Select(cartItem.id)) },
)

// ❌ WRONG CALL SITE: Do not pass modifier at the top when consuming a Composable with other args
CartItemCard(
    modifier = Modifier.fillMaxWidth(),                   // ❌ VIOLATION! Move to the bottom of call site!
    item = cartItem,
    onQuantityChange = { _, _ -> },
    onDeleteClick = {},
)
```

### Rule C: Hilt ViewModel in Compose — Mandatory Non-Deprecated Import
When injecting a Dagger Hilt `@HiltViewModel` inside a `@Composable` screen, AI models frequently hallucinate or use the **deprecated** `androidx.hilt.navigation.compose.hiltViewModel` import. You MUST strictly import `hiltViewModel` from `androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel` (introduced in `androidx.hilt:hilt-navigation-compose:1.3.0+` / `androidx.hilt:hilt-lifecycle-viewmodel-compose`):

```kotlin
// ✅ CORRECT IMPORT (Mandatory):
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

// ❌ DEPRECATED / FORBIDDEN IMPORT (NEVER USE THIS):
// import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,                  // 1. VERY TOP parameter in declaration!
    viewModel: CartViewModel = hiltViewModel(),     // 2. Uses androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CartContent(
        state = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,                        // ✅ VERY BOTTOM at call site!
    )
}
```

---

## ⚡ 3. Compose State Stability & Recomposition Discipline

1. **Annotate UI State Models**: Mark all UI state classes with `@Immutable` or `@Stable`.
2. **Immutable Collections**: Never use raw `List<T>` or `Map<K, V>` inside `@Immutable` state classes (standard lists are considered unstable by the Compose compiler). Always use `kotlinx.collections.immutable.ImmutableList` or wrap in a stable holder:
   ```kotlin
   @Immutable
   data class CartUiState(
       val items: ImmutableList<CartItemUiModel> = persistentListOf(),
       val subtotal: String = "",
       val isLoading: Boolean = false,
       val errorMessage: String? = null,
   )
   ```
3. **No Unstable Lambdas**: Pass method references or lambda instances that capture stable properties to prevent unnecessary recompositions.

---

## 📳 4. Tactile Haptic Feedback Standards

Obtain `val haptics = LocalHapticFeedback.current` in Composables. Fire tactile feedback strictly based on UI semantics:
- **`HapticFeedbackType.SegmentTick`**: Slider movements, segmented button toggles, tab switches.
- **`HapticFeedbackType.Confirm`**: Successful action completion (e.g. item added to cart, payment success).
- **`HapticFeedbackType.Reject`**: Validation errors, boundary bounces, blocked inputs.

---

## 🌐 5. Zero Hardcoded Strings & Multi-Language Localization

- Never embed literal text in `Text()` or content descriptions.
- Extract all user-visible strings to `res/values/strings.xml` with mandatory semantic prefixes:
  - `label_<name>` (e.g. `label_total_price`)
  - `action_<name>` (e.g. `action_checkout`)
  - `msg_<name>` (e.g. `msg_empty_cart`)
  - `title_<name>` (e.g. `title_cart`)
  - `desc_<name>` (e.g. `desc_remove_cart_item`)
- Resolve via `stringResource(R.string.key)`.
