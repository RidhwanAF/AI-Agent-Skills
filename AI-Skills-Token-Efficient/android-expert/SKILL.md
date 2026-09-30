---
name: android_expert
description: "Token-efficient Android engineering rules: Compose, Coroutines, Duration API, Coil shared elements, PredictiveBackHandler, DataStore, and clean architecture."
---

# Android Expert (Token-Efficient)

## Agent Execution Directives
- **Direct Code First:** Provide complete, working code immediately. Do not perform exploratory web or repository searches unless explicitly requested or critical project signatures are missing.
- **Zero Fluff:** Omit preambles, meta-commentary, conversational filler, and restating the prompt. Go straight to the solution.
- **No Verbose Comments:** Write self-documenting code. Never add robotic comments explaining obvious syntax.
- **Strip Dead Code:** Delete unused imports, dead functions, orphaned parameters, and commented-out code blocks immediately.

---

## Modern Kotlin Syntax & Hygiene
- **Duration API for ALL Time:** Always import `kotlin.time.Duration.Companion.*`. Never use raw integer milliseconds.
  ```kotlin
  // Literal or variable
  val retryDelay = 3.seconds
  delay(retryDelay)
  delay(500.milliseconds)
  withTimeout(5.seconds) { ... }
  val timeoutMs = 15.minutes.inWholeMilliseconds
  ```
- **Looping:** Prefer `collection.forEach { ... }` or `collection.forEachIndexed { idx, it -> ... }` over imperative index loops. Use destructuring for maps: `map.forEach { (k, v) -> ... }`.
- **Deprecation Avoidance:** Never use deprecated APIs (`onBackPressed`, `AsyncTask`, `SharedPreferences`, `Handler.postDelayed`). When platform fallbacks are required, gate with `Build.VERSION.SDK_INT`:
  ```kotlin
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      intent.getParcelableExtra(key, MyData::class.java)
  } else {
      @Suppress("DEPRECATION")
      intent.getParcelableExtra(key) as? MyData
  }
  ```
- **Preconditions & Nulls:** Use `require()`, `check()`, `?.let { }`. Prefer `sealed interface` and `data object`.

---

## Jetpack Compose & UI Architecture
- **State Collection:** Collect `StateFlow` in Composables via `collectAsStateWithLifecycle()`.
- **Edge-to-Edge (Android 15+):** Scaffold padding must be handled via `innerPadding` in `contentPadding` of lazy containers (`fillMaxSize()`), never clipping outer modifiers.
- **DataStore Only:** Use Jetpack DataStore (Preferences or Proto). NEVER `SharedPreferences`. Expose data as `Flow`.

---

## Shared Element Animations & Coil
- **Identical & Unique Keys:** Source and destination MUST use identical keys with unique item IDs:
  - Cards/Containers: `rememberSharedContentState(key = "card_${item.id}")`
  - Images: `rememberSharedContentState(key = "image_${item.id}")`
  - Text: `rememberSharedContentState(key = "title_${item.id}")`
- **Applicable Views:** List-to-detail, tap-to-dialog, bottom sheet expansions, and fullscreen overlays.
- **Coil (`AsyncImage`):** Remote/local images must use Coil's `AsyncImage` and participate in shared element transitions:
  ```kotlin
  // Source (List/Thumbnail)
  AsyncImage(
      model = item.imageUrl,
      contentDescription = stringResource(R.string.desc_item_image, item.title),
      contentScale = ContentScale.Crop,
      modifier = Modifier
          .size(80.dp)
          .clip(RoundedCornerShape(8.dp))
          .sharedElementWithCallerManagedVisibility(
              sharedContentState = rememberSharedContentState(key = "image_${item.id}"),
              visible = !isDetailOpen
          )
  )

  // Destination (Detail/Fullscreen)
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
- **PredictiveBackHandler:** Mandatory on detail/dialog screens with transitions. Animate intermediate progress smoothly back to the origin:
  ```kotlin
  PredictiveBackHandler { progress ->
      try {
          progress.collect { backEvent -> /* drive transition */ }
          onBack()
      } catch (_: CancellationException) {
          // Gesture cancelled; reset state
      }
  }
  ```

---

## Clean Architecture & Data Flow
- **Domain Independence:** Domain must contain pure Kotlin only. No `android.*`, Jetpack Compose, Retrofit, Room, or `@Serializable` annotations.
- **Typed Errors:** Return `AppResult<T, E : DomainError>` from UseCases and Repositories instead of raw untyped `Result<Throwable>`.
- **Main Safety:** All suspend functions in repositories/use cases must be main-safe (`withContext(Dispatchers.IO)`).
