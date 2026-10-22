---
name: compose_maplibre
description: "MapLibre Native integration for Jetpack Compose. Enforces lifecycle-safe MapView handling, MapLibre Compose Material 3 theming, GeoJSON layer caching, camera state animation, and memory-safe symbol management."
---

# 🗺️ MapLibre for Jetpack Compose (/compose-maplibre)

The **MapLibre Compose Skill** guides high-performance geospatial mapping in Android using MapLibre Native and modern Jetpack Compose. It ensures lifecycle safety, smooth vector tile rendering, declarative layer styling, and leak-free camera management.

---

## 🧭 Connected Skills & Routing

```mermaid
flowchart LR
    ML(["🗺️ /compose-maplibre"])
    
    CUI(["🎨 /compose-ui"])
    AA(["🏛️ /android-architecture"])
    GBH(["🐘 /gradle-build-hygiene"])
    ED(["🔍 /error-detective"])

    CUI -->|Embeds Map within Screen| ML
    AA -->|Supplies GeoJSON / Points| ML
    GBH -->|MapLibre Dependencies| ML
    ML -->|OpenGL / Surface Crashes| ED
```

---

## 🏛️ 1. Clean Architectural Separation for Maps

Never mix map surface rendering, vector tile parsing, and coordinate math inside a single file:

```
feature/map/
├── MapScreen.kt               # Container observing coordinates from ViewModel
├── components/
│   ├── MapLibreContainer.kt   # Composable wrapping MapView with lifecycle handling
│   ├── MapControlsOverlay.kt  # Zoom, pitch, compass, and layer switch buttons
│   └── LocationMarkerCard.kt  # Floating bottom card displaying selected point
├── model/
│   ├── GeoPointUiModel.kt     # Immutable UI point representation
│   └── CameraPositionState.kt # Stable camera state holder
└── domain/
    └── ConvertGeoJsonUseCase.kt # Pure Kotlin GeoJSON parser (zero Android UI imports)
```

---

## 🛡️ 2. Lifecycle Safety & GL Surface Management

`MapView` manages OpenGL / Vulkan native rendering surfaces. Improper lifecycle attachment causes severe memory leaks or crashes during screen rotation:

```kotlin
/**
 * Lifecycle-safe Composable wrapper around MapLibre MapView.
 *
 * @param styleUrl Vector tile style JSON URL.
 * @param cameraPosition Target camera center and zoom level.
 * @param onMapClick Fired when user selects coordinates on the map.
 * @param modifier Applied to the native AndroidView container (Top of optional params).
 */
@Composable
fun MapLibreContainer(
    styleUrl: String,
    cameraPosition: CameraPositionState,
    onMapClick: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            getMapAsync { maplibreMap ->
                maplibreMap.setStyle(styleUrl)
                maplibreMap.addOnMapClickListener { point ->
                    onMapClick(point.latitude, point.longitude)
                    true
                }
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
        update = {
            // Synchronize camera state updates safely
        }
    )
}
```

---

## 🎨 3. Material 3 Theming & Responsive Controls

- Position map overlays (compass, zoom buttons, location centering FAB) using Material 3 `FloatingActionButton` or `IconButton`.
- Respect edge-to-edge window insets by applying `WindowInsets.safeDrawing` to control overlays so navigation bars never obscure map interactions.
- Provide smooth camera animations via `animateCamera` rather than abrupt jumps.
