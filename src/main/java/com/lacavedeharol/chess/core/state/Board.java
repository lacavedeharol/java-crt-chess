package com.lacavedeharol.chess.core.state;

import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.core.ChessPiece;

/**
 * Board class represents the chess board.
 */
class Board {
    private final ChessPiece[][] pieces;
    private final List<ChessPiece> capturedPieces;

    /**
     * Constructor.
     */
    Board() {
        this.pieces = new ChessPiece[8][8];
        this.capturedPieces = new ArrayList<>();
    }

    /**
     * Clears the board.
     */
    void clear() {
        for (int f = 0; f < 8; f++)
            for (int r = 0; r < 8; r++)
                pieces[f][r] = null;
        capturedPieces.clear();
    }

    /**
     * Gets the piece at the specified position.
     * 
     * @param file the file
     * @param rank the rank
     * @return the piece
     */
    ChessPiece getPieceAt(int file, int rank) {
        return (file < 0 || file >= 8 || rank < 0 || rank >= 8) ? null : pieces[file][rank];
    }

    /**
     * Sets the piece at the specified position.
     * 
     * @param file  the file
     * @param rank  the rank
     * @param piece the piece
     */
    void setPieceAt(int file, int rank, ChessPiece piece) {
        if (file >= 0 && file < 8 && rank >= 0 && rank < 8) {
            pieces[file][rank] = piece;
            if (piece != null)
                piece.setPosition(file, rank);
        }
    }

    /**
     * Removes the piece at the specified position.
     * 
     * @param file the file
     * @param rank the rank
     */
    void removePieceAt(int file, int rank) {
        if (file >= 0 && file < 8 && rank >= 0 && rank < 8)
            pieces[file][rank] = null;
    }

    /**
     * Adds a captured piece.
     * 
     * @param piece the piece
     */
    void addCapturedPiece(ChessPiece piece) {
        capturedPieces.add(piece);
    }

    /**
     * Gets the captured pieces.
     * 
     * @return the captured pieces
     */
    List<ChessPiece> getCapturedPieces() {
        return capturedPieces;
    }

    /**
     * Gets the pieces array.
     * 
     * @return the pieces array
     */
    ChessPiece[][] getPiecesArray() {
        return pieces;
    }

    /**
     * Copies the board.
     * 
     * @return the copy
     */
    Board copy() {
        Board newBoard = new Board();
        for (int f = 0; f < 8; f++) {
            for (int r = 0; r < 8; r++) {
                if (this.pieces[f][r] != null)
                    newBoard.pieces[f][r] = this.pieces[f][r].copy();
            }
        }
        for (ChessPiece p : this.capturedPieces)
            newBoard.capturedPieces.add(p.copy());
        return newBoard;
    }

    /**
     * Initializes the board.
     */
    void initialize() {
        clear();
        ChessPiece.PieceType[] backRowOrder = {
                ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KING, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK
        };

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                switch (rank) {
                    case 1 -> setPieceAt(file, rank, new ChessPiece(true, ChessPiece.PieceType.PAWN, file, rank));
                    case 0 -> setPieceAt(file, rank, new ChessPiece(true, backRowOrder[file], file, rank));
                    case 6 -> setPieceAt(file, rank, new ChessPiece(false, ChessPiece.PieceType.PAWN, file, rank));
                    case 7 -> setPieceAt(file, rank, new ChessPiece(false, backRowOrder[file], file, rank));
                }
            }
        }
    }
}
