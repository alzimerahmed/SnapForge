# SnapForge — Rules for AI Agents

## Project

SnapForge = fork of T8RIN/ImageToolbox (upstream: https://github.com/T8RIN/ImageToolbox). FOSS Android image editor: crop, resize/convert, filters, EXIF, background erase, AI tools, PDF/color/GIF/steganography + 60 more modules. Repo: https://github.com/alzimerahmed/SnapForge. Fully independent from upstream (no sync; upstream history dropped at fork). GitHub Releases ONLY (upstream Play Store/F-Droid channels not ours; `market` flavor exists in code, not shipped by default).

**Stack:** Kotlin 2.4.20, Compose + M3 (Material You dynamic color), Hilt 2.60.1, Decompose 3.5.0 (nav/lifecycle), Kotlin Serialization, Moshi, convention plugins via `build-logic/` (typesafe project accessors ON). Min SDK 24, target/compile SDK 37, JVM target 21 (JDK 21 toolchain; one convention spot pins JVM_17 — do NOT "fix" blindly). AGP 9.3.3.

**Key subsystems:**
- **Modules:** `:app` + 10 `:core:*` (data, ui, domain, resources, settings, di, crash, utils, filters, ksp) + 60+ `:feature:*` (crop, resize-convert, filters, draw, erase-background, edit-exif, pdf-tools, cipher, ai-tools, recognize-text, document-scanner, gif-tools, jxl-tools, webp-tools, collage-maker, watermarking, scan-qr-code, quick-tiles, ...). Features depend on all core; `:app` depends on `:feature:root`, `:feature:media-picker`, `:feature:quick-tiles`. Module graph: run `./gradlew :app:dependencies` or see settings.gradle.kts.
- **Variants:** flavor dim `app` → `foss` (default, no GMS), `market` (GMS), `benchmark`. `:app` debug suffix `.debug`.
- **Identity:** applicationId `com.alzimerahmed.snapforge` (debug `.debug`); namespace/packages stay `com.t8rin.imagetoolbox`; file provider `com.alzimerahmed.snapforge.fileprovider`. Namespace rename deferred (docs/research.md ADR-002) — do NOT rename casually; applicationId = SnapForge identity.
- **Native libs under `lib/`**, JitPack/Sonatype-snapshot repos; strict repo resolution (`FAIL_ON_PROJECT_REPOS`).
- **CI:** `.github/workflows/` — android.yml (full), android_foss.yml, android_market.yml, android_market_debug.yml, tb_release.yml.

**Build/verify:** `./gradlew assembleFossDebug` (default dev) · `./gradlew testFossDebugUnitTest` (or `testDebugUnitTest` per variant) · `./gradlew :app:lintFossDebug` · release needs signing config. First build downloads many deps (JitPack + snapshots) — long warm-up. Build cache stays ON.

**Remote-first verification:** full gates on CI, not local. Local tiered: targeted compile + scoped tests while iterating → scoped lint before commit → CI before merge. Full local builds only if change touches build files/deps or on-device APK needed.

**CI on every push to master** (2026-09-19): `.github/workflows/quality.yml` gates push/PR with detekt + `testFossDebugUnitTest`. Green Quality Gates run = authoritative verification; do NOT repeat locally. Push instead of local build whenever possible.

**IMPORTANT — small-batch local builds (mandatory):** 70+ modules, multi-flavor; full multi-task Gradle invocations peg CPU, lag machine, heat laptop. When local builds needed:
- SMALL BATCHES: one variant/module per invocation (`./gradlew assembleFossDebug`, later `./gradlew assembleMarketDebug` — never both in one command).
- Scoped tasks: `:module:compileDebugKotlin`, `:module:testDebugUnitTest` > whole-repo `test`/`assemble`.
- Let config cache + up-to-date checks work; don't re-run green tasks.
- Long build → background + poll, keep machine responsive.

## Entry Point

Auto-loaded by Devin every session. Entry to prompt system in `.devin/prompt/`. Read `.devin/prompt/map.md` before any task — system map.

## Resource Discipline (mandatory, non-trivial tasks)

Before any non-trivial task:
1. Read `docs/toolset.md` intent-map (task type → resources)
2. Invoke every skill + sub-agent in that row
3. Read every rule for that task type (`.devin/rules/`)
4. At task end: `code-reviewer` sub-agent on final diff (non-negotiable)
5. Append learnings via `/ce-compound` if durable lesson

**Background sub-agents:** run sub-agents background (`is_background=true`) when result not immediately needed; keep working while they run — don't idle-block on `read_subagent`. Launch independent sub-agents parallel, continue local work (reads, edits, builds); notified on completion. Block only when sub-agent output = hard dependency for next step.

Phase implementations (task completes a docs/plan.md row): /ce-work mandatory.

Skip all this for single-line edits, pure Q&A, reading files.

## Project-Type Filter (Android native)

Native Android app, not website. Per `docs/toolset.md` intent-map:
- **Skip web-only:** frontend-designer, css-architect, pwa-engineer, seo-specialist, search-optimization, playwright-design-clone (except Compose UI vs design refs — use tastemaker/pixel-analyst).
- **Keep universal:** code-reviewer, debugger, test-engineer, security-auditor, performance-engineer, git-master, migration-specialist, docs-writer, i18n-specialist (Compose string resources; upstream ships many locales), build-optimizer, caveman-compressor, pixel-analyst, vibe-coding-auditor, type-safety-engineer (Kotlin), database-engineer (Room/DataStore where used).
- **Quality gates:** Android Lint, unit tests (JUnit/Robolectric), Compose UI tests, a11y via Compose semantics — no axe-core/browser tooling. Respect `foss` flavor: no GMS-only calls outside `market` gating; guard online features (load-net-image, ai-tools) so foss degrades gracefully.
- **Image pipeline:** features share `:core:filters`, `:core:domain` model `ImageModel`, save/convert pipeline — check core before per-feature logic.

## Communication Style

Default **caveman-lite** (lightly compressed, readable, technically accurate). `/caveman` skill for full/ultra/wenyan modes.

## Quick Task Flow

Quick tasks: `.devin/prompt/quick.md` (commandments) + `.devin/prompt/rules.md` (scoping, verification, escalation). Phased work: `.devin/prompt/phase.md`.

## Key References

- `docs/toolset.md` — intent map (task type → skills, sub-agents, rules)
- `docs/plan.md` — phased plan + status
- `docs/project.md` — project state/structure
- `docs/tools-log.md` — .devin resources invoked per session
- `docs/CONCEPTS.md` — project vocabulary (image-pipeline terms)
- `docs/research.md` — research, ADRs, gotchas
- `docs/idea.md` — competitive analysis
- Module graph: `settings.gradle.kts` (upstream ARCHITECTURE docs removed)
- Upstream repo — README/Wiki for feature/FAQ reference (no code sync)
