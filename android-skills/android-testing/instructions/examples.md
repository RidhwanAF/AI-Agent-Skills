# Android Testing Code Examples

## Example 1: ViewModel Unit Test with Turbine

```kotlin
package com.example.presentation.cart

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeCartRepository = FakeCartRepository()
    private lateinit var viewModel: CartViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CartViewModel(
            getCartItemsUseCase = GetCartItemsUseCase(fakeCartRepository),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `given cart items, when initialized, then state transitions from Loading to Content`() = runTest {
        viewModel.uiState.test {
            // Initial state
            assertEquals(CartUiState.Loading, awaitItem())

            // Seed fake data
            fakeCartRepository.emitItems(listOf(CartItem(id = "1", title = "Desk Lamp", price = 45.0)))
            advanceUntilIdle()

            val contentState = awaitItem() as CartUiState.Content
            assertEquals(1, contentState.items.size)
            assertEquals("Desk Lamp", contentState.items.first().title)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
```

---

## Example 2: In-Memory Fake Repository

```kotlin
package com.example.presentation.cart

import com.example.domain.model.CartItem
import com.example.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCartRepository : CartRepository {
    private val itemsFlow = MutableStateFlow<List<CartItem>>(emptyList())

    override fun observeCartItems(): Flow<List<CartItem>> = itemsFlow.asStateFlow()

    fun emitItems(items: List<CartItem>) {
        itemsFlow.value = items
    }
}
```
