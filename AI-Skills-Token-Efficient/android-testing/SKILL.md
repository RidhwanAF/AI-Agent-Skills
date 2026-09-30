---
name: android-testing
description: "Token-efficient unit and UI test generation for ViewModels, UseCases, Repositories, and Compose UI."
---

# Android Testing SOP (Token-Efficient)

## ViewModel Tests
- Test UI state emissions with `Turbine` on `StateFlow`.
- Replace `Dispatchers.Main` with `StandardTestDispatcher()` using a JUnit test rule.

## UseCase & Repository Tests
- Test success and failure outcomes using `MockK` mocks or fake repository implementations.
- Verify data transformations between Data, Domain, and UI layers.

## Compose UI Tests
- Use `createComposeRule()` or `createAndroidComposeRule<ComponentActivity>()`.
- Find nodes via `onNodeWithText()`, `onNodeWithContentDescription()`, perform actions with `performClick()`, and assert with `assertIsDisplayed()`.

## Structure
- Unit tests in `src/test/`, instrumented UI tests in `src/androidTest/`. Append `Test` to class names.
