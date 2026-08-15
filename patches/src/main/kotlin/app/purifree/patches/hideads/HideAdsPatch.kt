package app.purifree.patches.hideads

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.purifree.patches.shared.Constants.COMPATIBILITY_KLEINANZEIGEN

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Hides sponsored ads and Google Ads in Kleinanzeigen and disables Microsoft Clarity analytics.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_KLEINANZEIGEN)

    execute {
        AdLoaderFingerprint.method.addInstruction(0, "return-void")
    }
}
