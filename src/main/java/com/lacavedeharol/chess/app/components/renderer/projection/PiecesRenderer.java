package com.lacavedeharol.chess.app.components.renderer.projection;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lacavedeharol.chess.app.components.renderer.AssetManager;
import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Renders the chess pieces on the board.
 * 
 * @param <ProjectionRenderer> the type of renderer.
 */
class PiecesRenderer implements ProjectionRenderer {

    private ChessPiece[][] pieces;
    private final Map<String, BufferedImage> pieceSprites = new HashMap<>();
    private BufferedImage shadowSprite;

    /**
     * Creates a new instance of the PiecesRenderer.
     */
    PiecesRenderer() {
        loadSprites();
    }

    /**
     * Loads the sprites for the chess pieces.
     */
    private void loadSprites() {
        BufferedImage sheet = AssetManager.getInstance().getImage("chess_pieces");
        if (sheet == null)
            return;

        int width = sheet.getWidth() / 7;
        int height = sheet.getHeight() / 2;

        for (int i = 0; i < 6; i++) {
            pieceSprites.put("WHITE_" + ChessPiece.PieceType.values()[i].name(),
                    sheet.getSubimage(i * width, 0, width, height));
            pieceSprites.put("BLACK_" + ChessPiece.PieceType.values()[i].name(),
                    sheet.getSubimage(i * width, height, width, height));
        }
        shadowSprite = sheet.getSubimage(192, 0, width, height);
    }

    /**
     * Sets the pieces to render.
     * 
     * @param pieces The pieces to render.
     */
    void setPieces(ChessPiece[][] pieces) {
        this.pieces = pieces;
    }

    private ChessPiece draggedPiece;
    private Vector3 draggedPosition;
    private double rotationY = 0;

    /**
     * Sets the rotation of the pieces.
     * 
     * @param rotationY The rotation around the Y-axis.
     */
    void setRotation(double rotationY) {
        this.rotationY = rotationY;
    }

    /**
     * Sets the piece that is being dragged.
     * 
     * @param piece    The piece to set as dragged.
     * @param position The position of the dragged piece.
     */
    void setDraggedPiece(ChessPiece piece, Vector3 position) {
        this.draggedPiece = piece;
        this.draggedPosition = position;
    }

    /**
     * Renders the chess pieces on the board.
     * 
     * @param g2d            The graphics context to draw on.
     * @param viewProjection The view-projection matrix.
     * @param width          The width of the board.
     * @param height         The height of the board.
     */
    @Override
    public void render(Graphics2D g2d, Matrix4 viewProjection, int width, int height) {
        if (pieces == null)
            return;

        List<PieceRenderCommand> renderCommands = new ArrayList<>();

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                ChessPiece piece = pieces[col][row];
                if (piece != null && piece != draggedPiece) {
                    double x = piece.getFile() + 0.5;
                    double zOffset = 0.5 + 0.25 * Math.cos(rotationY);
                    double z = piece.getRank() + zOffset;
                    Vector3 pos = new Vector3(x, 0, z);

                    Vector3 proj = viewProjection.multiply(pos);

                    renderCommands.add(new PieceRenderCommand(piece, pos, proj.w));
                }
            }
        }

        if (draggedPiece != null && draggedPosition != null) {
            Vector3 proj = viewProjection.multiply(draggedPosition);
            renderCommands.add(new PieceRenderCommand(draggedPiece, draggedPosition, proj.w));
        }

        renderCommands.sort((a, b) -> Double.compare(b.depth, a.depth));

        for (PieceRenderCommand cmd : renderCommands)
            drawPiece(g2d, viewProjection, width, height, cmd.piece, cmd.position);
    }

    /**
     * Represents a command to render a piece.
     */
    private static class PieceRenderCommand {
        ChessPiece piece;
        Vector3 position;
        double depth;

        PieceRenderCommand(ChessPiece piece, Vector3 position, double depth) {
            this.piece = piece;
            this.position = position;
            this.depth = depth;
        }
    }

    /**
     * Draws a piece on the board.
     * 
     * @param g2d            The graphics context to draw on.
     * @param viewProjection The view-projection matrix.
     * @param w              The width of the board.
     * @param h              The height of the board.
     * @param piece          The piece to draw.
     * @param customPos      The custom position of the piece.
     */
    private void drawPiece(Graphics2D g2d, Matrix4 viewProjection, int w, int h, ChessPiece piece, Vector3 customPos) {
        if (piece.getPieceType() == null)
            return;
        String key = (piece.isWhite() ? "WHITE_" : "BLACK_") + piece.getPieceType().name();
        BufferedImage sprite = pieceSprites.get(key);
        if (sprite == null)
            return;

        Vector3 basePos;
        if (customPos != null)
            basePos = customPos;
        else {
            double x = piece.getFile() + 0.5;
            double zOffset = 0.5 + 0.25 * Math.cos(rotationY);
            double z = piece.getRank() + zOffset;
            basePos = new Vector3(x, 0, z);
        }

        Vector3 projBase = viewProjection.multiply(basePos);
        int sx = (int) ((projBase.x + 1) * 0.5 * w);
        int sy = (int) ((1 - (projBase.y + 1) * 0.5) * h);

        double baseScale = h * 1.0;
        int spriteHeight = (int) (baseScale / Math.abs(projBase.w));

        double aspect = (double) sprite.getWidth() / sprite.getHeight();
        int spriteWidth = (int) (spriteHeight * aspect);

        if (shadowSprite != null) {
            Vector3 shadowWorldPos = new Vector3(basePos.x, 0, basePos.z);
            Vector3 projShadow = viewProjection.multiply(shadowWorldPos);

            int shadowSx = (int) ((projShadow.x + 1) * 0.5 * w);
            int shadowSy = (int) ((1 - (projShadow.y + 1) * 0.5) * h);
            int shadowHeight = (int) (baseScale / Math.abs(projShadow.w));
            int shadowWidth = (int) (shadowHeight * aspect);

            g2d.drawImage(shadowSprite, shadowSx - shadowWidth / 2, shadowSy - shadowHeight, shadowWidth, shadowHeight,
                    null);
        }

        g2d.drawImage(sprite, sx - spriteWidth / 2, sy - spriteHeight, spriteWidth, spriteHeight, null);
    }
}
