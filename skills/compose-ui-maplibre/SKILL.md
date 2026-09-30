---
name: compose-ui-maplibre
description: Enforces Jetpack Compose UI patterns, zero hardcoded strings, MapLibre Compose Material 3 integration, and lifecycle safety.
---

# Jetpack Compose & MapLibre UI Guidelines

## String Localization & Formatting
1. **Zero Hardcoded Strings:** User-facing string literals in Composables, ViewModels, or data classes are strictly forbidden.
2. **Localization Flow & Semantic Naming:** Search `res/values/strings.xml` first. If missing, auto-generate a semantic key using mandatory prefixes (`label_`, `msg_`/`message_`, `title_`, `action_`/`btn_`, `error_`, `hint_`, `desc_`/`cd_`) in `strings.xml` and access it via `stringResource(R.string.key)`.
3. **KDocs:** Write KDocs for every public/internal class, function, parameter (`@param`), and return value (`@return`).

## MapLibre Compose Integration
1. **Libraries:** Use `org.maplibre.compose` and `maplibre-compose-material3` for UI controls and styling.
2. **State Hoisting:** Manage camera and style state using `rememberMapViewportState()` or custom Compose wrappers.
3. **Dynamic Styling:** Support dark/light mode dynamically using `isSystemInDarkTheme()` to pass matching style URLs.
4. **Thread Offloading:** Heavy geospatial processing (GeoJSON parsing, spatial math) must execute on `Dispatchers.Default` in the `domain` or `data` layer.

## Memory Leak & Lifecycle Protection
1. **Context Holding:** Never store Activity, View, or Composable Context references inside ViewModels, Singletons, or Repositories.
2. **Cleanup:** Use `DisposableEffect` inside Composables to unregister callbacks or observers when leaving composition.
3. **Time API:** Use Kotlin Duration API for delays: `import kotlin.time.Duration.Companion.milliseconds` and call `delay(5000.milliseconds)` instead of passing raw primitives.