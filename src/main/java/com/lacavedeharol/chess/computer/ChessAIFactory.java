package com.lacavedeharol.chess.computer;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates {@link ChessAI} instances for a given {@link Opponent}.
 *
 * <p>
 * This is the single place that knows which concrete implementation and
 * configuration backs each opponent. The rest of the application asks for a
 * {@code ChessAI} and never references {@link MinimaxAI}, {@link UciEngineAI},
 * or any engine config directly. Adding an opponent is one new enum value plus
 * one branch here.
 * </p>
 */
public final class ChessAIFactory {

    /** Minimax search depth for the EASY opponent. */
    private static final int EASY_DEPTH = 2;
    /** Minimax search depth for the HARD opponent. */
    private static final int HARD_DEPTH = 5;

    /** Stockfish skill level (0..20). */
    private static final int STOCKFISH_SKILL = 20;
    /** Stockfish think time per move, in milliseconds. */
    private static final int STOCKFISH_MOVETIME_MS = 1000;

    private ChessAIFactory() {
    }

    /**
     * Builds the AI for the given opponent.
     *
     * @param opponent which opponent to create.
     * @param isWhite  true if this AI plays the white pieces.
     * @return a ready-to-use {@link ChessAI}.
     */
    public static ChessAI create(Opponent opponent, boolean isWhite) {
        return opponent != Opponent.LOCAL ? switch (opponent) {
            case CRT_EASY -> new MinimaxAI(isWhite, EASY_DEPTH);
            case CRT_HARD -> new MinimaxAI(isWhite, HARD_DEPTH);
            case STOCKFISH -> new UciEngineAI(
                    EngineConfig.stockfish(STOCKFISH_MOVETIME_MS),
                    STOCKFISH_SKILL);
            case LOCAL -> throw new UnsupportedOperationException("Unimplemented case: " + opponent);
            default -> throw new IllegalArgumentException("Unknown opponent: " + opponent);
        } : null;
    }

    /**
     * Reports whether an opponent can currently be played. Minimax opponents are
     * always available; engine opponents require their binary to be installed in
     * the data directory.
     *
     * @param opponent the opponent to check.
     * @return true if the opponent is available.
     */
    public static boolean isAvailable(Opponent opponent) {
        return opponent != Opponent.LOCAL ? switch (opponent) {
            case CRT_EASY, CRT_HARD -> true;
            case STOCKFISH -> EngineConfig.stockfish(STOCKFISH_MOVETIME_MS).isAvailable();
            case LOCAL -> throw new UnsupportedOperationException("Unimplemented case: " + opponent);
            default -> throw new IllegalArgumentException("Unexpected value: " + opponent);
        } : false;
    }

    /**
     * Returns the opponents that are currently available to play, in enum order.
     * Intended for building the opponent-selection UI.
     *
     * @return a list of available opponents (never includes engines whose binary
     *         is not installed).
     */
    public static List<Opponent> availableOpponents() {
        List<Opponent> available = new ArrayList<>();
        for (Opponent o : Opponent.values())
            if (isAvailable(o))
                available.add(o);
        return available;
    }
}
