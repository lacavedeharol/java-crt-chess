package com.lacavedeharol.chess.core.state;

import java.awt.Point;

/**
 * GameContext class holds the game state.
 */
class GameContext {
    private boolean isWhiteToMove;
    private boolean isWhiteKingInCheck;
    private boolean isBlackKingInCheck;
    private Point enPassantTargetSquare;

    /**
     * Constructor for GameContext.
     */
    GameContext() {
        this.isWhiteToMove = true;
        this.isWhiteKingInCheck = false;
        this.isBlackKingInCheck = false;
        this.enPassantTargetSquare = null;
    }

    /**
     * Resets the game context.
     */
    void reset() {
        this.isWhiteToMove = true;
        this.isWhiteKingInCheck = false;
        this.isBlackKingInCheck = false;
        this.enPassantTargetSquare = null;
    }

    /**
     * Gets the current turn.
     * 
     * @return true if it is white's turn, false otherwise.
     */
    boolean isWhiteToMove() {
        return isWhiteToMove;
    }

    /**
     * Sets the current turn.
     * 
     * @param whiteToMove true if it is white's turn, false otherwise.
     */
    void setWhiteToMove(boolean whiteToMove) {
        isWhiteToMove = whiteToMove;
    }

    /**
     * Toggles the current turn.
     */
    void toggleTurn() {
        isWhiteToMove = !isWhiteToMove;
    }

    /**
     * Checks if the white king is in check.
     * 
     * @return true if the white king is in check, false otherwise.
     */
    boolean isWhiteKingInCheck() {
        return isWhiteKingInCheck;
    }

    /**
     * Sets the white king's check status.
     * 
     * @param whiteKingInCheck true if the white king is in check, false otherwise.
     */
    void setWhiteKingInCheck(boolean whiteKingInCheck) {
        isWhiteKingInCheck = whiteKingInCheck;
    }

    /**
     * Checks if the black king is in check.
     * 
     * @return true if the black king is in check, false otherwise.
     */
    boolean isBlackKingInCheck() {
        return isBlackKingInCheck;
    }

    /**
     * Sets the black king's check status.
     * 
     * @param blackKingInCheck true if the black king is in check, false otherwise.
     */
    void setBlackKingInCheck(boolean blackKingInCheck) {
        isBlackKingInCheck = blackKingInCheck;
    }

    /**
     * Gets the en passant target square.
     * 
     * @return the en passant target square.
     */
    Point getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

    /**
     * Sets the en passant target square.
     * 
     * @param enPassantTargetSquare the en passant target square.
     */
    void setEnPassantTargetSquare(Point enPassantTargetSquare) {
        this.enPassantTargetSquare = enPassantTargetSquare;
    }

    /**
     * Creates a copy of the game context.
     * 
     * @return a copy of the game context.
     */
    GameContext copy() {
        GameContext newContext = new GameContext();
        newContext.isWhiteToMove = this.isWhiteToMove;
        newContext.isWhiteKingInCheck = this.isWhiteKingInCheck;
        newContext.isBlackKingInCheck = this.isBlackKingInCheck;
        newContext.enPassantTargetSquare = this.enPassantTargetSquare != null ? new Point(this.enPassantTargetSquare)
                : null;

        return newContext;
    }
}
