<div align="center">

# SnapForge

[![Platform](https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=black&style=for-the-badge)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.4-a503fc?logo=kotlin&logoColor=white&style=for-the-badge)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/static/v1?style=for-the-badge&message=Jetpack+Compose&color=4285F4&logo=Jetpack+Compose&logoColor=FFFFFF&label=)](https://developer.android.com/compose)
[![Material You](https://custom-icon-badges.demolab.com/badge/material%20you-lightblue?style=for-the-badge&logoColor=333&logo=material-you)](https://m3.material.io)
[![License](https://img.shields.io/github/license/alzimerahmed/SnapForge?style=for-the-badge&logo=apache&color=blue)](./LICENSE)

*A FOSS Android image toolbox — 60+ editing and conversion tools in one offline-first app.*

[Download](https://github.com/alzimerahmed/SnapForge/releases) • [Features](#features) • [Building](#quick-start--building)

</div>

---

## Features

SnapForge is a fork of [T8RIN/ImageToolbox](https://github.com/T8RIN/ImageToolbox), independently maintained. Core capabilities:

- **Edit & transform** — crop, resize, convert formats, rotate, draw, filters, curves
- **EXIF** — view, edit, and strip metadata
- **AI & smart tools** — background erase, text recognition (OCR), AI image tools
- **Files & formats** — GIF/APNG/JXL/WebP/PDF tools, format conversion, archives
- **Create** — collage maker, gradients, mesh gradients, SVG maker, noise/texture generation
- **Utilities** — cipher (image steganography), QR scan, checksums, Base64, color tools, comparison
- **Material You** — dynamic color, dark theme, AMOLED, full RTL support

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
feature/        60+ self-contained tool modules (crop, filters, cipher, pdf-tools, ...)
build-logic/    Gradle convention plugins
lib/            bundled native/auxiliary libraries
benchmark/      macrobenchmark harness
```

## Quick Start / Building

```bash
git clone https://github.com/alzimerahmed/SnapForge.git
cd SnapForge
./gradlew assembleFossDebug
```

<details>
<summary>Advanced</summary>

- `./gradlew testFossDebugUnitTest` — unit tests
- `./gradlew :app:lintFossDebug` — Android Lint
- `market` flavor enables GMS features; `benchmark` builds the macrobenchmark harness
- Release builds require signing configuration

</details>

## License

Apache License 2.0 — based on ImageToolbox by T8RIN (Malik Mukhametzyanov). Maintained by Alzimer Ahmed.
