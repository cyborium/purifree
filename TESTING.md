# PuriFree Automated Testing Roadmap

## Overview

Multi-target patch bundles require automated verification across app versions to ensure:
1. Patches apply successfully (build + Morphe patching)
2. Bytecode modifications are correct (expected instructions present)
3. Runtime behavior matches expectations (UI/functional tests)
4. No regressions when adding new app targets

---

## Infrastructure Components

### 1. APK Archive

**Purpose:** Store historical APKs for each supported app/version.

**Structure:**
```
apks/
├── kleinanzeigen/
│   ├── 2026.32.0.apkm
│   ├── 2026.27.0.apkm
│   └── ...
├── googlenews/
│   └── ...
└── ...
```

**Action items:**
- [ ] Decide storage: Git LFS / GitLab releases / external host (APKMirror links)
- [ ] Document APK acquisition process (version + build code mapping)
- [ ] Add checksums (SHA-256) for integrity verification

---

### 2. CI Test Matrix

**Purpose:** Run tests against every declared app version on every PR.

**GitHub Actions workflow:**
```yaml
test_matrix:
  - app: kleinanzeigen
    versions: ["2026.32.0", "2026.27.0"]
  - app: googlenews
    versions: ["5.163.0.947799485"]
```

**Action items:**
- [ ] Create `.github/workflows/test.yml`
- [ ] Integrate with Morphe CLI for patching
- [ ] Cache patched APKs between test runs

---

### 3. Bytecode Verification

**Purpose:** Assert expected Smali instructions are present/absent in patched DEX.

**Example checks (Kleinanzeigen v2026.32.0):**
| Patch | Check | Expected |
|-------|-------|----------|
| Hide Ads | `instr[0]` of Liberty method | `RETURN_VOID` |
| Hide Pur | Before `IF_EQZ` (Pur gate) | `const/16 v19, 0x0` |
| Remove Tracking | `CONST_STRING` with UTM suffix | Absent (replaced with `""`) |

**Tooling:**
- `dexlib2` (via Java harness in `test/bytecode/`)
- Assertions in CI: `./gradlew :test:verifyBytecode`

**Action items:**
- [ ] Move `dexverify/` harnesses into `purifree/test/`
- [ ] Write JUnit tests per patch
- [ ] Fail CI if expected bytecode not found

---

### 4. UI Automation

**Purpose:** Verify runtime behavior on emulator/real device.

**Example checks (Kleinanzeigen):**
| Patch | Action | Expected |
|-------|--------|----------|
| Hide Pur | Open Settings → "Einstellungen & Hilfe" | "Kleinanzeigen Pur" row absent |
| Hide Ads | Browse feed | No sponsored listings |
| Remove Tracking | Share profile | URL has no `utm_*` params |

**Tooling:**
- Android Emulator (AVD)
- `uiautomator` / Appium for UI assertions
- `adb` for deep-link navigation + network inspection

**Action items:**
- [ ] Create `test/ui/` with Kotlin/Java test suite
- [ ] Write UI tests per patch (deep-link navigation)
- [ ] Run on GitHub Actions with emulator (or self-hosted runner)

---

### 5. Version Gate

**Purpose:** Prevent declaring untested versions in `COMPATIBILITY_*`.

**Policy (CR-001):**
> Before adding a new app version to a patch's `targets` list, the patch must be verified against that version (bytecode + UI tests pass).

**Enforcement:**
- [ ] CI check: declared versions must have passing test results
- [ ] `versions.json` or similar manifest of tested versions
- [ ] Block PR if new version added without tests

---

## Implementation Priority

| Phase | Components | Target |
|-------|------------|--------|
| **Phase 1** | APK archive + Bytecode verification | Kleinanzeigen only |
| **Phase 2** | CI matrix + Version gate | Multi-app ready |
| **Phase 3** | UI automation | Full E2E coverage |

---

## Current Status (v1.0.0)

| Component | Status |
|-----------|--------|
| APK archive | ❌ Not implemented |
| CI matrix | ❌ Not implemented |
| Bytecode verification | ⚠️ Manual harness (`dexverify/` in workspace root) |
| UI automation | ❌ Not implemented |
| Version gate | ⚠️ Policy documented (CR-001), not enforced |

---

## Notes

- Bytecode verification harness exists in workspace root (`C:\Users\user\AppData\Local\Temp\opencode\dexverify\`) — migrate to `purifree/test/`
- UI tests require emulator setup (AVD `kleinanzeigen_test` already configured)
- Consider using existing Morphe test infrastructure if available
