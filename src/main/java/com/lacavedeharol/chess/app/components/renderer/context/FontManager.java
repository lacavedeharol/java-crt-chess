package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages font caching for the application.
 */
class FontManager {

    private static FontManager instance;
    private final Map<Integer, Font> fontCache;
    private final Font baseFont;

    /**
     * Private constructor to prevent instantiation.
     */
    private FontManager() {
        fontCache = new HashMap<>();
        baseFont = loadBaseFont();
    }

    /**
     * Loads the Tiny5 font from the classpath.
     *
     * @return the loaded base font, or a fallback if loading fails.
     */
    private Font loadBaseFont() {
        try (InputStream is = FontManager.class.getResourceAsStream(
                "/fonts/Sixtyfour/Sixtyfour-Regular-VariableFont_BLED,SCAN.ttf")) {
            if (is == null)
                throw new IllegalStateException("Font resource not found: "
                        + "/fonts/Sixtyfour/Sixtyfour-Regular-VariableFont_BLED,SCAN.ttf");

            return Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException | IOException | IllegalStateException e) {
            return new Font("Segoe UI Symbol", Font.PLAIN, 12);
        }
    }

    /**
     * Returns the singleton instance of FontManager.
     * 
     * @return the singleton instance of FontManager.
     */
    static synchronized FontManager getInstance() {
        return instance != null ? instance : (instance = new FontManager());
    }

    /**
     * Returns the font of the specified size from the cache.
     * 
     * @param size the size of the font.
     * @return the font of the specified size.
     */
    Font getFont(int size) {
        return fontCache.computeIfAbsent(size, s -> baseFont.deriveFont(Font.PLAIN, (float) s));
    }
}
