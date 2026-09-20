<div align="center">

# SnapForge

[![Platform](https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=black&style=for-the-badge)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-a503fc?logo=kotlin&logoColor=white&style=for-the-badge)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label=)](https://developer.android.com/compose)
[![Material You](https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you)](https://m3.material.io)
[![License](https://img.shields.io/github/license/alzimerahmed/SnapForge?style=for-the-badge&logo=apache&color=blue)](./LICENSE)
[![Quality Gates](https://img.shields.io/badge/CI-detekt%20%2B%20tests-blue?style=for-the-badge)](.github/workflows/quality.yml)

*A FOSS Android image toolbox — 60+ editing and conversion tools in one offline-first app.*

[Download](https://github.com/alzimerahmed/SnapForge/releases) • [Features](#features) • [Building](#quick-start--building)

</div>

---

## Features

SnapForge is a fork of [T8RIN/ImageToolbox](https://github.com/T8RIN/ImageToolbox), independently maintained. Core capabilities:

- **Edit & transform** — crop, resize, convert formats, rotate, draw, filters, curves, one-tap enhance
- **EXIF** — view, edit, and strip metadata
- **AI & smart tools** — background erase, text recognition (OCR), AI image tools
- **Files & formats** — GIF/APNG/JXL/WebP/PDF tools, format conversion, archives
- **Create** — collage maker, gradients, mesh gradients, SVG maker, noise/texture generation
- **Utilities** — cipher (image steganography), QR scan, checksums, Base64, color tools, comparison
- **Fast workflows** — tool search, favorites & pinning, share-target quick actions (resize/convert/compress/crop/filter/watermark/PDF), social-platform resize presets, watermark presets
- **Material You** — dynamic color, dark theme, AMOLED, full RTL support

## Identity

Application ID: `com.alzimerahmed.snapforge` (debug builds: `.debug`). The `foss` flavor is the default and ships without Google Mobile Services; the `market` flavor enables GMS-powered features (ML Kit document scanner, selfie/subject segmentation) and is not a release target. Firebase is fully removed from all flavors.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.4 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Navigation | Decompose |
| Build | AGP 9, Gradle version catalogs + convention plugins |

## Project Structure

```
app/            entry point, DI wiring, flavors (foss / market / benchmark)
core/           data, domain, ui, resources, settings, di, filters, crash, utils, ksp
feature/        60+ self-contained tool modules (crop, filters, cipher, pdf-tools, batch, ...)
build-logic/    Gradle convention plugins (incl. detekt configuration)
config/detekt/  detekt rules + committed baseline
lib/            bundled native/auxiliary libraries
benchmark/      macrobenchmark harness
```

## Quality

- `detekt` (with Compose rules) runs per module; a committed baseline keeps legacy findings out of the way
- CI quality gate (`.github/workflows/quality.yml`): detekt + unit tests on every push/PR
- Unit tests (JUnit + Robolectric) cover core model/filter logic; screenshot/theme tests are a tracked follow-up

## Quick Start / Building

```bash
git clone https://github.com/alzimerahmed/SnapForge.git
cd SnapForge
./gradlew assembleFossDebug
```

> Tip for low-spec machines: build one variant at a time and cap resources in `~/.gradle/gradle.properties` (e.g. `org.gradle.workers.max=2`, `org.gradle.jvmargs=-Xmx3g`, `kotlin.daemon.jvmargs=-Xmx2g`). Full gates run in CI.

<details>
<summary>Advanced</summary>

- `./gradlew testFossDebugUnitTest` — unit tests
- `./gradlew detekt` — static analysis (baseline-aware)
- `./gradlew :app:lintFossDebug` — Android Lint
- `market` flavor enables GMS features; `benchmark` builds the macrobenchmark harness
- Release builds require signing configuration

</details>

## License

Apache License 2.0 — based on ImageToolbox by T8RIN (Malik Mukhametzyanov). Maintained by Alzimer Ahmed.
