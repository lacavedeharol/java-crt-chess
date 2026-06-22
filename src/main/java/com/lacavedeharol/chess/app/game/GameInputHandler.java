package com.lacavedeharol.chess.app.game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Point;

/**
 * GameInputHandler class handles mouse input for the game.
 */
class GameInputHandler extends MouseAdapter {
    private final GameController controller;

    /**
     * Constructor for GameInputHandler.
     * 
     * @param controller the GameController to be used.
     */
    public GameInputHandler(GameController controller) {
        this.controller = controller;
    }

    /**
     * Handles mouse movement events.
     * 
     * @param e the MouseEvent.
     */
    public void mouseMoved(MouseEvent e) {
        if (e.isConsumed())
            return;

        if (controller.rendererComponent.isMenuOpen())
            return;
        controller.rendererComponent.handleSettingsHover(e.getPoint());
    }

    /**
     * Handles mouse press events.
     * 
     * @param e the MouseEvent.
     */
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed())
            return;
        if (controller.rendererComponent.isMenuOpen()) {
            controller.rendererComponent.handleMenuPress(e.getPoint());
            controller.rendererComponent.handleSoundTogglePress(e.getPoint());
            return;
        }

        controller.rendererComponent.handleSoundTogglePress(e.getPoint());
        controller.rendererComponent.handleSettingsPress(e.getPoint());
        if (controller.rendererComponent.isSettingsOpen())
            return;

        if (controller.rendererComponent.getSquareAt(e.getX(), e.getY()) == null)
            return;

        if (controller.rendererComponent.isPromoting())
            return;
        if (e.getButton() != MouseEvent.BUTTON1)
            return;

        if (controller.rendererComponent.isSettingsOpen())
            return;

        Point square = controller.rendererComponent.getSquareAt(e.getX(), e.getY());
        controller.onSquarePressed(square);

        if (square != null)
            controller.onDrag(square);

    }

    /**
     * Handles mouse dragged events.
     * 
     * @param e the MouseEvent.
     */
    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed())
            return;
        if (controller.rendererComponent.isPromoting() || controller.rendererComponent.isMenuOpen())
            return;
        Point square = controller.rendererComponent.getSquareAt(e.getX(), e.getY());
        controller.onDrag(square);
    }

    /**
     * Handles mouse released events.
     * 
     * @param e the MouseEvent.
     */
    public void mouseReleased(MouseEvent e) {
        if (e.isConsumed())
            return;
        if (controller.rendererComponent.isMenuOpen())
            return;

        controller.rendererComponent.handleSettingsRelease(e.getPoint());

        if (controller.rendererComponent.isPromoting())
            return;
        Point target = controller.rendererComponent.getSquareAt(e.getX(), e.getY());
        controller.onSquareReleased(target);
    }

    /**
     * Handles mouse wheel events.
     * 
     * @param e the MouseWheelEvent.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed())
            return;
        if (controller.rendererComponent.isMenuOpen())
            return;

        if (controller.rendererComponent.isSettingsOpen()) {
            controller.rendererComponent.closeSettings();
            return;
        }
        if (controller.rendererComponent.isPromoting())
            return;

        double tiltChange = Math.toRadians(e.getPreciseWheelRotation() * 2.0);
        controller.rendererComponent.changeTilt(tiltChange);

    }
}
