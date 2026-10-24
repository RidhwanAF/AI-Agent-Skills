# MapLibre Compose Code Examples

## Example: Lifecycle-Safe MapView Wrapper

```kotlin
package com.example.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun InteractiveMapScreen(
    modifier: Modifier = Modifier, // ✅ Declaration: VERY TOP parameter
    styleUrl: String,
    onRecenterClick: () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        MapLibreSurface(
            styleUrl = styleUrl,
            modifier = Modifier.fillMaxSize(), // ✅ Call site: VERY BOTTOM argument
        )

        FloatingActionButton(
            onClick = onRecenterClick,
            modifier = Modifier // ✅ Call site: VERY BOTTOM argument
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Text("📍")
        }
    }
}

@Composable
fun MapLibreSurface(
    modifier: Modifier = Modifier, // ✅ Declaration: VERY TOP parameter
    styleUrl: String,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { maplibreMap ->
                maplibreMap.setStyle(Style.Builder().fromUri(styleUrl))
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
    )
}
```
