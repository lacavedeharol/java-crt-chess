package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates queen moves.
 */
class QueenMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a queen move.
     * 
     * @param piece     the queen to move.
     * @param fromFile  the file of the queen.
     * @param fromRank  the rank of the queen.
     * @param toFile    the file of the square to move to.
     * @param toRank    the rank of the square to move to.
     * @param gameState the game state.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        boolean straight = (fromFile == toFile || fromRank == toRank);
        boolean diagonal = (Math.abs(toFile - fromFile) == Math.abs(toRank - fromRank));

        if (!straight && !diagonal)
            return false;

        if (straight && !MoveUtils.isPathClearStraight(fromFile, fromRank, toFile, toRank, gameState))
            return false;

        if (diagonal && !MoveUtils.isPathClearDiagonal(fromFile, fromRank, toFile, toRank, gameState))
            return false;

        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}
