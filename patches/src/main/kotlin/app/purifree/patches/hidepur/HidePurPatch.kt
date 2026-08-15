package app.purifree.patches.hidepur

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.purifree.patches.shared.Constants.COMPATIBILITY_KLEINANZEIGEN
import com.android.tools.smali.dexlib2.Opcode
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

        val ifEqzIndex = instructions.indexOfFirst { it.opcode == Opcode.IF_EQZ }
        if (ifEqzIndex == -1) {
            throw PatchException("Failed to find an IF_EQZ instruction in method ${method.name} in ${method.definingClass}")
        }

        val register = (instructions[ifEqzIndex] as OneRegisterInstruction).registerA
        method.addInstruction(ifEqzIndex, "const/16 v$register, 0x0")
    }
}
