package app.purifree.patches.removetrackingparams

import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.patcher.extensions.InstructionExtensions.replaceInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.purifree.patches.shared.Constants.COMPATIBILITY_KLEINANZEIGEN
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.StringReference

/**
 * The exact UTM suffix the Kleinanzeigen app appends to public-profile share URLs
 * (`&utm_source=sharesheet&utm_campaign=socialbuttons&utm_medium=social_profil&utm_content=app_android`).
 */
private const val TRACKING_SUFFIX = "&utm_source=sharesheet&utm_campaign=socialbuttons&utm_medium=social_profil&utm_content=app_android"

@Suppress("unused")
val removeTrackingParametersFromShareUrlsPatch = bytecodePatch(
    name = "Remove tracking parameters from share URLs",
    description = "Strips UTM tracking parameters from URLs shared via the in-app share function.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_KLEINANZEIGEN)

    execute {
        val method = ProfileShareTrackingSuffixFingerprint.methodOrNull
            ?: return@execute

        val match = method.instructions
            .withIndex()
            .firstOrNull { (_, instruction) ->
                instruction.opcode == Opcode.CONST_STRING &&
                    instruction is ReferenceInstruction &&
                    (instruction.reference as? StringReference)?.string == TRACKING_SUFFIX
            }
            ?: return@execute

        method.replaceInstruction(
            match.index,
            "const-string v${(match.value as OneRegisterInstruction).registerA}, \"\""
        )
    }
}
