package com.lacavedeharol.chess.app.components.renderer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JPanel;
import javax.swing.Timer;

import com.lacavedeharol.chess.ai.Opponent;
import com.lacavedeharol.chess.app.components.renderer.context.ContextRenderingHandler;
import com.lacavedeharol.chess.app.components.renderer.context.TimerMode;
import com.lacavedeharol.chess.app.components.renderer.projection.Matrix4;
import com.lacavedeharol.chess.app.components.renderer.projection.ProjectionRenderingHandler;
import com.lacavedeharol.chess.app.components.renderer.projection.Vector3;
import com.lacavedeharol.chess.core.ChessPiece;

/**
 * GameRendererComponent class extends JPanel and handles the rendering of the
 * game.
 */
public class GameRendererComponent extends JPanel {

    /**
     * The projection matrix.
     */
    private Matrix4 projection;

    /**
     * The view matrix.
     */
    private Matrix4 view;

    /**
     * The view projection matrix.
     */
    private Matrix4 viewProjection;

    /**
     * The inverse view projection matrix.
     */
    private Matrix4 inverseViewProjection;

    /**
     * The rotation Y.
     */
    private double rotationY;

    /**
     * The tilt X.
     */
    private double tiltX;

    /**
     * The camera distance.
     */
    private double cameraDistance;

    /**
     * The target camera distance.
     */
    private double targetCameraDistance;

    /**
     * The auto rotation speed.
     */
    private double autoRotationSpeed;

    /**
     * The target rotation.
     */
    private Double targetRotation;

    /**
     * The target tilt X.
     */
    private Double targetTiltX;

    /**
     * The context handler.
     */
    private ContextRenderingHandler contextHandler;

    /**
     * The projection handler.
     */
    private ProjectionRenderingHandler projectionHandler;

    /**
     * The animation timer.
     */
    private Timer animationTimer;

    /**
     * The context timer.
     */
    private Timer contextTimer;

    /**
     * The width.
     */
    private int width;

    /**
     * The height.
     */
    private int height;

    /**
     * The on game start callback.
     */
    private Consumer<GameConfig> onGameStart;

    /**
     * The on exit callback.
     */
    private Runnable onExit;

    /**
     * The on restart callback.
     */
    private Runnable onRestart;

    /**
     * The on game resumed callback.
     */
    private Runnable onGameResumed;

    /**
     * The cell size.
     */
    private final int CELL_SIZE = 96;

    /**
     * Constructor for GameRendererComponent.
     * 
     * @param width  the width of the component.
     * @param height the height of the component.
     */
    public GameRendererComponent(int width, int height) {
        this.width = width;
        this.height = height;
        this.setOpaque(false);
        initRenderers();
        this.cameraDistance = 16.0;
        enterMenuMode();
        new MenuInputHandler(this);
    }

    /**
     * Initializes the renderers.
     */
    private void initRenderers() {
        contextHandler = new ContextRenderingHandler();
        projectionHandler = new ProjectionRenderingHandler();
        setupContextCallbacks();
        animationTimer = new Timer(16, e -> {
            updateCamera();
            this.repaint();
        });
        animationTimer.start();
        contextTimer = new Timer(500, e -> this.repaint());
    }

    /**
     * Sets up the context callbacks.
     */
    private void setupContextCallbacks() {
        contextHandler.setOnGameStart(config -> {
            targetCameraDistance = 10.0;
            targetRotation = null;

            rotationY = !config.playAsWhite() ? 0 : Math.PI;
            projectionHandler.setRotation(rotationY);
            updateMatrices();

            if (this.onGameStart != null)
                this.onGameStart.accept(config);
        });

        contextHandler.setOnExit(() -> {
            if (this.onExit != null)
                this.onExit.run();
        });

        contextHandler.setOnRestart(() -> {
            if (this.onRestart != null)
                this.onRestart.run();
        });

        contextHandler.setOnToggleViewMode(this::toggleViewMode);

        contextHandler.setOnToggleGuides(() -> {
            projectionHandler.setGuidesVisible(contextHandler.areGuidesOn());
        });

        contextHandler.setOnToggleCaptured(() -> {
            this.repaint();
        });

        contextHandler.setOnEnterMenu(this::enterMenuMode);
    }

    /**
     * Updates the camera position.
     */
    private void updateCamera() {
        if (Math.abs(cameraDistance - targetCameraDistance) > 0.1) {
            cameraDistance += (targetCameraDistance - cameraDistance) * 0.1;
            updateMatrices();
        }

        boolean matricesUpdated = false;

        if (targetRotation != null) {
            double diff = targetRotation - rotationY;
            while (diff <= -Math.PI)
                diff += 2 * Math.PI;
            while (diff > Math.PI)
                diff -= 2 * Math.PI;

            if (Math.abs(diff) > 0.01) {
                rotationY += diff * 0.1;
                projectionHandler.setRotation(rotationY);
                matricesUpdated = true;
            }
        } else if (contextHandler.isMenuOpen()) {
            rotationY += autoRotationSpeed;
            projectionHandler.setRotation(rotationY);
            matricesUpdated = true;
        }

        if (targetTiltX != null) {
            double diff = targetTiltX - tiltX;
            if (Math.abs(diff) > 0.01) {
                tiltX += diff * 0.1;
                matricesUpdated = true;

                boolean is3D = Math.toDegrees(tiltX) < 85;
                contextHandler.setSettings3D(is3D);
            } else {
                tiltX = targetTiltX;
                targetTiltX = null;
            }
        }

        if (matricesUpdated)
            updateMatrices();
    }

    /**
     * Sets the context message.
     * 
     * @param message the message to be set.
     * @param animate true to animate the message, false otherwise.
     */
    public void setContextMessage(String message, boolean animate) {
        contextHandler.setContextMessage(message, animate);
        if (message != null && !message.isEmpty()) {
            if (!contextTimer.isRunning())
                contextTimer.start();
        } else
            contextTimer.stop();
        this.repaint();
    }

    /**
     * Sets the context message.
     * 
     * @param message the message to be set.
     */
    public void setContextMessage(String message) {
        setContextMessage(message, true);
    }

    /**
     * Updates the captured pieces.
     * 
     * @param pieces the list of captured pieces.
     */
    public void updateCapturedPieces(List<ChessPiece> pieces) {
        contextHandler.setCapturedPieces(pieces);
        this.repaint();
    }

    /**
     * Updates the pieces.
     * 
     * @param pieces the 2D array of pieces.
     */
    public void updatePieces(ChessPiece[][] pieces) {
        projectionHandler.updatePieces(pieces);
        this.repaint();
    }

    /**
     * Sets the dragged piece.
     * 
     * @param piece    the piece to be dragged.
     * @param position the position of the piece.
     */
    public void setDraggedPiece(ChessPiece piece, Vector3 position) {
        projectionHandler.setDraggedPiece(piece, position);
        this.repaint();
    }

    /**
     * Sets the legal moves.
     * 
     * @param moves the list of legal moves.
     */
    public void setLegalMoves(List<Point> moves) {
        projectionHandler.setLegalMoves(moves);
        this.repaint();
    }

    /**
     * Updates the view matrix.
     */
    private void updateViewMatrix() {
        Matrix4 worldMove = Matrix4.translate(-4, 0, -4);
        Matrix4 worldMirror = Matrix4.scale(-1, 1, 1);
        Matrix4 worldTilt = Matrix4.rotateX(tiltX);
        Matrix4 worldRot = Matrix4.rotateY(rotationY);
        Matrix4 cameraDist = Matrix4.translate(0, 0, -cameraDistance);

        view = new Matrix4();
        view = view.multiply(cameraDist);
        view = view.multiply(worldTilt);
        view = view.multiply(worldRot);
        view = view.multiply(worldMirror);
        view = view.multiply(worldMove);
    }

    /**
     * Sets the rotation angle around the Y-axis.
     * 
     * @param rotationY the rotation angle in radians.
     */
    public void setRotationY(double rotationY) {
        this.rotationY = rotationY;
        projectionHandler.setRotation(rotationY);
        updateMatrices();
        this.repaint();
    }

    /**
     * Gets the rotation angle around the Y-axis.
     * 
     * @return the rotation angle in radians.
     */
    public double getRotationY() {
        return rotationY;
    }

    /**
     * Updates the matrices.
     */
    private void updateMatrices() {
        updateViewMatrix();

        double aspect = 1.0;
        if (getWidth() > 0 && getHeight() > 0)
            aspect = (double) getWidth() / getHeight();
        projection = Matrix4.perspective(Math.toRadians(60), aspect, 0.1, 100.0);

        if (view != null) {
            viewProjection = projection.multiply(view);
            inverseViewProjection = viewProjection.invert();
        }
    }

    /**
     * Paints the component.
     * 
     * @param g the Graphics object.
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = GraphicsUtils.getGraphics2D(g);

        g2d.setColor(GraphicsUtils.DARK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        updateMatrices();

        projectionHandler.render(g2d, viewProjection, getWidth(), getHeight());
        contextHandler.render(g2d, getWidth(), getHeight());
    }

    /**
     * Gets the square at the given screen coordinates.
     * 
     * @param screenX the x-coordinate of the screen.
     * @param screenY the y-coordinate of the screen.
     * @return the square at the given screen coordinates.
     */
    public Point getSquareAt(int screenX, int screenY) {
        Vector3 intersection = getRayIntersectionOnBoard(screenX,
                screenY);
        if (intersection == null)
            return null;

        int file = (int) Math.floor(intersection.x);
        int rank = (int) Math.floor(intersection.z);

        if (file >= 0 && file < 8 && rank >= 0 && rank < 8)
            return new Point(file, rank);

        return null;
    }

    /**
     * Gets the intersection point of the ray with the board.
     * 
     * @param screenX the x-coordinate of the screen.
     * @param screenY the y-coordinate of the screen.
     * @return the intersection point of the ray with the board.
     */
    public Vector3 getRayIntersectionOnBoard(int screenX,
            int screenY) {
        if (inverseViewProjection == null)
            return null;

        double x = (2.0 * screenX) / getWidth() - 1.0;
        double y = 1.0 - (2.0 * screenY) / getHeight();

        Vector3 rayStart = inverseViewProjection
                .multiply(new Vector3(x, y, -1.0));
        Vector3 rayEnd = inverseViewProjection
                .multiply(new Vector3(x, y, 1.0));

        Vector3 dir = new Vector3(
                rayEnd.x - rayStart.x,
                rayEnd.y - rayStart.y,
                rayEnd.z - rayStart.z);

        if (Math.abs(dir.y) < 1e-6)
            return null;

        double t = -rayStart.y / dir.y;
        if (t < 0)
            return null;

        return new Vector3(
                rayStart.x + dir.x * t,
                0,
                rayStart.z + dir.z * t);
    }

    /**
     * Gets the preferred size of the component.
     * 
     * @return the preferred size of the component.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CELL_SIZE * width, CELL_SIZE * height);
    }

    /**
     * Starts the promotion process.
     * 
     * @param isWhite    true if the piece to be promoted is white, false otherwise.
     * @param onSelected the consumer to be called when a piece type is selected.
     */
    public void startPromotion(boolean isWhite, Consumer<ChessPiece.PieceType> onSelected) {
        contextHandler.startPromotion(isWhite, onSelected);
        this.repaint();
    }

    /**
     * Checks if a piece is being promoted.
     * 
     * @return true if a piece is being promoted, false otherwise.
     */
    public boolean isPromoting() {
        return contextHandler.isPromoting();
    }

    /**
     * Handles a promotion click.
     * 
     * @param p the point where the click occurred.
     */
    public void handlePromotionClick(Point p) {
        contextHandler.handlePromotionClick(p);
        this.repaint();
    }

    /**
     * Handles a promotion hover.
     * 
     * @param p the point where the hover occurred.
     */
    public void handlePromotionHover(Point p) {
        contextHandler.handlePromotionHover(p);
        this.repaint();
    }

    /**
     * Checks if the menu is open.
     * 
     * @return true if the menu is open, false otherwise.
     */
    public boolean isMenuOpen() {
        return contextHandler.isMenuOpen();
    }

    /**
     * Handles a menu hover.
     * 
     * @param p the point where the hover occurred.
     */
    public void handleMenuHover(Point p) {
        if (isPromoting()) {
            handlePromotionHover(p);
            return;
        }
        if (!contextHandler.isMenuOpen())
            return;

        contextHandler.handleMenuHover(p);
        updateMenuRotationTarget();
    }

    /**
     * Handles a menu press.
     * 
     * @param p the point where the press occurred.
     */
    public void handleMenuPress(Point p) {
        contextHandler.handleMenuPress(p);
        this.repaint();
    }

    /**
     * Handles a menu release.
     * 
     * @param p the point where the release occurred.
     */
    public void handleMenuRelease(Point p) {
        contextHandler.handleMenuRelease(p);
        updateMenuRotationTarget();
        this.repaint();
    }

    /**
     * Updates the target rotation angle based on the menu state.
     */
    private void updateMenuRotationTarget() {
        if (contextHandler.isMenuOpen() && contextHandler.isConfigureExpanded()) {
            switch (contextHandler.getSidePreference()) {
                case WHITE -> targetRotation = Math.PI;
                case BLACK -> targetRotation = 0.0;
                default -> targetRotation = null;
            }
        } else
            targetRotation = null;
    }

    /**
     * Gets the game mode.
     */
    public enum GameMode {
        /**
         * The player is playing as white.
         */
        PLAY_AS_WHITE,
        /**
         * The player is playing as black.
         */
        PLAY_AS_BLACK
    }

    /**
     * Gets the game configuration.
     * 
     * @param playAsWhite true if the player is playing as white, false otherwise.
     * @param difficulty  the difficulty of the AI player.
     * @param timerMode   the timer mode.
     */
    public record GameConfig(boolean playAsWhite, Opponent difficulty,
            TimerMode timerMode) {
    }

    /**
     * Sets the consumer to be called when the game starts.
     * 
     * @param onGameStart the consumer to be called when the game starts.
     */
    public void setOnGameStart(Consumer<GameConfig> onGameStart) {
        this.onGameStart = onGameStart;
    }

    /**
     * Sets the runnable to be called when the game exits.
     * 
     * @param onExit the runnable to be called when the game exits.
     */
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    /**
     * Checks if the settings are open.
     * 
     * @return true if the settings are open, false otherwise.
     */
    public boolean isSettingsOpen() {
        return contextHandler.isSettingsOpen();
    }

    /**
     * Closes the settings.
     */
    public void closeSettings() {
        contextHandler.closeSettings();
        this.repaint();
        if (onGameResumed != null)
            onGameResumed.run();
    }

    /**
     * Handles a settings hover.
     * 
     * @param p the point where the hover occurred.
     */
    public void handleSettingsHover(Point p) {
        contextHandler.handleSettingsHover(p);
        this.repaint();
    }

    /**
     * Handles a settings press.
     * 
     * @param p the point where the press occurred.
     */
    public void handleSettingsPress(Point p) {
        contextHandler.handleSettingsPress(p);
        this.repaint();
    }

    /**
     * Handles a settings release.
     * 
     * @param p the point where the release occurred.
     */
    public void handleSettingsRelease(Point p) {
        contextHandler.handleSettingsRelease(p);
        this.repaint();
    }

    /**
     * Toggles the mute state.
     */
    public void toggleMute() {
        contextHandler.toggleMute();
        this.repaint();
    }

    /**
     * Checks if the game is muted.
     * 
     * @return true if the game is muted, false otherwise.
     */
    public boolean isMuted() {
        return contextHandler.isMuted();
    }

    /**
     * Shows the game over menu.
     * 
     * @param status the status of the game.
     */
    public void showGameOverMenu(String status) {
        contextHandler.showGameOverMenu(status);
        this.repaint();
    }

    /**
     * Handles a sound toggle click.
     * 
     * @param p the point where the click occurred.
     */
    public void handleSoundToggleClick(Point p) {
        contextHandler.handleSoundToggleClick(p);
        this.repaint();
    }

    /**
     * Enters the menu mode.
     */
    private void enterMenuMode() {

        targetCameraDistance = 16.0;
        autoRotationSpeed = new Random().nextBoolean() ? 0.000625 : -0.000625;
        tiltX = Math.toRadians(45);
        targetTiltX = null;

        targetRotation = null;

        updateMatrices();
        this.repaint();
    }

    /**
     * Changes the tilt angle.
     * 
     * @param delta the change in tilt angle.
     */
    public void changeTilt(double delta) {
        if (contextHandler.isMenuOpen() || contextHandler.isGameOver())
            return;

        targetTiltX = null;
        tiltX += delta;
        tiltX = Math.max(Math.toRadians(30), Math.min(Math.toRadians(90), tiltX));

        boolean is3D = Math.toDegrees(tiltX) < 85;
        contextHandler.setSettings3D(is3D);

        updateMatrices();
        this.repaint();
    }

    /**
     * Toggles the view mode.
     */
    private void toggleViewMode() {
        boolean isCurrent3D = Math.toDegrees(tiltX) < 80;
        targetTiltX = isCurrent3D ? Math.toRadians(90) : Math.toRadians(45);
        contextHandler.setSettings3D(!isCurrent3D);
    }

    /**
     * Sets the runnable to be called when the game restarts.
     * 
     * @param onRestart the runnable to be called when the game restarts.
     */
    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    /**
     * Sets the runnable to be called when the game resumes.
     * 
     * @param onGameResumed the runnable to be called when the game resumes.
     */
    public void setOnGameResumed(Runnable onGameResumed) {
        this.onGameResumed = onGameResumed;
    }

    /**
     * Cycles the auto-promotion setting.
     */
    public void cycleAutoPromotion() {
        contextHandler.cycleAutoPromotion();
        this.repaint();
    }
}
