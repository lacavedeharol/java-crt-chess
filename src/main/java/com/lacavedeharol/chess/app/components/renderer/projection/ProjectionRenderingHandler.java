package com.lacavedeharol.chess.app.components.renderer.projection;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Handles the projection rendering.
 */
public class ProjectionRenderingHandler {

    private final SquaresRenderer squaresRenderer;
    private final MoveHighlightRenderer moveRenderer;
    private final PiecesRenderer piecesRenderer;
    private final List<ProjectionRenderer> renderers;

    /**
     * Constructor.
     */
    public ProjectionRenderingHandler() {
        squaresRenderer = new SquaresRenderer();
        moveRenderer = new MoveHighlightRenderer();
        piecesRenderer = new PiecesRenderer();

        renderers = List.of(squaresRenderer, moveRenderer, piecesRenderer);
    }

    /**
     * Renders the projection.
     * 
     * @param g2d            the graphics object.
     * @param viewProjection the view projection matrix.
     * @param width          the width of the window.
     * @param height         the height of the window.
     */
    public void render(Graphics2D g2d, Matrix4 viewProjection, int width, int height) {
        for (ProjectionRenderer renderer : renderers)
            renderer.render(g2d, viewProjection, width, height);
    }

    /**
     * Updates the pieces.
     * 
     * @param pieces the pieces.
     */
    public void updatePieces(ChessPiece[][] pieces) {
        piecesRenderer.setPieces(pieces);
    }

    /**
     * Sets the dragged piece.
     * 
     * @param piece    the piece.
     * @param position the position.
     */
    public void setDraggedPiece(ChessPiece piece, Vector3 position) {
        piecesRenderer.setDraggedPiece(piece, position);
    }

    /**
     * Sets the legal moves.
     * 
     * @param moves the moves.
     */
    public void setLegalMoves(List<Point> moves) {
        moveRenderer.setLegalMoves(moves);
    }

    /**
     * Sets the rotation.
     * 
     * @param rotationY the rotation.
     */
    public void setRotation(double rotationY) {
        piecesRenderer.setRotation(rotationY);
    }

    /**
     * Sets the guides visibility.
     * 
     * @param visible the visibility.
     */
    public void setGuidesVisible(boolean visible) {
        moveRenderer.setVisible(visible);
    }

    /**
     * Checks if the guides are visible.
     * 
     * @return true if the guides are visible, false otherwise.
     */
    public boolean areGuidesVisible() {
        return moveRenderer.isVisible();
    }
}
