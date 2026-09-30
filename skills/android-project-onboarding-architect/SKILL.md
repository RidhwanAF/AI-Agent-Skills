---
name: android-project-onboarding-architect
description: Triggers automatically at the start of a task to check for or run project initialization, setup stack configs, and enforce global Android best practices.
---

# Global Android Architect & Project Initializer

## CORE PHILOSOPHY & DEFAULTS
By default, all Android projects handled by this agent adhere to:
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM + Multi-Module Pattern
- **Language & Runtime:** Modern Kotlin, Coroutines, Flow, Kotlin Duration API
- **Code Quality:** Zero hardcoded strings, explicit KDocs, strict lifecycle safety, main-thread protection

---

## STEP 1: INITIALIZATION CHECK & INTERACTIVE SETUP

Before writing or editing code in any Android repository, perform this check:

1. Look for `.project-config.json` at the root of the target Android project.
2. **If `.project-config.json` EXISTS:** Load the configuration silently and apply those tech-stack rules to the entire session.
3. **If `.project-config.json` DOES NOT EXIST:** Stop code generation immediately and execute the **Interactive Setup Wizard**.

### Interactive Setup Wizard (Rule: Ask ONE Question At A Time)

Ask the user these questions sequentially. **Do not combine questions into a single message.** Wait for their response before proceeding to the next step. If the user skips or answers "default", apply the fallback value.

* **Question 1 (Navigation):**  
  > "Which navigation library is this Android project using? (Default: `Compose Navigation 3 with @Serializable`)"

* **Question 2 (Networking):**  
  > "What is your networking stack? (Default: `Retrofit + OkHttp + kotlinx.serialization`)"

* **Question 3 (Local Persistence & Storage):**  
  > "What local storage solutions are used? (Default: `Room Database + Jetpack DataStore`)"

* **Question 4 (Dependency Injection):**  
  > "Which DI framework is configured? (Default: `Dagger Hilt`)"

* **Question 5 (Map / Special Libraries):**  
  > "Does this project use any mapping or spatial libraries (e.g., MapLibre Compose, Google Maps), or none? (Default: `None`)"

* **Question 6 (Observability & Monitoring):**  
  > "What crash reporting or analytics framework is installed? (Default: `Sentry`)"

### Step 1.1: Generate `.project-config.json`
Once all 6 answers are collected, create `.project-config.json` at the project root with the following structure:

```json
{
  "project_defaults": {
    "platform": "Android",
    "ui_framework": "Jetpack Compose (Material 3)",
    "architecture": "Clean Architecture + MVVM + Multi-Module",
    "language": "Kotlin",
    "strict_zero_hardcoding": true,
    "require_kdocs": true
  },
  "stack_configuration": {
    "navigation": "<User Answer Default or>",
    "networking": "<User Answer Default or>",
    "persistence": "<User Answer Default or>",
    "dependency_injection": "<User Answer Default or>",
    "mapping": "<User Answer Default or>",
    "observability": "<User Answer Default or>"
  },
  "build_and_quality": {
    "build_system": "Gradle Version Catalog (libs.versions.toml)",
    "proguard_enabled": true,
    "min_sdk_check": true,
    "static_analysis": ["spotless", "detekt"]
  }
}