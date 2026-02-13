package com.lacavedeharol.chess.app.components.renderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Manages the loading and caching of game assets.
 */
public class AssetManager {

    private static AssetManager instance;
    private final Map<String, BufferedImage> imageCache = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private AssetManager() {
    }

    /**
     * Get the singleton instance of the AssetManager.
     * 
     * @return The singleton instance of the AssetManager.
     */
    public static synchronized AssetManager getInstance() {
        return instance != null ? instance : (instance = new AssetManager());
    }

    /**
     * Get an image from the cache or load it from the file system.
     * 
     * @param relativePath The relative path to the image.
     * @return The image.
     */
    public BufferedImage getImage(String relativePath) {
        return imageCache.computeIfAbsent(relativePath, path -> {
            try {
                return ImageIO.read(AssetManager.class.getResource("/images/" + path + ".png"));
            } catch (IOException | IllegalArgumentException ex) {
                System.err.println("Failed to load image: " + path);
                return null;
            }
        });
    }

    /**
     * Clear the image cache.
     */
    public void clearCache() {
        imageCache.clear();
    }

    /**
     * Check if an image is cached.
     * 
     * @param path The path to the image.
     * @return True if the image is cached, false otherwise.
     */
    public boolean isCached(String path) {
        return imageCache.containsKey(path);
    }
}
