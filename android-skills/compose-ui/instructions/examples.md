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

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.presentation.cart.CartItemUiModel

/**
 * Isolated stateless card component representing a single cart item.
 *
 * @param item Immutable item model.
 * @param onQuantityChange Fired when the user updates the item quantity.
 * @param onDeleteClick Fired when the user removes the item.
 * @param modifier Applied to the outer card. TOP OF OPTIONAL PARAMETERS.
 * @param isHighlighted Whether an accent border is rendered.
 * @param elevation Card elevation.
 */
@Composable
fun CartItemCard(
    item: CartItemUiModel,                              // 1. Required data
    onQuantityChange: (itemId: String, qty: Int) -> Unit, // 2. Required callbacks
    onDeleteClick: (itemId: String) -> Unit,
    modifier: Modifier = Modifier,                      // 3. TOP OF OPTIONAL PARAMS
    isHighlighted: Boolean = false,                     // 4. Optional configuration
    elevation: Dp = 1.dp,                               // 5. Optional styling
) {
    ElevatedCard(
        modifier = modifier,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.priceFormatted, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onDeleteClick(item.id) }) {
                Text(text = "✕")
            }
        }
    }
}
```

---

### File 3: `CartContent.kt` (Call-Site Modifier Rule)
```kotlin
package com.example.presentation.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.presentation.cart.components.CartItemCard

@Composable
fun CartContent(
    state: CartUiState.Content,
    onEvent: (CartUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(state.items, key = { it.id }) { item ->
            // ✅ Call site: Data and callbacks first, modifier at the VERY BOTTOM
            CartItemCard(
                item = item,
                onQuantityChange = { id, qty -> onEvent(CartUiEvent.UpdateQuantity(id, qty)) },
                onDeleteClick = { id -> onEvent(CartUiEvent.RemoveItem(id)) },
                isHighlighted = item.isPending,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp) // BOTTOM OF ARGUMENTS
            )
        }
    }
}
```
