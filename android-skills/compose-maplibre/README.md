# 🗺️ MapLibre for Jetpack Compose Skill

The **MapLibre Compose Skill** guides high-performance geospatial mapping in Android using MapLibre Native and modern Jetpack Compose. It ensures lifecycle safety, smooth vector tile rendering, declarative layer styling, and leak-free camera management.

---

## 📂 Subfolder Structure

```
compose-maplibre/
├── SKILL.md                 # Agent entry point & mapping rules
├── README.md                # Human & Agent guide (this file)
└── instructions/            # Detailed reference guides & workflows
    ├── guidelines.md        # Lifecycle safety, GeoJSON threading, inset rules
    └── examples.md          # Complete lifecycle-safe MapView wrapper & overlay controls
```

---

## ⚡ How to Trigger

```text
/compose-maplibre render GeoJSON polygon layers with lifecycle-safe MapView in Compose
```
```text
/compose-maplibre integrate MapLibre vector tile styles with custom camera controls
```

---

## 🔗 Connected Skills
- **Screen Integration**: Embedded into screens via [`compose-ui`](../compose-ui/README.md)
- **Data & GeoJSON**: Sourced from [`android-architecture`](../android-architecture/README.md)
- **Dependencies**: Configured via [`gradle-build-hygiene`](../gradle-build-hygiene/README.md)
