package app.purifree.patches.hidepur

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.purifree.patches.shared.Constants.COMPATIBILITY_KLEINANZEIGEN
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Suppress("unused")
val hidePurPatch = bytecodePatch(
    name = "Hide Pur",
    description = "Hides the Pur ad-free subscription option from the settings menu.",
    default = true,
) {
    compatibleWith(COMPATIBILITY_KLEINANZEIGEN)

    execute {
        val method = SettingsAndHelpFragmentPurEntryFingerprint.method
        val implementation = method.implementation
            ?: throw PatchException("Failed to find the implementation of method ${method.name} in ${method.definingClass}")
        val instructions = implementation.instructions

        // The Pur row in the settings screen sets its subtitle from
        // R.string.ka_settings_v2_top_ad_free_subscription_pur_subtitle
        // (resource id 0x7f14140d). The row's visibility gate is the first
        // IF_EQZ that follows that constant. Anchoring on the subtitle resource
        // keeps the patch away from the neighbouring rows' logged-in/out gates.
        val purSubtitleResId = 0x7f14140d
        val subtitleIndex = instructions.indexOfFirst { instr ->
            instr is NarrowLiteralInstruction && instr.narrowLiteral == purSubtitleResId
        }
        if (subtitleIndex == -1) {
            throw PatchException(
                "Failed to find the Pur subtitle resource id $purSubtitleResId in method ${method.name} in ${method.definingClass}"
            )
        }

        val ifEqzIndex = instructions.withIndex().firstOrNull { (index, instr) ->
            index > subtitleIndex && instr.opcode == Opcode.IF_EQZ
        }?.index
        if (ifEqzIndex == null) {
            throw PatchException(
                "Failed to find the IF_EQZ gating the Pur row in method ${method.name} in ${method.definingClass}"
            )
        }

        val register = (instructions[ifEqzIndex] as OneRegisterInstruction).registerA
        method.addInstruction(ifEqzIndex, "const/16 v$register, 0x0")
    }
}
