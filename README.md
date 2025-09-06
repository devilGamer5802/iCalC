# iCalC — Intelligent Calculator + Whiteboard

A modern Android calculator with a handwriting whiteboard and AI-powered math solving. Built with Kotlin, XML + Jetpack Compose, and Material Design 3. Package: com.hatcorp.icalc. App name: iCalC.

## Highlights
- Adaptive UI with Material 3: bottom navigation on phones, navigation rail on larger screens.
- Modular navigation: Calculator, Converters, and Whiteboard surfaces.
- Kotlin-first architecture with a clean roadmap for Gemini and on-device handwriting recognition.

## Tech stack
- Language: Kotlin
- UI: Jetpack Compose + XML interop
- Design: Material Design 3 (dynamic color on Android 12+)
- Navigation: Navigation Compose
- Min SDK: 26 (Android 8)
- Toolchain (scaffold): Android Gradle Plugin 8.x, Kotlin 2.x, Compose BOM aligned
- Packaging: Single-app module (app)

## Features
- Calculator
    - Basic and scientific modes (fx-991ES parity targeted in future chapters)
    - Expression engine and history (planned)
- Converters
    - Currency (live rates), length, mass, area, time, volume, numeral systems, speed, temperature, BMI, GST (CGST/SGST), finance (loan/investments), date range (planned)
- Whiteboard
    - Handwriting input with stroke capture and recognition (planned)
    - Equation solving, graphing, steps/explanations with AI (planned)
- Graphing
    - Function plots (initially via AndroidView bridge; later pure Compose when stable) (planned)

## Project structure
- app/src/main/java/com/hatcorp/icalc
    - MainActivity.kt: Compose entry point
    - ui/: app scaffold and navigation host
    - ui/screens/: Calculator, Converters, Whiteboard screen stubs
    - ui/theme/: Material 3 theme (dynamic color, typography, shapes)
- Gradle setup
    - settings.gradle.kts: repositories and module includes
    - build.gradle.kts (root): pinned plugins
    - app/build.gradle.kts: Compose BOM, Material 3, Navigation

## Getting started
Prerequisites
- Android Studio Hedgehog+ (or current stable with Compose support)
- JDK 17 (configured via Gradle toolchain)
- Android SDK 35 installed

Clone and open
- Clone the repo and open the project root in Android Studio.
- Let Gradle sync; run on an emulator or device (min Android 8).

Build and run (terminal)
- From the project root:
```
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

## Branching and PR workflow
- Default branch: main
- Chapter branches: chapter/00-foundation, chapter/01-navigation-theme, chapter/01.5-readme, etc.
- Create a chapter branch, commit changes, push, then open a PR into main.

Example
```
git checkout -b chapter/01.5-readme
git add README.md
git commit -m "docs: add Chapter 1.5 README with project overview and workflow"
git push -u origin chapter/01.5-readme
```

## Roadmap
- Chapter 0: Foundation (Gradle, Compose, Material 3, Navigation)
- Chapter 1: Adaptive scaffold + routes/args (calculator modes, converter types)
- Chapter 1.5: README (this)
- Chapter 2: Calculator engine (basic + scientific UI/logic)
- Chapter 3: Converters (all listed domains + currency provider abstraction)
- Chapter 4: Whiteboard canvas + handwriting recognition (on-device)
- Chapter 5: Gemini integration (1.5 Flash, upgrade path to 2.x)
- Chapter 6: Graphing (Compose bridge; future native charts)
- Chapter 7: Finance calculators + explainers
- Chapter 8: Testing, performance, QA

## Configuration (future chapters)
- API keys and secrets
    - Gemini key: provide via local.properties or an environment variable; never commit secrets.
    - Currency API provider: configurable via buildConfig or DI.
- ML models
    - Digital Ink recognition models will be downloaded on-device per language/script.

## Testing (to be added)
- Unit tests for calculator and converter engines
- UI tests for navigation and adaptive layouts
- Baseline profiles and performance checks for handwriting/graph flows

## Code style and commits
- Kotlin conventions with Compose best practices
- Conventional Commits recommended (e.g., feat:, fix:, chore:, docs:, test:)

## Security and privacy
- No analytics or tracking in the scaffold
- When enabling AI or currency services, document data flows and add clear user consent and privacy disclosures

## License
- Apache-2.0 (proposed). Adjust as required for the organization.

## Acknowledgments
- Inspired by modern math tools and OS-level handwriting experiences
- Built with Android modern app architecture principles

If helpful, a ready-to-commit patch/diff for README.md and a short PR description can be provided next, along with a CI stub for lint/assemble checks to keep PRs green.

[1](https://ieeexplore.ieee.org/document/8667998/)
[2](https://www.semanticscholar.org/paper/c51a6634002008070d1ef2d50ebc438a78901342)
[3](https://ieeexplore.ieee.org/document/9978176/)
[4](https://dl.acm.org/doi/10.1145/3736758)
[5](https://dl.acm.org/doi/10.1145/3340496.3342759)
[6](https://ieeexplore.ieee.org/document/7180074/)
[7](https://ieeexplore.ieee.org/document/8530075/)
[8](https://link.springer.com/10.1007/s11616-023-00788-6)
[9](https://dl.acm.org/doi/10.1145/3324884.3416623)
[10](https://dl.acm.org/doi/10.1145/3551349.3561341)
[11](https://hrcak.srce.hr/322687)
[12](https://arxiv.org/abs/2104.08301)
[13](https://dl.acm.org/doi/pdf/10.1145/3636534.3649379)
[14](https://arxiv.org/pdf/2311.08649.pdf)
[15](https://www.mdpi.com/2073-8994/13/2/310/pdf)
[16](https://arxiv.org/html/2409.14337v2)
[17](https://arxiv.org/pdf/2111.01631.pdf)
[18](https://arxiv.org/pdf/2203.06420.pdf)
[19](https://gist.github.com/fb7d75a0176f7be2b02e)
[20](https://github.com/othneildrew/Best-README-Template)
