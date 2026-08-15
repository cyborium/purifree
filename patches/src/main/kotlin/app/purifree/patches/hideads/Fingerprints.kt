package app.purifree.patches.hideads

import app.morphe.patcher.Fingerprint

/**
 * Matches the method that initialises the Liberty SDK, which is responsible for
 * loading ads and Microsoft Clarity analytics in Kleinanzeigen.
 *
 * Class-name based identification is unreliable because of obfuscation differences
 * between app versions. The string literal `"KEY_LIBERTY_REFRESH_INTERVAL"` has been
 * stable across app versions 2026.9.0 through 2026.32.0.
 */
object AdLoaderFingerprint : Fingerprint(
    strings = listOf("KEY_LIBERTY_REFRESH_INTERVAL"),
)
