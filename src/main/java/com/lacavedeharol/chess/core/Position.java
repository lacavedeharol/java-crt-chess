package com.lacavedeharol.chess.core;

/**
 * Immutable record representing a position on the chess board.
 * Replaces java.awt.Point for type-safety and chess-specific functionality.
 * 
 * @param file the file (column) coordinate (0-7)
 * @param rank the rank (row) coordinate (0-7)
 */
record Position(int file, int rank) {

    /**
     * Factory method to create a Position.
     * 
     * @param file the file coordinate
     * @param rank the rank coordinate
     * @return a new Position
     */
    static Position of(int file, int rank) {
        return new Position(file, rank);
    }

    /**
     * Checks if this position is within the valid chess board bounds.
     * 
     * @return true if the position is valid (0-7 for both file and rank)
     */
    boolean isValid() {
        return file >= 0 && file < 8 && rank >= 0 && rank < 8;
    }

    /**
     * Checks if this position is on the same file as another position.
     * 
     * @param other the other position
     * @return true if both positions share the same file
     */
    boolean isSameFile(Position other) {
        return this.file == other.file;
    }

    /**
     * Checks if this position is on the same rank as another position.
     *
     * @param other the other position
     * @return true if both positions share the same rank
     */
    boolean isSameRank(Position other) {
        return this.rank == other.rank;
    }

    /**
     * Checks if this position is on the same diagonal as another position.
     * 
     * @param other the other position
     * @return true if both positions are on the same diagonal
     */
    boolean isSameDiagonal(Position other) {
        return Math.abs(this.file - other.file) == Math.abs(this.rank - other.rank);
    }

    /**
     * Returns the algebraic notation for this position.
     * 
     * @return the algebraic notation
     */
    @Override
    public String toString() {
        char fileChar = (char) ('a' + file);
        int rankNum = rank + 1;
        return "" + fileChar + rankNum;
    }
}
