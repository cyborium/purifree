package app.purifree.patches.removetrackingparams

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * Unit tests for [RemoveTrackingParamsPatch].
 *
 * These tests verify the patch replaces the UTM suffix literal with an
 * empty string (not early return, which is an alternative approach).
 */
class RemoveTrackingParamsPatchSourceTest {

    @Test
    fun `patch defines exact UTM tracking suffix constant`() {
        val source = readPatchSource()

        assertTrue(source.contains("TRACKING_SUFFIX"))
        assertTrue(source.contains("&utm_source=sharesheet"))
        assertTrue(source.contains("utm_campaign=socialbuttons"))
        assertTrue(source.contains("utm_medium=social_profil"))
        assertTrue(source.contains("utm_content=app_android"))
    }

    @Test
    fun `patch replaces const-string with empty string`() {
        val source = readPatchSource()

        assertTrue(source.contains("replaceInstruction"))
        assertTrue(source.contains("const-string"))
        assertTrue(source.contains("\"\"")) // Empty string replacement
    }

    @Test
    fun `patch uses methodOrNull for optional fingerprint`() {
        val source = readPatchSource()

        assertTrue(source.contains("methodOrNull"))
        assertTrue(source.contains("return@execute")) // Silent no-op on miss
    }

    @Test
    fun `fingerprint uses string filter (not class name)`() {
        val fingerprintSource = readFingerprintSource()

        assertTrue(fingerprintSource.contains("string("))
        assertTrue(fingerprintSource.contains("filters = listOf"))
        assertFalse(fingerprintSource.contains("classDef.type")) // Obfuscation-risk
    }

    @Test
    fun `patch description mentions public-profile share URL`() {
        val source = readPatchSource()

        assertTrue(source.contains("public-profile"))
        assertTrue(source.contains("share"))
    }

    private fun readPatchSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/removetrackingparams/RemoveTrackingParamsPatch.kt"),
        StandardCharsets.UTF_8
    )

    private fun readFingerprintSource(): String = Files.readString(
        Path.of("src/main/kotlin/app/purifree/patches/removetrackingparams/Fingerprints.kt"),
        StandardCharsets.UTF_8
    )
}
