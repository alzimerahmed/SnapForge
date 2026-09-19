# Contributing to SnapForge

Thanks for considering a contribution.

## Workflow

1. Open or comment on an issue describing the change before starting work.
2. Fork the repository, create a feature branch from `master`:

   ```bash
   git clone https://github.com/<your-username>/SnapForge.git
   cd SnapForge
   git checkout -b feat/my-feature
   ```

3. Make the change. Follow the existing code style (Kotlin official style, project convention
   plugins handle formatting-adjacent config). Keep changes scoped to the relevant
   `feature/*` or `core/*` module.
4. Verify locally before opening a PR:

   ```bash
   ./gradlew assembleFossDebug
   ./gradlew testFossDebugUnitTest
   ```

5. Open a pull request with a clear description of what changed and why. Conventional
   Commits style (`feat(scope):`, `fix(scope):`, ...) is preferred for commit messages.

## Notes for maintainers

- The `foss` flavor must always build without Google Mobile Services.
- New dependencies go through `gradle/libs.versions.toml` and need justification
  (maintenance status, license, size impact).
- CI builds run on GitHub Actions; a green CI check is required to merge.
