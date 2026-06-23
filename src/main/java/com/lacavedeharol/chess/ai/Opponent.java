package com.lacavedeharol.chess.ai;

/**
 * Identifies which opponent the human is playing against.
 *
 * <p>
 * Each value names an opponent, not a "difficulty" — some opponents are the
 * built-in minimax engine at different search depths, others are external UCI
 * engines. {@link ChessAIFactory} maps each value to the concrete
 * {@link ChessAI} that backs it, so adding a new opponent (e.g. a Maia rating
 * level or Leela) is one new enum value plus one factory branch.
 * </p>
 */
public enum Opponent {
    /** Built-in minimax engine, shallow search (weaker). */
    EASY_AI,
    /** Built-in minimax engine, deeper search (stronger). */
    HARD_AI,
    /** Stockfish UCI engine. */
    STOCKFISH,
    LOCAL
}
