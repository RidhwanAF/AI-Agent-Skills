package com.example.cryptoscalper.feature.watchlist.efficient

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil3.compose.AsyncImage
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Crypto(val id: String, val name: String, val symbol: String, val price: Double, val iconUrl: String)

@Immutable
data class CryptoUi(val id: String, val name: String, val symbol: String, val price: String, val iconUrl: String, val isFav: Boolean)

class EfficientWatchlistViewModel(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val favKey = stringPreferencesKey("fav_id")
    private val retryDelay = 3.seconds

    private val cryptos = MutableStateFlow(
        listOf(
            Crypto("btc", "Bitcoin", "BTC", 64500.0, "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
            Crypto("eth", "Ethereum", "ETH", 3450.0, "https://assets.coingecko.com/coins/images/279/large/ethereum.png"),
            Crypto("sol", "Solana", "SOL", 145.0, "https://assets.coingecko.com/coins/images/4128/large/solana.png")
        )
    )

    val uiState: StateFlow<ImmutableList<CryptoUi>> = combine(
        cryptos,
        dataStore.data.map { it[favKey] }
    ) { list, favId ->
        list.map { c ->
            CryptoUi(c.id, c.name, c.symbol, "$${String.format("%.2f", c.price)}", c.iconUrl, c.id == favId)
        }.toImmutableList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), emptyList<CryptoUi>().toImmutableList())

    fun toggleFav(id: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    dataStore.edit { it[favKey] = if (it[favKey] == id) "" else id }
                }
            } catch (_: Exception) {
                delay(retryDelay)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EfficientWatchlistScreen(viewModel: EfficientWatchlistViewModel, modifier: Modifier = Modifier) {
    val items by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }

    SharedTransitionLayout(modifier = modifier) {
        val selected = items.firstOrNull { it.id == selectedId }
        if (selected != null) {
            PredictiveBackHandler { progress ->
                try {
                    progress.collect { }
                    selectedId = null
                } catch (_: CancellationException) { }
            }
            Scaffold { padding ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .sharedBounds(rememberSharedContentState(key = "card_${selected.id}"), this@SharedTransitionLayout)
                ) {
                    Column(Modifier.padding(24.dp)) {
                        AsyncImage(
                            model = selected.iconUrl,
                            contentDescription = selected.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .sharedElement(rememberSharedContentState(key = "image_${selected.id}"), this@SharedTransitionLayout)
                        )
                        Text(
                            text = selected.name,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(top = 16.dp).sharedElement(rememberSharedContentState(key = "title_${selected.id}"), this@SharedTransitionLayout)
                        )
                        Text(selected.symbol, style = MaterialTheme.typography.bodyLarge)
                        Text(selected.price, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 12.dp))
                        IconButton(onClick = { viewModel.toggleFav(selected.id) }) {
                            Icon(if (selected.isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, "Fav")
                        }
                    }
                }
            }
        } else {
            Scaffold { padding ->
                val dir = LocalLayoutDirection.current
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = padding.calculateStartPadding(dir) + 16.dp,
                        end = padding.calculateEndPadding(dir) + 16.dp,
                        top = padding.calculateTopPadding() + 16.dp,
                        bottom = padding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { coin ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .sharedBoundsWithCallerManagedVisibility(
                                    rememberSharedContentState(key = "card_${coin.id}"),
                                    visible = selectedId != coin.id
                                )
                                .clickable { selectedId = coin.id }
                        ) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = coin.iconUrl,
                                    contentDescription = coin.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .sharedElementWithCallerManagedVisibility(
                                            rememberSharedContentState(key = "image_${coin.id}"),
                                            visible = selectedId != coin.id
                                        )
                                )
                                Column(Modifier.weight(1f).padding(start = 16.dp)) {
                                    Text(
                                        text = coin.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                            rememberSharedContentState(key = "title_${coin.id}"),
                                            visible = selectedId != coin.id
                                        )
                                    )
                                    Text(coin.symbol, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(coin.price, style = MaterialTheme.typography.titleSmall)
                                IconButton(onClick = { viewModel.toggleFav(coin.id) }) {
                                    Icon(if (coin.isFav) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder, "Fav")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
