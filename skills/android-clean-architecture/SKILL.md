---
name: android-clean-architecture
description: Enforces Clean Architecture boundaries, multi-module dependency rules, Dagger Hilt DI scope management, and Navigation 3 type-safe routes.
---

# Clean Architecture & Core Design Patterns

## Dependency Direction & Module Isolation
1. **Dependency Flow:** Strictly enforce `Feature (UI/ViewModel) -> Domain (Use Cases/Interfaces) -> Data (Repositories/DataSources)`.
2. **Domain Isolation:** `domain` modules MUST NEVER import `android.*` framework packages, `androidx.compose.runtime.*`, Retrofit annotations, Room annotations, serialization annotations (`@Serializable`, Gson, Moshi, Jackson), or concrete data-layer classes. If any such leak is detected, proactively flag it and provide a recommended remediation plan to decouple it.
3. **Domain Entity Stability:** Do NOT use `@Immutable` or `@Stable` annotations in the `domain` module. Achieve Compose stability for domain models using `kotlinx.collections.immutable` collections or by referencing domain packages in `compose_compiler_config.conf`.
4. **Typed Domain Errors:** Use cases and repository interfaces in `domain` must return typed functional outcomes (`AppResult<T, E : DomainError>`) rather than standard `Result<T>` with untyped `Throwable` exceptions.
5. **Kotlin Multiplatform (KMP) Readiness & `expect`/`actual`:** Design domain modules as 100% pure Kotlin so they can transition directly into shared `commonMain` multiplatform modules (`shared:domain`). Abstract platform capabilities (biometrics, secure storage, sensors, notifications) behind pure domain interfaces or `expect` declarations, implementing them via `actual` classes in platform-specific modules (`androidMain`, `iosMain`).
6. **Reactive Offline-First Monitoring:** Expose network state through a pure domain `NetworkMonitor` interface (`val isOnline: Flow<Boolean>`), implemented in `data` via `ConnectivityManager.NetworkCallback` to drive offline UX reactively.

## Dagger Hilt DI Standards
1. **Module Location:** Declare `@Module` and `@InstallIn` strictly inside the module providing the concrete implementation.
2. **Interface Binding:** Expose domain interfaces across module boundaries and bind them via `@Binds` or `@Provides` inside `data` or feature modules.
3. **Scope Guardrails:** Restrict `@Singleton` strictly to application-wide singletons. Use `@ViewModelScoped` or `@ActivityRetainedScoped` appropriately to prevent memory leaks.

## Compose Navigation 3 & Animations
1. **Type-Safety:** Use type-safe destinations (`NavKey`) with `@Serializable` objects or data classes using `kotlinx.serialization`. Never use string-based URI route paths.
2. **Animated Scope & Shared Element Transitions:** Inside `NavDisplay` / `entryProvider`, extract the current animated scope using `LocalNavAnimatedContentScope.current` and pass it as the `animatedVisibilityScope` to screens and `Modifier.sharedElement` / `Modifier.sharedBounds`.
3. **Adaptive Multi-Pane Scaffolding:** For List-Detail flows, pair `NavDisplay` with `rememberListDetailSceneStrategy()` or `NavigableListDetailPaneScaffold` to adapt smoothly between single-pane on phones and side-by-side dual panes on tablets and foldables.
4. **BackStack State Restoration:** Decouple and preserve type-safe back stacks with `rememberSaveable` and `SaveableStateHolder` to guarantee navigation and screen states survive process recreation.
5. **State Hoisting:** Keep navigation logic hoisted outside feature composables via event lambdas `(UIEvent) -> Unit` or dedicated NavHost handlers.