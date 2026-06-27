package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.moves.MoveManager.MoveValidationStrategy;

/**
 * Validates king moves.
 */
class KingMoveValidation implements MoveValidationStrategy {

    /**
     * Validates a king move.
     * 
     * @param king      the king to move
     * @param fromFile  the file of the king
     * @param fromRank  the rank of the king
     * @param toFile    the file of the square to move to
     * @param toRank    the rank of the square to move to
     * @param gameState the game state
     * @return true if the move is valid, false otherwise
     */
    @Override
    public boolean isValidMove(ChessPiece king, int fromFile, int fromRank,
            int toFile, int toRank, GameState gameState) {
        int fileDiff = Math.abs(toFile - fromFile);
        int rankDiff = Math.abs(toRank - fromRank);

        if (fileDiff <= 1 && rankDiff <= 1 && (fileDiff + rankDiff > 0)) {
            ChessPiece target = gameState.getPieceAt(toFile, toRank);
            return target == null || target.isWhite() != king.isWhite();
        }

        else if (rankDiff == 0 && fileDiff == 2) {

            if (king.hasMoved())
                return false;

            if (gameState.isSquareUnderAttack(fromFile, fromRank, !king.isWhite()))
                return false;

            if (toFile > fromFile) {
                // King-side castling: rook at h-file (7)
                ChessPiece rook = gameState.getPieceAt(7, fromRank);
                if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
                    return false;

                // Squares between king and rook (fromFile+1 and fromFile+2) must be empty
                if (gameState.getPieceAt(fromFile + 1, fromRank) != null ||
                        gameState.getPieceAt(fromFile + 2, fromRank) != null)
                    return false;

                // King must not pass through or land on an attacked square
                if (gameState.isSquareUnderAttack(fromFile + 1, fromRank, !king.isWhite()) ||
                        gameState.isSquareUnderAttack(fromFile + 2, fromRank, !king.isWhite()))
                    return false;

                return true;
            } else {
                // Queen-side castling: rook at a-file (0)
                ChessPiece rook = gameState.getPieceAt(0, fromRank);
                if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
                    return false;

                // Squares between rook and king (files 1 .. fromFile-1) must be empty
                for (int f = 1; f < fromFile; f++)
                    if (gameState.getPieceAt(f, fromRank) != null)
                        return false;

                // King must not pass through or land on an attacked square
                if (gameState.isSquareUnderAttack(fromFile - 1, fromRank, !king.isWhite()) ||
                        gameState.isSquareUnderAttack(fromFile - 2, fromRank, !king.isWhite()))
                    return false;

                return true;
            }
        }

        return false;
    }
}
