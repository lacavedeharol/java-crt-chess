package com.lacavedeharol.chess.ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.state.GameState.MoveResult;

/**
 * AI class to make moves for the AI player.
 */
public class AI {

    private final boolean isWhite;
    private final StockfishAI stockfishAI;
    private final Random random = new Random();
    private final int searchDepth;
    private final BoardEvaluator evaluator;

    /**
     * Tracks the destination of the last move the AI made, so we can penalise
     * immediately moving the same piece again (repetition discouragement).
     * -1 means no previous move recorded.
     */
    private int lastMovedToFile = -1;
    private int lastMovedToRank = -1;

    /**
     * Constructor.
     * 
     * @param isWhite    true if the AI player is white, false otherwise.
     * @param difficulty the difficulty of the AI player.
     */
    public AI(boolean isWhite, Opponent difficulty) {
        this.isWhite = isWhite;
        this.evaluator = new BoardEvaluator();
        this.searchDepth = switch (difficulty) {
            case EASY_AI -> 2;
            case HARD_AI -> 5;
            default -> 0;
        };
        this.stockfishAI = (difficulty == Opponent.STOCKFISH)
                ? new StockfishAI(20, 1000)
                : null;
    }

    /**
     * Makes a move for the AI player.
     * 
     * @param gameState the game state.
     * @return true if the move was successful, false otherwise.
     */
    public boolean makeMove(GameState gameState) {
        if (gameState.isWhiteToMove() != this.isWhite)
            return false;

        if (stockfishAI != null)
            return stockfishAI.makeMove(gameState);
        List<AIMove> allPossibleMoves = getAllLegalMoves(gameState);
        if (allPossibleMoves.isEmpty())
            return false;
        GameState searchState = gameState.copy();
        AIMove bestMove = findBestMove(searchState, allPossibleMoves, gameState);
        if (bestMove != null) {
            MoveResult result = gameState.movePiece(
                    bestMove.fromFile, bestMove.fromRank,
                    bestMove.toFile, bestMove.toRank);

            if (result == MoveResult.PROMOTION_REQUIRED)
                gameState.promotePawn(bestMove.toFile, bestMove.toRank, ChessPiece.PieceType.QUEEN);
            else if (result != MoveResult.VALID)
                return false;

            /*
             * Record where the AI's piece just arrived so we can penalise moving it again.
             */
            lastMovedToFile = bestMove.toFile;
            lastMovedToRank = bestMove.toRank;
        } else {
            /*
             * Fallback to random if no best move found (shouldn't happen if list not
             * empty).
             */
            AIMove randomMove = allPossibleMoves.get(random.nextInt(allPossibleMoves.size()));
            gameState.movePiece(randomMove.fromFile, randomMove.fromRank,
                    randomMove.toFile, randomMove.toRank);
            lastMovedToFile = randomMove.toFile;
            lastMovedToRank = randomMove.toRank;
        }
        return true;
    }

    /**
     * Finds the best move for the AI player.
     * 
     * @param gameState     The current state of the game (a copy used for search).
     * @param moves         The list of all legal moves.
     * @param realGameState The real game state (used for threat detection helpers).
     * @return The best move for the AI player.
     */
    private AIMove findBestMove(GameState gameState, List<AIMove> moves, GameState realGameState) {
        AIMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;
        boolean isEndgame = isEndgame(gameState);

        moves.sort(new MoveSorter(gameState, evaluator));

        for (AIMove move : moves) {
            ChessPiece movingPiece = gameState.getPieceAt(move.fromFile, move.fromRank);
            if (movingPiece == null)
                continue;

            ChessPiece capturedPiece = gameState.makeHypotheticalMove(
                    move.fromFile, move.fromRank, move.toFile, move.toRank);
            gameState.toggleTurn();

            boolean nextPlayerMaximizing = !this.isWhite;

            int evaluation = minimax(gameState, this.searchDepth - 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1,
                    nextPlayerMaximizing);

            int score = this.isWhite ? evaluation : -evaluation;

            gameState.toggleTurn();
            gameState.undoHypotheticalMove(
                    move.fromFile, move.fromRank, move.toFile, move.toRank,
                    movingPiece, capturedPiece);

            /*
             * King move penalty.
             * 
             * Discourage moving the king in the middlegame. In endgame the king
             * becomes an active piece and the penalty is lifted.
             * 
             * Allow castling (king moves 2 squares) without penalty.
             * 
             */
            if (movingPiece.getPieceType() == ChessPiece.PieceType.KING && !isEndgame) {
                if (Math.abs(move.toFile - move.fromFile) != 2)
                    score -= EvaluationConstants.KING_MOVE_PENALTY;
            }

            /*
             * Same-piece repetition penalty.
             * 
             * If this move picks up the piece we just moved last turn, that means we are
             * moving the same piece twice in a row. Apply a penalty unless:
             * a) it is capturing an enemy piece (tactical necessity).
             * b) the square it currently sits on is attacked by the opponent (saving it).
             */
            if (lastMovedToFile == move.fromFile && lastMovedToRank == move.fromRank) {
                boolean isCapture = capturedPiece != null;
                boolean squareUnderThreat = realGameState.isSquareUnderAttack(
                        move.fromFile, move.fromRank, !this.isWhite);
                if (!isCapture && !squareUnderThreat)
                    score -= EvaluationConstants.PIECE_REPEAT_PENALTY;
            }

            /* Small random tiebreak so equal positions don't produce identical games */
            score += random.nextInt(6) - 3;

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    /**
     * Performs minimax search to find the best move.
     * 
     * @param gameState    the current game state.
     * @param depth        the current depth.
     * @param alpha        the alpha value.
     * @param beta         the beta value.
     * @param isMaximizing true if the current player is maximizing, false
     *                     otherwise.
     * @return the best evaluation score.
     */
    private int minimax(GameState gameState, int depth, int alpha, int beta, boolean isMaximizing) {
        if (depth == 0)
            return quiescenceSearch(gameState, alpha, beta, isMaximizing);

        List<AIMove> moves = getAllLegalMoves(gameState);

        if (moves.isEmpty()) {
            if (gameState.isWhiteKingInCheck() || gameState.isBlackKingInCheck())
                return isMaximizing ? Integer.MIN_VALUE + 1000 + depth : Integer.MAX_VALUE - 1000 - depth;

            return 0;
        }

        moves.sort(new MoveSorter(gameState, evaluator));

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (AIMove move : moves) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                if (piece == null)
                    continue;

                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);
                gameState.toggleTurn();

                int eval = minimax(gameState, depth - 1, alpha, beta, false);

                gameState.toggleTurn();
                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha)
                    break;

            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (AIMove move : moves) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                if (piece == null)
                    continue;

                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);
                gameState.toggleTurn();

                int eval = minimax(gameState, depth - 1, alpha, beta, true);

                gameState.toggleTurn();
                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha)
                    break;

            }
            return minEval;
        }
    }

    /**
     * Performs quiescence search to find the best move.
     * 
     * @param gameState    the current game state.
     * @param alpha        the alpha value.
     * @param beta         the beta value.
     * @param isMaximizing true if the current player is maximizing, false
     *                     otherwise.
     * @return the best move.
     */
    private int quiescenceSearch(GameState gameState, int alpha, int beta, boolean isMaximizing) {
        int standPat = evaluator.evaluateBoard(gameState);
        if (isMaximizing) {
            if (standPat >= beta)
                return beta;
            if (alpha < standPat)
                alpha = standPat;
        } else {
            if (standPat <= alpha)
                return alpha;
            if (beta > standPat)
                beta = standPat;
        }

        List<AIMove> captures = getCaptureMoves(gameState);
        captures.sort(new MoveSorter(gameState, evaluator));

        if (isMaximizing) {
            for (AIMove move : captures) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                if (piece == null)
                    continue;

                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);
                gameState.toggleTurn();

                int score = quiescenceSearch(gameState, alpha, beta, false);

                gameState.toggleTurn();
                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                if (score >= beta)
                    return beta;
                if (score > alpha)
                    alpha = score;
            }
            return alpha;
        } else {
            for (AIMove move : captures) {
                ChessPiece piece = gameState.getPieceAt(move.fromFile, move.fromRank);
                if (piece == null)
                    continue;

                ChessPiece captured = gameState.makeHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank);
                gameState.toggleTurn();

                int score = quiescenceSearch(gameState, alpha, beta, true);

                gameState.toggleTurn();
                gameState.undoHypotheticalMove(
                        move.fromFile, move.fromRank, move.toFile, move.toRank, piece, captured);

                if (score <= alpha)
                    return alpha;
                if (score < beta)
                    beta = score;
            }
            return beta;
        }
    }

    /**
     * Gets all legal moves for the AI player.
     * 
     * @param gameState the game state.
     * @return a list of all legal moves.
     */
    private List<AIMove> getAllLegalMoves(GameState gameState) {
        List<AIMove> moves = new ArrayList<>();
        boolean whiteTurn = gameState.isWhiteToMove();

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == whiteTurn) {
                    List<Point> legalMoves = gameState.getLegalMovesForPiece(file, rank);
                    for (Point to : legalMoves)
                        moves.add(new AIMove(file, rank, to.x, to.y));
                }
            }
        }
        return moves;
    }

    /**
     * Gets all capture moves for the AI player.
     * 
     * @param gameState the game state.
     * @return a list of all capture moves.
     */
    private List<AIMove> getCaptureMoves(GameState gameState) {
        List<AIMove> captures = new ArrayList<>();
        List<AIMove> allMoves = getAllLegalMoves(gameState);

        for (AIMove move : allMoves) {
            ChessPiece target = gameState.getPieceAt(move.toFile, move.toRank);
            if (target != null)
                captures.add(move);
        }
        return captures;
    }

    /**
     * Returns true if the position is considered an endgame.
     * Uses the same heuristic as BoardEvaluator: fewer than 2 queens on the board.
     *
     * @param gameState the game state.
     * @return true if endgame.
     */
    private boolean isEndgame(GameState gameState) {
        int queens = 0;
        for (int rank = 0; rank < 8; rank++)
            for (int file = 0; file < 8; file++) {
                ChessPiece p = gameState.getPieceAt(file, rank);
                if (p != null && p.getPieceType() == ChessPiece.PieceType.QUEEN)
                    queens++;
            }
        return queens < 2;
    }

    /**
     * Disposes of the AI resources.
     */
    public void dispose() {
        if (stockfishAI != null)
            stockfishAI.dispose();
    }

    /**
     * Checks if the AI player is white.
     * 
     * @return true if the AI player is white, false otherwise.
     */
    boolean isWhite() {
        return this.isWhite;
    }
}
