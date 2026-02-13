package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;
import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Renders the context of the game.
 */
class ContextRenderer implements PlanarRenderer {

    /**
     * Represents a captured piece with its timestamp.
     */
    private static class CapturedItem {
        final ChessPiece piece;
        final double timestamp;

        /**
         * Creates a new CapturedItem.
         * 
         * @param piece     the captured piece
         * @param timestamp the timestamp of the capture
         */
        CapturedItem(ChessPiece piece, double timestamp) {
            this.piece = piece;
            this.timestamp = timestamp;
        }
    }

    private String message = "";
    private List<CapturedItem> capturedPieces = new ArrayList<>();
    private boolean isAnimated = true;
    private boolean isPaused = false;

    /**
     * Sets the message to be displayed.
     * 
     * @param message the message to be displayed
     * @param animate whether the message should be animated
     */
    void setMessage(String message, boolean animate) {
        this.message = message;
        this.isAnimated = animate;
    }

    /**
     * Sets the message to be displayed.
     * 
     * @param message the message to be displayed
     */
    void setMessage(String message) {
        this.message = message;
        this.isAnimated = true;
    }

    /**
     * Sets whether the game is paused.
     * 
     * @param paused whether the game is paused
     */
    void setPaused(boolean paused) {
        this.isPaused = paused;
    }

    /**
     * Sets the captured pieces.
     * 
     * @param pieces the captured pieces
     */
    void setCapturedPieces(List<ChessPiece> pieces) {
        List<CapturedItem> newItems = new ArrayList<>();
        double now = GraphicsUtils.getGlobalTime();

        for (ChessPiece p : pieces) {
            CapturedItem existing = null;
            for (CapturedItem item : capturedPieces) {
                if (item.piece == p) {
                    existing = item;
                    break;
                }
            }

            newItems.add(existing != null ? existing : new CapturedItem(p, now));
        }
        this.capturedPieces = newItems;
    }

    private boolean isCapturedVisible = true;

    /**
     * Sets whether the captured pieces are visible.
     * 
     * @param visible whether the captured pieces are visible
     */
    void setCapturedVisible(boolean visible) {
        this.isCapturedVisible = visible;
    }

    /**
     * Returns whether the captured pieces are visible.
     * 
     * @return whether the captured pieces are visible
     */
    boolean isCapturedVisible() {
        return isCapturedVisible;
    }

    private boolean isContextVisible = true;

    /**
     * Sets whether the context is visible.
     * 
     * @param visible whether the context is visible
     */
    void setContextVisible(boolean visible) {
        this.isContextVisible = visible;
    }

    /**
     * Returns whether the context is visible.
     * 
     * @return whether the context is visible
     */
    boolean isContextVisible() {
        return isContextVisible;
    }

    /**
     * Renders the context.
     * 
     * @param g2d    the Graphics2D object to render to
     * @param width  the width of the canvas
     * @param height the height of the canvas
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        int padding = Math.min(width, height) / 32;
        int fontSize = Math.min(width, height) / 64;
        if (isCapturedVisible && !capturedPieces.isEmpty()) {

            g2d.setColor(GraphicsUtils.LIGHT);
            g2d.setFont(FontManager.getInstance().getFont(fontSize));
            double now = GraphicsUtils.getGlobalTime();
            int count = 0;

            for (int i = capturedPieces.size() - 1; i >= 0; i--) {
                CapturedItem item = capturedPieces.get(i);
                if (now - item.timestamp > 10.0)
                    continue;

                String name = item.piece.getPieceType().toString().toLowerCase();
                String color = item.piece.isWhite() ? "white" : "black";
                String capturedString = "Captured: " + color + " " + name;
                g2d.drawString(capturedString, width - padding - g2d.getFontMetrics().stringWidth(capturedString),
                        (height / 2) + g2d.getFontMetrics().getAscent() + (count * g2d.getFontMetrics().getHeight()));
                count++;
            }
        }

        if (!isContextVisible || message == null || message.isEmpty())
            return;

        String finalMessage = message;

        if (isPaused)
            finalMessage = "(Game Paused) " + finalMessage;

        if (isAnimated) {
            int dots = (int) ((GraphicsUtils.getGlobalTime() * 2.0) % 3) + 1;
            finalMessage = finalMessage + " " + ".".repeat(dots);
        }

        g2d.setColor(GraphicsUtils.LIGHT);
        g2d.setFont(FontManager.getInstance().getFont(fontSize));
        g2d.drawString(finalMessage, padding, height - padding);
    }

}
