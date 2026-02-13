package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.app.components.renderer.AssetManager;
import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;
import com.lacavedeharol.chess.core.ChessPiece.PieceType;

/**
 * Renderer for the promotion menu.
 */
class PromotionRenderer implements PlanarRenderer {

    private boolean active = false, isWhite;
    private PieceType[] options = { PieceType.values()[1], PieceType.values()[2], PieceType.values()[3],
            PieceType.values()[4] };
    private BufferedImage[] images = new BufferedImage[4];
    private List<Rectangle> bounds = new ArrayList<>();
    private PieceType hoveredType = null;

    /**
     * Constructor.
     */
    PromotionRenderer() {
    }

    /**
     * Start the promotion menu.
     * 
     * @param isWhite true if the promotion is for a white piece, false otherwise
     */
    void startPromotion(boolean isWhite) {
        this.isWhite = isWhite;
        this.active = true;
        loadImages();
    }

    /**
     * Stop the promotion menu.
     */
    void stopPromotion() {
        this.active = false;
        this.hoveredType = null;
    }

    /**
     * Check if the promotion menu is active.
     * 
     * @return true if the promotion menu is active, false otherwise
     */
    boolean isActive() {
        return active;
    }

    /**
     * Load the images for the promotion menu.
     */
    private void loadImages() {
        BufferedImage spriteSheet = AssetManager.getInstance().getImage("chess_pieces");
        int yOffset = isWhite ? 0 : 64;

        images[0] = spriteSheet.getSubimage(1 * 32, yOffset, 32, 64);
        images[1] = spriteSheet.getSubimage(2 * 32, yOffset, 32, 64);
        images[2] = spriteSheet.getSubimage(3 * 32, yOffset, 32, 64);
        images[3] = spriteSheet.getSubimage(4 * 32, yOffset, 32, 64);
    }

    /**
     * Render the promotion menu.
     * 
     * @param g2d    the graphics context
     * @param width  the width of the screen
     * @param height the height of the screen
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        if (!active)
            return;

        GraphicsUtils.drawOverlay(g2d, width, height);

        bounds.clear();
        int imgW = 32 * 2;
        int imgH = 64 * 2;

        int totalW = options.length * imgW;
        int currentX = (width - totalW) / 2;
        int currentY = (height - imgH) / 2;

        for (int i = 0; i < options.length; i++) {
            BufferedImage img = images[i];

            int drawY = currentY;
            if (options[i] == hoveredType) {
                int offset = (int) ((GraphicsUtils.getSineWave(1.25) - 1.0) * 4.0);
                drawY += offset;
            }

            g2d.drawImage(img, currentX, drawY, imgW, imgH, null);
            bounds.add(new Rectangle(currentX, currentY, imgW, imgH));

            currentX += imgW;
        }

        g2d.setColor(GraphicsUtils.LIGHT);
        g2d.setFont(FontManager.getInstance().getFont(32));
        String title = "Promote Pawn";
        int titleW = g2d.getFontMetrics().stringWidth(title);
        int titleH = g2d.getFontMetrics().getHeight();
        int titleX = (width - titleW) / 2;
        int titleY = currentY - titleH;
        g2d.drawString(title, titleX, titleY + g2d.getFontMetrics().getAscent());

    }

    /**
     * Get the piece at the given point.
     * 
     * @param p the point to check
     * @return the piece at the given point
     */
    PieceType getPieceAt(Point p) {
        if (!active)
            return null;
        for (int i = 0; i < bounds.size(); i++)
            if (bounds.get(i).contains(p))
                return options[i];
        return null;
    }

    /**
     * Set the hovered piece.
     * 
     * @param p the point to check
     */
    void setHovered(Point p) {
        if (!active)
            return;
        hoveredType = getPieceAt(p);
    }

    /**
     * Get the hovered piece type.
     * 
     * @return the hovered piece type
     */
    PieceType getHoveredType() {
        return hoveredType;
    }
}
