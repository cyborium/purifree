# PuriFree

**Multi-target Morphe patch bundle** — currently supports Kleinanzeigen (hide ads, hide Pur subscription, strip UTM tracking from share URLs).

**Brand:** PuriFree (umbrella project for multiple app targets)

## Supported Apps

| App | Package | Patches | Verified Versions | Status |
|-----|---------|---------|-------------------|--------|
| Kleinanzeigen | `com.ebay.kleinanzeigen` | Hide ads, Hide Pur, Remove tracking | 2026.32.0, 2026.32.1 | ✅ Production |
| *More apps* | — | — | — | 🚧 Planned |

**See [Version Compatibility](#version-compatibility) for detailed patch-by-version matrix.**

## Patches (Kleinanzeigen)

### Version Compatibility

**Policy (CR-001):** Patches are only declared compatible with versions that have been verified (bytecode + UI testing).

| App Version | Hide Ads | Hide Pur | Remove Tracking | Verified Date | Notes |
|-------------|----------|----------|-----------------|---------------|-------|
| **2026.32.0** | ✅ Verified | ✅ Verified | ✅ Verified | 2026-08-15 | Production release |
| **2026.32.1** | ✅ Verified | ✅ Verified | ✅ Verified | 2026-08-16 | Bytecode structure identical to 2026.32.0 |
| **< 2026.32.0** | ❌ Unsupported | ❌ Unsupported | ❌ Unsupported | — | Not supported — fingerprints target 2026.32.x bytecode structure |

**Legend:**
- ✅ **Verified**: Bytecode + UI tested on this version
- ❌ **Unsupported**: Version not supported — patches target 2026.32.0 bytecode structure and may crash or fail to apply on other versions

To request support for a specific version, [open an issue](https://github.com/cyborium/purifree/issues) with the version number and APK build code.

<!-- PATCHES_START EXPANDED -->

<!-- Do not modify this section by hand. The patch list is generated when release.yml creates a new release.
     
     If you wish for the patches list to be collapsed, then remove the word 'EXPANDED' from the comment tag above.

     If you wish to manually keep this list updated then remove the PATCHES_START and PATCHES_END 
     comment blocks entirely. -->

#### A list of your patches will automatically be shown here after your first patches release is created.

&nbsp;

## Extensions Module

PuriFree includes an extensions module (`extensions/extension/`) for patches requiring:
- **New components**: Activities, Services, BroadcastReceivers, ContentProviders
- **Custom Java/Kotlin code**: Complex logic not expressible via bytecode patches alone
- **Resource additions**: Layouts, strings, drawables, styles
- **Manifest modifications**: Permissions, intents, metadata

Current Kleinanzeigen patches use **bytecode-only** modifications (no extension calls). The module is scaffolded for future multi-target patches that require it.

## Automated Testing Roadmap

Multi-target support requires automated testing across app versions. Planned infrastructure:

| Component | Purpose | Status |
|-----------|---------|--------|
| **APK archive** | Store historical APKs per app/version | 📋 TODO |
| **CI matrix** | Test each patch against declared versions | 📋 TODO |
| **Bytecode verification** | Assert expected instructions inserted | 📋 TODO |
| **UI automation** | Verify runtime behavior (e.g., Pur row absent) | 📋 TODO |
| **Version gate** | Block release if untested version declared | 📋 TODO |

See `docs/` in the workspace root for detailed specifications and requirements.

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for:
- Build setup and commands
- Code conventions and fingerprint guidelines
- Commit message format (Conventional Commits)
- Branch workflow (dev → main)
- Adding new patches (6-step checklist)
- Version verification policy (CR-001)

---

## Usage

Click here to add these patches to Morphe: https://morphe.software/add-source?github=cyborium/purifree

Or manually add this repository URL as a patch source in Morphe: https://github.com/cyborium/purifree

### Building

To build PuriFree,
you can follow the [Morphe documentation](https://github.com/MorpheApp/morphe-documentation).

## License

PuriFree is licensed under the [GNU General Public License v3.0](LICENSE)
