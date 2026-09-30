---
name: gradle-build-hygiene
description: Enforces version catalog (libs.versions.toml) management, R8/ProGuard safety, and Room database migrations.
---

# Gradle & Build Hygiene SOP

## Version Catalog (libs.versions.toml)
1. **Zero Inline Dependencies:** Never hardcode library coordinates or versions directly inside `build.gradle.kts` files.
2. **Catalog Flow:** When adding a new dependency:
   - Define the version in `[versions]` inside `gradle/libs.versions.toml`.
   - Define the library in `[libraries]` inside `gradle/libs.versions.toml`.
   - Reference it in `build.gradle.kts` using `libs.<alias>`.

## ProGuard & R8 Obfuscation Protection
1. **Serializable Data Classes & Navigation 3:** Ensure all classes annotated with `@Serializable` (for Network DTOs or Navigation 3 destinations) are protected from R8 stripping:
   ```proguard
   -keepattributes *Annotation*, InnerClasses
   -dontnote kotlinx.serialization.SerializationKt
   -keepclassmembers class * {
       *** Companion;
   }
   -keepclasseswithmembers class * {
       kotlinx.serialization.KSerializer serializer(...);
   }
   -keep,allowobfuscation @kotlinx.serialization.Serializable class *
   -keepclassmembers @kotlinx.serialization.Serializable class * {
       kotlinx.serialization.KSerializer serializer(...);
   }
   ```
2. **Keep Rules:** Verify obfuscation settings in `proguard-rules.pro` whenever adding custom Reflection, Room TypeConverters, or Serialization logic.

## Room Database Migrations
1. **Schema Changes:** Never modify a Room `@Entity` without incrementing the `version` field in the `@Database` declaration.
2. **Explicit Migrations:** Always write explicit `Migration` specs or `AutoMigration` definitions. Never permit `fallbackToDestructiveMigration()` in production code.