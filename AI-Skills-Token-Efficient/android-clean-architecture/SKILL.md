---
name: android-clean-architecture
description: "Token-efficient Clean Architecture: Layer boundaries, KMP domain isolation, Hilt DI scopes, and Navigation 3."
---

# Android Clean Architecture (Token-Efficient)

## Layer Boundaries & Isolation
1. **Flow:** `Feature (UI/ViewModel) -> Domain (Use Cases/Interfaces) -> Data (Repositories/DataSources)`.
2. **Domain Isolation:** NEVER import `android.*`, Compose runtime, Retrofit, Room, `@Serializable`, Gson, or Moshi in `domain`. Domain is 100% pure Kotlin, ready for KMP (`commonMain`).
3. **No Domain UI Annotations:** Never use `@Immutable` or `@Stable` in domain. Use `kotlinx.collections.immutable` collections.
4. **Typed Errors:** Return `sealed interface AppResult<out T, out E : DomainError>` from UseCases and Repositories. Avoid raw `Result<Throwable>`.
5. **Platform Capabilities:** Abstract OS-level features (biometrics, sensors, secure storage) behind pure domain interfaces; implement in `data` or platform modules via DI or `expect`/`actual`.

## Dagger Hilt Standards
1. **Modules:** Declare `@Module` and `@InstallIn` in the module providing concrete implementations.
2. **Binding:** Prefer `@Binds` over `@Provides` for interface-to-implementation mapping.
3. **Scopes:** Restrict `@Singleton` to app-wide singletons. Use `@ViewModelScoped` for ViewModel lifecycles.

## Compose Navigation 3
1. **Type-Safe Routes:** Define destinations with `@Serializable` data classes/objects (`NavKey`). Never use string route paths.
2. **Animated Scope:** In `NavDisplay` / `entryProvider`, pass `LocalNavAnimatedContentScope.current` as `animatedVisibilityScope` to screens for shared transitions.
3. **State Preservation:** Retain back stacks using `rememberSaveable` with `SaveableStateHolder`.
