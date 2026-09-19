# SnapForge — Project Rules for AI Agents

## Project

SnapForge (fork of T8RIN/ImageToolbox; upstream: https://github.com/T8RIN/ImageToolbox) — a FOSS Android image editor: crop, resize/convert, filters, EXIF editing, background erase, AI tools, PDF/color/GIF/steganography utilities and 60+ more feature modules. Repo: https://github.com/alzimerahmed/SnapForge. Fully independent from upstream (no sync; upstream history was dropped at fork time). Distributed via GitHub Releases ONLY (upstream's Play Store / F-Droid channels are not ours; the `market` flavor exists in code but is not shipped by default).

**Stack:** Kotlin 2.4.20, Jetpack Compose + Material 3 (Material You dynamic color), Hilt 2.60.1 (DI), Decompose 3.5.0 (navigation/lifecycle), Kotlin Serialization, Moshi, convention plugins via `build-logic/` (typesafe project accessors enabled). Min SDK 24, target/compile SDK 37, JVM target 21 (JDK 21 toolchain; one convention spot pins JVM_17 — do not "fix" blindly). AGP 9.3.3.

**Key subsystems:**
- **Module graph:** `:app` + 10 `:core:*` modules (data, ui, domain, resources, settings, di, crash, utils, filters, ksp) + 60+ `:feature:*` modules (crop, resize-convert, filters, draw, erase-background, edit-exif, pdf-tools, cipher, ai-tools, recognize-text, document-scanner, gif-tools, jxl-tools, webp-tools, collage-maker, watermarking, scan-qr-code, quick-tiles, ...). Feature modules depend on all core modules; `:app` depends on `:feature:root`, `:feature:media-picker`, `:feature:quick-tiles`. Full graph in `ARCHITECTURE.md` / `ARCHITECTURE_2`.
- **Build variants:** flavor dimension `app` → `foss` (default, no GMS), `market` (GMS-enabled), `benchmark`. `:app` debug builds use applicationIdSuffix `.debug`.
- **Identity:** applicationId `com.alzimerahmed.snapforge` (debug suffix `.debug`); namespace (internal code packages) remains `com.t8rin.imagetoolbox`; file provider `com.alzimerahmed.snapforge.fileprovider`. The namespace/package rename is deferred (docs/research.md ADR-002) — do NOT rename casually; applicationId is now the SnapForge identity.
- **Native libs under `lib/`** and JitPack/Sonatype-snapshot repositories — dependency resolution is strict (`FAIL_ON_PROJECT_REPOS`).
- **CI:** `.github/workflows/` — android.yml (full), android_foss.yml, android_market.yml, android_market_debug.yml, tb_release.yml.

**Build/verify:** `./gradlew assembleFossDebug` (default dev build) · `./gradlew testFossDebugUnitTest` (or `testDebugUnitTest` per variant) · `./gradlew :app:lintFossDebug` · release builds need signing config. First build downloads many deps (JitPack + snapshots) — expect a long warm-up. Build cache stays ON.

**Remote-first verification (policy):** full gates run on CI, not locally — the GitHub Actions workflows build/test on push. Local verification is tiered: targeted compile + scoped tests while iterating, scoped lint before commit, CI for the full gate before merge. Only run full local builds when the change touches build files/deps or an on-device APK is needed.

## Entry Point

This file is auto-loaded by Devin at every session start. It is the entry point to the full prompt system in `.devin/prompt/`. Read `.devin/prompt/map.md` before starting any task — it is the system map.

## Resource Discipline (mandatory for non-trivial tasks)

Before starting any non-trivial task:
1. Read `docs/toolset.md` intent-map (one table, task type → resources)
2. Identify the task type row; invoke every skill and sub-agent listed there
3. Read every rule listed for that task type (from `.devin/rules/`)
4. At task end: `code-reviewer` sub-agent on the final diff (non-negotiable)
5. Append learnings via `/ce-compound` if a durable lesson was learned

For phase implementations (any task completing a row in docs/plan.md), /ce-work is mandatory.

Skip this for single-line edits, pure Q&A, or reading files.

## Project-Type Filter (Android native app)

This is a native Android app, not a website. Per the intent-map in `docs/toolset.md`:
- **Skip web-only sub-agents/skills:** frontend-designer, css-architect, pwa-engineer, seo-specialist, search-optimization, playwright-design-clone (except when auditing Compose UI against design references — use tastemaker/pixel-analyst instead).
- **Keep universal ones:** code-reviewer, debugger, test-engineer, security-auditor, performance-engineer, git-master, migration-specialist, docs-writer, i18n-specialist (Compose string resources; upstream ships many locales), build-optimizer, caveman-compressor, pixel-analyst, vibe-coding-auditor, type-safety-engineer (Kotlin), database-engineer (Room/DataStore where used).
- **SnapForge-specific quality gates:** Android Lint, unit tests (JUnit/Robolectric), Compose UI tests, a11y via Compose semantics — not axe-core/browser tooling. Respect the `foss` flavor: never add GMS-only calls outside the `market` flavor gating; guard online features (load-net-image, ai-tools) so foss builds degrade gracefully.
- **Image-pipeline work:** many features share `:core:filters`, `:core:domain` model `ImageModel`, and the save/convert pipeline — check core before adding per-feature logic.

## Communication Style

Default to **caveman-lite** compression (lightly compressed, still readable, full technical accuracy). Use the `/caveman` skill for full / ultra / wenyan modes when tighter compression is needed.

## Quick Task Flow

For quick tasks, follow `.devin/prompt/quick.md` (commandments) and `.devin/prompt/rules.md` (scoping, verification, escalation). For phased work, follow `.devin/prompt/phase.md`.

## Key References

- `docs/toolset.md` — intent map (task type → skills, sub-agents, rules)
- `docs/plan.md` — phased plan and current status
- `docs/project.md` — project state and structure
- `docs/tools-log.md` — which .devin resources were invoked each session
- `docs/CONCEPTS.md` — project vocabulary (ubiquitous language for image-pipeline terms)
- `docs/research.md` — technical research, ADRs, gotchas
- `docs/idea.md` — competitive analysis (what to build)
- `ARCHITECTURE.md`, `ARCHITECTURE_2` — upstream module graph docs
- Upstream repo — README and Wiki for feature/FAQ reference (we do not sync code from it)
