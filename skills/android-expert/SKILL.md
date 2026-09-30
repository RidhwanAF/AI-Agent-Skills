---
name: android_expert
description: "Global Android engineering capability spanning legacy codebases, modern Jetpack Compose UI, background Services/WorkManager, Clean Architecture with MVVM/MVI, Coroutines & Flow, Hilt DI, and modern Android 14+ restrictions."
---

# Android Expert Skill

This skill provides comprehensive architectural patterns, production standards, and engineering discipline for developing robust, resilient, and performant Android applications. It bridges modern Jetpack Compose paradigms, Clean Architecture, reactive data flows, background execution compliance, backward compatibility, and strict code hygiene.

---

## 1. Core Engineering Discipline & Code Hygiene

### Zero Hardcoded Strings & Multi-Language Localization
- **Never hardcode UI strings** in Composable functions, XML layouts, or ViewModels.
- Always extract user-facing strings into `res/values/strings.xml`.
- **Multi-Language Support**: If the application supports multiple languages/locales, ensure corresponding translations are prepared in each qualified locale directory (e.g., `res/values/strings.xml` for default/English, `res/values-es/strings.xml`, `res/values-ja/strings.xml`, `res/values-id/strings.xml`). Never leave keys missing across supported locale bundles.
- In Compose, resolve strings via `stringResource(R.string.your_string_key)` or `stringResource(R.string.formatted_key, arg1)`.
- **Semantic String Key Naming Conventions (Mandatory Prefixes)**:
  - Always name string resource keys with descriptive semantic prefixes based on their UI purpose:
    - **Labels**: `label_<name>` (e.g., `label_username`, `label_email`, `label_first_name`, `label_settings`)
    - **Messages / Alerts**: `msg_<name>` or `message_<name>` (e.g., `msg_login_success`, `msg_empty_cart`, `message_session_expired`, `msg_delete_confirmation`)
    - **Titles / Headers**: `title_<screen_or_dialog>` (e.g., `title_user_profile`, `title_payment_details`, `title_error_dialog`)
    - **Actions / Buttons**: `action_<verb>` or `btn_<name>` (e.g., `action_submit`, `action_cancel`, `action_retry`, `btn_save`)
    - **Errors**: `error_<reason>` (e.g., `error_network_timeout`, `error_invalid_email`, `error_field_required`)
    - **Hints / Placeholders**: `hint_<field>` (e.g., `hint_search_products`, `hint_enter_password`)
    - **Accessibility / Content Descriptions**: `desc_<element>` or `cd_<icon>` (e.g., `desc_profile_avatar`, `desc_back_button`, `cd_expand_pane`)
  - Never use ambiguous or prefix-less keys like `name`, `text1`, `submit`, or `error` alone.
- **Compose Multiplatform (KMP) String Transition Strategy**:
  - When migrating or targeting Kotlin Multiplatform with Compose Multiplatform, strings migrate from Android `res/values/strings.xml` to `commonMain/composeResources/values/strings.xml` (and `values-es/strings.xml`), resolved via `Res.string.key`.
  - Maintain a clean, platform-agnostic `UiText` wrapper in the presentation or core module to decouple ViewModels from Android-specific `R.string`:
    ```kotlin
    sealed interface UiText {
        data class DynamicString(val value: String) : UiText
        data class StringResource(val id: Int, val args: List<Any> = emptyList()) : UiText // Android R.string
        // KMP: data class MultiplatformResource(val resource: StringResource, val args: List<Any> = emptyList()) : UiText
    }
    ```

### Clean Code, Top-Level Imports & Minimalist Comments
- **Never Write Fully-Qualified Package Names Inline**: Do NOT call packages, classes, functions, or extensions inline within functions or classes (e.g., avoid `kotlin.math.cos(...)`, `kotlinx.coroutines.delay(...)`, or `android.util.Log.d(...)` in code blocks). Always import the required class, function, or extension at the top of the file (e.g. `import kotlin.math.cos`) and call it directly (`cos(...)`).
- **No Unused Imports**: Remove unused, wildcard (`import com.example.*`), or obsolete imports before finalizing edits.
- **No AI-like Robotic Comments**: Eliminate superfluous comments like `// Here we define the variable`, `// Return result`, or restating what the code does. Keep code self-documenting with intention-revealing names.
- Document only **non-obvious rationale**, tricky hardware/OEM workarounds, threading assumptions, or complex business logic.

### Proactive Rule Violation Auditing & Remediation Planning
- **Zero Tolerance for Architectural Leaks**: When analyzing or modifying existing code, if you detect violations of architectural rules (for instance: finding `@Serializable`, Retrofit/Room annotations, or `android.*` framework dependencies inside the **Domain Layer**), **do NOT silently ignore or perpetuate the violation**.
- **Proactive Remediation Plan**: Immediately flag the violation to the user and outline a recommended refactoring plan to fix the boundary violation and restore clean separation of concerns before or alongside the requested changes.

### Project-Scoped AI Documentation & Memory System (`.gemini/`, `.claude/`, `.codex/`, `.cursor/`, etc.)
- **Never Write Project Docs to Global Folders**: All project-specific architectural docs, changelogs, implementation plans, and memory contexts must reside strictly within the project's local AI agent directory (e.g., `<project-root>/.gemini/`, `<project-root>/.claude/`, `<project-root>/.codex/`, `<project-root>/.cursor/`, etc., matching whichever AI tool is in use), NEVER in global user configuration folders.
- **Changelog & Implementation Plans**:
  - Document substantial features, refactorings, or migrations in `<project-root>/<ai-folder>/docs/changes/<date>-<topic>.md`.
  - Document pre-implementation roadmaps in `<project-root>/<ai-folder>/docs/plans/<topic>-plan.md`.
  - Each changelog entry must detail:
    1. **What Changed**: Precise summary of components, classes, and configurations modified.
    2. **Advantages**: Architectural benefits, performance gains, testability, or DX improvements.
    3. **Risks & Mitigations**: Potential regressions and how they are guarded against.
- **Categorized Project Memory (`<project-root>/<ai-folder>/memory/<category>.md`)**:
  - Once an implementation or feature step is finished, append a persistent summary to `<project-root>/<ai-folder>/memory/`.
  - Group memories strictly by category in dedicated Markdown files (e.g., `architecture.md`, `features.md`, `fixes.md`, `ui.md`). Maintain and update the same MD file per category.
  - **Context-Retrieval Instruction**: Whenever any AI assistant (Gemini, Claude, Codex, Cursor, etc.) requires context regarding recent changes, architectural decisions, or technical nuances in the project, it must inspect and read `<project-root>/<ai-folder>/memory/` first before taking action.

### Evidence-Based Engineering & Official Docs
- **Never guess or assume APIs**: Do not invent unofficial parameters or hallucinate methods. Verify API signatures against official Google Android documentation (`developer.android.com`), Jetpack library releases, or Kotlin language specifications.
- **Problem Solving Order**:
  1. Official Android & Jetpack documentation.
  2. Official Android GitHub samples (`android/architecture-samples`, `android/nowinandroid`).
  3. Trusted community patterns (Google Developers blogs, Kotlin Coroutines GitHub issues, verified AOSP discussions).
- **Up-to-Date Idiomatic Syntax**:
  - Always prefer modern, idiomatic Kotlin syntax over obsolete Java-isms or deprecated APIs:
    - **`kotlin.time.Duration` for ALL Time Values (Mandatory)**:
      - Import `kotlin.time.Duration.Companion.seconds`, `.milliseconds`, `.minutes`, etc. at top level.
      - **Literal delays**: Use `delay(500.milliseconds)` / `delay(5.seconds)` instead of raw magic integer milliseconds `delay(5000L)` or `delay(5000)`.
      - **Variable-based delays/timeouts**: When a duration is stored in a variable, the variable itself MUST be typed as `Duration` or converted at the call site using the Duration extension:
        ```kotlin
        // ❌ WRONG — raw Long with no type safety
        val retryDelay = 3000L
        delay(retryDelay)

        // ✅ CORRECT — variable is a Duration
        val retryDelay = 3.seconds
        delay(retryDelay)

        // ✅ ALSO CORRECT — convert at usage if value comes from external source
        val retryDelayMs = config.getRetryDelayMs() // Long from external config
        delay(retryDelayMs.milliseconds)
        ```
      - **Timeout APIs**: Use `withTimeout(5.seconds)` and `withTimeoutOrNull(2.seconds)` instead of `withTimeout(5000)`.
      - **Flow operators**: Use `debounce(300.milliseconds)`, `timeout(10.seconds)`, `sample(500.milliseconds)`.
      - **WorkManager / AlarmManager intervals**: When computing intervals for scheduling APIs that require `Long` millis, convert explicitly: `val intervalMs = 15.minutes.inWholeMilliseconds`.
      - **Measuring elapsed time**: Use `measureTime { ... }` or `measureTimedValue { ... }` from `kotlin.time`, never `System.currentTimeMillis()` subtraction.
    - **Prefer `forEach` / `forEachIndexed` Over Traditional Loops**:
      - Use `collection.forEach { item -> ... }` or `collection.forEachIndexed { index, item -> ... }` instead of `for (i in 0 until list.size)` or `for (item in list)` when the loop body is a simple side-effect operation and early `return`/`break`/`continue` control flow is not needed.
      - For `Map` iteration, use `map.forEach { (key, value) -> ... }` with destructuring.
      - In Compose `LazyColumn`/`LazyRow`, always use `items(...)` / `itemsIndexed(...)` DSL — never manual index loops inside the `LazyListScope`.
      - When `break`/`continue`/`return` is genuinely needed, a standard `for` loop is acceptable — but prefer transforming data with `filter`/`map`/`takeWhile` first to eliminate the need for early exits.
    - Use `AutoCloseable` or `use { ... }` for resource management.
    - Use modern standard library extensions (`maxOrNull()`, `firstOrNull()`, `buildList { ... }`, `buildMap { ... }`, `buildString { ... }`).
    - **Scope functions**: Prefer `let`, `also`, `apply`, `run`, `with` appropriately — use `?.let { }` for null-safe operations instead of explicit `if (x != null)` blocks when the result is a single expression or chain.
    - **String templates**: Use `"Hello, $name"` or `"Count: ${list.size}"` instead of string concatenation (`"Hello, " + name`).
    - **Trailing lambda syntax**: Always place the last lambda argument outside parentheses.
    - **Destructuring declarations**: Use destructuring for `Pair`, `Triple`, data classes, and `Map.Entry` where it improves readability.

### Dead Code & Unused Symbol Cleanup (Mandatory)
- **Zero Tolerance for Dead Code**: Aggressively identify and remove unused code during every edit session. Dead code increases cognitive load, hides bugs, inflates APK size, and misleads future developers (including AI assistants).
- **What Constitutes Dead Code**:
  - Unused `private` functions, properties, or classes that are never called or referenced.
  - Unused function parameters (add `@Suppress("unused")` ONLY if the parameter is required by an interface/override contract).
  - Commented-out code blocks ("just in case" code). If it's needed, it belongs in version control history, not in comments.
  - Unreachable branches (e.g., `else` after exhaustive `when`, conditions that are always true/false).
  - Unused `import` statements (including wildcard imports).
  - Empty method bodies that serve no purpose (e.g., empty `catch` blocks without explicit suppression rationale, empty lifecycle callbacks).
  - Unused string resources, drawable resources, or layout files.
  - Deprecated wrapper functions that simply delegate to the new API without adding value.
- **Cleanup Protocol**:
  1. Before finalizing any code edit, scan the modified file(s) for newly orphaned symbols.
  2. When modifying a class or function, check if any existing members of that class became unreferenced as a result.
  3. When deleting or renaming a public symbol, trace all usages across the project and clean up dangling references.
  4. Never leave `TODO` comments without a linked issue tracker reference (e.g., `// TODO(#1234): Implement caching`).

### Deprecation Avoidance & API Currency (Mandatory)
- **Never Use Deprecated APIs**: Before writing or suggesting any API call, verify it is not deprecated in the target SDK / library version. If a deprecated API is found in existing code during an edit, proactively migrate it.
- **Common Deprecated → Modern Replacements**:
  - `onBackPressed()` → `OnBackPressedCallback` / `PredictiveBackHandler` (Compose)
  - `startActivityForResult()` → `ActivityResultContract` / `rememberLauncherForActivityResult()`
  - `AsyncTask` → `kotlinx.coroutines` with `viewModelScope` / `lifecycleScope`
  - `LocalBroadcastManager` → `SharedFlow` / `StateFlow` / explicit callbacks
  - `Loader` / `CursorLoader` → Room `Flow` + ViewModel
  - `ProgressDialog` → Inline `CircularProgressIndicator` or `LinearProgressIndicator` in Compose
  - `kotlin.jvm.Throws` misuse → proper `runCatching` or typed result
  - `Handler(Looper.getMainLooper()).postDelayed()` → `lifecycleScope.launch { delay(duration) }`
  - `Resources.getColor(int)` → `ContextCompat.getColor(context, resId)` or `colorResource()` in Compose
  - `Resources.getDrawable(int)` → `ContextCompat.getDrawable(context, resId)`
  - `PackageManager.getPackageInfo(String, Int)` → `PackageManager.getPackageInfo(String, PackageManager.PackageInfoFlags)` (API 33+)
  - `Environment.getExternalStorageDirectory()` → Scoped storage (`MediaStore`, `context.getExternalFilesDir()`)
  - `Window.setStatusBarColor()` / `Window.setNavigationBarColor()` → edge-to-edge with `WindowInsetsCompat` (mandatory Android 15+)
  - `Log.d()` / `Log.e()` (production) → `Timber.d()` / `Timber.e()` or a centralized logging abstraction
- **Version-Gated Migration**: When a modern replacement requires a higher minSdk than the project supports, use `Build.VERSION.SDK_INT` checks with the deprecated API as the `else` fallback:
  ```kotlin
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      // Modern API (non-deprecated)
      intent.getParcelableExtra(key, MyParcelable::class.java)
  } else {
      // Legacy fallback (suppress deprecation warning with justification)
      @Suppress("DEPRECATION")
      intent.getParcelableExtra(key) as? MyParcelable
  }
  ```
- **`@Suppress("DEPRECATION")` Discipline**: Only suppress deprecation warnings when:
  1. The deprecated API is used inside a version-gated `else` branch targeting older API levels.
  2. A comment documents the minimum API level that will retire this path.
  3. Never blanket-suppress at file or class level.

### Additional Modern Kotlin Idioms
- **`sealed interface` over `sealed class`**: Prefer `sealed interface` for state/event hierarchies — it allows multiple interface inheritance and avoids forcing a common superclass.
- **`data object` for singletons**: Use `data object` (Kotlin 1.9+) for sealed hierarchy leaf nodes with no properties (e.g., `data object Loading : UiState`).
- **`value class` (inline class)**: Wrap primitive types with `@JvmInline value class` for type safety without runtime allocation overhead (e.g., `value class UserId(val value: String)`).
- **Explicit API mode awareness**: In library modules with `explicitApi()` in `build.gradle.kts`, always specify visibility modifiers (`public`, `internal`, `private`) explicitly.
- **`require` / `check` / `error` for preconditions**: Use `require(condition) { "message" }` for argument validation, `check(condition) { "message" }` for state validation, and `error("message")` for unreachable code paths — never throw raw `IllegalArgumentException` / `IllegalStateException` directly.
- **Collection operations chain**: Prefer functional chain (`filter { }.map { }.sortedBy { }`) over imperative loops with mutable accumulators. Use `asSequence()` when the collection is large and multiple intermediate operations are chained.

### Modern API Support with Graceful Legacy Compatibility
- **Always Target Latest SDK**: Write implementations targeting the latest stable Android platform features (Android 14+, API 34+).
- **Graceful Backward Degradation**: Always validate platform version boundaries using explicit SDK level checks before invoking newer framework APIs:
  ```kotlin
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      // Modern API 34+ execution path
  } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      // API 33 fallback
  } else {
      // Legacy execution path
  }
  ```
- Use AndroidX Compat wrappers (`NotificationCompat`, `ServiceCompat`, `ContextCompat`, `WindowInsetsCompat`, `ActivityCompat`) to minimize boilerplate branching.


---

## 2. Architectural Blueprint (Clean MVVM / MVI)

```
┌────────────────────────────────────────────────────────┐
│                   Presentation Layer                   │
│   Composable Screens / Fragments  ◄─── ViewModels      │
│   (StateFlow / SharedFlow UI State & Single Events)    │
└───────────────────────────┬────────────────────────────┘
                            │ depends on
                            ▼
┌────────────────────────────────────────────────────────┐
│                      Domain Layer                      │
│   UseCases / Interactors (Pure Kotlin / coroutines)    │
│   Domain Models (Immutable) & Repository Interfaces    │
└───────────────────────────▲────────────────────────────┘
                            │ implements / provides
┌───────────────────────────┴────────────────────────────┐
│                       Data Layer                       │
│   Repository Implementations                           │
│   Local (Room / DataStore) + Remote (Retrofit / Ktor)  │
└────────────────────────────────────────────────────────┘
```

### Presentation Layer
- **Jetpack Compose (Modern Primary)**:
  - **Single UI State**: Emit UI state from `ViewModel` as a single, immutable `StateFlow<ScreenUiState>` initialized with `SharingStarted.WhileSubscribed(5_000)`.
  - **Lifecycle-Aware State Collection**: Screen composables collect state using `collectAsStateWithLifecycle()` from `androidx.lifecycle:lifecycle-runtime-compose` to prevent collecting flows while in the background.
  - **Unidirectional Data Flow (UDF)**: Screen composables accept immutable state and emit events via lambda callbacks `(UiEvent) -> Unit` or explicit lambda slots.
  - **Android 15+ (API 35/36+) Mandatory Edge-to-Edge & System Insets**:
    - Edge-to-edge is mandatory and enabled by default in Android 15 (`targetSdk = 35+`). Apps cannot disable edge-to-edge or set opaque system bar background colors via Window APIs.
    - Always handle insets dynamically using `WindowInsets.safeDrawing`, `WindowInsets.displayCutout`, and `Modifier.imePadding()`. Never hardcode status/navigation bar heights.
  - **Modifier Order & Content Padding for Lazy Components & Scrollable Containers**:
    - **Applies to all Lazy Components & Scrollable Containers**: `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`, `LazyVerticalStaggeredGrid`, `LazyLayout`, etc.
    - **Never apply `Modifier.padding(innerPadding)` to the lazy container itself**: Applying padding to the outer container clips the scroll viewport at the insets boundary, abruptly chopping off items at the system bars during scroll. Always apply `Modifier.fillMaxSize()` to the container and pass insets into `contentPadding`.
    - **Full Edge-to-Edge Clickable Bounds (Full-Bleed Ripple vs Floating Cards)**:
      - **For Full-Bleed List Items**: When item clicks should trigger a Material ripple that extends across the entire screen width edge-to-edge, do **NOT** add horizontal padding (no `+ 16.dp`) in `contentPadding`. Pass ONLY `innerPadding.calculateStartPadding(layoutDirection)` and `calculateEndPadding(layoutDirection)`.
        Then, inside the item component, chain modifiers wisely:
        ```kotlin
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { onItemClick(item.id) }) // Full-width ripple
            .padding(horizontal = 16.dp, vertical = 12.dp)  // Inset content safely
        ```
        Placing `.clickable(...)` BEFORE `.padding(...)` guarantees that touch feedback and ripple span the full width from edge to edge, while the content text/icons remain comfortably inset.
      - **For Floating Card Items**: Add horizontal margin (`+ 16.dp`) to `contentPadding` (or set card outer margins) so the cards float with uniform side gutters.
      - *Use padding wisely and intentionally based on whether the component requires full-width touch targets or isolated floating bounds.*
    - **For Standard Scrollable Containers (`Column` / `Row` with `verticalScroll`)**:
      - **Modifier Order is Critical**: Always apply `.verticalScroll(...)` **BEFORE** `.padding(innerPadding)`:
        ```kotlin
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        )
        ```
        Placing `.verticalScroll(...)` first ensures the scroll touch/viewport area spans the full screen without being cut off by the navigation bar or status bar.
    - **Selective Inset Distribution for Multi-Component / Split Layouts**:
      - When a screen features multiple components (e.g., sticky top header + scrollable list below):
        - Distribute `innerPadding` selectively and wisely:
          - Top fixed component consumes `innerPadding.calculateTopPadding()`, `start`, and `end`.
          - Bottom scrollable component consumes `innerPadding.calculateBottomPadding()` inside its `contentPadding` along with `start` and `end`, leaving `top = 0.dp` to prevent redundant gaps.
  - **Compose Adaptive Layout for List-Detail (`ListDetailPaneScaffold` & Navigation 3)**:
    - For any master-detail or list-detail user flow, utilize Material 3 Adaptive components (`ListDetailPaneScaffold`, `NavigableListDetailPaneScaffold`, or `NavDisplay` with `rememberListDetailSceneStrategy()`).
    - **Automatic Multi-Window & Form-Factor Adaptation**:
      - **Compact Screens (Phones)**: Automatically displays a single pane, transitioning smoothly between list and detail screens with predictive back gestures and shared element animations.
      - **Expanded Screens (Tablets, Foldables, Desktops)**: Automatically displays list and detail panes side-by-side in dual-pane view without requiring separate Activity or Fragment codebases.
    - In Navigation 3, pass `rememberListDetailSceneStrategy()` into `NavDisplay` to coordinate list and detail backstack navigation natively.
  - **Kotlin 2.0+ Compose Compiler & Strong Skipping Mode**:
    - With Kotlin 2.0+ (`org.jetbrains.kotlin.plugin.compose`), **Strong Skipping Mode** is enabled by default. Composables with unstable parameters become skippable via reference equality (`===`).
    - Standard collections still require `kotlinx.collections.immutable.ImmutableList<T>` to guarantee reliable skipping without relying on reference comparisons of mutating instances.
    - Annotate UI models with `@Immutable` (read-only) or `@Stable` (observable mutability).
    - Use `rememberUpdatedState` when capturing callbacks or state in long-lived coroutines or effects to prevent stale captures without triggering recompositions.
  - **State Caching & Retention (`remember`, `rememberSaveable`, `retain`)**:
    - Use `remember(key1, key2)` to cache expensive computations or derived states across recompositions. Always pass controlling state dependencies as keys.
    - Use `rememberSaveable` for transient UI states (active search queries, scroll offsets, bottom sheet states) to survive device rotations and process death. Supply a custom `Saver` when handling complex UI types.
    - For architectural retaining across configuration changes without leaking Android components, utilize architecture retainers (e.g., `retain` or retained `ViewModelStoreOwner`).
  - **Looping & Lazy Component Keys (`key` & `contentType`)**:
    - **Always provide a stable, unique `key`** in `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, and loops (`items(items = users, key = { it.id })` or `key(item.id) { ... }`).
    - Provide `contentType` when lists contain heterogeneous view types to allow Compose to reuse compositions efficiently.
    - Avoid passing unstable parameters directly to list item composables; ensure item composables are skippable.
  - **Shared Element Transitions & Navigation 3 Animations**:
    - **Shared Element Transitions**: Enclose root navigation content in `SharedTransitionLayout` to expose `SharedTransitionScope`.
    - Apply `Modifier.sharedElement` or `Modifier.sharedBounds` with `rememberSharedContentState(key = ...)` to animate components smoothly from their original place to a new screen or expanded view, and back to their original position upon reverse navigation.
    - **Shared Element Key Matching (Critical — Must Be Identical & Unique Per Item)**:
      - The shared element key passed to `rememberSharedContentState(key = ...)` **MUST be identical** on both the source (list item / origin component) and the destination (detail screen / dialog / fullscreen overlay). If keys differ, no animation occurs — the component simply appears/disappears.
      - Keys **MUST be unique per item** — always include the item's unique identifier in the key string. Use a consistent prefix + ID pattern:
        ```kotlin
        // Source (list item)
        rememberSharedContentState(key = "card_${item.id}")
        rememberSharedContentState(key = "title_${item.id}")
        rememberSharedContentState(key = "image_${item.id}")

        // Destination (detail / dialog / fullscreen) — SAME keys
        rememberSharedContentState(key = "card_${item.id}")
        rememberSharedContentState(key = "title_${item.id}")
        rememberSharedContentState(key = "image_${item.id}")
        ```
      - Never use generic keys like `"card"`, `"title"`, or `"image"` without the item ID — this causes key collisions across list items and breaks all transitions.
      - **Multiple shared elements per item**: Each visual element that should animate independently (card container, title text, thumbnail image, subtitle) gets its own key with the same item ID suffix (e.g., `"card_${id}"`, `"title_${id}"`, `"thumb_${id}"`).
    - **Image Loading with Coil & Shared Element Transitions (Mandatory)**:
      - **Always use Coil** (`io.coil-kt.coil3:coil-compose`) as the image loading library. Use `AsyncImage` composable for all remote/local image loading — never use `BitmapFactory`, `Glide`, or `Picasso`.
      - **Images MUST participate in shared element transitions**: When a list item contains a thumbnail that opens into a detail view with a larger image, both the source thumbnail and the destination image must share the same key:
        ```kotlin
        // Source: list item thumbnail
        AsyncImage(
            model = item.imageUrl,
            contentDescription = stringResource(R.string.desc_item_thumbnail, item.title),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .sharedElementWithCallerManagedVisibility(
                    sharedContentState = rememberSharedContentState(key = "image_${item.id}"),
                    visible = !isDetailOpen
                )
        )

        // Destination: detail screen full image — SAME key
        AsyncImage(
            model = item.imageUrl,
            contentDescription = stringResource(R.string.desc_item_image, item.title),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .sharedElement(
                    state = rememberSharedContentState(key = "image_${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        )
        ```
      - **Same `model` URL on both sides**: Coil caches images by URL — when the source and destination use the same `model`, the full-size image is already in memory/disk cache, preventing flicker or reload during the transition.
      - **`ContentScale` consistency**: Use the same or compatible `ContentScale` (e.g., `Crop`) on both source and destination for a seamless morph. Mismatched scales (e.g., `Crop` → `Fit`) can cause visual jumps during the animation.
      - **Placeholder & error states**: Always provide `placeholder` and `error` drawables/painters in `AsyncImage` to prevent blank frames during loading:
        ```kotlin
        AsyncImage(
            model = item.imageUrl,
            contentDescription = stringResource(R.string.desc_item_image, item.title),
            placeholder = painterResource(R.drawable.placeholder_image),
            error = painterResource(R.drawable.error_image),
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
        ```
      - **Fullscreen image viewer**: For tap-to-expand image galleries, apply `sharedElement` with `"image_${item.id}"` on the fullscreen `AsyncImage`. The image animates from its thumbnail size/position to fill the screen, and reverses smoothly on `PredictiveBackHandler` swipe-back.
      - **Coil best practices**:
        - Use `ImageLoader` with `crossfade(true)` for smooth fade-in on first load.
        - For lists, set `size(Size.ORIGINAL)` or a fixed pixel size via `ImageRequest.Builder` to avoid re-decoding at different sizes.
        - Use `SubcomposeAsyncImage` only when you need custom loading/error composables — prefer `AsyncImage` for simpler cases.
    - **Transitions for All Destination Types (Navigation, Dialogs, Bottom Sheets, Fullscreen Overlays)**:
      - **List → Detail Navigation**: Use `sharedBoundsWithCallerManagedVisibility` on the source card and `sharedBounds` on the destination card, both with `key = "card_${item.id}"`. The card morphs from its list position to the detail layout and back.
      - **Item → Dialog / Bottom Sheet**: When tapping a list item opens a dialog or bottom sheet, apply `sharedBoundsWithCallerManagedVisibility` on the list item and `sharedBounds` on the dialog/sheet content using the same key. This makes the dialog appear to expand from the item's position and collapse back on dismiss.
      - **Item → Fullscreen Overlay**: For fullscreen image viewers or custom overlay views, apply `sharedElementWithCallerManagedVisibility` on the source thumbnail and `sharedElement` on the fullscreen image using the same key (e.g., `"image_${item.id}"`). The image smoothly scales from its list position to fill the screen.
      - **Grid / Staggered Grid Items**: Same rules apply — each grid cell gets unique keys based on item ID. The transition animates the cell from its grid position to the destination view regardless of scroll position.
    - **Predictive Back Navigation & Spring Physics**: Support Android 14+ predictive back gestures by integrating `PredictiveBackHandler` with `SharedTransitionScope`. Use spring physics for `boundsTransform` (e.g., `spring(stiffness = Spring.StiffnessMediumLow)`) over linear/tween animations for natural spatial motion.
    - **`PredictiveBackHandler` with Animated Progress (Mandatory for All Detail/Overlay Screens)**:
      - **Always use `PredictiveBackHandler`** instead of `BackHandler` on any screen that has shared element transitions. `BackHandler` snaps instantly; `PredictiveBackHandler` lets the user interactively swipe back and see the shared elements animating back toward their original positions in real-time.
      - Collect the `progress` flow to drive animated state (e.g., scale, alpha, offset) during the back gesture. On completion, invoke the back callback; on cancellation (user lifts finger before threshold), let the view snap back:
        ```kotlin
        PredictiveBackHandler { progress ->
            try {
                progress.collect { backEvent ->
                    // Drive intermediate animation state from backEvent.progress (0f..1f)
                    // e.g., scale = lerp(1f, 0.9f, backEvent.progress)
                }
                // Gesture completed past threshold — navigate back
                onBack()
            } catch (_: CancellationException) {
                // Gesture canceled — reset to full view
            }
        }
        ```
      - The shared element system automatically participates in the predictive back gesture when `PredictiveBackHandler` is used — the shared elements smoothly animate back to their original list/grid positions as the user swipes.
      - **Never use `onBackPressed()` override or `BackHandler` for screens with shared transitions** — they bypass the predictive animation pipeline entirely.
    - **Navigation 3 Integration & State Restoration**:
      - In Navigation 3 `NavDisplay` / `entryProvider`, extract the current entry's animated scope via `LocalNavAnimatedContentScope.current` and pass it as the `animatedVisibilityScope` to screens and shared element modifiers.
      - Decouple and preserve type-safe `@Serializable` back stacks using `rememberSaveable` with `SaveableStateHolder` to guarantee state restoration across configuration changes and process recreation.
    - **Transitions for All List Items & Openable Components (Lazy Layouts, Grids, Loops, Carousels, Master-Detail)**:
      - Whenever any item or component in a list, grid, or collection (including `LazyColumn`, `LazyRow`, `LazyVerticalGrid`, `LazyHorizontalGrid`, `LazyLayout`, `FlowRow`, `FlowColumn`, standard collection loops, or carousels) can be opened into a detail screen, expanded dialog, bottom sheet, or preview:
      - **Do NOT manually wrap items in `AnimatedVisibility`**: Wrapping items in `AnimatedVisibility` introduces artificial layout nodes, disrupts item recycling/pooling in lazy containers, causes measurement jumping, and degrades scroll performance.
      - **Always use Caller-Managed Visibility Modifiers**: Apply `Modifier.sharedElementWithCallerManagedVisibility` or `Modifier.sharedBoundsWithCallerManagedVisibility` directly to the item/card and its elements:
        ```kotlin
        Modifier.sharedElementWithCallerManagedVisibility(
            sharedContentState: SharedTransitionScope.SharedContentState,
            visible: Boolean, // Pass !isDetailOpen (or check if detail is visible)
            boundsTransform: BoundsTransform = SharedTransitionDefaults.BoundsTransform,
            placeholderSize: SharedTransitionScope.PlaceholderSize = ContentSize,
            renderInOverlayDuringTransition: Boolean = true,
            zIndexInOverlay: Float = 0f,
            clipInOverlayDuringTransition: SharedTransitionScope.OverlayClip = ParentClip
        )
        ```
      - Passing `visible = !isDetailOpen` (or evaluation if the item is opened) gives the shared transition engine direct visibility control:
        - When the detail/expanded view is opened (`isDetailOpen == true`), `visible` becomes `false`: the shared element system renders the component in the transition overlay while hiding the source element without mutating the list layout hierarchy.
        - Upon back navigation or closing (`isDetailOpen == false`), `visible` returns to `true`, and the component smoothly animates back into its exact original position in the list or collection.
  - **Navigation**: Use Navigation Compose or Navigation 3 with type-safe destinations (`@Serializable` route definitions).
  - **Tactile & Haptic Feedback (`LocalHapticFeedback`)**:
    - **Accessing Haptic Performer**: In Compose UI, obtain the feedback performer via `val localHapticFeedback = LocalHapticFeedback.current`.
    - **Triggering Feedback**: Call `localHapticFeedback.performHapticFeedback(HapticFeedbackType.<Type>)`.
    - **Purpose-Specific `HapticFeedbackType` Selection**: Never use arbitrary feedback types. Map feedback strictly to user interaction purpose per the official Android specification:
      - `Confirm`: Signals confirmation or successful completion of a user interaction (e.g. successful form submission, save, or completed transaction).
      - `Reject`: Signals rejection or failure of a user interaction (e.g. validation failure, unauthorized action, declined payment, shake animation).
      - `ToggleOn`: User toggled a switch, checkbox, or button into the *on* / active position.
      - `ToggleOff`: User toggled a switch, checkbox, or button into the *off* / inactive position.
      - `LongPress`: User performed a long press resulting in an action being performed (e.g. context menu display, drag initiation).
      - `SegmentTick`: Switching between a discrete series of potential choices (e.g. discrete slider steps, item snapping in wheel/list pickers).
      - `SegmentFrequentTick`: Switching rapidly between many potential choices (e.g. clock face minutes, percentage scrubbers).
      - `GestureThresholdActivate`: Swipe/drag gesture reaching an activation threshold where an action becomes eligible (e.g. pull-to-refresh trigger threshold).
      - `GestureEnd`: Finished a gesture interaction (e.g. soft keyboard gestures, custom gesture canvas completion).
      - `ContextClick`: Performed a context/secondary click on an object.
      - `VirtualKey`: Pressed an on-screen virtual keypad key.
      - `KeyboardTap`: Pressed a soft keyboard key.
      - `TextHandleMove`: Selection or cursor insertion handle move in a text field.

#### Jetpack Compose Stability, Localization & LazyList Example:
```kotlin
package com.example.app.ui.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class UserItemUiModel(
    val id: String,
    val name: String,
    val email: String
)

@Immutable
sealed interface UserListUiState {
    data object Loading : UserListUiState
    data class Success(
        val users: ImmutableList<UserItemUiModel> = persistentListOf()
    ) : UserListUiState
    data class Error(val message: String) : UserListUiState
}

@Composable
fun UserListRoute(
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UserListScreen(
        uiState = uiState,
        onUserClick = onUserClick,
        modifier = modifier
    )
}

@Composable
fun UserListScreen(
    uiState: UserListUiState,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        when (uiState) {
            is UserListUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is UserListUiState.Success -> {
                val layoutDirection = LocalLayoutDirection.current
                // Edge-to-edge list: LazyColumn spans fillMaxSize() so scroll area is not cut off,
                // while contentPadding incorporates scaffold insets with element margins.
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                        end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                        top = innerPadding.calculateTopPadding() + 16.dp,
                        bottom = innerPadding.calculateBottomPadding() + 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.users,
                        key = { user -> user.id },
                        contentType = { "user_card" }
                    ) { user ->
                        UserCardItem(
                            user = user,
                            onClick = { onUserClick(user.id) }
                        )
                    }
                }
            }
            is UserListUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun UserCardItem(
    user: UserItemUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}
```

#### Shared Element Transitions with Navigation 3 and LazyList Example:
```kotlin
package com.example.app.ui.navigation

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.NavDisplay
import androidx.navigation3.LocalNavAnimatedContentScope
import androidx.navigation3.entryProvider
import coil3.compose.AsyncImage
import com.example.app.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.serialization.Serializable

@Serializable
data object ItemListRoute

@Serializable
data class ItemDetailRoute(val itemId: String)

@Immutable
data class ItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainSharedNavHost(
    items: ImmutableList<ItemUiModel>,
    modifier: Modifier = Modifier
) {
    // rememberSaveable survives configuration changes & process death
    var activeDetailId by rememberSaveable { mutableStateOf<String?>(null) }

    SharedTransitionLayout(modifier = modifier) {
        NavDisplay(
            backStack = rememberSaveable { listOf(ItemListRoute) },
            entryProvider = entryProvider {
                entry<ItemListRoute> {
                    val animatedScope = LocalNavAnimatedContentScope.current
                    ItemListScreen(
                        items = items,
                        selectedItemId = activeDetailId,
                        onItemClick = { activeDetailId = it },
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = animatedScope
                    )
                }
                entry<ItemDetailRoute> { route ->
                    val animatedScope = LocalNavAnimatedContentScope.current
                    val item = items.firstOrNull { it.id == route.itemId }
                    if (item != null) {
                        ItemDetailScreen(
                            item = item,
                            onBack = { activeDetailId = null },
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = animatedScope
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ItemListScreen(
    items: ImmutableList<ItemUiModel>,
    selectedItemId: String?,
    onItemClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    // Spring bounds transform for natural spatial motion
    val springBoundsTransform = BoundsTransform { _, _ ->
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }

    Scaffold(modifier = modifier) { innerPadding ->
        with(sharedTransitionScope) {
            val layoutDirection = LocalLayoutDirection.current
            // Edge-to-edge scrolling: LazyColumn fills screen; contentPadding applies scaffold insets + margin.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                    end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = items,
                    key = { it.id },
                    contentType = { "item_card" }
                ) { item ->
                    val isDetailOpen = selectedItemId == item.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .sharedBoundsWithCallerManagedVisibility(
                                sharedContentState = rememberSharedContentState(key = "card_${item.id}"),
                                visible = !isDetailOpen,
                                boundsTransform = springBoundsTransform
                            )
                            .clickable { onItemClick(item.id) }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {
                            AsyncImage(
                                model = item.imageUrl,
                                contentDescription = stringResource(R.string.desc_item_thumbnail, item.title),
                                placeholder = painterResource(R.drawable.placeholder_image),
                                error = painterResource(R.drawable.error_image),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = "image_${item.id}"),
                                        visible = !isDetailOpen,
                                        boundsTransform = springBoundsTransform
                                    )
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.sharedElementWithCallerManagedVisibility(
                                        sharedContentState = rememberSharedContentState(key = "title_${item.id}"),
                                        visible = !isDetailOpen,
                                        boundsTransform = springBoundsTransform
                                    )
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodyMedium
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
fun ItemDetailScreen(
    item: ItemUiModel,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier
) {
    // Android 14+ Predictive Back Gesture — shared elements animate back smoothly
    PredictiveBackHandler { progress ->
        try {
            progress.collect { backEvent ->
                // Shared elements automatically participate in the predictive back animation.
                // Optionally drive additional state from backEvent.progress (0f..1f)
            }
            onBack()
        } catch (_: CancellationException) {
            // Back gesture canceled by user — view stays in place
        }
    }

    val springBoundsTransform = BoundsTransform { _, _ ->
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    }

    with(sharedTransitionScope) {
        Card(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "card_${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = springBoundsTransform
                )
                .clickable { onBack() }
        ) {
            Column {
                // Full-width image — SAME key as list thumbnail for seamless animation
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = stringResource(R.string.desc_item_image, item.title),
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.error_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .sharedElement(
                            state = rememberSharedContentState(key = "image_${item.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = springBoundsTransform
                        )
                )
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.sharedElement(
                            state = rememberSharedContentState(key = "title_${item.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    )
                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
```

- **Legacy XML Layouts & Fragments**:
  - Always use **ViewBinding**; never use `findViewById` or deprecated synthetic bindings.
  - In `Fragment`, nullify `_binding` in `onDestroyView()` to prevent severe memory leaks.
  - Avoid nested weights in `LinearLayout` to prevent exponential measure passes (`O(2^n)`). Flatten nested hierarchies with `ConstraintLayout`.

#### Legacy ViewBinding Fragment Example:
```kotlin
package com.example.app.ui.legacy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.databinding.FragmentLegacyUserBinding

class LegacyUserFragment : Fragment(R.layout.fragment_legacy_user) {
    private var _binding: FragmentLegacyUserBinding? = null
    private val binding get() = checkNotNull(_binding) { "Binding accessed outside view lifecycle" }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLegacyUserBinding.bind(view)
        
        binding.btnSubmit.setOnClickListener {
            // View action
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

---

### Domain Layer & Kotlin Multiplatform (KMP) Readiness
- **Strict Framework Independence (100% Shared KMP Ready)**: The Domain Layer contains **pure business rules** and MUST NOT import `android.*` packages, Jetpack Compose runtime, Retrofit, Room, or serialization libraries (e.g. `kotlinx.serialization.@Serializable`, Gson, Moshi, Jackson).
  - This strict independence guarantees that the Domain layer is completely decoupled and ready to be placed directly into a Kotlin Multiplatform `commonMain` source set (`shared:domain`) shared across Android, iOS, Desktop, and Web without any architectural refactoring.
  - If `@Serializable` or database/network annotations are discovered on domain models, immediately flag it and offer a recommended plan to decouple them into Data Layer DTOs or Presentation routes.
- **Platform Capability Abstraction (`expect` / `actual` Pattern)**:
  - When platform-specific hardware or OS capabilities are required (e.g., biometric authentication, secure keystore/keychain, push notifications, local file system paths, camera/sensors):
    1. Declare clean pure-Kotlin interfaces or `expect` declarations inside the shared domain or core module.
    2. Provide concrete platform implementations via `actual` classes or separate platform data modules (`androidMain`, `iosMain`, `desktopMain`).
    3. Inject these abstractions via dependency injection so domain business rules remain 100% agnostic of the underlying operating system.
- **Typed Domain Error Modeling (No Raw `Result<T>`)**:
  - Avoid returning standard Kotlin `Result<T>` with untyped `Throwable` from UseCases and Repositories. Raw exceptions swallow business error context and leak transport/infrastructure failures (HTTP status codes, SQL exceptions) into presentation layers.
  - Standardize on a functional outcome wrapper:
    ```kotlin
    sealed interface AppResult<out T, out E : DomainError> {
        data class Success<out T>(val data: T) : AppResult<T, Nothing>
        data class Error<out E : DomainError>(val error: E) : AppResult<Nothing, E>
    }
    sealed interface DomainError
    ```
- **Single Responsibility UseCases**: Every UseCase encapsulates one specific user action or domain rule (e.g. `GetUserDataUseCase`, `VerifySessionUseCase`).
- Use the `operator fun invoke` pattern for intuitive execution.
- Define Repository abstractions (interfaces) in the Domain Layer, implemented in the Data Layer.

#### Domain Result & UseCase Example:
```kotlin
package com.example.app.domain.usecase

import com.example.app.domain.error.AppResult
import com.example.app.domain.error.UserDomainError
import com.example.app.domain.model.User
import com.example.app.domain.repository.UserRepository

class GetUserDataUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): AppResult<User, UserDomainError> {
        if (userId.isBlank()) {
            return AppResult.Error(UserDomainError.InvalidUserId)
        }
        return userRepository.getUser(userId)
    }
}
```

---

### Data Layer
- **Repository Pattern**: Acts as the single source of truth for domain data, orchestrating local cache and remote networks.
- **Local Persistence**: Use **Room** with type-safe DAOs and Kotlin `Flow` for reactive caching.
- **Networking**: Abstract HTTP clients with **Retrofit** or **Ktor**, mapping DTOs into pure domain models at repository boundaries.
- **Dispatchers**: Repositories must be main-safe (`withContext(Dispatchers.IO)` or `withContext(Dispatchers.Default)`) so callers can safely execute from the main thread.
- **Jetpack DataStore for Key-Value & Settings Storage (Mandatory — Never SharedPreferences)**:
  - **Always use Jetpack DataStore** (`Preferences DataStore` or `Proto DataStore`) for all key-value preference storage, user settings, feature flags, onboarding state, cached tokens, and simple app configuration.
  - **Never use `SharedPreferences`** — it blocks the UI thread on reads, has no safe async API, no built-in type safety, no reactive Flow observation, and is prone to data corruption on multi-process access.
  - **If existing code uses `SharedPreferences`**, proactively migrate it to DataStore. Use `SharedPreferencesMigration` for seamless migration:
    ```kotlin
    val Context.settingsDataStore by preferencesDataStore(
        name = "app_settings",
        produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, "legacy_prefs"))
        }
    )
    ```
  - **Preferences DataStore** (simple key-value): Use for flat settings like theme mode, notification toggle, last sync timestamp.
  - **Proto DataStore** (typed schemas): Use for structured settings with nested objects, enums, or lists that benefit from compile-time type safety via Protocol Buffers.
  - **Always expose DataStore reads as `Flow`** and collect with `collectAsStateWithLifecycle()` in Compose or `collect` in ViewModel — never use `.first()` on the main thread for synchronous reads except inside coroutines.

#### Repository Implementation Example:
```kotlin
package com.example.app.data.repository

import com.example.app.data.local.UserDao
import com.example.app.data.local.toDomainModel
import com.example.app.data.local.toEntity
import com.example.app.data.remote.UserApiService
import com.example.app.data.remote.toDomainModel
import com.example.app.domain.error.AppResult
import com.example.app.domain.error.UserDomainError
import com.example.app.domain.model.User
import com.example.app.domain.repository.UserRepository
import java.io.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val apiService: UserApiService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : UserRepository {

    override suspend fun getUser(userId: String): AppResult<User, UserDomainError> = withContext(ioDispatcher) {
        try {
            val cachedUser = userDao.getUserById(userId)?.toDomainModel()
            if (cachedUser != null) {
                return@withContext AppResult.Success(cachedUser)
            }

            val networkResponse = apiService.fetchUser(userId)
            val networkUser = networkResponse.toDomainModel()
            userDao.insertUser(networkUser.toEntity())
            AppResult.Success(networkUser)
        } catch (_: IOException) {
            AppResult.Error(UserDomainError.NetworkIssue)
        } catch (_: Exception) {
            AppResult.Error(UserDomainError.Unknown)
        }
    }
}
```

#### Offline-First Reactive Network Monitor Example:
```kotlin
package com.example.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.app.domain.network.NetworkMonitor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn

class ConnectivityManagerNetworkMonitor(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        if (connectivityManager == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }
            override fun onLost(network: Network) {
                trySend(false)
            }
            override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
                val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                  capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(hasInternet)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.flowOn(ioDispatcher).conflate()
}
```

- **R8 / ProGuard Minification Safety (Navigation 3 & Kotlinx Serialization)**:
  - When building release artifacts (`minifyEnabled = true`), R8 can strip or obfuscate synthetic `$serializer` companion objects and polymorphic subclasses required by Navigation 3 and Kotlinx Serialization.
  - Enforce ProGuard keep rules inside `proguard-rules.pro` to prevent runtime serialization crashes:
    ```proguard
    # Kotlinx Serialization Keep Rules for Navigation 3 Routes
    -keepattributes *Annotation*, InnerClasses
    -dontnote kotlinx.serialization.SerializationKt
    -keepclassmembers class * {
        *** Companion;
    }
    -keepclasseswithmembers class * {
        kotlinx.serialization.KSerializer serializer(...);
    }
    -keep,allowobfuscation @kotlinx.serialization.Serializable class *
    -keepclassmembers @kotlinx.serialization.Serializable class * {
        kotlinx.serialization.KSerializer serializer(...);
    }
    ```

---

## 3. Asynchronous Flow & Concurrency

### Kotlin Coroutines & Flow
- **Main Safety**: Every suspend function exposed from UseCases or Repositories must be safe to call from `Dispatchers.Main`.
- **Dispatcher Assignment**:
  - `Dispatchers.IO`: Blocking I/O, disk, database, network operations.
  - `Dispatchers.Default`: Heavy CPU computations, sorting large collections, JSON parsing.
  - `Dispatchers.Main.immediate`: Immediate UI updates without dispatch delay.
- **Idiomatic Duration Syntax (Enforced Everywhere)**:
  - Use `kotlin.time.Duration` extensions: `delay(250.milliseconds)`, `delay(2.seconds)`. Never pass raw un-typed `Long` values.
  - For retry/backoff delays, store the value as a `Duration` variable:
    ```kotlin
    var backoffDelay = 1.seconds
    repeat(maxRetries) {
        try { return doWork() } catch (_: IOException) {
            delay(backoffDelay)
            backoffDelay = (backoffDelay * 2).coerceAtMost(30.seconds)
        }
    }
    ```
  - For `SharingStarted.WhileSubscribed()`, pass Duration: `SharingStarted.WhileSubscribed(5.seconds)`.
- **Structured Concurrency**:
  - Never use `GlobalScope`. Always scope coroutines to `viewModelScope`, `lifecycleScope`, or an injected `CoroutineScope`.
  - Use `supervisorScope` or `SupervisorJob` when child failures should not cancel siblings.
  - Use `coroutineScope` for parallel decomposition where any failure should cancel all work.
  - Prefer `launch` for fire-and-forget side effects, `async`/`await` for parallel result composition.
- **Cancellation Compliance**:
  - Always use cancellable suspend functions (`delay`, `yield`, `withContext`). Long-running CPU loops must call `ensureActive()` or `yield()` periodically.
  - Use `NonCancellable` only for cleanup in `finally` blocks that must complete: `withContext(NonCancellable) { saveState() }`.
- **StateFlow vs SharedFlow**:
  - Use `StateFlow` for state that has a current value and emits updates to new subscribers.
  - Use `SharedFlow` or Channels with `Channel.BUFFERED` for one-time events (navigation, snackbar triggers) to prevent re-execution on configuration changes.
- **Flow Best Practices**:
  - Use `catch { }` operator for upstream error handling — never wrap `collect` in try/catch for flow errors.
  - Use `flowOn(dispatcher)` to shift upstream execution context — never use `withContext` inside a `flow { }` builder.
  - Use `distinctUntilChanged()` to avoid redundant emissions and recompositions.
  - Use `combine()` to merge multiple flows into a single UI state flow.
  - Use `flatMapLatest` when only the latest emission matters (e.g., search-as-you-type).

#### ViewModel State Flow Pattern:
```kotlin
package com.example.app.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.domain.error.AppResult
import com.example.app.domain.error.UserDomainError
import com.example.app.domain.model.User
import com.example.app.domain.usecase.GetUserDataUseCase
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

sealed interface UserUiState {
    data object Loading : UserUiState
    data class Success(val user: User) : UserUiState
    data class Error(val message: String) : UserUiState
}

class UserViewModel(
    private val getUserDataUseCase: GetUserDataUseCase
) : ViewModel() {

    val uiState: StateFlow<UserUiState> = flow {
        emit(UserUiState.Loading)
        delay(300.milliseconds)
        when (val result = getUserDataUseCase("current_user_id")) {
            is AppResult.Success -> emit(UserUiState.Success(result.data))
            is AppResult.Error -> {
                val errorMsg = when (result.error) {
                    UserDomainError.InvalidUserId -> "Invalid user identifier"
                    UserDomainError.NetworkIssue -> "Network connection failure"
                    UserDomainError.UserNotFound -> "User not found"
                    UserDomainError.Unknown -> "An unexpected error occurred"
                }
                emit(UserUiState.Error(errorMsg))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserUiState.Loading
    )

    fun loadUser() {
        // Refresh trigger
    }
}
```

---

## 4. Background Work & Modern Android Restrictions (API 26 to 35+)

### WorkManager vs Foreground Services
- **WorkManager**: Primary solution for deferrable or persistent tasks (data sync, periodic cleanup, batch uploads). Survives process death and reboots.
- **Foreground Service**: Reserved strictly for user-visible, immediate actions that must not be interrupted (audio playback, active GPS navigation, active VoIP calls, ongoing screen recording).

### Foreground Service Execution Checklist (Android 14+ / API 34+):
1. **Declare Type**: Specify `android:foregroundServiceType` in `<service>` in `AndroidManifest.xml` (e.g. `dataSync`, `mediaPlayback`, `location`).
2. **Permissions**:
   - Android 9 (API 28): `FOREGROUND_SERVICE`
   - Android 14 (API 34): Specific type permission (e.g. `FOREGROUND_SERVICE_DATA_SYNC`)
   - Android 13 (API 33): Request runtime `POST_NOTIFICATIONS` before posting notifications.
3. **5-Second Window**: When started via `ContextCompat.startForegroundService()`, call `ServiceCompat.startForeground()` within **5 seconds** to prevent an ANR/`ForegroundServiceDidNotStartInTimeException`.

#### Production Foreground Service Example with Version Branching:
```kotlin
package com.example.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.app.R

class TaskProcessingService : Service() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val notification = buildNotification(getString(R.string.service_processing_task))

        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            serviceType
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(contentText: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.service_notification_title))
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.service_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.service_channel_description)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "task_service_channel"
    }
}
```

---

## 5. Java Interoperability & Legacy Safety

When working on mixed Kotlin-Java codebases or maintaining legacy Java blocks:
- **Explicit Nullability Annotations**: Annotate all Java method parameters and return types with `@NonNull` or `@Nullable` from `androidx.annotation`.
- **Platform Types**: Prevent Kotlin from inferring unannotated Java types as ambiguous platform types (`T!`), which can lead to sudden `NullPointerException` crashes.
- **Exceptions**: Use `@Throws` annotation in Kotlin when a method throws checked exceptions consumed by Java.
- **Jvm Annotations**: Use `@JvmStatic`, `@JvmOverloads`, and `@JvmField` appropriately to keep Kotlin APIs idiomatic and performant for Java callers.

#### Java Nullability Safety Example:
```java
package com.example.app.legacy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class LegacyDataFormatter {

    private LegacyDataFormatter() {}

    @NonNull
    public static String formatName(@Nullable String firstName, @NonNull String lastName) {
        if (lastName == null) {
            throw new NullPointerException("lastName cannot be null");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            return lastName.trim();
        }
        return firstName.trim() + " " + lastName.trim();
    }
}
```

---

## 6. Dependency Injection (Dagger Hilt)

- **Standard Component Scoping**:
  - `@Singleton`: Application lifecycle. Use sparingly to conserve memory.
  - `@ActivityRetainedScoped`: Retained across configuration changes (e.g. repositories shared among activity screens).
  - `@ViewModelScoped`: Retained for a specific `ViewModel` instance lifecycle.
- **Interface Binding**: Prefer `@Binds` over `@Provides` when mapping an interface to an implementation class; it reduces generated code overhead.
- **Context Injection**: Use `@ApplicationContext` when application-level context is needed to prevent Activity memory leaks.

```kotlin
package com.example.app.di

import com.example.app.data.repository.UserRepositoryImpl
import com.example.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
```

---

## 7. Testing & Quality Assurance

- **Unit Testing**:
  - ViewModels & UseCases: Test state transitions and failure conditions using `kotlinx-coroutines-test` (`StandardTestDispatcher` and `runTest`).
  - Use `Turbine` for testing Kotlin `Flow` and `StateFlow` emissions.
- **Edge Cases**:
  - Always verify screen behavior during process death and configuration changes.
  - Assert HTTP error codes (401 Unauthorized, 404, 500) and network disconnect states.
