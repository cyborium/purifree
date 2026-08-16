package app.purifree.patches.hidepur

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Unit tests for [HidePurPatch].
 *
 * These tests verify the patch source code anchors on the Pur subtitle
 * resource constant (not the first IF_EQZ, which gates a different row).
 */
class HidePurPatchSourceTest {

    @Test
    fun `patch anchors on pur_subtitle resource constant`() {
        val source = readPatchSource()

        assertTrue(source.contains("purSubtitleResId"))
        assertTrue(source.contains("0x7f14140d")) // R.string.ka_settings_v2_top_ad_free_subscription_pur_subtitle
    }

    @Test
    fun `patch finds IF_EQZ after subtitle constant`() {
        val source = readPatchSource()

        assertTrue(source.contains("index > subtitleIndex"))
        assertTrue(source.contains("IF_EQZ"))
    }

    @Test
    fun `patch uses const16 for register-agnostic zeroing`() {
        val source = readPatchSource()

        assertTrue(source.contains("const/16"))
        assertFalse(source.contains("const/4")) // const/4 only supports v0-v15
    }

    @Test
    fun `patch throws PatchException on missing subtitle constant`() {
        val source = readPatchSource()

        assertTrue(source.contains("PatchException"))
        assertTrue(source.contains("Failed to find the Pur subtitle"))
    }

    @Test
    fun `fingerprint matches method with at least 5 Z params and at least 1 String`() {
        val fingerprintSource = readFingerprintSource()

        assertTrue(fingerprintSource.contains(">= 5"))
        assertTrue(fingerprintSource.contains("Z"))
        assertTrue(fingerprintSource.contains("String"))
        assertTrue(fingerprintSource.contains("SettingsAndHelpFragment"))
    }

    @Test
    fun `patch does not target first IF_EQZ (broken approach)`() {
        val source = readPatchSource()

        // The old broken approach targeted the first IF_EQZ in the method,
        // which gates the Profil row (not Pur). This test ensures we don't
        // regress to that pattern.
        assertFalse(source.contains("indexOfFirst { it.opcode == Opcode.IF_EQZ }"))
        assertFalse(source.contains("first IF_EQZ"))
    }

    private fun readPatchSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/hidepur/HidePurPatch.kt"),
        StandardCharsets.UTF_8
    )

    private fun readFingerprintSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/hidepur/Fingerprints.kt"),
        StandardCharsets.UTF_8
    )
}
