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
