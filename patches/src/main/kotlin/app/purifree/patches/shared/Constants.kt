package app.purifree.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    val COMPATIBILITY_KLEINANZEIGEN = Compatibility(
        name = "Kleinanzeigen", // App name as it appears in the Android launcher.
        packageName = "com.ebay.kleinanzeigen",
        apkFileType = ApkFileType.APK, // Preferred or recommended file type.
        appIconColor = 0x2EAD33, // Icon color in Morphe Manager. Usually the same color as the icon background.
        targets = listOf(
            // App version confirmed 100% working.
            AppTarget(
                version = "2026.32.0"
            ),
            AppTarget(
                version = "2026.32.1" // Verified 2026-08-16: bytecode structure identical to 2026.32.0
            )
        )
    )
}
