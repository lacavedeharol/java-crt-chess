package com.lacavedeharol.chess.app.components.renderer.context;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.app.components.renderer.AssetManager;
import com.lacavedeharol.chess.app.components.renderer.GraphicsUtils;

/**
 * A renderer for the settings menu.
 * 
 * @param <SettingsAction> the type of action to perform
 */
class SettingsMenuRenderer extends BaseMenuRenderer<SettingsAction> {

    private boolean isOpen = false, is3D = true, areGuidesOn = true, areCapturedOn = true,
            isMoveConfirm = false, showSettings = true, isGameOver = false, visible = true, isVisualsOpen = false,
            isGameOpen = false;
    private AutoPromotion autoPromotion = AutoPromotion.OFF;
    private Rectangle gearBounds;
    private BufferedImage iconSprite;

    private String statusTitle = "game paused";

    private final List<MenuItem<SettingsAction>> settingsItems = new ArrayList<>(), statusItems = new ArrayList<>();

    /**
     * Constructor.
     */
    SettingsMenuRenderer() {
        loadIcons();
        rebuildMenu();
    }

    /**
     * Set the status title.
     * 
     * @param title the status title to set
     */
    void setStatusTitle(String title) {
        this.statusTitle = title;
    }

    /**
     * Load the icons.
     */
    private void loadIcons() {
        BufferedImage sheet = AssetManager.getInstance().getImage("icons");
        if (sheet != null)
            iconSprite = sheet.getSubimage(0, 0, 8, 8);
    }

    /**
     * Rebuild the menu.
     */
    private void rebuildMenu() {
        settingsItems.clear();
        statusItems.clear();
        items.clear();

        settingsItems.add(new MenuItem<>("visuals", SettingsAction.TOGGLE_VISUALS_MENU));
        if (isVisualsOpen) {
            settingsItems.add(new MenuItem<>("view mode", SettingsAction.TOGGLE_VIEW, true));
            settingsItems.add(new MenuItem<>("display guides", SettingsAction.TOGGLE_GUIDES, true));
            settingsItems.add(new MenuItem<>("display captured pieces", SettingsAction.TOGGLE_CAPTURED, true));
        }

        settingsItems.add(new MenuItem<>("game", SettingsAction.TOGGLE_GAME_MENU));
        if (isGameOpen) {
            settingsItems.add(new MenuItem<>("auto queen", SettingsAction.TOGGLE_AUTO_QUEEN, true));
            settingsItems.add(new MenuItem<>("move confirmation", SettingsAction.TOGGLE_MOVE_CONFIRM, true));
        }

        statusItems.add(new MenuItem<>("restart", SettingsAction.RESTART));
        statusItems.add(new MenuItem<>("exit", SettingsAction.EXIT));
    }

    /**
     * Set the menu open state.
     * 
     * @param open true to open the menu, false to close it
     */
    void setOpen(boolean open) {
        this.isOpen = open;
        if (open) {
            this.showSettings = true;
            this.isGameOver = false;
            isVisualsOpen = false;
            isGameOpen = false;
            rebuildMenu();
        }
    }

    /**
     * Reset the menu.
     */
    void reset() {
        this.isOpen = false;
        this.isGameOver = false;
        this.showSettings = true;
        this.statusTitle = "game paused";
        this.isVisualsOpen = false;
        this.isGameOpen = false;
        rebuildMenu();
    }

    /**
     * Show the game over screen.
     * 
     * @param status the status message to display
     */
    void showGameOver(String status) {
        this.statusTitle = status;
        this.showSettings = false;
        setOpen(true);
        this.showSettings = false;
        this.isGameOver = true;
    }

    /**
     * Check if the game is over.
     * 
     * @return true if the game is over, false otherwise
     */
    boolean isGameOver() {
        return isGameOver;
    }

    /**
     * Check if the menu is open.
     * 
     * @return true if the menu is open, false otherwise
     */
    boolean isOpen() {
        return isOpen;
    }

    /**
     * Set the menu visibility.
     * 
     * @param visible true to make the menu visible, false to hide it
     */
    void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Set the 3D mode.
     * 
     * @param v true to enable 3D mode, false to disable it
     */
    void set3D(boolean v) {
        is3D = v;
    }

    /**
     * Set the guides visibility.
     * 
     * @param v true to show guides, false to hide them
     */
    void setGuidesOn(boolean v) {
        areGuidesOn = v;
    }

    /**
     * Set the captured pieces visibility.
     * 
     * @param v true to show captured pieces, false to hide them
     */
    void setCapturedOn(boolean v) {
        areCapturedOn = v;
    }

    /**
     * Set the auto promotion mode.
     * 
     * @param v the auto promotion mode to set
     */
    void setAutoPromotion(AutoPromotion v) {
        autoPromotion = v;
    }

    /**
     * Cycle the auto promotion mode.
     */
    void cycleAutoPromotion() {
        autoPromotion = switch (autoPromotion) {
            case OFF -> AutoPromotion.QUEEN;
            case QUEEN -> AutoPromotion.ROOK;
            case ROOK -> AutoPromotion.BISHOP;
            case BISHOP -> AutoPromotion.KNIGHT;
            case KNIGHT -> AutoPromotion.OFF;
        };
    }

    /**
     * Set the move confirmation mode.
     * 
     * @param v true to enable move confirmation, false to disable it
     */
    void setMoveConfirm(boolean v) {
        isMoveConfirm = v;
    }

    /**
     * Check if guides are on.
     * 
     * @return true if guides are on, false otherwise
     */
    boolean areGuidesOn() {
        return areGuidesOn;
    }

    /**
     * Check if captured pieces are on.
     * 
     * @return true if captured pieces are on, false otherwise
     */
    boolean areCapturedOn() {
        return areCapturedOn;
    }

    /**
     * Get the auto promotion mode.
     * 
     * @return the auto promotion mode
     */
    AutoPromotion getAutoPromotion() {
        return autoPromotion;
    }

    /**
     * Check if move confirmation is enabled.
     * 
     * @return true if move confirmation is enabled, false otherwise
     */
    boolean isMoveConfirm() {
        return isMoveConfirm;
    }

    /**
     * Toggle the visuals menu.
     */
    void toggleVisualsMenu() {
        isVisualsOpen = !isVisualsOpen;
        if (isVisualsOpen)
            isGameOpen = false;
        rebuildMenu();
    }

    /**
     * Toggle the game menu.
     */
    void toggleGameMenu() {
        isGameOpen = !isGameOpen;
        if (isGameOpen)
            isVisualsOpen = false;
        rebuildMenu();
    }

    /**
     * Render the menu.
     * 
     * @param g2d    the graphics context
     * @param width  the width of the screen
     * @param height the height of the screen
     */
    @Override
    public void render(Graphics2D g2d, int width, int height) {
        int padding = Math.min(width, height) / 32;
        int fontSize = Math.min(width, height) / 32;

        if (!visible)
            return;

        int iconX = width - padding - fontSize;
        int iconY = padding;

        gearBounds = IconRenderer.getBounds(iconX, iconY, fontSize);
        boolean isIconPressed = (pressedAction == SettingsAction.TOGGLE_MENU);

        if (!isGameOver && !isOpen)
            IconRenderer.drawIcon(g2d, iconX, iconY, fontSize, iconSprite, isIconPressed);

        if (!isOpen)
            return;

        GraphicsUtils.drawOverlay(g2d, width, height);

        g2d.setFont(FontManager.getInstance().getFont(fontSize));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.setColor(GraphicsUtils.LIGHT);

        if (showSettings) {
            int titleX = padding;
            int titleY = padding + fm.getAscent();
            g2d.drawString("settings", titleX, titleY);

            int leftY = titleY + fm.getHeight();

            for (MenuItem<SettingsAction> item : settingsItems) {
                updateItemText(item);
                int leftX = padding + (item.level > 0 ? fm.stringWidth(BaseMenuRenderer.HOVER_INDICATOR) : 0);
                drawItem(g2d, item, leftX, leftY, fm, false);
                leftY += fm.getHeight();
            }
        }

        int statusX = padding;
        int statusY = height / 2 + fm.getAscent();

        g2d.drawString(statusTitle, statusX, statusY);

        int statusListY = statusY + fm.getHeight();

        for (MenuItem<SettingsAction> item : statusItems) {
            updateItemText(item);
            drawItem(g2d, item, statusX, statusListY, fm, false);
            statusListY += fm.getHeight();
        }

    }

    /**
     * Update the item text.
     * 
     * @param item the item to update
     */
    private void updateItemText(MenuItem<SettingsAction> item) {
        switch (item.action) {
            case TOGGLE_VIEW -> item.text = is3D ? "view mode: 3d" : "view mode: 2d";
            case TOGGLE_GUIDES -> item.text = areGuidesOn ? "display guides: on" : "display guides: off";
            case TOGGLE_CAPTURED ->
                item.text = areCapturedOn ? "display captured pieces: on" : "display captured pieces: off";
            case TOGGLE_AUTO_QUEEN -> item.text = "auto promotion: " + autoPromotion.name().toLowerCase();
            case TOGGLE_MOVE_CONFIRM -> item.text = isMoveConfirm ? "move confirmation: on (yet to be implemented)"
                    : "move confirmation: off";
            default -> {
            }

        }
    }

    /**
     * Get the action at the given point.
     * 
     * @param p the point to check
     * @return the action at the given point
     */
    SettingsAction getActionAt(Point p) {
        if (!isGameOver && !isOpen && gearBounds != null && gearBounds.contains(p))
            return SettingsAction.TOGGLE_MENU;

        if (isOpen) {
            for (MenuItem<SettingsAction> item : statusItems) {
                if (item.bounds != null && item.bounds.contains(p))
                    return item.action;

            }

            if (showSettings) {
                for (MenuItem<SettingsAction> item : settingsItems) {
                    if (item.bounds != null && item.bounds.contains(p))
                        return item.action;
                }
            }
        }
        return SettingsAction.NONE;
    }

    /**
     * Draw an icon.
     * 
     * @param g2d        the graphics context
     * @param x          the x coordinate of the icon
     * @param y          the y coordinate of the icon
     * @param targetSize the target size of the icon
     * @param icon       the icon to draw
     * @param isPressed  true if the icon is pressed, false otherwise
     */
    private static class IconRenderer {

        static void drawIcon(Graphics2D g2d, int x, int y, int targetSize, BufferedImage icon,
                boolean isPressed) {

            if (icon == null)
                return;

            int iconW = targetSize;
            int iconH = targetSize;

            int iconX = x + (targetSize - iconW) / 2;
            int iconY = y + (targetSize - iconH) / 2;

            if (isPressed) {
                float scale = 0.9f;
                int newSize = Math.max(1, (int) (targetSize * scale));

                int smallIconX = x + (targetSize - newSize) / 2;
                int smallIconY = y + (targetSize - newSize) / 2;

                g2d.drawImage(icon, smallIconX, smallIconY, newSize, newSize, null);
            } else
                g2d.drawImage(icon, iconX, iconY, targetSize, targetSize, null);
        }

        /**
         * Get the bounds of the icon.
         * 
         * @param x       the x coordinate of the icon
         * @param y       the y coordinate of the icon
         * @param boxSize the size of the icon
         * @return the bounds of the icon
         */
        public static Rectangle getBounds(int x, int y, int boxSize) {
            return new Rectangle(x, y, boxSize, boxSize);
        }
    }
}
