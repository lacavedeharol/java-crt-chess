package com.lacavedeharol.chess.ai;

import com.lacavedeharol.chess.core.state.GameState;

/**
 * A chess-playing opponent.
 *
 * <p>
 * This is the only AI type the rest of the application depends on. Concrete
 * implementations include the built-in minimax engine ({@link MinimaxAI}) and
 * external UCI engines ({@link UciEngineAI}). Callers obtain instances from
 * {@link ChessAIFactory} and never reference the concrete classes directly.
 * </p>
 *
 * <p>
 * Contract: {@link #makeMove(GameState)} reads the current position, chooses a
 * move, applies it to the given {@code GameState} in place, and returns whether
 * a move was made. This mutate-and-return-boolean shape matches how the game
 * controller drives turns.
 * </p>
 */
public interface ChessAI {

    /**
     * Chooses and applies a move for the side to move.
     *
     * @param gameState the current game state, mutated in place on success.
     * @return {@code true} if a legal move was applied, {@code false} otherwise.
     */
    boolean makeMove(GameState gameState);

    /**
     * Releases any resources held by this AI (e.g. an external engine process).
     * The default does nothing, which is correct for in-process AIs like.
     * minimax; engine-backed implementations override it.
     */
    default void dispose() {
        // No resources to release by default.
    }
}
