# Contributing to PuriFree

Thank you for contributing to PuriFree! This document covers the development workflow, code conventions, and contribution guidelines.

---

## Table of Contents

- [Building](#building)
- [Running Tests](#running-tests)
- [Code Conventions](#code-conventions)
- [Commit Messages](#commit-messages)
- [Branch Workflow](#branch-workflow)
- [Adding New Patches](#adding-new-patches)
- [Version Verification](#version-verification)
- [Pull Requests](#pull-requests)

---

## Building

### Prerequisites

- **JDK 21** (Temurin recommended)
- **Android SDK** (set `ANDROID_HOME` or create `local.properties` with `sdk.dir=...`)
- **GitHub credentials** for Morphe registry (see below)

### Gradle Credentials

PuriFree depends on `morphe-patcher` from GitHub Packages. Add to `~/.gradle/gradle.properties`:

```properties
gpr.user=your-github-username
gpr.key=your-github-token
```

Or set environment variables:

```bash
export GITHUB_ACTOR=your-github-username
export GITHUB_TOKEN=your-github-token
```

### Build Commands

```bash
# Build patches (produces patches/build/libs/patches-*.mpp)
./gradlew :patches:buildAndroid

# Generate patches-list.json
./gradlew generatePatchesList

# Full release build
./gradlew :patches:buildAndroid generatePatchesList clean

# Run unit tests
./gradlew :patches:test

# Build extension module (if needed)
./gradlew :extensions:extension:assembleRelease
```

---

## Running Tests

```bash
# Run all tests
./gradlew :patches:test

# Run specific test class
./gradlew :patches:test --tests "app.purifree.patches.hidepur.HidePurPatchSourceTest"

# View test report
open patches/build/reports/tests/test/index.html
```

**Test types:**
- **Source assertions**: Verify patch source contains expected patterns
- **Bytecode verification** (future): Verify patched DEX has expected instructions
- **UI automation** (future): Verify runtime behavior on emulator

---

## Code Conventions

### Kotlin Style

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- 4-space indentation (no tabs)
- Max line length: 120 characters
- Explicit visibility modifiers (`public`, `internal`, `private`)
- KDoc for public APIs

### Package Structure

```
patches/src/main/kotlin/app/purifree/patches/
├── <app-name>/              # e.g., kleinanzeigen, googlenews
│   ├── <patch-name>/
│   │   ├── Fingerprints.kt  # Fingerprint objects
│   │   └── <PatchName>Patch.kt
│   └── shared/
│       └── Constants.kt     # Shared compatibility definitions
└── util/
    └── PatchListGenerator.kt
```

### Patch Naming

- **File**: `HideAdsPatch.kt`, `HidePurPatch.kt`
- **Variable**: `hideAdsPatch`, `hidePurPatch` (camelCase, `val`)
- **Fingerprint**: `AdLoaderFingerprint`, `SettingsAndHelpFragmentPurEntryFingerprint` (PascalCase, `object`)

### Fingerprint Guidelines

1. **Avoid class-name matching** for obfuscated apps (use string literals instead)
2. **Use `definingClass`** field when class is stable (not obfuscated)
3. **Use `methodOrNull`** for optional fingerprints (silent no-op on miss)
4. **Use `method`** for required fingerprints (hard-fail on miss)

Example:
```kotlin
// Stable class (not obfuscated)
object SettingsAndHelpFragmentPurEntryFingerprint : Fingerprint(
    definingClass = "Lebk/ui/preferences/settings/settings_and_help/SettingsAndHelpFragment;",
    custom = { method, _ ->
        !method.name.startsWith("access$") &&
            method.parameterTypes.count { it.toString() == "Z" } >= 5
    }
)

// Obfuscated class (use string literal)
object AdLoaderFingerprint : Fingerprint(
    strings = listOf("KEY_LIBERTY_REFRESH_INTERVAL")
)
```

### Patch Implementation Guidelines

1. **Anchor on stable literals** (resource IDs, string constants)
2. **Validate anchors** with explicit `PatchException` messages
3. **Use `const/16`** for register-agnostic instructions (supports v0-v255)
4. **Document version-specific values** (e.g., `0x7f14140d` for 2026.32.0)

Example:
```kotlin
val purSubtitleResId = 0x7f14140d // R.string.ka_settings_v2_top_ad_free_subscription_pur_subtitle (2026.32.0)
val subtitleIndex = instructions.indexOfFirst { instr ->
    instr is NarrowLiteralInstruction && instr.narrowLiteral == purSubtitleResId
}
if (subtitleIndex == -1) {
    throw PatchException("Failed to find Pur subtitle resource id $purSubtitleResId")
}
```

---

## Commit Messages

PuriFree uses [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body> (optional)
```

### Types

| Type | Use for |
|------|---------|
| `feat` | New patch, new feature |
| `fix` | Bug fix (e.g., wrong fingerprint, broken patch logic) |
| `docs` | Documentation only |
| `test` | Adding/updating tests |
| `chore` | Build config, dependencies, repo maintenance |
| `refactor` | Code restructuring (no behavior change) |
| `build` | Build system changes that trigger releases |

### Scope Examples

- `hide-ads`, `hide-pur`, `remove-tracking` (patch-specific)
- `extensions`, `ci`, `release`, `deps` (infrastructure)

### Subject Rules

- Max 50 characters
- Imperative mood ("Add" not "Added")
- No period at end
- Lowercase after scope

### Examples

```
feat: Add Hide Pur patch for Kleinanzeigen
fix(hide-pur): Anchor on Pur subtitle constant instead of first IF_EQZ
docs: Add CONTRIBUTING.md
test: Add unit tests for all 3 patches
chore(deps): Update morphe-patcher to 1.8.0
build: Enable test task in patches/build.gradle.kts
```

### Release-Triggering Commits

Semantic-release triggers on:
- `fix:`, `feat:`, `perf:`, `build(Needs bump):`
- Bump version manually: `build(Needs bump): update version for new patches`

---

## Branch Workflow

### Branches

| Branch | Purpose |
|--------|---------|
| `main` | Production releases (stable) |
| `dev` | Pre-release channel (auto PR to `main`) |

### Workflow

1. **Feature development**: Create branch from `dev` (e.g., `feat/hide-pur-fix`)
2. **PR to `dev`**: Review, merge
3. **Auto PR `dev` → `main`**: Created by `open_pull_request.yml`
4. **Merge to `main`**: Triggers semantic-release

### Local Development

```bash
# Work on dev branch
git checkout dev
git pull origin dev
git checkout -b feat/your-feature

# After development
git commit -m "feat: Add your feature"
git push origin feat/your-feature
# Create PR to dev branch on GitHub
```

---

## Adding New Patches

### Step 1: Create Patch Structure

```bash
mkdir -p patches/src/main/kotlin/app/purifree/patches/<app>/<patch-name>
```

### Step 2: Implement Fingerprint

```kotlin
// Fingerprints.kt
package app.purifree.patches.<app>.<patch-name>

import app.morphe.patcher.Fingerprint

object <PatchName>Fingerprint : Fingerprint(
    // Use string literals for obfuscated apps
    strings = listOf("STABLE_LITERAL"),
    // OR custom predicate for stable classes
    custom = { method, classDef ->
        classDef.type == "Lebk/ui/.../ClassName;" &&
            method.parameterTypes.size == 2
    }
)
```

### Step 3: Implement Patch

```kotlin
// <PatchName>Patch.kt
package app.purifree.patches.<app>.<patch-name>

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.purifree.patches.shared.Constants.COMPATIBILITY_<APP>

@Suppress("unused")
val <patchName>Patch = bytecodePatch(
    name = "<Patch Name>",
    description = "Description of what the patch does.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_<APP>)

    execute {
        val method = <PatchName>Fingerprint.method // or methodOrNull
            ?: throw PatchException("Failed to find method")
        
        // Implementation
        method.addInstruction(0, "return-void")
    }
}
```

### Step 4: Add to Constants.kt

```kotlin
// shared/Constants.kt
val COMPATIBILITY_<APP> = Compatibility(
    name = "<App Name>",
    packageName = "com.example.app",
    apkFileType = ApkFileType.APK,
    appIconColor = 0x123456,
    targets = listOf(
        AppTarget(version = "1.0.0") // Verified version only (CR-001)
    )
)
```

### Step 5: Write Tests

```kotlin
// src/test/kotlin/.../<patch-name>/<PatchName>PatchSourceTest.kt
class <PatchName>PatchSourceTest {
    @Test
    fun `patch does what it should`() {
        val source = readPatchSource()
        assertTrue(source.contains("expected pattern"))
    }
}
```

### Step 6: Verify Bytecode (Manual for Now)

1. Build: `./gradlew :patches:buildAndroid`
2. Patch APK via Morphe Manager
3. Verify behavior (UI check, bytecode dump)
4. Add verified version to `COMPATIBILITY_<APP>`

---

## Version Verification

**Policy (CR-001):** Before declaring a patch compatible with a new app version, verify it works:

1. **Build patch** against target version APK
2. **Patch APK** via Morphe Manager
3. **Verify bytecode** (expected instructions present)
4. **Verify UI** (observable behavior matches requirement)
5. **Add version** to `COMPATIBILITY_<APP>.targets`

**Do not** add untested versions — the patch may silently fail or cause crashes.

---

## Pull Requests

### PR Checklist

- [ ] Code follows conventions (package structure, naming, KDoc)
- [ ] Tests added/updated (source assertions at minimum)
- [ ] `./gradlew :patches:test` passes locally
- [ ] Commit message follows Conventional Commits
- [ ] New versions verified (CR-001) if compatibility changed

### Review Process

1. **CI checks**: Build + tests must pass
2. **Code review**: Maintainer reviews patch logic, tests, conventions
3. **Merge to `dev`**: Auto PR to `main` created
4. **Release**: Merged to `main` → semantic-release publishes

---

## Questions?

- **Bug reports**: [GitHub Issues](https://github.com/cyborium/purifree/issues)
- **Discussions**: [GitHub Discussions](https://github.com/cyborium/purifree/discussions)
- **Spec questions**: See `docs/` in workspace root (clean-room specification)

---

## License

By contributing, you agree your contributions are licensed under the [GPLv3 License](LICENSE).
