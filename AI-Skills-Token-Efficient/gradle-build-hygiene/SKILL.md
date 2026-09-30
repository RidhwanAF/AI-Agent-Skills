---
name: gradle-build-hygiene
description: "Token-efficient build hygiene: Version catalog (libs.versions.toml), R8/ProGuard keep rules, and Room migrations."
---

# Gradle & Build Hygiene (Token-Efficient)

## Version Catalog (`libs.versions.toml`)
1. **No Inline Dependencies:** Always declare versions in `[versions]`, libraries in `[libraries]`, and reference via `libs.<alias>` in `build.gradle.kts`.
2. **Plugins:** Declare plugins in `[plugins]` and apply via `alias(libs.plugins.<alias>)`.

## ProGuard & R8 Safety
Protect `@Serializable` models and Navigation 3 routes in `proguard-rules.pro`:
```proguard
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keepclassmembers class * { *** Companion; }
-keepclasseswithmembers class * { kotlinx.serialization.KSerializer serializer(...); }
-keep,allowobfuscation @kotlinx.serialization.Serializable class *
-keepclassmembers @kotlinx.serialization.Serializable class * { kotlinx.serialization.KSerializer serializer(...); }
```

## Room Database Migrations
1. Always increment `@Database(version = ...)` on entity changes.
2. Provide explicit `Migration` or `AutoMigration`. NEVER use `fallbackToDestructiveMigration()` in production.
