# 💉 Koin DI Guidelines

Standards, version catalog setup, and best practices for Koin in modern Android and Compose applications.

---

## 🎯 Implementation Protocols

1. **Gradle Dependency Setup (`libs.versions.toml`)**:
   Always declare Koin dependencies via Version Catalog:
   ```toml
   [versions]
   koin = "4.0.0"

   [libraries]
   koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
   koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
   koin-androidx-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koin" }
   koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }
   koin-test = { module = "io.insert-koin:koin-test", version.ref = "koin" }
   koin-test-junit4 = { module = "io.insert-koin:koin-test-junit4", version.ref = "koin" }
   ```

2. **Constructor DSL Over Manual Get**:
   - Prefer `singleOf(::MyRepoImpl) { bind<MyRepo>() }` over `single<MyRepo> { MyRepoImpl(get(), get()) }`.
   - Prefer `viewModelOf(::MyViewModel)` over `viewModel { MyViewModel(get()) }`.
   - Only drop back to lambda `single { ... }` when invoking third-party builders (e.g. `Room.databaseBuilder()`, `Retrofit.Builder().build()`).

3. **Application Lifecycle Setup**:
   Initialize Koin inside `Application.onCreate()`:
   ```kotlin
   class MainApplication : Application() {
       override fun onCreate() {
           super.onCreate()
           startKoin {
               androidLogger(Level.DEBUG)
               androidContext(this@MainApplication)
               modules(appModules)
           }
       }
   }
   ```

4. **Jetpack Compose Consumption**:
   - Use `val viewModel: MyViewModel = koinViewModel()` in screen container composables.
   - Always place `modifier: Modifier = Modifier` at the **very top** of the Composable parameter list, followed by `viewModel: CartViewModel = koinViewModel()` for effortless `@Preview` mocking:
     ```kotlin
     @Composable
     fun CartScreen(
         modifier: Modifier = Modifier, // ✅ Declaration: VERY TOP parameter
         viewModel: CartViewModel = koinViewModel(),
     )
     ```

5. **Static Verification in Unit Tests**:
   - Write a dedicated verification test using `modules.verify()` from `koin-test`.
   - Specify extra types (`Context::class`, `SavedStateHandle::class`) so the static analysis passes cleanly.
