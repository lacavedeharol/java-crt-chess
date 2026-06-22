package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates bishop moves.
 */
class BishopMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a bishop move.
     * 
     * @param piece     the bishop to move.
     * @param fromFile  the file of the bishop.
     * @param fromRank  the rank of the bishop.
     * @param toFile    the file of the square to move to.
     * @param toRank    the rank of the square to move to.
     * @param gameState the game state.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        if (Math.abs(toFile - fromFile) != Math.abs(toRank - fromRank))
            return false;

        if (!MoveUtils.isPathClearDiagonal(fromFile, fromRank, toFile, toRank, gameState))
            return false;

        ChessPiece target = gameState.getPieceAt(toFile, toRank);
        return target == null || target.isWhite() != piece.isWhite();
    }
}
