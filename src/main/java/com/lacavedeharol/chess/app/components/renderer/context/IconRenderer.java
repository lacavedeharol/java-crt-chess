package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 * Draw an icon.
 * 
 * @param g2d        the graphics context..
 * @param x          the x coordinate of the icon.
 * @param y          the y coordinate of the icon.
 * @param targetSize the target size of the icon.
 * @param icon       the icon to draw.
 * @param isPressed  true if the icon is pressed, false otherwise.
 */
class IconRenderer {
    static void drawIcon(Graphics2D g2d, int x, int y, int targetSize, BufferedImage icon,
            boolean isPressed) {

        if (icon == null)
            return;

        int iconW = targetSize;
        int iconH = targetSize;

        int iconX = x + (targetSize - iconW) / 2;
        int iconY = y + (targetSize - iconH) / 2;

        if (isPressed) {
            float scale = 0.9f;
            int newSize = Math.max(1, (int) (targetSize * scale));

            int smallIconX = x + (targetSize - newSize) / 2;
            int smallIconY = y + (targetSize - newSize) / 2;

            g2d.drawImage(icon, smallIconX, smallIconY, newSize, newSize, null);
        } else
            g2d.drawImage(icon, iconX, iconY, targetSize, targetSize, null);
    }

    /**
     * Get the bounds of the icon.
     * 
     * @param x       the x coordinate of the icon.
     * @param y       the y coordinate of the icon.
     * @param boxSize the size of the icon.
     * @return the bounds of the icon.
     */
    static Rectangle getBounds(int x, int y, int boxSize) {
        return new Rectangle(x, y, boxSize, boxSize);
    }
}
