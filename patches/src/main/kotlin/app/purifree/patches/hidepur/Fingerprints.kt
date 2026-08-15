package app.purifree.patches.hidepur

import app.morphe.patcher.Fingerprint

/**
 * Matches the method in `SettingsAndHelpFragment` that decides the visibility of the
 * Pur subscription entry in the Kleinanzeigen settings menu.
 *
 * The full internal type is not obfuscated and has been stable across tested app
 * versions. The method satisfies all of:
 * - name does not start with `access$`
 * - parameter list contains at least 5 boolean (`Z`) parameters
 * - parameter list contains at least one `String` parameter
 */
object SettingsAndHelpFragmentPurEntryFingerprint : Fingerprint(
    definingClass = "Lebk/ui/preferences/settings/settings_and_help/SettingsAndHelpFragment;",
    custom = { method, _ ->
        !method.name.startsWith("access$") &&
            method.parameterTypes.count { it.toString() == "Z" } >= 5 &&
            method.parameterTypes.any { it.toString() == "Ljava/lang/String;" }
    }
)
