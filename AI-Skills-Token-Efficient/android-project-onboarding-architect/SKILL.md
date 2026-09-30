---
name: android-project-onboarding-architect
description: "Token-efficient project initializer: Applies standard Android stack defaults quickly without conversational overhead."
---

# Android Project Onboarding (Token-Efficient)

## Standard Stack Defaults
- **UI:** Jetpack Compose (Material 3), Edge-to-Edge (API 35+)
- **Architecture:** Clean Architecture + MVVM + Multi-Module
- **Navigation:** Compose Navigation 3 with `@Serializable`
- **Network & Serialization:** Retrofit + OkHttp + `kotlinx.serialization`
- **Persistence:** Room Database + Jetpack DataStore (Preferences/Proto)
- **DI:** Dagger Hilt
- **Build:** Gradle Version Catalog (`libs.versions.toml`)

## Protocol
1. Check for `.project-config.json` at project root.
2. If present, adhere strictly to its configured stack.
3. If absent, apply the standard stack defaults above immediately to avoid consuming tokens with multi-step interactive wizards, unless custom requirements are specified by the user.
