package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * A renderer for the sound toggle.
 */
class SoundToggleRenderer implements PlanarRenderer {

    private boolean isMuted = false;
    private Rectangle bounds;

    /**
     * Set the muted state of the renderer.
     * 
     * @param muted true to mute the renderer, false to unmute it
     */
    void setMuted(boolean muted) {
        this.isMuted = muted;
    }

    /**
     * Get the muted state of the renderer.
     * 
     * @return true if the renderer is muted, false otherwise
     */
    boolean isMuted() {
        return isMuted;
    }

    /**
     * Render the sound toggle.
     * 
     * @param g2d    the graphics context to draw on
     * @param width  the width of the renderer
     * @param height the height of the renderer
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        String icon = isMuted ? "🔇" : "🔊";

        int padding = Math.min(width, height) / 32;
        int fontSize = Math.min(width, height) / 32;

        g2d.setFont(FontManager.getInstance().getFont(fontSize));
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(icon);
        int textHeight = fm.getHeight();

        int x = width - padding - textWidth;
        int y = height - padding;

        g2d.setColor(GraphicsUtils.LIGHT);
        g2d.drawString(icon, x, y);

        bounds = new Rectangle(x, y - fm.getAscent(), textWidth, textHeight);
    }

    /**
     * Handle a click event.
     * 
     * @param p the point where the click occurred
     * @return true if the click occurred within the bounds of the renderer, false
     *         otherwise
     */
    boolean handleClick(Point p) {
        return (bounds != null && bounds.contains(p)) ? true : false;
    }

    /**
     * Check if the renderer is hovered.
     * 
     * @param p the point to check
     * @return true if the point is within the bounds of the renderer, false
     *         otherwise
     */
    boolean isHovered(Point p) {
        return (bounds != null && bounds.contains(p)) ? true : false;
    }
}
