package com.lacavedeharol.chess.ai;

import java.util.Comparator;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.ChessPiece.PieceType;

/**
 * Class used to sort moves based on their score.
 */
class MoveSorter implements Comparator<AIMove> {

    private final GameState gameState;
    private final BoardEvaluator evaluator;

    /**
     * Constructor.
     * 
     * @param gameState the game state
     * @param evaluator the board evaluator
     */
    MoveSorter(GameState gameState, BoardEvaluator evaluator) {
        this.gameState = gameState;
        this.evaluator = evaluator;
    }

    /**
     * Compares two moves based on their score.
     * 
     * @param m1 the first move
     * @param m2 the second move
     * @return the comparison result
     */
    @Override
    public int compare(AIMove m1, AIMove m2) {
        int score1 = scoreMove(m1);
        int score2 = scoreMove(m2);
        return Integer.compare(score2, score1);
    }

    /**
     * Scores a move based on the piece values and the piece type.
     * 
     * @param m the move to score
     * @return the score of the move
     */
    private int scoreMove(AIMove m) {
        int score = 0;
        ChessPiece victim = gameState.getPieceAt(m.toFile, m.toRank);
        ChessPiece aggressor = gameState.getPieceAt(m.fromFile, m.fromRank);

        if (aggressor == null)
            return 0;

        if (victim != null)
            score += 10 * evaluator.getPieceValue(victim) - evaluator.getPieceValue(aggressor);

        if (aggressor.getPieceType() == PieceType.PAWN)
            if (m.toRank == 0 || m.toRank == 7)
                score += 900;

        return score;
    }
}
