# 🗺️ Compose Google Maps Production Examples

Idiomatic, production-ready implementation of Google Maps in Jetpack Compose with marker clustering, camera animations, and floating overlay controls.

---

## 1. Marker Cluster Model (`model/PlaceClusterItem.kt`)

```kotlin
package com.example.app.presentation.map.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class PlaceClusterItem(
    val id: String,
    val name: String,
    val address: String,
    val location: LatLng,
) : ClusterItem {
    override fun getPosition(): LatLng = location
    override fun getTitle(): String = name
    override fun getSnippet(): String = address
    override fun getZIndex(): Float? = null
}
```

---

## 2. Interactive Map Screen with Clustering (`MapScreen.kt`)

```kotlin
package com.example.app.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.app.presentation.map.model.PlaceClusterItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    modifier: Modifier = Modifier, // ✅ Declaration: VERY TOP parameter
    places: List<PlaceClusterItem>,
    userLocation: LatLng?,
    hasLocationPermission: Boolean,
    onPlaceSelected: (PlaceClusterItem) -> Unit,
) {
    val initialCenter = userLocation ?: LatLng(37.7749, -122.4194)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialCenter, 13f)
    }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission,
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false, // Handled by custom floating button
                zoomControlsEnabled = false,
            ),
            modifier = Modifier.fillMaxSize(), // ✅ Call site: VERY BOTTOM argument
        ) {
            Clustering(
                items = places,
                onClusterClick = { cluster ->
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(
                                cluster.position,
                                cameraPositionState.position.zoom + 2f,
                            ),
                            durationMs = 600,
                        )
                    }
                    true
                },
                onClusterItemClick = { place ->
                    onPlaceSelected(place)
                    true
                },
            )
        }

        // Custom My Location Floating Action Button
        if (hasLocationPermission && userLocation != null) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(userLocation, 15f),
                            durationMs = 800,
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Recenter on My Location",
                )
            }
        }
    }
}
```
