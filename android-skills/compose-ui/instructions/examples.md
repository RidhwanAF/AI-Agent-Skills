# Compose UI Modular Code Examples

## Example: Multi-File Screen Decomposition

### File 1: `CartUiState.kt`
```kotlin
package com.example.presentation.cart

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class CartItemUiModel(
    val id: String,
    val title: String,
    val priceFormatted: String,
    val quantity: Int,
    val isPending: Boolean = false,
)

@Immutable
sealed interface CartUiState {
    data object Loading : CartUiState
    
    data class Content(
        val items: ImmutableList<CartItemUiModel> = persistentListOf(),
        val totalFormatted: String = "",
        val isCheckoutEnabled: Boolean = true,
    ) : CartUiState

    data class Error(val message: String) : CartUiState
}

sealed interface CartUiEvent {
    data class UpdateQuantity(val itemId: String, val quantity: Int) : CartUiEvent
    data class RemoveItem(val itemId: String) : CartUiEvent
    data object Checkout : CartUiEvent
}
```

---

### File 2: `components/CartItemCard.kt`
```kotlin
package com.example.presentation.cart.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.cart.CartItemUiModel

/**
 * Isolated stateless card component representing a single cart item.
 *
 * @param modifier Applied to the outer card. MUST BE AT THE VERY TOP OF PARAMETERS.
 * @param item Immutable item model.
 * @param onQuantityChange Fired when the user updates the item quantity.
 * @param onDeleteClick Fired when the user removes the item.
 * @param isHighlighted Whether an accent border is rendered.
 * @param elevation Card elevation.
 */
@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,                        // 1. VERY TOP OF PARAMETERS (Declaration)
    item: CartItemUiModel,                                // 2. Required data
    onQuantityChange: (itemId: String, qty: Int) -> Unit, // 3. Required callbacks
    onDeleteClick: (itemId: String) -> Unit,
    isHighlighted: Boolean = false,                       // 4. Optional configuration
    elevation: Dp = 1.dp,                                 // 5. Optional styling
) {
    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        modifier = modifier,                              // ✅ Call site: modifier at the BOTTOM
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier                           // ✅ Call site: modifier at the BOTTOM
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Column {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.priceFormatted, style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { onQuantityChange(item.id, item.quantity + 1) }) {
                    Text(text = "+")
                }
                IconButton(onClick = { onDeleteClick(item.id) }) {
                    Text(text = "✕")
                }
            }
        }
    }
}
```

---

### File 3: `CartContent.kt` (Declaration Top vs. Call-Site Bottom Rule)
```kotlin
package com.example.presentation.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.presentation.cart.components.CartItemCard

@Composable
fun CartContent(
    modifier: Modifier = Modifier,                        // ✅ Declaration: VERY TOP parameter
    state: CartUiState.Content,
    onEvent: (CartUiEvent) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxSize(),                // ✅ Call site: VERY BOTTOM argument
    ) {
        items(state.items, key = { it.id }) { item ->
            // ✅ Call site: Data and callbacks first, modifier at the VERY BOTTOM
            CartItemCard(
                item = item,
                onQuantityChange = { id, qty -> onEvent(CartUiEvent.UpdateQuantity(id, qty)) },
                onDeleteClick = { id -> onEvent(CartUiEvent.RemoveItem(id)) },
                isHighlighted = item.isPending,
                modifier = Modifier                       // ✅ Call site: VERY BOTTOM argument
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            )
        }
    }
}
```

---

### File 4: `CartScreen.kt` (Hilt ViewModel Non-Deprecated Import & Top Modifier)
```kotlin
package com.example.presentation.cart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel // ✅ MANDATORY NON-DEPRECATED IMPORT
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// ❌ NEVER import the deprecated package:
// import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CartScreen(
    modifier: Modifier = Modifier,                  // ✅ Declaration: VERY TOP (#1 parameter)
    viewModel: CartViewModel = hiltViewModel(),     // ✅ Injected via androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is CartUiState.Loading -> Unit
        is CartUiState.Error -> Unit
        is CartUiState.Content -> {
            CartContent(
                state = state,
                onEvent = viewModel::onEvent,
                modifier = modifier.fillMaxSize(),  // ✅ Call site: VERY BOTTOM argument
            )
        }
    }
}
```

---

### File 5: `ExpandableSubSectionBox.kt` (Anti-Misclick Architecture & Full-Card Ripple)
```kotlin
package com.example.presentation.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Collapsible section ensuring full card edge-to-edge clickability when collapsed,
 * while isolating child controls from accidental toggle events when expanded.
 */
@Composable
fun ExpandableSubSectionBox(
    modifier: Modifier = Modifier,
    title: String,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val cardShape = RoundedCornerShape(18.dp)
    val chevronRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 380f),
        label = "expandableSubSectionChevron",
    )

    Surface(
        shape = cardShape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
        ),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .clickable {
                        haptics.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        onToggleExpand()
                    }
                    .padding(horizontal = 14.dp, vertical = 14.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .graphicsLayer { rotationZ = chevronRotation },
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(180)) + expandVertically(spring(dampingRatio = 0.82f, stiffness = 380f)),
                exit = fadeOut(tween(140)) + shrinkVertically(spring(dampingRatio = 0.86f, stiffness = 400f)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                    content = content,
                )
            }
        }
    }
}
```

---

### File 6: `ModernStatefulTextField.kt` (Compose 1.13+ Migration)
```kotlin
package com.example.presentation.common.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.flow.drop

@Composable
fun ModernStatefulTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState = remember { TextFieldState() },
    onTextUserChange: () -> Unit,
    onSubmit: (String) -> Unit,
) {
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .drop(1)
            .collect { onTextUserChange() }
    }

    BasicTextField(
        state = state,
        lineLimits = TextFieldLineLimits.SingleLine,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
        onKeyboardAction = {
            val text = state.text.toString().trim()
            if (text.isNotEmpty()) {
                onSubmit(text)
                state.clearText()
            }
        },
        modifier = modifier,
    )
}
```

### 11. Haptic Feedback for Sliders & Continuous Dragging (`SegmentFrequentTick`)

```kotlin
package com.example.ui.components

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import kotlin.math.roundToInt

@Composable
fun TactileScrubSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    val haptics = LocalHapticFeedback.current
    val totalBuckets = 32
    fun bucketFor(v: Float): Int {
        val span = (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.0001f)
        val fraction = ((v - valueRange.start) / span).coerceIn(0f, 1f)
        return (fraction * totalBuckets).roundToInt()
    }

    var lastBucket by remember(valueRange) { mutableIntStateOf(bucketFor(value)) }

    Slider(
        value = value,
        onValueChange = { next ->
            val bucket = bucketFor(next)
            if (bucket != lastBucket) {
                lastBucket = bucket
                // Fluid high-frequency tick for continuous dragging / sliding
                haptics.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
            }
            onValueChange(next)
        },
        valueRange = valueRange,
        modifier = modifier,
    )
}
```
