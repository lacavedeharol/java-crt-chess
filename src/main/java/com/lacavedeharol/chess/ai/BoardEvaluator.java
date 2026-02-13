package com.lacavedeharol.chess.ai;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.ChessPiece.PieceType;

/**
 * Class used to evaluate the board.
 */
class BoardEvaluator {

    /**
     * Evaluates the board.
     * 
     * @param gameState the game state
     * @return the board evaluation
     */
    int evaluateBoard(GameState gameState) {
        int score = 0;
        boolean isEndgame = isEndgame(gameState);

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null) {
                    int pieceValue = getPieceValue(piece);
                    int positionalValue = getPositionalValue(piece, file, rank, isEndgame);

                    int totalValue = pieceValue + positionalValue;

                    score = piece.isWhite() ? score + totalValue : score - totalValue;
                }
            }
        }
        return score;
    }

    /**
     * Checks if the game is in the endgame.
     * 
     * @param gameState the game state
     * @return true if the game is in the endgame, false otherwise
     */
    private boolean isEndgame(GameState gameState) {
        int scale = 0;
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                ChessPiece p = gameState.getPieceAt(f, r);
                if (p != null && (p.getPieceType() == PieceType.QUEEN))
                    scale += 2;
            }
        }
        return scale < 2;
    }

    /**
     * Gets the value of a piece.
     * 
     * @param piece the piece
     * @return the value of the piece
     */
    int getPieceValue(ChessPiece piece) {
        if (piece == null)
            return 0;
        return switch (piece.getPieceType()) {
            case PAWN -> (piece.getRank() == 0 || piece.getRank() == 7) ? 450 : 50;
            case KNIGHT -> 160;
            case BISHOP -> 165;
            case ROOK -> 250;
            case QUEEN -> 450;
            case KING -> 10000;
        };
    }

    /**
     * Gets the positional value of a piece.
     * 
     * @param piece     the piece
     * @param file      the file of the piece
     * @param rank      the rank of the piece
     * @param isEndgame true if the game is in the endgame, false otherwise
     * @return the positional value of the piece
     */
    private int getPositionalValue(ChessPiece piece, int file, int rank, boolean isEndgame) {
        int tableRank = piece.isWhite() ? rank : (7 - rank);
        int tableFile = file;

        return switch (piece.getPieceType()) {
            case PAWN -> EvaluationConstants.PAWN_TABLE[tableRank][tableFile];
            case KNIGHT -> EvaluationConstants.KNIGHT_TABLE[tableRank][tableFile];
            case BISHOP -> EvaluationConstants.BISHOP_TABLE[tableRank][tableFile];
            case ROOK -> EvaluationConstants.ROOK_TABLE[tableRank][tableFile];
            case QUEEN -> EvaluationConstants.QUEEN_TABLE[tableRank][tableFile];
            case KING -> isEndgame
                    ? EvaluationConstants.KING_TABLE_ENDGAME[tableRank][tableFile]
                    : EvaluationConstants.KING_TABLE_MIDGAME[tableRank][tableFile];
        };
    }
}
