# Clean Architecture Guidelines (KMP-Ready)

## 1. Domain Layer Rules (Pure Kotlin)
1. **Zero Framework Dependencies**:
   - Strictly prohibit any import starting with `android.*`.
   - Never import `android.content.Context`, `android.os.Bundle`, or `android.util.Log`.
   - Never use Android lifecycle annotations or view models in the domain layer.
2. **KMP Compatibility**:
   - All domain entities, UseCases, and repository interfaces must compile under Kotlin Multiplatform `commonMain`.
   - Use standard Kotlin libraries only: `kotlinx.coroutines`, `kotlinx.datetime`, `kotlin.time.Duration`.
3. **Single-Expression UseCases**:
   - Model UseCases as single-purpose interactors with `operator fun invoke(...)`.
   - Prefer single-expression bodies: `operator fun invoke(id: UserId): Flow<User> = repository.observeUser(id)`.

## 2. Data Layer Rules (Encapsulation & Persistence)
1. **Information Hiding**:
   - Room `@Entity`, `@Dao`, and Retrofit API services must remain `internal` to the data layer.
   - Domain layer must only see the clean repository interface.
2. **One-Liner Extension Mappers**:
   - Never pass raw API DTOs or database entities to the domain layer.
   - Use extension functions: `fun UserDto.toDomain(): User = User(...)`.
3. **Jetpack DataStore**:
   - Use `PreferencesDataStore` or `ProtoDataStore` for preferences. `SharedPreferences` is deprecated.

## 3. Dependency Injection (Dagger Hilt)
1. Bind repository implementations using `@Binds` in abstract Hilt modules.
2. Use `@Singleton` for repositories and Room database instances.
3. Use `@ViewModelScoped` for presentation-specific state holders.
4. **Hilt Compose ViewModel Import Rule**:
   - Strictly use `import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel`.
   - Never use the deprecated `import androidx.hilt.navigation.compose.hiltViewModel`.

## 4. KDocs Documentation Standard
- Document every public interface, class, and method.
- Explain parameter requirements, invariants, and threading context.
