# 🗺️ Compose Google Maps Guidelines

Standards, version catalog setup, and implementation protocols for Google Maps in Jetpack Compose.

---

## 🎯 Implementation Protocols

1. **Gradle Dependency Setup (`libs.versions.toml`)**:
   Always declare official Google Maps Compose artifacts via Version Catalog:
   ```toml
   [versions]
   maps-compose = "6.1.2"
   play-services-maps = "19.0.0"

   [libraries]
   maps-compose = { module = "com.google.maps.android:maps-compose", version.ref = "maps-compose" }
   maps-compose-utils = { module = "com.google.maps.android:maps-compose-utils", version.ref = "maps-compose" }
   maps-compose-widgets = { module = "com.google.maps.android:maps-compose-widgets", version.ref = "maps-compose" }
   play-services-maps = { module = "com.google.android.gms:play-services-maps", version.ref = "play-services-maps" }
   ```

2. **API Key Security**:
   - Store the API key in `local.properties` as `MAPS_API_KEY=AIzaSy...`.
   - Use the **Secrets Gradle Plugin for Android** (`com.google.android.libraries.mapsplatform.secrets-gradle-plugin`) to inject the key into `AndroidManifest.xml` as a manifest placeholder.
   - Never commit raw API keys to Git.

3. **Performance & Recomposition Control**:
   - Do NOT observe raw `cameraPositionState.position` directly in composables that render frequently.
   - Wrap coordinate queries in `derivedStateOf { ... }` or listen only to completion events (`cameraPositionState.isMoving == false`).
   - Use immutable data models (`@Immutable data class MarkerUiModel(...)`) to prevent unnecessary marker re-draws.

4. **Clustering Strategy**:
   - Implement `com.google.maps.android.clustering.ClusterItem` on your domain or UI model.
   - Provide custom composables via `clusterContent` and `clusterItemContent` for customized branding.
   - Set cluster click listeners to zoom in automatically on the cluster center.

5. **Location & Edge-to-Edge Support**:
   - Apply window insets to overlay controls using `Modifier.windowInsetsPadding(WindowInsets.safeDrawing)`.
   - Never let overlay controls obscure Google Maps copyright notices and logo (enforce official Google Maps Platform branding policies).
