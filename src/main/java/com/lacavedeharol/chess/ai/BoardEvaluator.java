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

        /*
         * Check bonus: reward having the opponent king in check
         */
        if (gameState.isBlackKingInCheck())
            score += EvaluationConstants.CHECK_BONUS;
        if (gameState.isWhiteKingInCheck())
            score -= EvaluationConstants.CHECK_BONUS;

        /*
         * King escape penalty: penalise the side whose king has more free escape
         * squares
         * (from white's perspective: white wants black king to have FEW escape squares)
         */
        score -= countKingMobility(gameState, false) * EvaluationConstants.KING_ESCAPE_PENALTY;
        score += countKingMobility(gameState, true) * EvaluationConstants.KING_ESCAPE_PENALTY;

        return score;
    }

    /**
     * Counts how many adjacent squares the king of the given colour can safely move
     * to.
     * Uses raw attack-detection rather than full legal-move generation to stay
     * cheap.
     *
     * @param gameState the game state
     * @param isWhite   true for the white king, false for the black king
     * @return number of unattacked adjacent squares available to the king
     */
    private int countKingMobility(GameState gameState, boolean isWhite) {
        java.awt.Point kingPos = gameState.findKing(isWhite);
        if (kingPos == null)
            return 0;

        int[] dx = { -1, 0, 1, -1, 1, -1, 0, 1 };
        int[] dy = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int mobility = 0;

        for (int i = 0; i < 8; i++) {
            int nf = kingPos.x + dx[i];
            int nr = kingPos.y + dy[i];
            if (nf < 0 || nf > 7 || nr < 0 || nr > 7)
                continue;
            ChessPiece occupant = gameState.getPieceAt(nf, nr);
            /*
             * Square must be empty or occupied by an enemy piece AND not attacked by
             * opponent
             */
            if (occupant != null && occupant.isWhite() == isWhite)
                continue;
            if (!gameState.isSquareUnderAttack(nf, nr, !isWhite))
                mobility++;
        }
        return mobility;
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
