# Gradle Build Hygiene Guidelines

## 1. Grounding in Official Google Documentation (No Hallucinations)
1. **Authoritative Sources**:
   - Android Gradle Plugin Release Notes: `developer.android.com/build/releases/gradle-plugin`
   - Compose to Kotlin Mapping: `developer.android.com/jetpack/androidx/releases/compose-kotlin`
   - Google Maven Repository: `maven.google.com`
2. **Compatibility Matrices**:
   - Verify AGP, Gradle Wrapper, and Kotlin compiler versions form a supported set.
   - For Kotlin 2.0+, always use the official Compose Compiler Gradle plugin: `org.jetbrains.kotlin.plugin.compose`.

## 2. Centralized Version Catalog (`gradle/libs.versions.toml`)
1. Never hardcode dependencies in `build.gradle.kts`.
2. Group related dependencies into bundles (e.g. `libs.bundles.compose = ["compose-ui", "compose-material3"]`).
3. Follow naming conventions: lowercase with hyphens for TOML, mapped to camelCase accessors in Gradle.

## 3. R8 / ProGuard Keep Rules
1. Never use blanket wildcard `-keep class com.example.** { *; }`.
2. Apply keep rules strictly to reflection boundaries (e.g. JNI bindings or reflection-based JSON serializers).
