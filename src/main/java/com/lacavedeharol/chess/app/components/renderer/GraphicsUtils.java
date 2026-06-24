package com.lacavedeharol.chess.app.components.renderer;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * GraphicsUtils class provides utility methods for graphics operations.
 */
public abstract class GraphicsUtils {

    private GraphicsUtils() {
    }

    /**
     * The dark and light colors.
     */
    public static final Color DARK, LIGHT;

    static {
        DARK = Color.decode("#222323");
        LIGHT = Color.decode("#f0f6f0");
    }

    /**
     * Gets a Graphics2D object from a Graphics object.
     * 
     * @param g the Graphics object.
     * @return the Graphics2D object.
     */
    public static Graphics2D getGraphics2D(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        return g2d;
    }

    /**
     * Draws an overlay on the Graphics2D object.
     * 
     * @param g2d    the Graphics2D object.
     * @param width  the width of the overlay.
     * @param height the height of the overlay.
     */
    public static void drawOverlay(Graphics2D g2d, int width, int height) {
        g2d.setColor(new Color(0, 0, 0, 96));
        g2d.fillRect(0, 0, width, height);
    }

    /**
     * Gets the global time in seconds.
     * 
     * @return the global time in seconds.
     */
    public static double getGlobalTime() {
        return System.currentTimeMillis() / 1000.0;
    }

    /**
     * Checks if the given interval has blinked.
     * 
     * @param intervalSeconds the interval in seconds.
     * @return true if the interval has blinked, false otherwise.
     */
    public static boolean isBlink(double intervalSeconds) {
        return (getGlobalTime() / intervalSeconds) % 2.0 < 1.0;
    }

    /**
     * Gets a sine wave value.
     * 
     * @param frequency the frequency of the sine wave.
     * @return the sine wave value.
     */
    public static double getSineWave(double frequency) {
        return Math.sin(getGlobalTime() * frequency * 2.0 * Math.PI);
    }
}
