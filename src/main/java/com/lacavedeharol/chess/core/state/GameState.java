package com.lacavedeharol.chess.core.state;

import java.awt.Point;
import java.util.List;
import java.util.Stack;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.moves.Move;

/**
 * GameState class represents the state of the chess game.
 */
public class GameState {

    private final Board board;
    private final GameContext context;
    private final GameRules gameRules;

    // Cached king positions for performance
    private Point whiteKingPosition;
    private Point blackKingPosition;

    /**
     * Constructor for GameState.
     */
    public GameState() {
        this.board = new Board();
        this.context = new GameContext();
        this.gameRules = new GameRules(this);
        reset();
    }

    private GameState(Board board, GameContext context,
            java.util.Stack<com.lacavedeharol.chess.core.moves.Move> history) {
        this.board = board;
        this.context = context;
        this.gameRules = new GameRules(this);
        this.moveHistory.addAll(history);
    }

    /**
     * Enum representing the result of a move.
     */
    public enum MoveResult {
        /**
         * The move is valid.
         */
        VALID,
        /**
         * The move is invalid.
         */
        INVALID,
        /**
         * The move requires promotion.
         */
        PROMOTION_REQUIRED
    }

    /**
     * Returns the chess pieces.
     * 
     * @return the chess pieces
     */
    public ChessPiece[][] getPieces() {
        return board.getPiecesArray();
    }

    /**
     * Returns the captured pieces.
     * 
     * @return the captured pieces
     */
    public List<ChessPiece> getCapturedPieces() {
        return board.getCapturedPieces();
    }

    /**
     * Resets the game state to the initial state.
     */
    public void reset() {
        board.initialize();
        context.reset();
        moveHistory.clear();
        updateKingPositionCache();
    }

    /**
     * Initializes the chess board with the starting positions of the pieces.
     */
    public void initializePieces() {
        board.initialize();
    }

    /**
     * Moves a piece from one square to another.
     * 
     * @param fromFile the file of the piece to move
     * @param fromRank the rank of the piece to move
     * @param toFile   the file of the square to move to
     * @param toRank   the rank of the square to move to
     * @return the result of the move
     */
    public MoveResult movePiece(int fromFile, int fromRank, int toFile, int toRank) {
        ChessPiece piece = getPieceAt(fromFile, fromRank);
        if (piece == null || piece.isWhite() != context.isWhiteToMove())
            return MoveResult.INVALID;

        List<Point> legalMoves = getLegalMovesForPiece(fromFile, fromRank);
        if (!legalMoves.contains(new Point(toFile, toRank)))
            return MoveResult.INVALID;

        Point previousEnPassantTarget = context.getEnPassantTargetSquare();

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && new Point(toFile, toRank).equals(previousEnPassantTarget)) {

            int capturedPawnRank = fromRank;
            int capturedPawnFile = toFile;

            ChessPiece capturedPawn = getPieceAt(capturedPawnFile, capturedPawnRank);
            if (capturedPawn != null) {
                board.addCapturedPiece(capturedPawn);
                board.removePieceAt(capturedPawnFile, capturedPawnRank);
            }
        } else {
            ChessPiece captured = getPieceAt(toFile, toRank);
            moveHistory.push(
                    new com.lacavedeharol.chess.core.moves.Move(fromFile, fromRank, toFile, toRank, piece, captured));
            if (captured != null)
                board.addCapturedPiece(captured);
        }

        board.setPieceAt(toFile, toRank, piece);
        board.removePieceAt(fromFile, fromRank);
        piece.markAsMoved();

        // Update king position cache if king moved
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.isWhite()) {
                whiteKingPosition = new Point(toFile, toRank);
            } else {
                blackKingPosition = new Point(toFile, toRank);
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.KING && Math.abs(toFile - fromFile) == 2) {
            if (toFile > fromFile) {
                ChessPiece rook = getPieceAt(7, fromRank);
                board.removePieceAt(7, fromRank);
                board.setPieceAt(5, fromRank, rook);
                rook.markAsMoved();
            } else {
                ChessPiece rook = getPieceAt(0, fromRank);
                board.removePieceAt(0, fromRank);
                board.setPieceAt(3, fromRank, rook);
                rook.markAsMoved();
            }
        }

        context.setEnPassantTargetSquare(null);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(fromRank - toRank) == 2)
            context.setEnPassantTargetSquare(new Point(toFile, (fromRank + toRank) / 2));

        boolean isPromotion = (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && (toRank == 0 || toRank == 7));
        if (isPromotion)
            return MoveResult.PROMOTION_REQUIRED;

        context.toggleTurn();
        updateCheckStatus();
        return MoveResult.VALID;
    }

    /**
     * Gets the legal moves for a piece.
     * 
     * @param file the file of the piece
     * @param rank the rank of the piece
     * @return the legal moves for the piece
     */
    public List<Point> getLegalMovesForPiece(int file, int rank) {
        return gameRules.getLegalMovesForPiece(file, rank);
    }

    /**
     * Promotes a pawn to a different piece.
     * 
     * @param file   the file of the pawn
     * @param rank   the rank of the pawn
     * @param choice the piece to promote to
     */
    public void promotePawn(int file, int rank, ChessPiece.PieceType choice) {
        ChessPiece pawn = getPieceAt(file, rank);
        if (pawn == null || pawn.getPieceType() != ChessPiece.PieceType.PAWN)
            return;

        ChessPiece promotedPiece = new ChessPiece(pawn.isWhite(), choice, file, rank);
        promotedPiece.markAsMoved();
        board.setPieceAt(file, rank, promotedPiece);

        context.toggleTurn();
        updateCheckStatus();
    }

    /**
     * Gets the piece at a specific square.
     * 
     * @param file the file of the square
     * @param rank the rank of the square
     * @return the piece at the square
     */
    public ChessPiece getPieceAt(int file, int rank) {
        return board.getPieceAt(file, rank);
    }

    /**
     * Functional interface for board iteration callbacks.
     */
    @FunctionalInterface
    public interface BoardSquareConsumer {
        /**
         * Accepts a square on the board.
         * 
         * @param file  the file of the square
         * @param rank  the rank of the square
         * @param piece the piece at the square
         */
        void accept(int file, int rank, ChessPiece piece);
    }

    /**
     * Iterates over all squares on the board.
     * 
     * @param consumer the consumer to call for each square
     */
    public void forEachSquare(BoardSquareConsumer consumer) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                ChessPiece piece = getPieceAt(file, rank);
                consumer.accept(file, rank, piece);
            }
        }
    }

    /**
     * Iterates over all pieces of a specific color.
     * 
     * @param isWhite  true for white pieces, false for black pieces
     * @param consumer the consumer to call for each piece
     */
    public void forEachPiece(boolean isWhite, BoardSquareConsumer consumer) {
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                ChessPiece piece = getPieceAt(file, rank);
                if (piece != null && piece.isWhite() == isWhite) {
                    consumer.accept(file, rank, piece);
                }
            }
        }
    }

    /**
     * Checks if the white king is in check.
     * 
     * @return true if the white king is in check, false otherwise
     */
    public boolean isWhiteKingInCheck() {
        return context.isWhiteKingInCheck();
    }

    /**
     * Checks if the black king is in check.
     * 
     * @return true if the black king is in check, false otherwise
     */
    public boolean isBlackKingInCheck() {
        return context.isBlackKingInCheck();
    }

    /**
     * Gets the en passant target square.
     * 
     * @return the en passant target square
     */
    public Point getEnPassantTargetSquare() {
        return context.getEnPassantTargetSquare();
    }

    /**
     * Checks if it is white's turn to move.
     * 
     * @return true if it is white's turn to move, false otherwise
     */
    public boolean isWhiteToMove() {
        return context.isWhiteToMove();
    }

    /**
     * Toggles the turn.
     */
    public void toggleTurn() {
        context.toggleTurn();
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
    public boolean isSquareUnderAttack(int file, int rank, boolean isAttackedByWhite) {
        return gameRules.isSquareUnderAttack(file, rank, isAttackedByWhite);
    }

    /**
     * Finds the king of a specific color.
     * 
     * @param isWhite true if the king is white, false otherwise
     * @return the position of the king
     */
    public Point findKing(boolean isWhite) {
        // Return cached position if available
        Point cached = isWhite ? whiteKingPosition : blackKingPosition;
        if (cached != null) {
            return cached;
        }
        // Fallback to scanning (shouldn't happen in normal gameplay)
        return gameRules.findKing(isWhite);
    }

    /**
     * Updates the cached king positions by scanning the board.
     * Called during initialization and copying.
     */
    private void updateKingPositionCache() {
        whiteKingPosition = gameRules.findKing(true);
        blackKingPosition = gameRules.findKing(false);
    }

    /**
     * Updates the check status of the kings.
     */
    private void updateCheckStatus() {
        Point whiteKingPos = findKing(true);
        context.setWhiteKingInCheck(
                (whiteKingPos != null) ? gameRules.isSquareUnderAttack(whiteKingPos.x, whiteKingPos.y, false) : false);

        Point blackKingPos = findKing(false);
        context.setBlackKingInCheck(
                (blackKingPos != null) ? gameRules.isSquareUnderAttack(blackKingPos.x, blackKingPos.y, true) : false);
    }

    /**
     * Makes a hypothetical move without updating the game state.
     * 
     * @param fromFile the file of the piece to move
     * @param fromRank the rank of the piece to move
     * @param toFile   the file of the square to move to
     * @param toRank   the rank of the square to move to
     * @return the captured piece
     */
    public ChessPiece makeHypotheticalMove(int fromFile, int fromRank, int toFile, int toRank) {
        ChessPiece movingPiece = getPieceAt(fromFile, fromRank);
        ChessPiece capturedPiece = getPieceAt(toFile, toRank);

        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && new Point(toFile, toRank).equals(context.getEnPassantTargetSquare())) {
            int capturedPawnFile = toFile;
            int capturedPawnRank = fromRank;
            capturedPiece = getPieceAt(capturedPawnFile, capturedPawnRank);
            board.removePieceAt(capturedPawnFile, capturedPawnRank);
        }

        board.setPieceAt(toFile, toRank, movingPiece);
        board.removePieceAt(fromFile, fromRank);

        return capturedPiece;
    }

    private final Stack<Move> moveHistory = new Stack<>();

    /**
     * Gets the last move made.
     * 
     * @return the last move made
     */
    public Move getLastMove() {
        return moveHistory.isEmpty() ? null : moveHistory.peek();
    }

    /**
     * Undoes a hypothetical move.
     * 
     * @param fromFile      the file of the piece that was moved
     * @param fromRank      the rank of the piece that was moved
     * @param toFile        the file of the square the piece was moved to
     * @param toRank        the rank of the square the piece was moved to
     * @param originalPiece the original piece that was moved
     * @param capturedPiece the piece that was captured
     */
    public void undoHypotheticalMove(int fromFile, int fromRank, int toFile, int toRank,
            ChessPiece originalPiece, ChessPiece capturedPiece) {
        board.setPieceAt(fromFile, fromRank, originalPiece);

        if (originalPiece.getPieceType() == ChessPiece.PieceType.PAWN
                && new Point(toFile, toRank).equals(context.getEnPassantTargetSquare())) {
            board.removePieceAt(toFile, toRank);
            if (capturedPiece != null)
                board.setPieceAt(toFile, fromRank, capturedPiece);
        } else
            board.setPieceAt(toFile, toRank, capturedPiece);

    }

    /**
     * Gets the status of the game.
     * 
     * @return the status of the game
     */
    public GameStatus getGameStatus() {
        return gameRules.getGameStatus();
    }

    /**
     * Checks if the current player is in check.
     * 
     * @return true if the current player is in check, false otherwise
     */
    public boolean isCheck() {
        return context.isWhiteToMove() ? context.isWhiteKingInCheck() : context.isBlackKingInCheck();
    }

    /**
     * Enum representing the status of the game.
     */
    public enum GameStatus {
        /**
         * The game is in progress.
         */
        IN_PROGRESS,
        /**
         * White has won the game.
         */
        CHECKMATE_WHITE_WINS,
        /**
         * Black has won the game.
         */
        CHECKMATE_BLACK_WINS,
        /**
         * The game is a stalemate.
         */
        STALEMATE,
        /**
         * The game is a draw due to insufficient material.
         */
        DRAW_INSUFFICIENT_MATERIAL
    }

    /**
     * Makes a move and returns undo information.
     * This is more efficient than copy() for AI move simulation.
     * 
     * @param fromFile the file of the piece to move
     * @param fromRank the rank of the piece to move
     * @param toFile   the file of the square to move to
     * @param toRank   the rank of the square to move to
     * @return MoveUndo object containing information to reverse the move
     */
    public MoveUndo makeMove(int fromFile, int fromRank, int toFile, int toRank) {
        ChessPiece piece = getPieceAt(fromFile, fromRank);
        ChessPiece capturedPiece = getPieceAt(toFile, toRank);
        boolean pieceHadMoved = piece.hasMoved();

        // Save context state
        Point previousEnPassantTarget = context.getEnPassantTargetSquare();
        boolean previousWhiteKingInCheck = context.isWhiteKingInCheck();
        boolean previousBlackKingInCheck = context.isBlackKingInCheck();
        boolean wasWhiteToMove = context.isWhiteToMove();

        // Check for special moves
        boolean isEnPassantCapture = false;
        boolean isCastling = false;
        ChessPiece castledRook = null;
        int rookFromFile = -1, rookToFile = -1;

        // Handle en passant capture
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && new Point(toFile, toRank).equals(previousEnPassantTarget)) {
            isEnPassantCapture = true;
            int capturedPawnFile = toFile;
            int capturedPawnRank = fromRank;
            capturedPiece = getPieceAt(capturedPawnFile, capturedPawnRank);
            board.removePieceAt(capturedPawnFile, capturedPawnRank);
        }

        // Make the move
        board.setPieceAt(toFile, toRank, piece);
        board.removePieceAt(fromFile, fromRank);
        piece.markAsMoved();

        // Update king position cache if king moved
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.isWhite()) {
                whiteKingPosition = new Point(toFile, toRank);
            } else {
                blackKingPosition = new Point(toFile, toRank);
            }

            // Handle castling
            if (Math.abs(toFile - fromFile) == 2) {
                isCastling = true;
                if (toFile > fromFile) {
                    rookFromFile = 7;
                    rookToFile = 5;
                } else {
                    rookFromFile = 0;
                    rookToFile = 3;
                }
                castledRook = getPieceAt(rookFromFile, fromRank);
                board.removePieceAt(rookFromFile, fromRank);
                board.setPieceAt(rookToFile, fromRank, castledRook);
                castledRook.markAsMoved();
            }
        }

        // Update en passant target
        context.setEnPassantTargetSquare(null);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && Math.abs(fromRank - toRank) == 2)
            context.setEnPassantTargetSquare(new Point(toFile, (fromRank + toRank) / 2));

        // Toggle turn
        context.toggleTurn();

        // Create and return undo information
        return new MoveUndo(fromFile, fromRank, toFile, toRank, piece, capturedPiece, pieceHadMoved,
                previousEnPassantTarget, previousWhiteKingInCheck, previousBlackKingInCheck, wasWhiteToMove,
                isEnPassantCapture, isCastling, castledRook, rookFromFile, rookToFile);
    }

    /**
     * Undoes a move using the provided undo information.
     * 
     * @param undo the MoveUndo object containing information to reverse the move
     */
    public void unmakeMove(MoveUndo undo) {
        ChessPiece piece = undo.movedPiece;

        // Restore piece position
        board.setPieceAt(undo.fromFile, undo.fromRank, piece);
        board.setPieceAt(undo.toFile, undo.toRank, undo.capturedPiece);

        // Restore piece moved status
        piece.setHasMoved(undo.pieceHadMoved);

        // Handle en passant undo
        if (undo.wasEnPassantCapture) {
            board.removePieceAt(undo.toFile, undo.toRank);
            board.setPieceAt(undo.toFile, undo.fromRank, undo.capturedPiece);
        }

        // Handle castling undo
        if (undo.wasCastling && undo.castledRook != null) {
            board.removePieceAt(undo.rookToFile, undo.fromRank);
            board.setPieceAt(undo.rookFromFile, undo.fromRank, undo.castledRook);
            // Restore rook's moved status (it was moved during castling)
            undo.castledRook.setHasMoved(false);
        }

        // Restore king position cache
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.isWhite()) {
                whiteKingPosition = new Point(undo.fromFile, undo.fromRank);
            } else {
                blackKingPosition = new Point(undo.fromFile, undo.fromRank);
            }
        }

        // Restore context state
        context.setEnPassantTargetSquare(undo.previousEnPassantTarget);
        context.setWhiteKingInCheck(undo.previousWhiteKingInCheck);
        context.setBlackKingInCheck(undo.previousBlackKingInCheck);
        context.setWhiteToMove(undo.wasWhiteToMove);
    }

    /**
     * Creates a deep copy of the game state.
     * 
     * @return a new GameState with the copied state
     */
    public GameState copy() {
        GameState copy = new GameState(board.copy(), context.copy(), moveHistory);
        copy.whiteKingPosition = this.whiteKingPosition != null ? new Point(this.whiteKingPosition) : null;
        copy.blackKingPosition = this.blackKingPosition != null ? new Point(this.blackKingPosition) : null;
        return copy;
    }
}
