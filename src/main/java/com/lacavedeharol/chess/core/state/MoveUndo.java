package com.lacavedeharol.chess.core.state;

import java.awt.Point;
import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Stores information needed to undo a move.
 * Used by the make/unmake pattern for efficient AI move simulation.
 */
class MoveUndo {

    final int fromFile, fromRank, toFile, toRank;

    final ChessPiece movedPiece, capturedPiece;
    final boolean pieceHadMoved;

    final Point previousEnPassantTarget;
    final boolean previousWhiteKingInCheck, previousBlackKingInCheck, wasWhiteToMove;

    final boolean wasEnPassantCapture, wasCastling;
    final ChessPiece castledRook;
    final int rookFromFile, rookToFile;

    /**
     * Constructor for regular moves.
     */
    MoveUndo(int fromFile, int fromRank, int toFile, int toRank,
            ChessPiece movedPiece, ChessPiece capturedPiece, boolean pieceHadMoved,
            Point previousEnPassantTarget, boolean previousWhiteKingInCheck,
            boolean previousBlackKingInCheck, boolean wasWhiteToMove) {
        this.fromFile = fromFile;
        this.fromRank = fromRank;
        this.toFile = toFile;
        this.toRank = toRank;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.pieceHadMoved = pieceHadMoved;
        this.previousEnPassantTarget = previousEnPassantTarget;
        this.previousWhiteKingInCheck = previousWhiteKingInCheck;
        this.previousBlackKingInCheck = previousBlackKingInCheck;
        this.wasWhiteToMove = wasWhiteToMove;
        this.wasEnPassantCapture = false;
        this.wasCastling = false;
        this.castledRook = null;
        this.rookFromFile = -1;
        this.rookToFile = -1;
    }

    /**
     * Constructor for special moves (en passant, castling).
     */
    MoveUndo(int fromFile, int fromRank, int toFile, int toRank,
            ChessPiece movedPiece, ChessPiece capturedPiece, boolean pieceHadMoved,
            Point previousEnPassantTarget, boolean previousWhiteKingInCheck,
            boolean previousBlackKingInCheck, boolean wasWhiteToMove,
            boolean wasEnPassantCapture, boolean wasCastling,
            ChessPiece castledRook, int rookFromFile, int rookToFile) {
        this.fromFile = fromFile;
        this.fromRank = fromRank;
        this.toFile = toFile;
        this.toRank = toRank;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.pieceHadMoved = pieceHadMoved;
        this.previousEnPassantTarget = previousEnPassantTarget;
        this.previousWhiteKingInCheck = previousWhiteKingInCheck;
        this.previousBlackKingInCheck = previousBlackKingInCheck;
        this.wasWhiteToMove = wasWhiteToMove;
        this.wasEnPassantCapture = wasEnPassantCapture;
        this.wasCastling = wasCastling;
        this.castledRook = castledRook;
        this.rookFromFile = rookFromFile;
        this.rookToFile = rookToFile;
    }
}
