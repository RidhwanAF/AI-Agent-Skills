# 📱 Android Engineering Skills Suite

Welcome to the **Android Engineering Skills Suite**. Rather than bundling all capabilities into a single monolithic skill, this module provides **8 specialized, modular skills** that collaborate seamlessly to enforce enterprise Clean Architecture, pure KMP-ready business logic, modular Jetpack Compose UI decomposition, Google Maps and MapLibre geospatial mapping, Dagger Hilt & Koin dependency injection, automated testing, official Google CLI dispatch, and official-docs-grounded Gradle hygiene.

---

## 🧭 Navigation & Connected Skills Hub

| Hub Directory | Domain | Direct Link |
|---|---|---|
| 🏠 **Root Hub** | Full Suite Overview & Universal Installer | [Root README](../README.md) |
| 🚀 **Engineer Mode** | Master Entry Point & Adaptive Playbook Router | [Engineer Mode README](../engineer-mode/README.md) |
| ⚛️ **React TS Skills** | Next.js, Vite, TypeScript, Tailwind, Web Vitals | [React TS README](../react-ts-skills/README.md) |
| 🔍 **Diagnostic Skills** | Error Detective, ADR Detection, App Design, Changelogs | [Diagnostic README](../diagnostic-skills/README.md) |
| 🌿 **Git Skills** | Branching, Safe Rebase, Conflict Resolution, PRs | [Git README](../git-skills/README.md) |

---

## 📦 Modular Android Skills Matrix

| Skill Directory | Slash Command | Primary Purpose | Deliverables |
|---|---|---|---|
| [**`android-architecture/`**](./android-architecture/README.md) | `/android-architecture` | Enforce Clean Architecture with pure Kotlin domain (zero `android.*` framework code, KMP-ready), Room/Retrofit data layer, Hilt DI, and KDocs. | Domain UseCases, Repository contracts, Room DAOs |
| [**`compose-ui/`**](./compose-ui/README.md) | `/compose-ui` | Modern Jetpack Compose UI. Enforces multi-file decomposition (never 1 giant file), strict modifier parameter rules, state stability, and haptics. | Screen, Content, Component, and Preview files |
| [**`compose-google-maps/`**](./compose-google-maps/README.md) | `/compose-google-maps` | Official Google Maps SDK in Compose: `GoogleMap`, `rememberCameraPositionState`, marker clustering via `maps-compose-utils`, cloud/JSON styling. | GoogleMap composables, marker clustering, overlay controls |
| [**`compose-maplibre/`**](./compose-maplibre/README.md) | `/compose-maplibre` | MapLibre Native in Compose with lifecycle safety, GeoJSON caching, Material 3 overlays, and camera animations. | MapView composable wrappers and overlays |
| [**`koin-di/`**](./koin-di/README.md) | `/koin-di` | Pragmatic Dependency Injection for Android & KMP. Enforces Constructor DSL (`singleOf`, `viewModelOf`), Compose `koinViewModel()`, and `verify()` testing. | Koin modules, Compose ViewModel injection, graph verification tests |
| [**`android-testing/`**](./android-testing/README.md) | `/android-testing` | Automated test engineering: Turbine for Coroutines Flow, MockK/Fakes for UseCases, ComposeTestRule semantic UI tests. | Unit and UI test suites |
| [**`gradle-build-hygiene/`**](./gradle-build-hygiene/README.md) | `/gradle-build-hygiene` | Version Catalog (`libs.versions.toml`) management, R8/ProGuard rules, official Google AGP documentation grounding without guesswork. | `libs.versions.toml`, `build.gradle.kts` |
| [**`android-cli-router/`**](./android-cli-router/README.md) | `/android-cli-router` | Dispatches high-level workflows to the official Google `android-cli` binary and official skill (`android doctor`, `android init`, `android run`, `android doc`). | Official CLI command execution, virtual device management |

---

## 🏗️ Architecture Blueprint: Pure Domain & KMP Readiness

```mermaid
flowchart TD
    subgraph PresentationLayer ["🎨 Presentation: /compose-ui"]
        Screen["Screen Container (ViewModel Observer)"]
        Components["Modular Child Components (Cards, Bars)"]
        Screen --> Components
    end

    subgraph DomainLayer ["🧠 Pure Domain: /android-architecture (KMP commonMain)"]
        direction TB
        UC["UseCase (Single-expression invoke)"]
        Entity["Pure Domain Models & Value Classes"]
        RepoContract["Repository Interface"]
        UC --> Entity
        UC --> RepoContract
    end

    subgraph DataLayer ["💾 Data Layer: /android-architecture"]
        RepoImpl["Repository Implementation"]
        RoomDB[("Room Database (Local)")]
        RetrofitAPI["Retrofit API (Remote)"]
        Mappers["Single-Expression Extension Mappers"]
        RepoImpl --> RoomDB
        RepoImpl --> RetrofitAPI
        RepoImpl --> Mappers
    end

    Screen -->|Executes| UC
    RepoImpl -.->|Implements| RepoContract
```

---

## 🎨 Compose UI: File Separation & Modifier Ordering

### 1. Multi-File Decomposition (Never 1 Giant File)
Each Compose screen must be decomposed into dedicated, testable files:
- `CartScreen.kt`: Stateful container handling ViewModel observation.
- `CartContent.kt`: Stateless layout organizing child components.
- `CartItemCard.kt`: Isolated item card component in `components/`.
- `CartUiState.kt`: Immutable state class with immutable collections.
- `CartPreview.kt`: Light and dark mode previews.

### 2. The Modifier Rule (Declaration Top vs. Call-Site Bottom)
- **In Composable Function Declaration (`@Composable fun`)**: `modifier: Modifier = Modifier` MUST be placed at the **very first top parameter (`#1`)** of the function signature — **never** at the bottom of the parameters!
- **At the Call Site (Consumption)**: Pass business data and callbacks first, and place the `modifier = Modifier...` chain at the **very bottom of the call arguments** so data arguments stay visible at the top:

```kotlin
// ✅ Declaration: modifier is at the VERY TOP (#1 parameter)
@Composable
fun CartItemCard(
    modifier: Modifier = Modifier,      // 1. VERY TOP parameter in declaration!
    item: CartItemUiModel,
    onDeleteClick: (String) -> Unit,
    isHighlighted: Boolean = false,
) { ... }

// ✅ Call site (Consumption): modifier is passed at the VERY BOTTOM
CartItemCard(
    item = currentItem,
    onDeleteClick = { viewModel.onEvent(CartEvent.Delete(it)) },
    isHighlighted = true,
    modifier = Modifier                 // VERY BOTTOM of call-site arguments!
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
)
```

### 3. Hilt ViewModel Non-Deprecated Compose Import
- **ALWAYS import `hiltViewModel` from**:
  ```kotlin
  import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
  ```
- **NEVER use the deprecated import**: `import androidx.hilt.navigation.compose.hiltViewModel`.

### 4. Strict Kotlin & Lint Hygiene (Enforced Automatically by `/engineer-mode`)
- **Android KTX Extensions (`UseKtx`)**: Always use `uriString.toUri()` (`import androidx.core.net.toUri`) instead of `Uri.parse(uriString)`, and `hex.toColorInt()` (`import androidx.core.graphics.toColorInt`) instead of `Color.parseColor(hex)`.
- **Top-Level Imports (Zero Inline FQNs)**: Never write `kotlin.math.abs(x)` or `kotlin.math.max(a, b)` inline; always add `import kotlin.math.abs` at the top of the file and call `abs(x)`.
- **Functional Iteration**: Prefer `.forEach { ... }`, `.forEachIndexed { ... }`, `.map { ... }`, and `.filter { ... }` over imperative `for` loops.
- **Zero Unused Code & Redundant `@OptIn`**: Remove all unused imports (no wildcard `import foo.*`), unused parameters (use `_` in lambdas), and unnecessary `@OptIn(Experimental...)` annotations.
- **Automated Lint Check & Fix**: Whenever `/engineer-mode` runs on an Android project, it automatically executes `./gradlew lintDebug` (and compiler checks) and fixes all reported lint/compiler issues before completing.

---

## 🛠️ Google Android CLI Routing & Live ADB App Logcat Analysis

When you need device automation, emulator execution, live app log analysis, or SDK inspection, the Android skills route directly to Google's official `android-cli` and active ADB telemetry:
- **Active ADB App Logcat Capture**: Automatically checks `adb devices -l`, resolves the active app PID (`adb shell pidof -s <applicationId>` or foreground activity via `adb shell dumpsys window | grep -E 'mCurrentFocus|mFocusedApp'`), and captures scoped app logs (`adb logcat -d --pid=<PID> -v threadtime` and `adb logcat -d -b crash`) whenever a device/emulator is connected.
- Run APK on device: `android run`
- Health check: `android doctor`
- Layout & visual inspection: `android layout`, `android screen`
- Official docs search: `android docs search "<query>"`

---

## ⚡ How to Trigger

```text
/android-architecture design an offline-first repository with pure KMP domain models
```
```text
/compose-ui decompose a checkout screen into modular component files with strict modifier ordering
```
```text
/compose-maplibre render GeoJSON polygon layers with lifecycle-safe MapView in Compose
```
```text
/android-testing generate Turbine unit tests for CartViewModel and a ComposeTestRule UI test
```
```text
/gradle-build-hygiene update libs.versions.toml to Kotlin 2.0.21 referencing official docs
```
