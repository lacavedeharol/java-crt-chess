package com.lacavedeharol.chess.ai;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;

/**
 * Drives a {@link StockfishEngine} and applies its chosen move to a
 * {@link GameState}, matching the engine-mutates-then-returns-boolean contract
 * used by the rest of the AI code.
 */
final class StockfishAI {

    /** How long Stockfish may think per move, in milliseconds. */
    private final int moveTimeMs;

    private final StockfishEngine engine = new StockfishEngine();
    private boolean ready;

    /**
     * Creates the bridge.
     *
     * @param skillLevel the Stockfish skill level (0..20)
     * @param moveTimeMs think time per move in milliseconds
     */
    StockfishAI(int skillLevel, int moveTimeMs) {
        this.moveTimeMs = moveTimeMs;
        this.ready = engine.start();
        if (ready)
            engine.setSkillLevel(skillLevel);
    }

    /**
     * Asks Stockfish for a move and applies it to the given state.
     *
     * @param state the current game state (mutated in place on success)
     * @return {@code true} if a legal move was applied
     */
    boolean makeMove(GameState state) {
        if (!ready)
            return false;

        String fen = FenBuilder.build(state);
        String uci = engine.getBestMove(fen, moveTimeMs);
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
     * Shuts down the underlying engine process. Call when the game is disposed.
     */
    void dispose() {
        engine.stop();
        ready = false;
    }
}
