package com.lacavedeharol.chess.app.components.renderer;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Handles mouse input for the menu.
 * 
 * @param <MenuInputHandler> the type of menu input handler.
 */
class MenuInputHandler extends MouseAdapter {
    private final GameRendererComponent gameRenderer;

    /**
     * Constructor.
     * 
     * @param gameRenderer the game renderer.
     */
    MenuInputHandler(GameRendererComponent gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.gameRenderer.addMouseListener(this);
        this.gameRenderer.addMouseMotionListener(this);
    }

    /**
     * Handles mouse move events.
     * 
     * @param e the mouse event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        if (gameRenderer.isPromoting()) {
            gameRenderer.handlePromotionHover(e.getPoint());
            return;
        }

        if (gameRenderer.isMenuOpen()) {
            if (gameRenderer.isSettingsOpen())
                gameRenderer.handleSettingsHover(e.getPoint());
            else
                gameRenderer.handleMenuHover(e.getPoint());
            gameRenderer.repaint();
            e.consume();
        }
    }

    /**
     * Handles mouse press events.
     * 
     * @param e the mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameRenderer.isPromoting()) {
            gameRenderer.handlePromotionClick(e.getPoint());
            e.consume();
            return;
        }

        gameRenderer.handleSoundTogglePress(e.getPoint());

        if (gameRenderer.isMenuOpen()) {
            if (gameRenderer.isSettingsOpen())
                gameRenderer.handleSettingsPress(e.getPoint());
            else
                gameRenderer.handleMenuPress(e.getPoint());
        }

        gameRenderer.repaint();
    }

    /**
     * Handles mouse release events.
     * 
     * @param e the mouse event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        gameRenderer.handleSoundToggleRelease(e.getPoint());

        if (gameRenderer.isMenuOpen()) {
            if (gameRenderer.isSettingsOpen())
                gameRenderer.handleSettingsRelease(e.getPoint());
            else
                gameRenderer.handleMenuRelease(e.getPoint());
        }

        gameRenderer.repaint();
    }

}
