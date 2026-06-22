package com.lacavedeharol.chess.app.components.renderer.projection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.List;

import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * A renderer that highlights legal moves on the board.
 * 
 * @param <ProjectionRenderer> the type of renderer.
 */
class MoveHighlightRenderer implements ProjectionRenderer {

    private List<Point> legalMoves;

    /**
     * Set the legal moves to highlight.
     * 
     * @param moves the legal moves to highlight.
     */
    void setLegalMoves(List<Point> moves) {
        this.legalMoves = moves;
    }

    private boolean isVisible = true;

    /**
     * Set the visibility of the renderer.
     * 
     * @param visible true to make the renderer visible, false to hide it.
     */
    void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Get the visibility of the renderer.
     * 
     * @return true if the renderer is visible, false otherwise.
     */
    boolean isVisible() {
        return isVisible;
    }

    /**
     * Render the legal moves on the board.
     * 
     * @param g2d            The graphics context to draw on.
     * @param viewProjection The view projection matrix.
     * @param width          The width of the board.
     * @param height         The height of the board.
     */
    @Override
    public void render(Graphics2D g2d, Matrix4 viewProjection, int width, int height) {
        if (!isVisible || legalMoves == null || legalMoves.isEmpty())
            return;

        for (Point move : legalMoves)
            drawTarget(g2d, viewProjection, width, height, move.x, move.y);
    }

    /**
     * Draw a target-like highlight on the board.
     * 
     * @param g2d            The graphics context to draw on.
     * @param viewProjection The view projection matrix.
     * @param w              The width of the board.
     * @param h              The height of the board.
     * @param x              The x coordinate of the target.
     * @param z              The z coordinate of the target.
     */
    private void drawTarget(Graphics2D g2d, Matrix4 viewProjection, int w, int h, int x, int z) {
        boolean isLight = (x + z) % 2 != 0;
        Color baseColor = isLight ? GraphicsUtils.DARK : GraphicsUtils.LIGHT;
        Color maskColor = isLight ? GraphicsUtils.LIGHT : GraphicsUtils.DARK;

        double oscillation = GraphicsUtils.getSineWave(1.5);

        double baseInset = 0.05;
        double baseGapSize = 0.5;

        double insetMod = (oscillation + 1.0) * 0.5 * 0.025;
        double gapMod = (oscillation + 1.0) * 0.5 * 0.05;

        double gapSize = baseGapSize - gapMod;
        double borderThickness = 0.05;

        double inset = baseInset + insetMod;
        drawProjectedRect(g2d, viewProjection, w, h,
                x + inset, z + inset,
                1.0 - 2 * inset, 1.0 - 2 * inset,
                0.02, baseColor);

        double maskW = gapSize;
        double maskH = 1.0;
        drawProjectedRect(g2d, viewProjection, w, h,
                x + 0.5 - maskW / 2, z,
                maskW, maskH,
                0.025, maskColor);

        double maskW2 = 1.0;
        double maskH2 = gapSize;
        drawProjectedRect(g2d, viewProjection, w, h,
                x, z + 0.5 - maskH2 / 2,
                maskW2, maskH2,
                0.025, maskColor);

        double innerSize = (1.0 - 2 * inset) - 2 * borderThickness;
        drawProjectedRect(g2d, viewProjection, w, h,
                x + 0.5 - innerSize / 2, z + 0.5 - innerSize / 2,
                innerSize, innerSize,
                0.025, maskColor);
    }

    /**
     * Draw a rectangle in 3D space projected to 2D screen space.
     * 
     * @param g2d            The graphics context to draw on.
     * @param viewProjection The view projection matrix.
     * @param screenW        The width of the screen.
     * @param screenH        The height of the screen.
     * @param x              The x coordinate of the rectangle.
     * @param z              The z coordinate of the rectangle.
     * @param w              The width of the rectangle.
     * @param h              The height of the rectangle.
     * @param yLevel         The y level of the rectangle.
     * @param color          The color of the rectangle.
     */
    private void drawProjectedRect(Graphics2D g2d, Matrix4 viewProjection, int screenW, int screenH,
            double x, double z, double w, double h, double yLevel, Color color) {

        g2d.setColor(color);

        Polygon poly = new Polygon();
        addPoint(poly, viewProjection, new Vector3(x, yLevel, z), screenW, screenH);
        addPoint(poly, viewProjection, new Vector3(x + w, yLevel, z), screenW, screenH);
        addPoint(poly, viewProjection, new Vector3(x + w, yLevel, z + h), screenW, screenH);
        addPoint(poly, viewProjection, new Vector3(x, yLevel, z + h), screenW, screenH);

        g2d.fillPolygon(poly);
    }

    /**
     * Add a point to the polygon.
     * 
     * @param p              The polygon to add the point to.
     * @param viewProjection The view projection matrix.
     * @param v              The point to add.
     * @param w              The width of the screen.
     * @param h              The height of the screen.
     */
    private void addPoint(Polygon p, Matrix4 viewProjection, Vector3 v, int w, int h) {
        Vector3 proj = viewProjection.multiply(v);
        int sx = (int) ((proj.x + 1) * 0.5 * w);
        int sy = (int) ((1 - (proj.y + 1) * 0.5) * h);
        p.addPoint(sx, sy);
    }
}
