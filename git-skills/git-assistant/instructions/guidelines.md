# 🌿 Git Assistant Guidelines

Safety-first protocols for version control operations, atomic commits, and merge conflict resolution.

---

## 🎯 Git Operational Protocols

1. **Atomic & Intentional Staging**:
   - Inspect `git status` before adding files.
   - Stage logical units together (`git add path/to/file`).
   - Forbid staging secrets, temporary files, `.DS_Store`, or build outputs.
2. **Commit Hygiene**:
   - Strictly follow Conventional Commits format (`type(scope): summary`).
   - Write commit messages in imperative present tense ("add feature" not "added feature").
3. **Safe Branch Synchronization**:
   - Prefer `git pull --rebase origin <branch>` to keep clean linear history.
   - Never force-push (`-f`) to shared branches (`main`, `master`, `develop`).
   - Use `--force-with-lease` when updating personal feature branches after rebase.
4. **Conflict Resolution Discipline**:
   - Always create a temporary safety tag before complex merges or rebases (`git tag backup/pre-rebase-$(date +%s)`).
   - Inspect both sides of conflict markers, preserve all legitimate logic, and run tests before `git rebase --continue`.
