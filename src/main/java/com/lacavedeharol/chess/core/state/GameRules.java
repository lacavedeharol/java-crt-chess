package com.lacavedeharol.chess.core.state;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.moves.MoveManager;
import com.lacavedeharol.chess.core.state.GameState.GameStatus;

/**
 * GameRules class encapsulates the rules and logic of the chess game.
 * It handles move validation, check detection, and game status evaluation.
 */
class GameRules {

    private final GameState gameState;

    /**
     * Constructor for GameRules.
     * 
     * @param gameState the game state
     */
    GameRules(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Gets the legal moves for a piece.
     * 
     * @param file the file of the piece
     * @param rank the rank of the piece
     * @return the legal moves for the piece
     */
    List<Point> getLegalMovesForPiece(int file, int rank) {
        ChessPiece piece = gameState.getPieceAt(file, rank);
        if (piece == null || piece.isWhite() != gameState.isWhiteToMove())
            return new ArrayList<>();

        List<Point> pseudoLegalMoves = MoveManager.generateLegalMoves(gameState, piece, file, rank);
        List<Point> legalMoves = new ArrayList<>();
        Point kingPosition = findKing(gameState.isWhiteToMove());

        if (kingPosition == null)
            return pseudoLegalMoves;

        for (Point move : pseudoLegalMoves) {
            ChessPiece capturedPiece = gameState.makeHypotheticalMove(file, rank, move.x, move.y);
            Point currentKingPos = (piece.getPieceType() == ChessPiece.PieceType.KING) ? move : kingPosition;
            boolean kingIsInCheck = isSquareUnderAttack(currentKingPos.x, currentKingPos.y, !gameState.isWhiteToMove());
            gameState.undoHypotheticalMove(file, rank, move.x, move.y, piece, capturedPiece);

            if (!kingIsInCheck)
                legalMoves.add(move);
        }
        return legalMoves;
    }

    /**
     * Checks if a square is under attack.
     * 
     * @param file              the file of the square
     * @param rank              the rank of the square
     * @param isAttackedByWhite true if the square is attacked by white, false
     *                          otherwise
     * @return true if the square is under attack, false otherwise
     */
    boolean isSquareUnderAttack(int file, int rank, boolean isAttackedByWhite) {
        for (int f = 0; f < 8; f++) {
            for (int r = 0; r < 8; r++) {
                ChessPiece piece = gameState.getPieceAt(f, r);
                if (piece != null && piece.isWhite() == isAttackedByWhite) {
                    if (MoveManager.getValidator(piece.getPieceType()).isValidMove(piece, f, r, file, rank,
                            gameState)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Finds the king of a specific color.
     * 
     * @param isWhite true if the king is white, false otherwise
     * @return the position of the king
     */
    Point findKing(boolean isWhite) {
        for (int r = 0; r < 8; r++) {
            for (int f = 0; f < 8; f++) {
                ChessPiece piece = gameState.getPieceAt(f, r);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.isWhite() == isWhite)
                    return new Point(f, r);
            }
        }
        return null;
    }

    /**
     * Gets the status of the game.
     * 
     * @return the status of the game
     */
    GameStatus getGameStatus() {
        if (hasLegalMoves(gameState.isWhiteToMove()))
            return GameStatus.IN_PROGRESS;

        if (isInsufficientMaterial())
            return GameStatus.DRAW_INSUFFICIENT_MATERIAL;

        return gameState.isWhiteToMove()
                ? (gameState.isWhiteKingInCheck() ? GameStatus.CHECKMATE_BLACK_WINS : GameStatus.STALEMATE)
                : (gameState.isBlackKingInCheck() ? GameStatus.CHECKMATE_WHITE_WINS : GameStatus.STALEMATE);
    }

    /**
     * Checks if a player has any legal moves.
     * 
     * @param isWhite true if checking for white, false otherwise
     * @return true if the player has legal moves, false otherwise
     */
    boolean hasLegalMoves(boolean isWhite) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                ChessPiece piece = gameState.getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == isWhite)
                    if (!getLegalMovesForPiece(file, rank).isEmpty())
                        return true;
            }
        }
        return false;
    }

    /**
     * Checks if there is insufficient material for a checkmate.
     * 
     * @return true if there is insufficient material, false otherwise
     */
    boolean isInsufficientMaterial() {
        List<ChessPiece> whitePieces = new ArrayList<>();
        List<ChessPiece> blackPieces = new ArrayList<>();

        ChessPiece[][] pieces = gameState.getPieces();

        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                ChessPiece p = pieces[file][rank];
                if (p != null)
                    if (p.isWhite())
                        whitePieces.add(p);
                    else
                        blackPieces.add(p);
            }
        }
        if (whitePieces.size() == 1 && blackPieces.size() == 1)
            return true;
        if (whitePieces.size() == 1 && blackPieces.size() == 2)
            return isKingVsKingAndMinor(blackPieces);
        if (blackPieces.size() == 1 && whitePieces.size() == 2)
            return isKingVsKingAndMinor(whitePieces);
        if (whitePieces.size() == 2 && blackPieces.size() == 2) {
            ChessPiece whiteBishop = getBishop(whitePieces);
            ChessPiece blackBishop = getBishop(blackPieces);
            if (whiteBishop != null && blackBishop != null) {
                boolean whiteBishopOnWhite = (whiteBishop.getFile() + whiteBishop.getRank()) % 2 != 0;
                boolean blackBishopOnWhite = (blackBishop.getFile() + blackBishop.getRank()) % 2 != 0;
                return whiteBishopOnWhite == blackBishopOnWhite;
            }
        }
        return false;
    }

    /**
     * Checks if there is insufficient material for a checkmate.
     * 
     * @param pieces the list of pieces
     * @return true if there is insufficient material, false otherwise
     */
    private boolean isKingVsKingAndMinor(List<ChessPiece> pieces) {
        for (ChessPiece p : pieces)
            if (p.getPieceType() != ChessPiece.PieceType.KING &&
                    p.getPieceType() != ChessPiece.PieceType.KNIGHT &&
                    p.getPieceType() != ChessPiece.PieceType.BISHOP)
                return false;
        return true;
    }

    /**
     * Gets the bishop from a list of pieces.
     * 
     * @param pieces the list of pieces
     * @return the bishop
     */
    private ChessPiece getBishop(List<ChessPiece> pieces) {
        return pieces.stream().filter(p -> p.getPieceType() == ChessPiece.PieceType.BISHOP).findFirst().orElse(null);
    }
}
