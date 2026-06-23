package com.lacavedeharol.chess.computer;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates {@link ChessAI} instances for a given {@link Opponent}, and reports
 * which opponents are currently available to select.
 *
 * <p>
 * This is the single place that knows which concrete implementation and
 * configuration backs each opponent. The rest of the application asks for a
 * {@code ChessAI}, asks which opponents are available, and never references
 * {@link MinimaxAI}, {@link UciEngineAI}, or any engine config directly. Adding
 * an opponent is one new enum value plus one branch here.
 * </p>
 */
public final class ChessAIFactory {

    private static final int EASY_DEPTH = 2, HARD_DEPTH = 5;

    /** Stockfish skill level (0..20). */
    private static final int STOCKFISH_SKILL = 20, STOCKFISH_MOVETIME_MS = 1000;

    private ChessAIFactory() {
    }

    /**
     * Builds the AI for the given opponent, or {@code null} for {@link
     * Opponent#LOCAL} (human vs human, no AI on that side).
     *
     * @param opponent which opponent to create.
     * @param isWhite  true if this AI plays the white pieces.
     * @return a ready-to-use {@link ChessAI}, or {@code null} for LOCAL.
     */
    public static ChessAI create(Opponent opponent, boolean isWhite) {
        return switch (opponent) {
            case CRT_EASY -> new MinimaxAI(isWhite, EASY_DEPTH);
            case CRT_HARD -> new MinimaxAI(isWhite, HARD_DEPTH);
            case STOCKFISH -> new UciEngineAI(
                    EngineConfig.stockfish(STOCKFISH_MOVETIME_MS),
                    STOCKFISH_SKILL);
            case LOCAL -> null;
        };
    }

    /**
     * Reports whether an opponent can currently be selected. The built-in
     * opponents (minimax and local human-vs-human) are always available; engine
     * opponents require their binary to be installed in the data directory.
     *
     * @param opponent the opponent to check.
     * @return true if the opponent is available to select.
     */
    public static boolean isAvailable(Opponent opponent) {
        return switch (opponent) {
            case CRT_EASY, CRT_HARD, LOCAL -> true;
            case STOCKFISH -> EngineConfig.stockfish(STOCKFISH_MOVETIME_MS).isAvailable();
        };
    }

    /**
     * Returns the opponents that are currently available to select, in enum
     * order. Intended for building the opponent-selection UI: engines whose
     * binary is not installed are omitted.
     *
     * @return a non-empty list of available opponents (always includes the
     *         built-in opponents).
     */
    public static List<Opponent> availableOpponents() {
        List<Opponent> available = new ArrayList<>();
        for (Opponent o : Opponent.values())
            if (isAvailable(o))
                available.add(o);
        return available;
    }
}
