package com.example.cryptoscalper.feature.watchlist.standard

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ============================================================================
 * DOMAIN LAYER (100% Pure Kotlin - Decoupled from Framework & Android APIs)
 * ============================================================================
 */

/**
 * Immutable Domain entity representing cryptocurrency market details.
 */
data class CryptoCoin(
    val id: String,
    val name: String,
    val symbol: String,
    val priceUsd: Double,
    val iconUrl: String
)

/**
 * Domain-level error hierarchy for typed functional error handling.
 */
sealed interface WatchlistDomainError {
    data object NetworkTimeout : WatchlistDomainError
    data object StorageFailure : WatchlistDomainError
    data object Unknown : WatchlistDomainError
}

/**
 * Repository contract defined in the Domain layer for dependency inversion.
 */
interface CryptoWatchlistRepository {
    fun getCryptoList(): Flow<List<CryptoCoin>>
    fun getFavoriteCoinId(): Flow<String?>
    suspend fun saveFavoriteCoinId(coinId: String)
}

/**
 * ============================================================================
 * DATA LAYER (Repository implementation, DataStore preferences, Network mock)
 * ============================================================================
 */

class CryptoWatchlistRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CryptoWatchlistRepository {

    private val favoriteKey = stringPreferencesKey("favorite_coin_id")

    override fun getCryptoList(): Flow<List<CryptoCoin>> = MutableStateFlow(
        listOf(
            CryptoCoin("btc", "Bitcoin", "BTC", 64500.0, "https://assets.coingecko.com/coins/images/1/large/bitcoin.png"),
            CryptoCoin("eth", "Ethereum", "ETH", 3450.0, "https://assets.coingecko.com/coins/images/279/large/ethereum.png"),
            CryptoCoin("sol", "Solana", "SOL", 145.0, "https://assets.coingecko.com/coins/images/4128/large/solana.png")
        )
    )

    override fun getFavoriteCoinId(): Flow<String?> = dataStore.data.map { preferences ->
        preferences[favoriteKey]
    }

    override suspend fun saveFavoriteCoinId(coinId: String): Unit = withContext(ioDispatcher) {
        dataStore.edit { preferences ->
            if (preferences[favoriteKey] == coinId) {
                preferences.remove(favoriteKey)
            } else {
                preferences[favoriteKey] = coinId
            }
        }
    }
}

/**
 * ============================================================================
 * PRESENTATION LAYER (UI State, ViewModel with Duration API, Compose UI)
 * ============================================================================
 */

@Immutable
data class CryptoItemUiModel(
    val id: String,
    val name: String,
    val symbol: String,
    val priceFormatted: String,
    val iconUrl: String,
    val isFavorite: Boolean
)

@Immutable
sealed interface WatchlistUiState {
    data object Loading : WatchlistUiState
    data class Success(val coins: ImmutableList<CryptoItemUiModel> = persistentListOf()) : WatchlistUiState
    data class Error(val message: String) : WatchlistUiState
}

class StandardWatchlistViewModel(
    private val repository: CryptoWatchlistRepository
) : ViewModel() {

    private val retryDelay = 3.seconds

    val uiState: StateFlow<WatchlistUiState> = combine(
        repository.getCryptoList(),
        repository.getFavoriteCoinId()
    ) { coins, favoriteId ->
        val uiModels = coins.map { coin ->
            CryptoItemUiModel(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol,
                priceFormatted = "$${String.format("%.2f", coin.priceUsd)}",
                iconUrl = coin.iconUrl,
                isFavorite = coin.id == favoriteId
            )
        }.toImmutableList()
        WatchlistUiState.Success(uiModels)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = WatchlistUiState.Loading
    )

    fun toggleFavorite(coinId: String) {
        viewModelScope.launch {
            try {
                repository.saveFavoriteCoinId(coinId)
            } catch (_: Exception) {
                delay(retryDelay)
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StandardCryptoWatchlistNavHost(
    viewModel: StandardWatchlistViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCoinId by rememberSaveable { mutableStateOf<String?>(null) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SharedTransitionLayout(modifier = modifier) {
        when (val state = uiState) {
            is WatchlistUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WatchlistUiState.Success -> {
                val selectedCoin = state.coins.firstOrNull { it.id == selectedCoinId }
                if (selectedCoin != null) {
                    StandardCryptoDetailScreen(
                        coin = selectedCoin,
                        onBack = { selectedCoinId = null },
                        onToggleFavorite = { viewModel.toggleFavorite(selectedCoin.id) },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = null
                    )
                } else {
                    StandardCryptoListScreen(
                        coins = state.coins,
                        selectedCoinId = selectedCoinId,
                        onCoinClick = { selectedCoinId = it },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        sharedTransitionScope = this@SharedTransitionLayout
                    )
                }
            }
            is WatchlistUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StandardCryptoListScreen(
    coins: ImmutableList<CryptoItemUiModel>,
    selectedCoinId: String?,
    onCoinClick: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier
) {
    val springBounds = BoundsTransform { _, _ ->
        spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
    }

    Scaffold(modifier = modifier) { innerPadding ->
        val layoutDir = LocalLayoutDirection.current
        with(sharedTransitionScope) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = innerPadding.calculateStartPadding(layoutDir) + 16.dp,
                    end = innerPadding.calculateEndPadding(layoutDir) + 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = coins, key = { it.id }) { coin ->
                    val isDetailOpen = selectedCoinId == coin.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sharedBoundsWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(key = "card_${coin.id}"),
                                visible = !isDetailOpen,
                                boundsTransform = springBounds
                            )
                            .clickable { onCoinClick(coin.id) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = coin.iconUrl,
                                contentDescription = coin.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = "image_${coin.id}"),
                                        visible = !isDetailOpen,
                                        boundsTransform = springBounds
                                    )
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                                Text(
                                    text = coin.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = "title_${coin.id}"),
                                        visible = !isDetailOpen,
                                        boundsTransform = springBounds
                                    )
                                )
                                Text(text = coin.symbol, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(text = coin.priceFormatted, style = MaterialTheme.typography.titleSmall)
                            IconButton(onClick = { onToggleFavorite(coin.id) }) {
                                Icon(
                                    imageVector = if (coin.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (coin.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun StandardCryptoDetailScreen(
    coin: CryptoItemUiModel,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    modifier: Modifier = Modifier
) {
    PredictiveBackHandler { progress ->
        try {
            progress.collect { /* Drive gesture animation */ }
            onBack()
        } catch (_: CancellationException) {
            // Cancelled
        }
    }

    val springBounds = BoundsTransform { _, _ ->
        spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow)
    }

    with(sharedTransitionScope) {
        Scaffold(modifier = modifier) { innerPadding ->
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "card_${coin.id}"),
                        animatedVisibilityScope = animatedVisibilityScope ?: this,
                        boundsTransform = springBounds
                    )
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AsyncImage(
                        model = coin.iconUrl,
                        contentDescription = coin.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .sharedElement(
                                state = rememberSharedContentState(key = "image_${coin.id}"),
                                animatedVisibilityScope = animatedVisibilityScope ?: this,
                                boundsTransform = springBounds
                            )
                    )
                    Text(
                        text = coin.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .sharedElement(
                                state = rememberSharedContentState(key = "title_${coin.id}"),
                                animatedVisibilityScope = animatedVisibilityScope ?: this,
                                boundsTransform = springBounds
                            )
                    )
                    Text(text = coin.symbol, style = MaterialTheme.typography.bodyLarge)
                    Text(text = coin.priceFormatted, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 12.dp))
                    IconButton(onClick = onToggleFavorite) {
                        Icon(
                            imageVector = if (coin.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (coin.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
