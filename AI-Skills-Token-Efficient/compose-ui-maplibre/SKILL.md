---
name: compose-ui-maplibre
description: "Token-efficient MapLibre Compose UI: Zero hardcoded strings, state hoisting, and lifecycle safety."
---

# MapLibre Compose UI (Token-Efficient)

## String Localization
- Zero hardcoded user strings. Extract to `strings.xml` with semantic prefixes (`label_`, `msg_`, `title_`, `btn_`, `error_`, `hint_`, `desc_`) and resolve via `stringResource()`.

## MapLibre Integration
- Use `org.maplibre.compose` and `maplibre-compose-material3`.
- Hoist viewport/style state with `rememberMapViewportState()`.
- Process geospatial data (GeoJSON, spatial math) on `Dispatchers.Default`.

## Lifecycle & Resources
- Clean up listeners in Composables using `DisposableEffect`.
- Never hold Activity or View `Context` inside ViewModels.
- Use `kotlin.time.Duration` for time values (`delay(5.seconds)`).
