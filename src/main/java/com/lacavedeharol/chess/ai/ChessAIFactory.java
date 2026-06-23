package com.lacavedeharol.chess.ai;

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
            case EASY_AI -> new MinimaxAI(isWhite, EASY_DEPTH);
            case HARD_AI -> new MinimaxAI(isWhite, HARD_DEPTH);
            case STOCKFISH -> new UciEngineAI(
                    EngineConfig.stockfish(STOCKFISH_MOVETIME_MS),
                    STOCKFISH_SKILL);
            default -> throw new IllegalArgumentException("Unknown opponent: " + opponent);
        } : null;
    }
}
