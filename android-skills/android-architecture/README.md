# 🏛️ Android Clean Architecture Skill

The **Android Clean Architecture Skill** enforces strict layer boundaries, pure Kotlin domain models ready for Kotlin Multiplatform (KMP `commonMain`), decoupled reactive data sources, Dagger Hilt dependency injection, and comprehensive KDocs.

---

## 📂 Subfolder Structure

```
android-architecture/
├── SKILL.md                 # Agent entry point & core architectural rules
├── README.md                # Human & Agent guide (this file)
└── instructions/            # Detailed reference guides & workflows
    ├── guidelines.md        # Boundary rules, KMP compliance, Hilt DI patterns
    └── examples.md          # Complete UseCase, Repository, and Mapper implementations
```

---

## ⚡ How to Trigger

```text
/android-architecture design an offline-first repository with pure KMP domain models
```
```text
/android-architecture refactor this ViewModel to extract pure UseCases with single-expression invoke
```

---

## 📋 Architectural Layers Overview

| Layer | Responsibility | Framework Allowed |
|---|---|---|
| **Domain Layer** | Business logic, UseCases, Domain Entities, Value Classes | Pure Kotlin only (Zero `android.*`) |
| **Data Layer** | Room Database, Retrofit Network, DataStore, DTO Mappers | AndroidX, Room, Retrofit, Ktor |
| **Presentation Layer** | ViewModels, StateFlow, Navigation, Compose UI | Jetpack Compose, Lifecycle, Hilt |

---

## 🔗 Connected Skills
- **UI Presentation**: Hands off state to [`compose-ui`](../compose-ui/README.md)
- **Unit Testing**: Verified via [`android-testing`](../android-testing/README.md)
- **Build Configurations**: Governed by [`gradle-build-hygiene`](../gradle-build-hygiene/README.md)
- **Architecture Records**: Documented via [`adr-analyze`](../../diagnostic-skills/adr-analyze/README.md)
