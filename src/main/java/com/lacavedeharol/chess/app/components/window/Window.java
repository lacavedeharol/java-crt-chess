package com.lacavedeharol.chess.app.components.window;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.lacavedeharol.chess.ai.ChessAIFactory;
import com.lacavedeharol.chess.app.components.renderer.AssetManager;
import com.lacavedeharol.chess.app.components.renderer.GameRendererComponent;
import com.lacavedeharol.chess.app.game.GameController;
import com.lacavedeharol.chess.core.state.GameState;

/**
 * The main window of the application.
 */
public class Window extends JFrame {

    /**
     * Constructor.
     */
    public Window() {
        new WindowInputHandler(this);
        setTitle("Java CRT Chess");
        add(new GamePanel());
        pack();
        setMinimumSize(this.getPreferredSize());
        setIconImage(AssetManager.getInstance().getImage("icons").getSubimage(8, 0, 48, 48));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * The game panel.
     */
    private class GamePanel extends JPanel {
        private GameController activeController;

        /**
         * Constructor.
         */
        private GamePanel() {
            setLayout(new BorderLayout());
            initializeGame();
        }

        /**
         * Initializes the game.
         */
        private void initializeGame() {
            GameState gameState = new GameState();
            GameRendererComponent rendererComponent = new GameRendererComponent(8, 8);
            rendererComponent.updatePieces(gameState.getPieces());

            rendererComponent.setOnGameStart(config -> {
                if (activeController != null)
                    activeController.dispose();

                activeController = new GameController(gameState, rendererComponent,
                        config.playAsWhite() ? null : ChessAIFactory.create(config.difficulty(), true),
                        config.playAsWhite() ? ChessAIFactory.create(config.difficulty(), false) : null,
                        config.timerMode());
            });

            rendererComponent.setOnExit(() -> System.exit(0));
            this.add(rendererComponent, BorderLayout.CENTER);
            rendererComponent.setFocusable(true);
            rendererComponent.requestFocusInWindow();
        }
    }

}
