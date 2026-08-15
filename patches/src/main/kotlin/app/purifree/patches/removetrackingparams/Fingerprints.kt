package app.purifree.patches.removetrackingparams

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string

/**
 * Matches the method that builds the public-profile share URL in Kleinanzeigen by the
 * exact UTM tracking suffix it appends client-side.
 *
 * Verified against the `2026.32.0` APK (all three ABI bundles): this is the only place
 * in the app where a share URL is constructed with UTM query parameters appended in the
 * DEX. The string literal is stable and independent of obfuscated class names, so the
 * patch resolves it with `methodOrNull` and silently no-ops on versions where the
 * literal no longer exists.
 */
object ProfileShareTrackingSuffixFingerprint : Fingerprint(
    filters = listOf(
        string(
            "&utm_source=sharesheet&utm_campaign=socialbuttons&utm_medium=social_profil&utm_content=app_android"
        )
    )
)
