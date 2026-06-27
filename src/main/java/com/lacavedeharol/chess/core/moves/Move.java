package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Represents a chess move.
 * 
 * @param fromFile      The starting file index (0-7)
 * @param fromRank      The starting rank index (0-7)
 * @param toFile        The destination file index (0-7)
 * @param toRank        The destination rank index (0-7)
 * @param piece         The piece being moved
 * @param capturedPiece The piece being captured, if any
 */
public record Move(int fromFile, int fromRank, int toFile, int toRank, ChessPiece piece, ChessPiece capturedPiece) {
    @Override
    public String toString() {
        return String.format("%s from (%d, %d) to (%d, %d)", piece.getPieceType(), fromFile, fromRank, toFile, toRank);
    }
}
