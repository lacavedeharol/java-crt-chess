package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import com.lacavedeharol.chess.ai.Opponent;
import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * Renderer for the main menu.
 */
class MainMenuRenderer extends BaseMenuRenderer<MenuAction> {

    private boolean isConfigureExpanded = false, visible = false;

    /**
     * Check if the configure menu is expanded.
     * 
     * @return true if the configure menu is expanded, false otherwise.
     */
    boolean isConfigureExpanded() {
        return isConfigureExpanded;
    }

    private Opponent opponent = Opponent.EASY_AI;
    private TimerMode timerMode = TimerMode.CLASSIC;
    private SidePreference sidePreference = SidePreference.RANDOM;

    private Rectangle titleBounds;

    /**
     * Initialize the menu items.
     */
    MainMenuRenderer() {
        initItems();
    }

    /**
     * Initialize the menu items.
     */
    private void initItems() {
        items.clear();

        items.add(new MenuItem<>("play game", MenuAction.PLAY_GAME, 0));
        items.add(new MenuItem<>("configure game", MenuAction.CONFIGURE_GAME, 0));

        if (isConfigureExpanded) {
            String sideText = "play as: " + sidePreference.toString().toLowerCase();
            items.add(new MenuItem<>(sideText, MenuAction.TOGGLE_SIDE, 1));

            items.add(
                    new MenuItem<>("choose opponent: " + opponent.toString().toLowerCase().replace('_', ' '),
                            MenuAction.AI_OPPONENT, 1));

            String timerText = "game category: " + timerMode.toString().toLowerCase();
            items.add(new MenuItem<>(timerText, MenuAction.TOGGLE_TIMER, 1));
        }

        items.add(new MenuItem<>("exit", MenuAction.EXIT, 0));
    }

    /**
     * Toggle the configure menu.
     */
    void toggleConfigure() {
        isConfigureExpanded = !isConfigureExpanded;
        initItems();
    }

    /**
     * Toggle the side preference.
     */
    void toggleSide() {
        sidePreference = switch (sidePreference) {
            case WHITE -> SidePreference.BLACK;
            case BLACK -> SidePreference.RANDOM;
            case RANDOM -> SidePreference.WHITE;
        };
        initItems();
    }

    /**
     * Toggle the timer mode.
     */
    void toggleTimer() {
        timerMode = switch (timerMode) {
            case BLITZ -> TimerMode.RAPID;
            case RAPID -> TimerMode.CLASSIC;
            case CLASSIC -> TimerMode.BLITZ;
        };
        initItems();
    }

    /**
     * Toggle the AI opponent.
     */
    void toggleOpponent() {
        opponent = switch (opponent) {
            case EASY_AI -> Opponent.HARD_AI;
            case HARD_AI -> Opponent.STOCKFISH;
            case STOCKFISH -> Opponent.EASY_AI;
        };
        initItems();
    }

    /**
     * Get the side preference.
     * 
     * @return the side preference.
     */
    SidePreference getSidePreference() {
        return sidePreference;
    }

    /**
     * Get the AI opponent.
     * 
     * @return the AI opponent.
     */
    Opponent getOpponent() {
        return opponent;
    }

    /**
     * Get the timer mode.
     * 
     * @return the timer mode.
     */
    TimerMode getTimerMode() {
        return timerMode;
    }

    /**
     * Reset the main menu.
     */
    void reset() {
        isConfigureExpanded = false;
        opponent = Opponent.EASY_AI;
        timerMode = TimerMode.CLASSIC;
        sidePreference = SidePreference.RANDOM;
        initItems();
    }

    /**
     * Set the visibility of the main menu.
     * 
     * @param visible true to make the main menu visible, false otherwise.
     */
    void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Render the main menu.
     * 
     * @param g2d    the graphics context.
     * @param width  the width of the screen.
     * @param height the height of the screen.
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        if (!visible)
            return;

        GraphicsUtils.drawOverlay(g2d, width, height);

        int padding = Math.min(width, height) / 48;

        g2d.setColor(GraphicsUtils.LIGHT);
        int titleSize = Math.min(width, height) / 32;
        g2d.setFont(FontManager.getInstance().getFont(titleSize));
        FontMetrics titleFM = g2d.getFontMetrics();
        String title = "crt chess";
        int titleX = padding;
        int titleY = padding + titleFM.getAscent();
        g2d.drawString(title, titleX, titleY);

        titleBounds = new Rectangle(titleX, titleY - titleFM.getAscent(), titleFM.stringWidth(title),
                titleFM.getHeight());

        int fontSize = Math.min(width, height) / 48;
        g2d.setFont(FontManager.getInstance().getFont(fontSize));
        FontMetrics menuFM = g2d.getFontMetrics();
        int lineHeight = menuFM.getHeight() + (menuFM.getHeight() / 2);

        for (int i = 0; i < items.size(); i++) {
            MenuItem<MenuAction> item = items.get(i);

            int x = padding + (item.level > 0 ? menuFM.stringWidth(BaseMenuRenderer.HOVER_INDICATOR) : 0);

            int y = (titleY + lineHeight) + (i * lineHeight);

            drawItem(g2d, item, x, y, menuFM, false);
        }
    }

    /**
     * Get the action at the given point.
     * 
     * @param p the point to check.
     * @return the action at the given point.
     */
    MenuAction getActionAt(Point p) {
        if (titleBounds != null && titleBounds.contains(p))
            return MenuAction.TITLE;

        for (MenuItem<MenuAction> item : items) {
            if (item.bounds != null && item.bounds.contains(p))
                return item.action;
        }
        return MenuAction.NONE;
    }

    /**
     * Handle a click event.
     * 
     * @param p the point where the click occurred.
     * @return the action that was performed.
     */
    MenuAction handleClick(Point p) {
        if (titleBounds != null && titleBounds.contains(p)) {
            if (isConfigureExpanded)
                toggleConfigure();
            return MenuAction.TITLE;
        }

        for (MenuItem<MenuAction> item : items) {
            if (item.bounds != null && item.bounds.contains(p)) {
                switch (item.action) {
                    case CONFIGURE_GAME -> toggleConfigure();
                    case TOGGLE_SIDE -> toggleSide();
                    case AI_OPPONENT -> toggleOpponent();
                    case TOGGLE_TIMER -> toggleTimer();
                    default -> {
                    }
                }

                return item.action;
            }
        }
        return MenuAction.NONE;
    }

}
