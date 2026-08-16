package app.purifree.extension.extension;

/**
 * PuriFree extension entry point.
 * 
 * <p>Use this class for complex Java logic that cannot be expressed
 * purely through bytecode patches. Simple patches that override with
 * fixed values do not need to use or call extension code.</p>
 * 
 * <p>Example usage from a bytecode patch:</p>
 * <pre>{@code
 * INVOKE_STATIC Lapp/purifree/extension/extension/PuriFreeExtension;->shouldShowAds()Z {v0}
 * IF_EQZ v0, :hide_ads
 * }</pre>
 */
@SuppressWarnings("unused")
public class PuriFreeExtension {

    /**
     * Example extension method. Currently unused.
     * 
     * @return false to hide ads (placeholder for future multi-target patches)
     */
    public static boolean shouldShowAds() {
        // Complex Java logic goes here.
        // Simple patches that override with fixed values do not need to use or call extension code.
        return false;
    }
}
