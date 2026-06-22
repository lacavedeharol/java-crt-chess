package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.lacavedeharol.chess.app.components.renderer.AssetManager;

/**
 * A renderer for the sound toggle.
 */
class SoundMenuRenderer extends BaseMenuRenderer<SoundAction> {

    private boolean isMuted = false, visible = true;
    private Rectangle bounds;
    private BufferedImage speakerSprite, mutedSprite;

    /**
     * Constructor.
     */
    SoundMenuRenderer() {
        loadIcons();
    }

    /**
     * Load the icons.
     */
    private void loadIcons() {
        BufferedImage sheet = AssetManager.getInstance().getImage("icons");
        if (sheet != null) {
            speakerSprite = sheet.getSubimage(0, 8, 8, 8);
            mutedSprite = sheet.getSubimage(0, 16, 8, 8);
        }
    }

    /**
     * Set the muted state of the renderer.
     * 
     * @param muted true to mute the renderer, false to unmute it.
     */
    void setMuted(boolean muted) {
        this.isMuted = muted;
    }

    /**
     * Get the muted state of the renderer.
     * 
     * @return true if the renderer is muted, false otherwise.
     */
    boolean isMuted() {
        return isMuted;
    }

    /**
     * Set the renderer visibility.
     * 
     * @param visible true to make the renderer visible, false to hide it.
     */
    void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Render the sound toggle.
     * 
     * @param g2d    the graphics context to draw on.
     * @param width  the width of the renderer.
     * @param height the height of the renderer.
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        if (!visible)
            return;

        int padding = Math.min(width, height) / 48;
        int fontSize = Math.min(width, height) / 48;
        int boxSize = fontSize + fontSize / 3;

        int x = width - padding - boxSize;
        int y = height - padding - boxSize;

        bounds = IconRenderer.getBounds(x, y, boxSize);

        BufferedImage icon = isMuted ? mutedSprite : speakerSprite;
        IconRenderer.drawIcon(g2d, x, y, boxSize, icon, (pressedAction == SoundAction.TOGGLE_SOUND));
    }

    /**
     * Get the action at the given point.
     * 
     * @param p the point to check.
     * @return the action at the given point.
     */
    SoundAction getActionAt(Point p) {
        if (visible && bounds != null && bounds.contains(p))
            return SoundAction.TOGGLE_SOUND;
        return SoundAction.NONE;
    }
}