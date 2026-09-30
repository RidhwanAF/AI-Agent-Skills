---
name: compose-ui-preview-design
description: "Token-efficient Compose design: Stability annotations, edge-to-edge insets, haptic feedback, and offline fakes."
---

# Compose UI Design System & Previews (Token-Efficient)

## UI State Stability
1. **Annotations:** Annotate UI state models with `@Immutable` and mutable state controllers with `@Stable`.
2. **Collections:** Use `kotlinx.collections.immutable.ImmutableList` in UI state classes to guarantee recomposition skipping.
3. **Model Decoupling:** Never pass Room Entities or Network DTOs directly to Composables. Map to UI models first.
4. **Stable Keys:** Always provide unique, stable keys in `LazyColumn`/`LazyRow` (`items(list, key = { it.id })`).

## Edge-to-Edge & System Insets (Android 15+)
1. **Mandatory Inset Handling:** Handle insets dynamically via `Scaffold(innerPadding)` and `Modifier.imePadding()`.
2. **Lazy Layouts:** Apply `Modifier.fillMaxSize()` to lazy containers. Pass insets through `contentPadding`. Never apply `Modifier.padding(innerPadding)` to the lazy container itself.
3. **Full-Bleed Ripples:** For full-width ripple feedback, place `.clickable()` BEFORE `.padding(horizontal = 16.dp)` on item layouts.
4. **Scrollable Containers:** Apply `.verticalScroll()` BEFORE `.padding(innerPadding)` on standard `Column`/`Row`.

## Haptic Feedback (`LocalHapticFeedback`)
Use `LocalHapticFeedback.current.performHapticFeedback(type)` with purpose-specific types:
- `Confirm`: Successful action, form submit, save.
- `Reject`: Validation error, declined action, failure.
- `ToggleOn` / `ToggleOff`: Switch/checkbox state changes.
- `LongPress`: Context menu, drag start.
- `SegmentTick` / `SegmentFrequentTick`: Sliders, discrete pickers, scrubbers.

## Previews & Fakes
1. **Multi-Preview:** Provide `@Preview` for Light Mode and Dark Mode (`uiMode = UI_MODE_NIGHT_YES`).
2. **Offline Fakes:** Supply a Fake repository with static data for fast, offline UI rendering and testing.
