package com.lacavedeharol.chess.core.moves;

import java.awt.Point;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates pawn moves.
 */
class PawnMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a pawn move.
     * 
     * @param piece     the pawn to move.
     * @param fromFile  the file of the pawn.
     * @param fromRank  the rank of the pawn.
     * @param toFile    the file of the square to move to.
     * @param toRank    the rank of the square to move to.
     * @param gameState the game state.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean isValidMove(ChessPiece piece, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {

        int direction = piece.isWhite() ? 1 : -1;

        if (toFile == fromFile) {
            ChessPiece target = gameState.getPieceAt(toFile, toRank);

            if (toRank == fromRank + direction)
                return target == null;

            if (toRank == fromRank + 2 * direction) {
                if (piece.hasMoved() || target != null)
                    return false;
                return gameState.getPieceAt(fromFile, fromRank + direction) == null;
            }
        }

        if (Math.abs(toFile - fromFile) == 1 && toRank == fromRank + direction) {

            ChessPiece target = gameState.getPieceAt(toFile, toRank);
            if (target != null && target.isWhite() != piece.isWhite())
                return true;

            Point enPassantTarget = gameState.getEnPassantTargetSquare();
            if (enPassantTarget != null && toFile == enPassantTarget.x && toRank == enPassantTarget.y)
                return true;
        }

        return false;
    }
}
