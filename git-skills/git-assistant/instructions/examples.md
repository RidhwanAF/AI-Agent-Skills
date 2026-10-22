# 🌿 Git Assistant Examples

Reference examples for conventional commits, PR briefings, and safe conflict resolution.

---

## 1. Conventional Commit Messages

```text
feat(auth): add biometric authentication support

Integrate AndroidKeyStore cipher decryption into AuthenticateBiometricsUseCase.
Adds hardware biometric prompt with fallback to device PIN.

Closes #42
```

```text
fix(cart): prevent crash on null avatar url

Correct DTO-to-Domain mapper to safely fallback to empty string when
backend avatar URL is omitted.

Docs: docs/error-fix/2026-10-22-null-user-profile-crash.md
Fixes #89
```

---

## 2. Pull Request Briefing Template

```markdown
## 📋 Summary of Changes
- Refactored Cart screen into modular Composable components.
- Enforced Compose modifier parameter convention.
- Integrated tactile haptic feedback on purchase action.

## 🎯 Motivation & Architectural Context
Decomposes monolithic UI file and isolates domain logic from Android framework.
Refer to ADR: [docs/adr/ADR-0002-modular-ui-composition.md](docs/adr/ADR-0002-modular-ui-composition.md)

## 🔬 Testing Evidence
- Unit Tests: `./gradlew testDebugUnitTest` ➔ 42 passed.
- Compose UI Test: `./gradlew connectedAndroidTest` ➔ All passed on Pixel 8.

## 📦 Detailed Changelog
Generated changelog record: [docs/changelogs/2026-10-22_21-15-00_decompose-product-screen.md](docs/changelogs/2026-10-22_21-15-00_decompose-product-screen.md)
```

---

## 3. Interactive Push & Merge Confirmation Prompt (Mandatory)

```text
Agent:
I have prepared the commit locally:
- Branch: feature/modular-cart
- Commit: a1b2c3d feat(cart): decompose cart screen into modular composables
- Status: 4 files changed, 120 insertions(+), 85 deletions(-)
- Tests: All unit tests passed (42/42)

How would you like to proceed with shipping?
[A] Push to remote branch (`git push origin feature/modular-cart`)
[B] Push and create Pull Request / Merge
[C] Keep changes local only (Do not push)

User: A

Agent:
Proceeding to push branch 'feature/modular-cart' to origin...
Running: git push origin feature/modular-cart
✓ Pushed successfully to origin/feature/modular-cart.
```
