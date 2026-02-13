package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates rook moves.
 */
class RookMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a rook move.
     * 
     * @param piece     the rook to move
     * @param fromFile  the file of the rook
     * @param fromRank  the rank of the rook
     * @param toFile    the file of the square to move to
     * @param toRank    the rank of the square to move to
     * @param gameState the game state
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank, int toFile, int toRank,
            GameState gameState) {
        if (!(fromFile == toFile || fromRank == toRank))
            return false;

        if (!MoveUtils.isPathClearStraight(fromFile, fromRank, toFile, toRank, gameState))
            return false;
        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }

}
