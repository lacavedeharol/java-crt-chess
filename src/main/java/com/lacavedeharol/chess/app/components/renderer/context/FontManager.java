package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages font caching for the application.
 */
class FontManager {

    private static FontManager instance;
    private final Map<Integer, Font> fontCache;

    /**
     * Private constructor to prevent instantiation.
     */
    private FontManager() {
        fontCache = new HashMap<>();
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
        return fontCache.computeIfAbsent(size, s -> new Font("Segoe UI Symbol", Font.PLAIN, s));
    }
}
