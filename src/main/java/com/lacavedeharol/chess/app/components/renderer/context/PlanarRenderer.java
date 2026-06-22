package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;

/**
 * Interface for planar renderers.
 */
interface PlanarRenderer {
    /**
     * Renders the planar renderer.
     * 
     * @param g2d    the graphics context.
     * @param width  the width of the renderer.
     * @param height the height of the renderer.
     */
    void render(Graphics2D g2d, int width, int height);
}
