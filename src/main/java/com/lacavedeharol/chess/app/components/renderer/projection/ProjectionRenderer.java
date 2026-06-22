package com.lacavedeharol.chess.app.components.renderer.projection;

import java.awt.Graphics2D;

/**
 * Renders the chess pieces on the board.
 * 
 * @param <ProjectionRenderer> the type of renderer.
 */
interface ProjectionRenderer {
    /**
     * Renders the chess pieces on the board.
     * 
     * @param g2d            the graphics context.
     * @param viewProjection the view-projection matrix.
     * @param width          the width of the board.
     * @param height         the height of the board.
     */
    void render(Graphics2D g2d, Matrix4 viewProjection, int width, int height);
}
