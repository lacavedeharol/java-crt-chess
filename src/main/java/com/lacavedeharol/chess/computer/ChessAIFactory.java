package com.lacavedeharol.chess.computer;

import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.computer.minimax.MinimaxAI;
import com.lacavedeharol.chess.computer.uci.UciEngineAI;

/**
 * Creates {@link ChessAI} instances for a given {@link Opponent}, and reports
 * which opponents are currently available to select.
 *
 * <p>
 * This contract-layer factory holds no implementation details: it dispatches to
 * the implementation packages' own static factories
 * ({@link MinimaxAI#easy}, {@link MinimaxAI#hard},
 * {@link UciEngineAI#stockfish})
 * and availability checks. Engine-specific constants (search depths, skill
 * levels, think times, binary locations) live entirely within those
 * implementation packages. Adding an opponent is one new enum value plus one
 * dispatch branch here.
 * </p>
 */
public final class ChessAIFactory {

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
            case CRT_EASY -> MinimaxAI.easy(isWhite);
            case CRT_HARD -> MinimaxAI.hard(isWhite);
            case STOCKFISH -> UciEngineAI.stockfish();
            case BERSERK -> UciEngineAI.berserk();
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
            case STOCKFISH -> UciEngineAI.isStockfishAvailable();
            case BERSERK -> UciEngineAI.isBerserkAvailable();
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
