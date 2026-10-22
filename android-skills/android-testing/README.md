# 🧪 Android Testing Standards Skill

The **Android Testing Skill** establishes rigorous testing standards across unit, integration, and Compose UI levels. It enforces isolated, deterministic test suites utilizing Turbine for Coroutines Flow testing, clean offline fakes, Compose semantic testing, and strict Arrange-Act-Assert (AAA) hygiene.

---

## 📂 Subfolder Structure

```
android-testing/
├── SKILL.md                 # Agent entry point & test standards
├── README.md                # Human & Agent guide (this file)
└── instructions/            # Detailed reference guides & workflows
    ├── guidelines.md        # Coroutines testing rules, Turbine usage, Compose semantic matchers
    └── examples.md          # Complete Turbine ViewModel test and Fake repository implementations
```

---

## ⚡ How to Trigger

```text
/android-testing generate Turbine unit tests for CartViewModel and a ComposeTestRule UI test
```
```text
/android-testing convert brittle mock-based tests into fast in-memory fake repositories
```

---

## 🔗 Connected Skills
- **Architecture**: Tests pure UseCases from [`android-architecture`](../android-architecture/README.md)
- **UI Components**: Tests screens from [`compose-ui`](../compose-ui/README.md)
- **Failed Tests**: Diagnosed via [`error-detective`](../../diagnostic-skills/error-detective/README.md)
- **Device Journeys**: Routed to Google `android-cli`
