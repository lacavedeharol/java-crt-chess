package com.lacavedeharol.chess.app.components.renderer.projection;

import java.awt.Graphics2D;
import java.awt.Polygon;

import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * Renders the squares of the chessboard.
 * 
 * @param <ProjectionRenderer> the type of renderer
 */
class SquaresRenderer implements ProjectionRenderer {

    /**
     * Renders the squares of the chessboard.
     * 
     * @param g2d            the graphics object
     * @param viewProjection the view projection matrix
     * @param width          the width of the window
     * @param height         the height of the window
     */
    @Override
    public void render(Graphics2D g2d, Matrix4 viewProjection, int width, int height) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean isLight = (row + col) % 2 == 0;
                g2d.setColor(isLight ? GraphicsUtils.LIGHT : GraphicsUtils.DARK);

                double x1 = col;
                double z1 = row;

                double x2 = col + 1;
                double z2 = row;

                double x3 = col + 1;
                double z3 = row + 1;

                double x4 = col;
                double z4 = row + 1;

                Polygon poly = new Polygon();
                addPoint(poly, viewProjection, new Vector3(x1, 0, z1), width, height);
                addPoint(poly, viewProjection, new Vector3(x2, 0, z2), width, height);
                addPoint(poly, viewProjection, new Vector3(x3, 0, z3), width, height);
                addPoint(poly, viewProjection, new Vector3(x4, 0, z4), width, height);

                g2d.fillPolygon(poly);
            }
        }
    }

    /**
     * Adds a point to the polygon.
     * 
     * @param p              the polygon
     * @param viewProjection the view projection matrix
     * @param v              the vector
     * @param w              the width of the window
     * @param h              the height of the window
     */
    private void addPoint(Polygon p, Matrix4 viewProjection, Vector3 v, int w, int h) {
        Vector3 proj = viewProjection.multiply(v);

        int sx = (int) ((proj.x + 1) * 0.5 * w);
        int sy = (int) ((1 - (proj.y + 1) * 0.5) * h);

        p.addPoint(sx, sy);
    }

}
