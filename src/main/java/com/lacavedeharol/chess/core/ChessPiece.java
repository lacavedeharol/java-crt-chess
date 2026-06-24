package com.lacavedeharol.chess.core;

/**
 * ChessPiece class represents a chess piece.
 */
public class ChessPiece {

    /**
     * Enum representing the type of the chess piece.
     */
    public enum PieceType {
        /** Pawn piece. */
        PAWN,
        /** Bishop piece. */
        BISHOP,
        /** Knight piece. */
        KNIGHT,
        /** Rook piece. */
        ROOK,
        /** Queen piece. */
        QUEEN,
        /** King piece. */
        KING
    }

    // Core Data:
    private final boolean isWhite;
    private final PieceType pieceType;
    private int file, rank;
    private boolean hasMoved;

    /**
     * Constructor for ChessPiece.
     * 
     * @param isWhite   true if the piece is white, false otherwise.
     * @param pieceType the type of the piece.
     * @param file      the file of the piece.
     * @param rank      the rank of the piece.
     */
    public ChessPiece(boolean isWhite, PieceType pieceType, int file, int rank) {
        this.isWhite = isWhite;
        this.pieceType = pieceType;
        this.file = file;
        this.rank = rank;
        this.hasMoved = false;
    }

    /**
     * Returns true if the piece is white.
     * 
     * @return true if the piece is white, false otherwise.
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Returns the type of the piece.
     * 
     * @return the type of the piece.
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Returns the file of the piece.
     * 
     * @return the file of the piece.
     */
    public int getFile() {
        return file;
    }

    /**
     * Returns the rank of the piece.
     * 
     * @return the rank of the piece.
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns true if the piece has moved.
     * 
     * @return true if the piece has moved, false otherwise.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Sets the position of the piece.
     * 
     * @param file the file of the piece.
     * @param rank the rank of the piece.
     */
    public void setPosition(int file, int rank) {
        this.file = file;
        this.rank = rank;
    }

    /**
     * Marks the piece as moved.
     */
    public void markAsMoved() {
        this.hasMoved = true;
    }

    /**
     * Sets the moved status of the piece.
     * Used for unmake operations.
     * 
     * @param hasMoved the moved status.
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Creates a deep copy of this chess piece.
     * 
     * @return a new ChessPiece with the same state.
     */
    public ChessPiece copy() {
        ChessPiece newPiece = new ChessPiece(this.isWhite, this.pieceType, this.file, this.rank);
        if (this.hasMoved)
            newPiece.markAsMoved();
        return newPiece;
    }
}
