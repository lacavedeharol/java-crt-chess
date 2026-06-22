package com.lacavedeharol.chess.app.game;

import javax.swing.Timer;

import com.lacavedeharol.chess.app.components.renderer.context.TimerMode;

/**
 * GameTimer class represents the timer for the game.
 */
class GameTimer {

    private long whiteTimeMillis, blackTimeMillis, lastTickTime;
    private boolean isWhiteToMove, isRunning;
    private Timer timer;
    private Runnable onTimeUpdate, onTimeout;

    private final TimerMode mode;

    /**
     * Constructor for GameTimer.
     * 
     * @param mode         the timer mode.
     * @param onTimeUpdate the Runnable to be called when the time updates.
     * @param onTimeout    the Runnable to be called when the timer times out.
     */
    GameTimer(TimerMode mode, Runnable onTimeUpdate, Runnable onTimeout) {
        this.mode = mode;
        long initialMillis = getMillisForMode(mode);
        this.whiteTimeMillis = initialMillis;
        this.blackTimeMillis = initialMillis;
        this.onTimeUpdate = onTimeUpdate;
        this.onTimeout = onTimeout;

        this.timer = new Timer(100, e -> tick());
    }

    private long getMillisForMode(TimerMode mode) {
        return switch (mode) {
            case BLITZ -> 3 * 60 * 1000L;
            case RAPID -> 10 * 60 * 1000L;
            case CLASSIC -> 60 * 60 * 1000L;
        };
    }

    /**
     * Starts the timer.
     * 
     * @param isWhiteToMove true if it's white's turn, false otherwise.
     */
    void start(boolean isWhiteToMove) {
        this.isWhiteToMove = isWhiteToMove;
        this.lastTickTime = System.currentTimeMillis();
        this.isRunning = true;
        this.timer.start();
    }

    /**
     * Stops the timer.
     */
    void stop() {
        this.isRunning = false;
        this.timer.stop();
    }

    /**
     * Resets the timer.
     */
    void reset() {
        long initialMillis = getMillisForMode(mode);
        this.whiteTimeMillis = initialMillis;
        this.blackTimeMillis = initialMillis;
        if (onTimeUpdate != null)
            onTimeUpdate.run();
    }

    /**
     * Switches the turn.
     */
    void switchTurn() {
        if (!isRunning)
            return;

        tick();

        this.isWhiteToMove = !this.isWhiteToMove;
        this.lastTickTime = System.currentTimeMillis();
    }

    /**
     * Ticks the timer.
     */
    private void tick() {
        if (!isRunning)
            return;

        long now = System.currentTimeMillis();
        long delta = now - lastTickTime;
        lastTickTime = now;

        if (isWhiteToMove) {
            whiteTimeMillis -= delta;
            if (whiteTimeMillis <= 0) {
                whiteTimeMillis = 0;
                stop();
                if (onTimeout != null)
                    onTimeout.run();
            }
        } else {
            blackTimeMillis -= delta;
            if (blackTimeMillis <= 0) {
                blackTimeMillis = 0;
                stop();
                if (onTimeout != null)
                    onTimeout.run();
            }
        }

        if (onTimeUpdate != null)
            onTimeUpdate.run();
    }

    /**
     * Gets the formatted time.
     * 
     * @param forWhite true if it's white's turn, false otherwise.
     * @return the formatted time.
     */
    String getFormattedTime(boolean forWhite) {
        long millis = forWhite ? whiteTimeMillis : blackTimeMillis;
        long seconds = (millis / 1000) + (millis % 1000 > 0 ? 1 : 0);
        long mins = seconds / 60;
        long secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    /**
     * Gets the current turn.
     * 
     * @return true if it's white's turn, false otherwise.
     */
    boolean isWhiteTurn() {
        return isWhiteToMove;
    }
}
