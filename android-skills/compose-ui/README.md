# 🎨 Compose UI Skill

The **Compose UI Skill** enforces production standards for modern Jetpack Compose applications: modular multi-file component decomposition, the strict Modifier ordering rule, state stability (`@Immutable`), tactile haptic feedback, and multi-language localization.

---

## 📂 Subfolder Structure

```
compose-ui/
├── SKILL.md                 # Agent entry point & core UI rules
├── README.md                # Human & Agent guide (this file)
└── instructions/            # Detailed reference guides & workflows
    ├── guidelines.md        # File decomposition, modifier rules, stability & haptics
    └── examples.md          # Multi-file Compose code examples and call-site ordering
```

---

## ⚡ How to Trigger

```text
/compose-ui decompose this checkout screen into separate content, card, and state files
```
```text
/compose-ui audit this Composable for modifier parameter placement and recomposition stability
```

---

## 📐 The Modifier Rule Summary

- **In Declaration (`@Composable fun`)**: `modifier: Modifier = Modifier` MUST be placed at the **very first top parameter (`#1`)** of the function signature — **never** at the bottom!
- **At Call Site (Consuming a Composable)**: Pass business data and callbacks first, and place `modifier = Modifier...` at the **very bottom of the call arguments**.

---

## 🔗 Connected Skills
- **Architecture**: Binds to StateFlow from [`android-architecture`](../android-architecture/README.md)
- **Geospatial Maps**: Embeds maps via [`compose-maplibre`](../compose-maplibre/README.md)
- **UI Tests**: Tested via [`android-testing`](../android-testing/README.md)
- **Recomposition Bugs**: Diagnosed via [`error-detective`](../../diagnostic-skills/error-detective/README.md)
