# MapLibre Compose Integration Guidelines

## 1. Lifecycle Attachment Rules
1. `MapView` manages native OpenGL/Vulkan rendering surfaces. It MUST be bound to the surrounding `LifecycleOwner` via `DisposableEffect`.
2. Ensure `onStart`, `onResume`, `onPause`, `onStop`, and `onDestroy` are propagated directly to the `MapView` instance to prevent memory leaks and black screen glitches on rotation.

## 2. GeoJSON Caching & Layer Management
1. Separate coordinate math and GeoJSON parsing into the Domain layer; never parse heavy JSON files on the main Compose thread.
2. Use background Dispatchers (`Dispatchers.Default`) for GeoJSON string parsing and feature collection generation.
3. Update sources dynamically via `GeoJsonSource.setGeoJson(...)`.

## 3. Camera Position State
1. Store camera target, zoom, pitch, and bearing in a stable state holder class.
2. Animate camera updates smoothly using `maplibreMap.animateCamera(...)`.

## 4. UI Overlays & Inset Hygiene
1. Floating map controls (zoom buttons, compass, location tracking) must respect `WindowInsets.safeDrawing` padding so system navigation bars do not block user touch targets.
