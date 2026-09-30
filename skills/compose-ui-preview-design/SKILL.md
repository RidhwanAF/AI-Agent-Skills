---
name: compose-ui-preview-design
description: Enforces edge-to-edge window insets, Material 3 previews, Compose state stability (@Immutable/@Stable), purpose-driven LocalHapticFeedback, and fake offline repository mocking.
---

# Jetpack Compose UI Design System & Previews SOP

## UI State & Stability Annotations
1. **UI State Classes:** Always annotate presentation UI State data classes with `@Immutable` (e.g., `@Immutable data class HomeUiState(...)`).
2. **State Holders:** Annotate custom state holder classes managing internal mutable state with `@Stable`.
3. **Collection Stability:** Always use `kotlinx.collections.immutable.ImmutableList` or `ImmutableSet` for collections in UI State to prevent unneeded recompositions. Avoid standard `java.util.List` in state classes.
4. **Data Layer Mappings:** Network DTOs and Room Entities must NEVER be passed directly to Composables. Always map them to `@Immutable` UI models or Domain models first.
5. **State Caching & Retention:** Use `remember` with keys for expensive in-composition calculations, `rememberSaveable` to persist transient UI state across configuration changes/process death, and architecture retainers (`retain`) across navigation.
6. **Loop & Lazy Component Keys:** Always supply a stable, unique `key` for items in `LazyColumn`, `LazyRow`, and loops (`items(items, key = { it.id })` or `key(item.id)`). Avoid positional index keys.
7. **Shared Element Transitions for All List Items & Openable Components:** For any list item, card, grid tile, or collection component that can be opened into a detail view, sheet, or destination screen, use `Modifier.sharedElementWithCallerManagedVisibility` / `Modifier.sharedBoundsWithCallerManagedVisibility` with `visible = !isDetailOpen` instead of manually wrapping in `AnimatedVisibility`. This avoids layout disruption, measurement jumping, and recycling bugs while animating smoothly back and forth.

## Edge-to-Edge & Window Insets (Android 15+ Enforcement)
1. **Mandatory Edge-to-Edge (API 35+):** Edge-to-edge is enabled by default in Android 15. Apps cannot disable it or set opaque system bar background colors. Always handle insets dynamically using `WindowInsets.safeDrawing`, `WindowInsets.displayCutout`, or `Scaffold` `innerPadding`.
2. **Lazy Layout Inset & Full-Bleed Ripple Rule:** Never apply `Modifier.padding(innerPadding)` to lazy containers (`LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`, `LazyStaggeredGrid`, `LazyLayout`). Make the container `Modifier.fillMaxSize()`.
   - **For full-bleed list items with edge-to-edge ripples:** Pass strictly `innerPadding` horizontal insets in `contentPadding` without extra `+ 16.dp`, then chain `Modifier.fillMaxWidth().clickable { ... }.padding(horizontal = 16.dp, vertical = 12.dp)` on the item container. This ensures touch ripples stretch edge-to-edge across the screen while text/icons stay inset.
   - **For floating cards:** Add horizontal gutters (`+ 16.dp`) to `contentPadding` or specify card outer margins. Use padding wisely based on UX needs.
3. **Modifier Order for Standard Scrollable Containers:** In `Column` or `Row`, always chain `.verticalScroll(rememberScrollState())` **BEFORE** `.padding(innerPadding)` so the scroll viewport spans full screen edge-to-edge.
4. **Selective Inset Distribution:** When a screen has multiple components (e.g. fixed top header + bottom scrolling list), distribute `innerPadding` strategically: top component consumes `calculateTopPadding()`, while bottom scrolling component consumes `calculateBottomPadding()` in its `contentPadding`.
5. **IME Padding:** Form inputs and text fields must handle the software keyboard dynamically using `Modifier.imePadding()`.

## Tactile & Haptic Feedback (`LocalHapticFeedback`)
1. **Accessing Haptic Feedback:** In Compose UI, obtain the feedback performer via `LocalHapticFeedback.current`:
   ```kotlin
   val localHapticFeedback = LocalHapticFeedback.current
   // Trigger feedback
   localHapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
   ```
2. **Purpose-Specific `HapticFeedbackType` Selection:** Never use arbitrary feedback types. Map feedback strictly to user interaction purpose per the official Android specification:
   - `HapticFeedbackType.Confirm`: A haptic effect to signal the confirmation or successful completion of a user interaction (e.g., successful submission, save, payment, or completing a task).
   - `HapticFeedbackType.Reject`: A haptic effect to signal the rejection or failure of a user interaction (e.g., validation error, unauthorized action, failed payment, or shake/error event).
   - `HapticFeedbackType.ToggleOn`: The user has toggled a switch or button into the on position.
   - `HapticFeedbackType.ToggleOff`: The user has toggled a switch or button into the off position.
   - `HapticFeedbackType.LongPress`: The user has performed a long press on an object that is resulting in an action being performed (e.g., opening a context menu, starting drag-and-drop).
   - `HapticFeedbackType.SegmentTick`: The user is switching between a series of potential choices, for example items in a list or discrete points on a slider.
   - `HapticFeedbackType.SegmentFrequentTick`: The user is switching between a series of many potential choices, for example minutes on a clock face, or individual percentages.
   - `HapticFeedbackType.GestureThresholdActivate`: The user is executing a swipe/drag-style gesture, such as pull-to-refresh, where the gesture action is eligible at a certain threshold of movement, and can be cancelled by moving back past the threshold.
   - `HapticFeedbackType.GestureEnd`: The user has finished a gesture (e.g. on the soft keyboard or gesture navigation).
   - `HapticFeedbackType.ContextClick`: The user has performed a context click on an object (e.g., secondary/right click or stylus button click).
   - `HapticFeedbackType.VirtualKey`: The user has pressed on a virtual on-screen key.
   - `HapticFeedbackType.KeyboardTap`: The user has pressed a soft keyboard key.
   - `HapticFeedbackType.TextHandleMove`: The user has performed a selection/insertion handle move on a text field.

## Adaptive Multi-Pane Layouts (Tablets & Foldables)
1. **List-Detail Flows:** Implement `ListDetailPaneScaffold` / `rememberListDetailPaneScaffoldNavigator` (or Navigation 3 `rememberListDetailSceneStrategy()`) so the UI automatically displays single-pane on phones and side-by-side dual panes on tablets, foldables, and desktop displays.
2. **Adaptive Previews:** Include multi-device previews (`device = Devices.PIXEL_TABLET` or `device = Devices.FOLDABLE`) alongside phone previews to verify pane transitions and layout scalability.

## Previews & Offline Fakes
1. **Multi-Preview:** Provide `@Preview` annotations covering both Light Mode (`uiMode = UI_MODE_NIGHT_NO`) and Dark Mode (`uiMode = UI_MODE_NIGHT_YES`) for every standalone screen or component.
2. **Fake Repositories:** When building new UI features, supply a Fake implementation of the domain repository containing static mock data so screens can be rendered and tested offline without network dependencies.