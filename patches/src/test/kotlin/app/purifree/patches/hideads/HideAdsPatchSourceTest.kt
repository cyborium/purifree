package app.purifree.patches.hideads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Unit tests for [HideAdsPatch].
 *
 * These tests verify the patch source code contains expected patterns
 * and does not contain anti-patterns (e.g., hardcoded class names).
 */
class HideAdsPatchSourceTest {

    @Test
    fun `patch inserts return-void at index 0 of Liberty init method`() {
        val source = readPatchSource()

        assertTrue(source.contains("addInstruction(0, \"return-void\")"))
        assertTrue(source.contains("AdLoaderFingerprint.method"))
    }

    @Test
    fun `fingerprint anchors on KEY_LIBERTY_REFRESH_INTERVAL string`() {
        val fingerprintSource = readFingerprintSource()

        assertTrue(fingerprintSource.contains("KEY_LIBERTY_REFRESH_INTERVAL"))
        assertFalse(fingerprintSource.contains("classDef.type.contains")) // Obfuscation-risk pattern
    }

    @Test
    fun `patch description mentions Clarity analytics`() {
        val source = readPatchSource()

        assertTrue(source.contains("Microsoft Clarity"))
        assertTrue(source.contains("analytics"))
    }

    private fun readPatchSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/hideads/HideAdsPatch.kt"),
        StandardCharsets.UTF_8
    )

    private fun readFingerprintSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/hideads/Fingerprints.kt"),
        StandardCharsets.UTF_8
    )
}
