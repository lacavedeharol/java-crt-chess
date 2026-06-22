package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.function.Consumer;

import com.lacavedeharol.chess.app.components.renderer.GameRendererComponent.GameConfig;
import com.lacavedeharol.chess.app.game.SoundManager;
import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.ChessPiece.PieceType;

/**
 * Class handling the rendering of the context.
 */
public class ContextRenderingHandler {

    private final ContextRenderer contextRenderer;
    private final PromotionRenderer promotionRenderer;
    private final MainMenuRenderer menuRenderer;
    private final SettingsMenuRenderer settingsRenderer;
    private final SoundToggleRenderer soundToggleRenderer;
    private final List<PlanarRenderer> renderers;

    private boolean isMenuOpen = true;

    private Consumer<PieceType> onPromotionSelected;
    private Runnable onExit, onRestart;
    private Consumer<GameConfig> onGameStart;
    private Runnable onToggleViewMode, onToggleGuides, onToggleCaptured, onEnterMenu;

    /**
     * Constructor.
     */
    public ContextRenderingHandler() {
        contextRenderer = new ContextRenderer();
        promotionRenderer = new PromotionRenderer();
        menuRenderer = new MainMenuRenderer();
        settingsRenderer = new SettingsMenuRenderer();
        soundToggleRenderer = new SoundToggleRenderer();

        renderers = List.of(contextRenderer, promotionRenderer, menuRenderer, settingsRenderer, soundToggleRenderer);

        enterMenuMode();
    }

    /**
     * Renders the context.
     * 
     * @param g2d    the graphics object to render to.
     * @param width  the width of the window.
     * @param height the height of the window.
     */
    public void render(Graphics2D g2d, int width, int height) {
        for (PlanarRenderer renderer : renderers)
            renderer.render(g2d, width, height);
    }

    /**
     * Sets the callback to be called when a game is started.
     * 
     * @param onGameStart the callback to be called when a game is started.
     */
    public void setOnGameStart(Consumer<GameConfig> onGameStart) {
        this.onGameStart = onGameStart;
    }

    /**
     * Sets the callback to be called when the game is exited.
     * 
     * @param onExit the callback to be called when the game is exited.
     */
    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    /**
     * Sets the callback to be called when the game is restarted.
     * 
     * @param onRestart the callback to be called when the game is restarted.
     */
    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    /**
     * Sets the callback to be called when the menu is entered.
     * 
     * @param onEnterMenu the callback to be called when the menu is entered.
     */
    public void setOnEnterMenu(Runnable onEnterMenu) {
        this.onEnterMenu = onEnterMenu;
    }

    /**
     * Sets the callback to be called when the view mode is toggled.
     * 
     * @param onToggleViewMode the callback to be called when the view mode is
     *                         toggled.
     */
    public void setOnToggleViewMode(Runnable onToggleViewMode) {
        this.onToggleViewMode = onToggleViewMode;
    }

    /**
     * Sets the callback to be called when the guides are toggled.
     * 
     * @param onToggleGuides the callback to be called when the guides are toggled.
     */
    public void setOnToggleGuides(Runnable onToggleGuides) {
        this.onToggleGuides = onToggleGuides;
    }

    /**
     * Sets the callback to be called when the captured pieces are toggled.
     * 
     * @param onToggleCaptured the callback to be called when the captured pieces
     *                         are toggled.
     */
    public void setOnToggleCaptured(Runnable onToggleCaptured) {
        this.onToggleCaptured = onToggleCaptured;
    }

    /**
     * Sets the context message.
     * 
     * @param message the message to set.
     * @param animate whether to animate the message.
     */
    public void setContextMessage(String message, boolean animate) {
        if (settingsRenderer.isGameOver())
            message = "";
        contextRenderer.setMessage(message, animate);
    }

    /**
     * Sets the context message.
     * 
     * @param message the message to set.
     */
    public void setContextMessage(String message) {
        setContextMessage(message, true);
    }

    /**
     * Sets the captured pieces.
     * 
     * @param pieces the pieces to set.
     */
    public void setCapturedPieces(List<ChessPiece> pieces) {
        contextRenderer.setCapturedPieces(pieces);
    }

    /**
     * Sets the visibility of the captured pieces.
     * 
     * @param visible whether the captured pieces are visible.
     */
    public void setCapturedVisible(boolean visible) {
        contextRenderer.setCapturedVisible(visible);
        settingsRenderer.setCapturedOn(visible);
    }

    /**
     * Returns whether the captured pieces are visible.
     * 
     * @return whether the captured pieces are visible.
     */
    public boolean isCapturedVisible() {
        return contextRenderer.isCapturedVisible();
    }

    /**
     * Starts the promotion process.
     * 
     * @param isWhite    whether the pieces to promote are white.
     * @param onSelected the callback to be called when a piece is selected.
     */
    public void startPromotion(boolean isWhite, Consumer<PieceType> onSelected) {
        if (settingsRenderer.getAutoPromotion() != AutoPromotion.OFF) {
            onSelected.accept(switch (settingsRenderer.getAutoPromotion()) {
                case QUEEN -> PieceType.QUEEN;
                case ROOK -> PieceType.ROOK;
                case BISHOP -> PieceType.BISHOP;
                case KNIGHT -> PieceType.KNIGHT;
                default -> PieceType.QUEEN;
            });
            return;
        }

        this.onPromotionSelected = onSelected;
        promotionRenderer.startPromotion(isWhite);
        contextRenderer.setPaused(true);
    }

    /**
     * Returns whether a promotion is active.
     * 
     * @return whether a promotion is active.
     */
    public boolean isPromoting() {
        return promotionRenderer.isActive();
    }

    /**
     * Handles a promotion click.
     * 
     * @param p the point where the click occurred.
     */
    public void handlePromotionClick(Point p) {
        if (!isPromoting())
            return;

        PieceType type = promotionRenderer.getPieceAt(p);
        if (type != null) {
            promotionRenderer.stopPromotion();
            contextRenderer.setPaused(false);
            if (onPromotionSelected != null)
                onPromotionSelected.accept(type);
            SoundManager.getInstance().playSound("menu_click");
        }
    }

    /**
     * Handles a promotion hover.
     * 
     * @param p the point where the hover occurred.
     */
    public void handlePromotionHover(Point p) {
        if (!isPromoting())
            return;
        PieceType current = promotionRenderer.getHoveredType();
        promotionRenderer.setHovered(p);
        if (promotionRenderer.getHoveredType() != current && promotionRenderer.getHoveredType() != null)
            SoundManager.getInstance().playSound("menu_hover");
    }

    /**
     * Returns whether the menu is open.
     * 
     * @return whether the menu is open.
     */
    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    /**
     * Enters menu mode.
     */
    void enterMenuMode() {
        isMenuOpen = true;
        menuRenderer.setVisible(true);
        settingsRenderer.setVisible(false);

        settingsRenderer.reset();
        settingsRenderer.set3D(true);

        menuRenderer.reset();

        contextRenderer.setPaused(false);
        contextRenderer.setMessage("");
        contextRenderer.setContextVisible(false);

        if (onEnterMenu != null)
            onEnterMenu.run();
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
        if (!isMenuOpen)
            return;

        MenuAction action = menuRenderer.getActionAt(p);
        if (action != MenuAction.NONE && action != menuRenderer.getHoveredAction())
            SoundManager.getInstance().playSound("menu_hover");

        menuRenderer.setHoveredAction(action);

    }

    /**
     * Returns the side preference.
     * 
     * @return the side preference.
     */
    public SidePreference getSidePreference() {
        return menuRenderer.getSidePreference();
    }

    /**
     * Returns whether the configure menu is expanded.
     * 
     * @return whether the configure menu is expanded.
     */
    public boolean isConfigureExpanded() {
        return menuRenderer.isConfigureExpanded();
    }

    /**
     * Handles a menu press.
     * 
     * @param p the point where the press occurred.
     */
    public void handleMenuPress(Point p) {
        if (isPromoting() || !isMenuOpen)
            return;
        menuRenderer.setPressedAction(menuRenderer.getActionAt(p));
    }

    /**
     * Handles a menu release.
     * 
     * @param p the point where the release occurred.
     */
    public void handleMenuRelease(Point p) {
        if (isPromoting()) {
            handlePromotionClick(p);
            return;
        }
        if (!isMenuOpen)
            return;

        MenuAction pressed = menuRenderer.getPressedAction();
        menuRenderer.setPressedAction(null);

        MenuAction action = menuRenderer.getActionAt(p);
        if (action != pressed && pressed != null)
            return;

        action = menuRenderer.handleClick(p);
        if (action == MenuAction.NONE)
            return;

        switch (action) {
            case TOGGLE_SIDE -> {
            }
            case PLAY_GAME -> startGame();
            case EXIT -> {
                if (onExit != null)
                    onExit.run();
            }
            case SETTINGS -> settingsRenderer.setOpen(true);
            default -> {
            }
        }
        SoundManager.getInstance().playSound("menu_click");
    }

    /**
     * Starts a new game.
     */
    private void startGame() {
        boolean isWhite = switch (menuRenderer.getSidePreference()) {
            case RANDOM -> new java.util.Random().nextBoolean();
            case WHITE -> true;
            case BLACK -> false;
        };

        isMenuOpen = false;
        menuRenderer.setVisible(false);
        settingsRenderer.setVisible(true);
        settingsRenderer.reset();
        settingsRenderer.setOpen(false);

        contextRenderer.setContextVisible(true);
        contextRenderer.setCapturedVisible(settingsRenderer.areCapturedOn());

        if (onGameStart != null)
            onGameStart.accept(new GameConfig(isWhite, menuRenderer.getDifficulty(), menuRenderer.getTimerMode()));
    }

    /**
     * Returns whether the settings are open.
     * 
     * @return whether the settings are open.
     */
    public boolean isSettingsOpen() {
        return settingsRenderer.isOpen();
    }

    /**
     * Closes the settings.
     */
    public void closeSettings() {
        if (settingsRenderer.isOpen() && !settingsRenderer.isGameOver()) {
            settingsRenderer.setOpen(false);
            contextRenderer.setPaused(false);
            contextRenderer.setContextVisible(true);
            contextRenderer.setCapturedVisible(settingsRenderer.areCapturedOn());
        }
    }

    /**
     * Sets whether the settings are in 3D mode.
     * 
     * @param is3D whether the settings are in 3D mode.
     */
    public void setSettings3D(boolean is3D) {
        settingsRenderer.set3D(is3D);
    }

    /**
     * Handles a settings hover.
     * 
     * @param p the point where the hover occurred.
     */
    public void handleSettingsHover(Point p) {
        if (isMenuOpen)
            return;
        SettingsAction action = settingsRenderer.getActionAt(p);
        if (action != SettingsAction.NONE && action != SettingsAction.TOGGLE_MENU
                && action != settingsRenderer.getHoveredAction())
            SoundManager.getInstance().playSound("menu_hover");
        settingsRenderer.setHoveredAction(action);
    }

    /**
     * Handles a settings press.
     * 
     * @param p the point where the press occurred.
     */
    public void handleSettingsPress(Point p) {
        if (isMenuOpen)
            return;
        settingsRenderer.setPressedAction(settingsRenderer.getActionAt(p));
    }

    /**
     * Handles a settings release.
     * 
     * @param p the point where the release occurred.
     */
    public void handleSettingsRelease(Point p) {
        if (isMenuOpen)
            return;

        SettingsAction pressed = settingsRenderer.getPressedAction();
        settingsRenderer.setPressedAction(null);

        SettingsAction action = settingsRenderer.getActionAt(p);
        if (action != pressed && pressed != null)
            return;

        switch (action) {
            case TOGGLE_MENU -> {
                boolean opening = !settingsRenderer.isOpen();
                settingsRenderer.setOpen(opening);
                contextRenderer.setPaused(opening);
                contextRenderer.setContextVisible(!opening);
                contextRenderer.setCapturedVisible(opening ? false : settingsRenderer.areCapturedOn());
            }
            case TOGGLE_VISUALS_MENU -> settingsRenderer.toggleVisualsMenu();
            case TOGGLE_GAME_MENU -> settingsRenderer.toggleGameMenu();
            case RESTART -> {
                settingsRenderer.reset();
                contextRenderer.setPaused(false);
                contextRenderer.setCapturedVisible(settingsRenderer.areCapturedOn());
                contextRenderer.setContextVisible(true);
                if (onRestart != null)
                    onRestart.run();
            }
            case EXIT -> {
                if (onRestart != null)
                    onRestart.run();
                settingsRenderer.reset();
                enterMenuMode();
            }
            case TOGGLE_VIEW -> {
                if (onToggleViewMode != null)
                    onToggleViewMode.run();
            }
            case TOGGLE_GUIDES -> {
                boolean current = settingsRenderer.areGuidesOn();
                settingsRenderer.setGuidesOn(!current);
                if (onToggleGuides != null)
                    onToggleGuides.run();
            }
            case TOGGLE_CAPTURED -> {
                boolean current = contextRenderer.isCapturedVisible();
                settingsRenderer.setCapturedOn(!current);
                contextRenderer.setCapturedVisible(!current);
                if (onToggleCaptured != null)
                    onToggleCaptured.run();
            }
            case TOGGLE_AUTO_QUEEN -> settingsRenderer.cycleAutoPromotion();
            case TOGGLE_MOVE_CONFIRM -> settingsRenderer.setMoveConfirm(!settingsRenderer.isMoveConfirm());
            case NONE -> {
                if (settingsRenderer.isOpen() && !settingsRenderer.isGameOver())
                    closeSettings();
            }
            default -> {
            }
        }

        if (action != SettingsAction.NONE)
            SoundManager.getInstance().playSound("menu_click");
    }

    /**
     * Returns whether guides are on.
     * 
     * @return whether guides are on.
     */
    public boolean areGuidesOn() {
        return settingsRenderer.areGuidesOn();
    }

    /**
     * Toggles the mute state.
     */
    public void toggleMute() {
        boolean isMuted = !soundToggleRenderer.isMuted();
        soundToggleRenderer.setMuted(isMuted);
        SoundManager.getInstance().setMuted(isMuted);
    }

    /**
     * Returns whether sound is muted.
     * 
     * @return whether sound is muted.
     */
    public boolean isMuted() {
        return soundToggleRenderer.isMuted();
    }

    /**
     * Shows the game over menu.
     * 
     * @param status the game over status
     */
    public void showGameOverMenu(String status) {
        settingsRenderer.showGameOver(status);
        contextRenderer.setPaused(true);
        contextRenderer.setMessage("");
        contextRenderer.setCapturedVisible(false);
        contextRenderer.setContextVisible(false);
    }

    /**
     * Returns whether the game is over.
     * 
     * @return whether the game is over.
     */
    public boolean isGameOver() {
        return settingsRenderer.isGameOver();
    }

    /**
     * Handles a sound toggle click.
     * 
     * @param p the point where the click occurred.
     */
    public void handleSoundToggleClick(Point p) {
        if (soundToggleRenderer.handleClick(p)) {
            toggleMute();
            if (!soundToggleRenderer.isMuted())
                SoundManager.getInstance().playSound("menu_click");
        }
    }

    /**
     * Cycles the auto promotion setting.
     */
    public void cycleAutoPromotion() {
        settingsRenderer.cycleAutoPromotion();
    }

    /**
     * Returns the menu action at the given point.
     * 
     * @param p the point where the click occurred.
     * @return the menu action at the given point.
     */
    MenuAction getMenuActionAt(Point p) {
        return menuRenderer.getActionAt(p);
    }
}
