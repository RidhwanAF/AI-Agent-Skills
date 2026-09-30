---
name: android-data-layer
description: Enforces best practices for Retrofit, Room Database, Jetpack DataStore, and kotlinx.serialization without data layer leakage.
---

# Data Layer & Persistence SOP

## Networking (Retrofit + kotlinx.serialization)
1. **Serialization:** Use `kotlinx.serialization` exclusively for JSON parsing. Do NOT use Moshi or Gson.
2. **DTO Scope:** Annotate network DTOs with `@Serializable` and keep them strictly within the `data` layer.
3. **Data Mapping:** Map network DTOs to pure Domain Entities using explicit mapper functions before returning data to the `domain` layer.
4. **Retrofit Signatures:** Declare endpoint methods as `suspend` functions.
5. **Timeouts:** Always configure explicit OkHttp timeouts (`connectTimeout`, `readTimeout`) using the Kotlin Duration API (e.g., `30.seconds`).

## Local Persistence (Room & DataStore)
1. **Room Scope:** Keep `@Entity`, `@Dao`, and `RoomDatabase` instances strictly within the `data` layer.
2. **Mapping:** Map Room Entities to Domain Entities via mapper functions.
3. **DAO Operations:** Expose cold `Flow` returns for reactive queries. Use `suspend` functions for one-shot CUD operations. Always execute DAO operations on `Dispatchers.IO`.
4. **DataStore:** Use Jetpack DataStore (Preferences/Proto) for key-value or setting storage. Never use `SharedPreferences`.