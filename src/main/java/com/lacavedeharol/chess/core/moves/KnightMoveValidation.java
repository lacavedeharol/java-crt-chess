package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates knight moves.
 */
class KnightMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a knight move.
     * 
     * @param piece     the knight to move.
     * @param fromFile  the file of the knight.
     * @param fromRank  the rank of the knight.
     * @param toFile    the file of the square to move to.
     * @param toRank    the rank of the square to move to.
     * @param gameState the game state.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        int fileDiff = Math.abs(toFile - fromFile);
        int rankDiff = Math.abs(toRank - fromRank);

        if (!((fileDiff == 2 && rankDiff == 1) || (fileDiff == 1 && rankDiff == 2)))
            return false;

        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}
