# 📝 Changelog Recorder Guidelines

Guidelines for generating timestamped changelog documents and maintaining the changelog registry.

---

## 🎯 Protocol & Formatting Rules

1. **Strict Timestamp Naming**:
   - Save in `<project-root>/docs/changelogs/YYYY-MM-DD_HH-mm-ss_<title>.md`.
   - The timestamp format is Year-Month-Day, underscore, Hour-Minute-Second, underscore, kebab-case title.
2. **Mandatory Report Sections**:
   - Executive Summary.
   - Motivation & Problem Statement.
   - File Modifications (Added, Modified, Deleted).
   - Architectural & KMP / TypeScript Impact.
   - Verification & Testing Evidence (commands executed and test outcomes).
   - Next Steps.
3. **Index Registry Maintenance**:
   - Update `<project-root>/docs/changelogs/README.md` with every new changelog entry.
4. **Language Standards**:
   - All changelog files must be in English.
