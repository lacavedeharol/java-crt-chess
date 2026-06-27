package com.lacavedeharol.chess.computer.minimax;

/**
 * Represents a move for the AI player.
 */
class AIMove {
    final int fromFile, fromRank, toFile, toRank;

    /**
     * Constructor.
     * 
     * @param fromFile the file of the piece to move
     * @param fromRank the rank of the piece to move
     * @param toFile   the file of the destination square
     * @param toRank   the rank of the destination square
     */
    AIMove(int fromFile, int fromRank, int toFile, int toRank) {
        this.fromFile = fromFile;
        this.fromRank = fromRank;
        this.toFile = toFile;
        this.toRank = toRank;
    }

    /**
     * Returns a string representation of the move.
     * 
     * @return the string representation of the move
     */
    @Override
    public String toString() {
        return String.format("(%d,%d) -> (%d,%d)", fromFile, fromRank, toFile, toRank);
    }
}
