---
name: android-data-layer
description: "Token-efficient data layer rules: Retrofit, kotlinx.serialization, Room Database, and Jetpack DataStore."
---

# Android Data Layer (Token-Efficient)

## Networking (Retrofit + kotlinx.serialization)
1. **Serialization:** Use `kotlinx.serialization` exclusively. Never use Gson or Moshi.
2. **DTO Scope:** DTOs are `@Serializable` and stay strictly in `data`. Map to domain models via explicit mapper functions before returning to domain.
3. **Endpoint Methods:** Declare all Retrofit methods as `suspend`.
4. **Timeouts:** Configure OkHttp timeouts using Kotlin Duration API: `connectTimeout(30.seconds)`, `readTimeout(30.seconds)`.

## Local Persistence (Room & DataStore)
1. **Room Scope:** `@Entity`, `@Dao`, and `RoomDatabase` remain strictly in `data`. Map entities to domain models via mappers.
2. **DAO Signatures:** Expose cold `Flow<T>` for reactive queries; use `suspend` for one-shot insert/update/delete.
3. **Threading:** Always run DAO operations on `Dispatchers.IO`.
4. **DataStore (Mandatory):** Use Jetpack DataStore (Preferences or Proto) for settings, tokens, and preferences. NEVER use `SharedPreferences`. Expose data as `Flow`.
