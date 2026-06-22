package com.lacavedeharol.chess.core.moves;

import com.lacavedeharol.chess.core.state.GameState;

/**
 * Utility class for move validation.
 */
final class MoveUtils {

    private MoveUtils() {
    }

    /**
     * Checks if the path between two squares is clear for a straight move.
     * 
     * @param fromFile  the starting file.
     * @param fromRank  the starting rank.
     * @param toFile    the destination file.
     * @param toRank    the destination rank.
     * @param gameState the game state.
     * @return true if the path is clear, false otherwise.
     */
    protected static boolean isPathClearStraight(int fromFile, int fromRank,
            int toFile, int toRank,
            GameState gameState) {

        if (fromFile == toFile) { // Vertical move.
            int step = Integer.signum(toRank - fromRank);

            for (int r = fromRank + step; r != toRank; r += step)
                if (gameState.getPieceAt(fromFile, r) != null)
                    return false;
        } else {
            int step = Integer.signum(toFile - fromFile);

            for (int f = fromFile + step; f != toFile; f += step)
                if (gameState.getPieceAt(f, fromRank) != null)
                    return false;
        }
        return true;
    }

    /**
     * Checks if the path between two squares is clear for a diagonal move.
     * 
     * @param fromFile  the starting file.
     * @param fromRank  the starting rank.
     * @param toFile    the destination file.
     * @param toRank    the destination rank.
     * @param gameState the game state.
     * @return true if the path is clear, false otherwise.
     */
    protected static boolean isPathClearDiagonal(int fromFile, int fromRank,
            int toFile, int toRank,
            GameState gameState) {

        int fileStep = Integer.signum(toFile - fromFile);
        int rankStep = Integer.signum(toRank - fromRank);

        int currentFile = fromFile + fileStep;
        int currentRank = fromRank + rankStep;

        while (currentFile != toFile) {
            if (gameState.getPieceAt(currentFile, currentRank) != null)
                return false;
            currentFile += fileStep;
            currentRank += rankStep;
        }
        return true;
    }
}
