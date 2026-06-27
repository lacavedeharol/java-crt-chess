package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * Abstract base class for menu renderers.
 * 
 * @param <E> The type of menu actions
 * 
 */
abstract class BaseMenuRenderer<E extends Enum<E>> implements PlanarRenderer {

    /**
     * Represents a menu item.
     * 
     * @param <E> The type of menu actions
     */
    protected static class MenuItem<E> {
        String text;
        E action;
        Rectangle bounds;
        int level;

        MenuItem(String text, E action, int level) {
            this.text = text;
            this.action = action;
            this.level = level;
        }

        MenuItem(String text, E action, boolean isSubItem) {
            this(text, action, isSubItem ? 1 : 0);
        }

        MenuItem(String text, E action) {
            this(text, action, 0);
        }
    }

    static final String HOVER_INDICATOR = ". ";
    protected final List<MenuItem<E>> items = new ArrayList<>();
    protected E hoveredAction;
    protected E pressedAction;

    /**
     * Set the action that is currently being pressed.
     * 
     * @param action The action to set as pressed
     */
    void setPressedAction(E action) {
        this.pressedAction = action;
    }

    /**
     * Get the action that is currently being pressed.
     * 
     * @return The action that is currently being pressed
     */
    E getPressedAction() {
        return pressedAction;
    }

    /**
     * Check if the menu should blink.
     * 
     * @return True if the menu should blink, false otherwise
     */
    protected boolean shouldBlink() {
        return GraphicsUtils.isBlink(0.25);
    }

    /**
     * Draw a menu item.
     * 
     * @param g2d        The graphics context to draw on
     * @param item       The menu item to draw
     * @param x          The x-coordinate of the menu item
     * @param y          The y-coordinate of the menu item
     * @param fm         The font metrics
     * @param alignRight True if the menu item should be right-aligned, false
     *                   otherwise
     */
    protected void drawItem(Graphics2D g2d, MenuItem<E> item, int x, int y, FontMetrics fm, boolean alignRight) {
        g2d.setColor(GraphicsUtils.LIGHT);

        String display = item.text;

        int w = fm.stringWidth(display);
        int finalX = alignRight ? x - w : x;

        int indicatorW = fm.stringWidth(HOVER_INDICATOR);

        boolean isHovered = (item.action == hoveredAction && hoveredAction != null);
        boolean isPressed = (item.action == pressedAction && pressedAction != null);

        if (isHovered) {
            if (alignRight) {
                finalX -= indicatorW;
                if (shouldBlink())
                    g2d.drawString(HOVER_INDICATOR, finalX - indicatorW, y);
            } else {
                if (shouldBlink())
                    g2d.drawString(HOVER_INDICATOR, finalX, y);
                finalX += indicatorW;
            }
        }

        item.bounds = new Rectangle(finalX, y - fm.getAscent(), w, fm.getHeight());

        if (isPressed) {
            Font originalFont = g2d.getFont();
            float newSize = Math.max(1, originalFont.getSize() * 0.9f);
            Font smallerFont = originalFont.deriveFont(newSize);
            g2d.setFont(smallerFont);
            FontMetrics smallFM = g2d.getFontMetrics();

            int newW = smallFM.stringWidth(display);
            int centerX = finalX + w / 2;
            int drawX = centerX - newW / 2;
            int origCY = y - fm.getAscent() + fm.getHeight() / 2;
            int drawY = origCY + smallFM.getAscent() - smallFM.getHeight() / 2;

            g2d.drawString(display, drawX, drawY);

            g2d.setFont(originalFont);
        } else
            g2d.drawString(display, finalX, y);

    }

    /**
     * Set the action that is currently being hovered.
     * 
     * @param action The action to set as hovered
     */
    void setHoveredAction(E action) {
        this.hoveredAction = action;
    }

    /**
     * Get the action that is currently being hovered.
     * 
     * @return The action that is currently being hovered
     */
    E getHoveredAction() {
        return hoveredAction;
    }
}
