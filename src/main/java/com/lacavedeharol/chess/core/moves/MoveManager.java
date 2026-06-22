package com.lacavedeharol.chess.core.moves;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;
import com.lacavedeharol.chess.core.ChessPiece.PieceType;

/**
 * Manages move validation strategies for different piece types.
 * Static utility class with stateless validators for performance.
 */
public final class MoveManager {

    /**
     * Interface for move validation strategies.
     */
    public interface MoveValidationStrategy {
        /**
         * Validates a move.
         * 
         * @param piece     the piece to move.
         * @param fromFile  the file of the piece.
         * @param fromRank  the rank of the piece.
         * @param toFile    the file to move to.
         * @param toRank    the rank to move to.
         * @param gameState the game state.
         * @return true if the move is valid, false otherwise.
         */
        boolean isValidMove(ChessPiece piece, int fromFile, int fromRank, int toFile, int toRank, GameState gameState);
    }

    // Static, immutable validators
    private static final Map<PieceType, MoveValidationStrategy> VALIDATORS = Map.of(
            PieceType.PAWN, new PawnMoveValidation(),
            PieceType.BISHOP, new BishopMoveValidation(),
            PieceType.KNIGHT, new KnightMoveValidation(),
            PieceType.ROOK, new RookMoveValidation(),
            PieceType.QUEEN, new QueenMoveValidation(),
            PieceType.KING, new KingMoveValidation());

    /**
     * Private constructor to prevent instantiation.
     */
    private MoveManager() {
    }

    /**
     * Generates a list of legal moves for a given piece.
     * 
     * @param gameState The current game state.
     * @param piece     The piece to generate moves for.
     * @param fromFile  The file of the piece.
     * @param fromRank  The rank of the piece.
     * @return A list of legal moves for the piece.
     */
    public static List<Point> generateLegalMoves(GameState gameState, ChessPiece piece, int fromFile, int fromRank) {
        MoveValidationStrategy validator = VALIDATORS.get(piece.getPieceType());
        if (validator == null)
            return new ArrayList<>();

        List<Point> moves = new ArrayList<>();
        for (int toFile = 0; toFile < 8; toFile++)
            for (int toRank = 0; toRank < 8; toRank++)
                if (validator.isValidMove(piece, fromFile, fromRank, toFile, toRank, gameState))
                    moves.add(new Point(toFile, toRank));
        return moves;
    }

    /**
     * Gets the move validation strategy for a given piece type.
     * 
     * @param pieceType The type of the piece.
     * @return The move validation strategy for the given piece type.
     */
    public static MoveValidationStrategy getValidator(PieceType pieceType) {
        return VALIDATORS.get(pieceType);
    }
}
