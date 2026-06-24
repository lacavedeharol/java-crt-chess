package com.lacavedeharol.chess.computer.uci;

import com.lacavedeharol.chess.computer.ChessAI;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;

/**
 * A {@link ChessAI} backed by an external UCI engine. Builds a FEN from the
 * game state, asks the engine for a move, and applies it back. Works with any
 * engine described by an {@link EngineConfig} (Stockfish today; Leela/Maia or
 * others later).
 */
public final class UciEngineAI implements ChessAI {

    /** Stockfish skill level (0..20). */
    private static final int STOCKFISH_SKILL = 20, STOCKFISH_MOVETIME_MS = 1000;

    /**
     * Creates the Stockfish-backed opponent. Knows Stockfish's own config so the
     * contract layer does not have to.
     *
     * @return a Stockfish UCI opponent (its process starts on construction).
     */
    public static UciEngineAI stockfish() {
        return new UciEngineAI(EngineConfig.stockfish(STOCKFISH_MOVETIME_MS), STOCKFISH_SKILL);
    }

    /**
     * Reports whether the Stockfish binary is installed and available to launch.
     *
     * @return true if Stockfish can be played.
     */
    public static boolean isStockfishAvailable() {
        return EngineConfig.stockfish(STOCKFISH_MOVETIME_MS).isAvailable();
    }

    /** Berserk think time per move, in milliseconds. */
    private static final int BERSERK_MOVETIME_MS = 1000;

    /**
     * Creates the Berserk-backed opponent at full strength (movetime-limited).
     *
     * @return a Berserk UCI opponent (its process starts on construction).
     */
    public static UciEngineAI berserk() {
        // Strength value is ignored: Berserk's config has no skill option.
        return new UciEngineAI(EngineConfig.berserk(BERSERK_MOVETIME_MS), 0);
    }

    /**
     * Reports whether the Berserk binary is installed and available to launch.
     *
     * @return true if Berserk can be played.
     */
    public static boolean isBerserkAvailable() {
        return EngineConfig.berserk(BERSERK_MOVETIME_MS).isAvailable();
    }

    private final UciEngine engine;
    private boolean ready;

    /**
     * Creates the engine-backed AI and starts the underlying process.
     *
     * @param config   how to launch and drive the engine.
     * @param strength engine-specific strength value, applied if the engine has
     *                 a strength option (ignored otherwise).
     */
    public UciEngineAI(EngineConfig config, int strength) {
        this.engine = new UciEngine(config);
        this.ready = engine.start();
        if (ready)
            engine.setStrength(strength);
    }

    /**
     * Asks the engine for a move and applies it to the given state.
     *
     * @param state the current game state (mutated in place on success).
     * @return {@code true} if a legal move was applied.
     */
    @Override
    public boolean makeMove(GameState state) {
        if (!ready)
            return false;

        String fen = FenBuilder.build(state);
        String uci = engine.getBestMove(fen);
        if (uci == null || uci.length() < 4)
            return false;

        int fromFile = uci.charAt(0) - 'a';
        int fromRank = uci.charAt(1) - '1';
        int toFile = uci.charAt(2) - 'a';
        int toRank = uci.charAt(3) - '1';

        if (!inBounds(fromFile, fromRank) || !inBounds(toFile, toRank))
            return false;

        GameState.MoveResult result = state.movePiece(fromFile, fromRank, toFile, toRank);

        if (result == GameState.MoveResult.PROMOTION_REQUIRED) {
            ChessPiece.PieceType promotion = parsePromotion(uci);
            state.promotePawn(toFile, toRank, promotion);
            return true;
        }

        return result == GameState.MoveResult.VALID;
    }

    /**
     * Parses the promotion piece from a UCI move (the optional 5th character).
     * Defaults to queen, which is also what UCI implies when omitted.
     */
    private static ChessPiece.PieceType parsePromotion(String uci) {
        if (uci.length() < 5)
            return ChessPiece.PieceType.QUEEN;
        return switch (Character.toLowerCase(uci.charAt(4))) {
            case 'r' -> ChessPiece.PieceType.ROOK;
            case 'b' -> ChessPiece.PieceType.BISHOP;
            case 'n' -> ChessPiece.PieceType.KNIGHT;
            default -> ChessPiece.PieceType.QUEEN;
        };
    }

    private static boolean inBounds(int file, int rank) {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }

    /**
     * Shuts down the underlying engine process.
     */
    @Override
    public void dispose() {
        engine.stop();
        ready = false;
    }
}
