package com.lacavedeharol.chess.computer.uci;

import java.awt.Point;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;

/**
 * Builds a FEN (Forsyth-Edwards Notation) string from a {@link GameState}.
 *
 * <p>
 * Coordinate convention (matches the engine's internal board):
 * file 0..7 = a..h, rank 0..7 = 1..8. White's back rank is 0, black's is 7.
 * After the standard king/queen layout fix, queen sits on file 3 (d) and king
 * on file 4 (e), so this produces legal standard-chess FEN with no mirroring.
 * </p>
 */
final class FenBuilder {

    private FenBuilder() {
    }

    /**
     * Builds a FEN string for the given game state.
     *
     * @param state the game state.
     * @return the FEN string.
     */
    static String build(GameState state) {
        StringBuilder fen = new StringBuilder(80);

        appendPiecePlacement(fen, state);
        fen.append(' ');
        fen.append(state.isWhiteToMove() ? 'w' : 'b');
        fen.append(' ');
        appendCastling(fen, state);
        fen.append(' ');
        appendEnPassant(fen, state);

        /*
         * Halfmove clock and fullmove number: GameState does not track these.
         * Stockfish does not need accurate counters to choose a move, so we emit
         * neutral defaults. (The fullmove number must be >= 1.).
         */
        fen.append(" 0 1");

        return fen.toString();
    }

    /**
     * Appends the piece-placement field. FEN ranks go from 8 down to 1, i.e.
     * from internal rank 7 down to rank 0, each scanned file a..h (0..7).
     */
    private static void appendPiecePlacement(StringBuilder fen, GameState state) {
        for (int rank = 7; rank >= 0; rank--) {
            int empty = 0;
            for (int file = 0; file < 8; file++) {
                ChessPiece piece = state.getPieceAt(file, rank);
                if (piece == null) {
                    empty++;
                    continue;
                }
                if (empty > 0) {
                    fen.append(empty);
                    empty = 0;
                }
                fen.append(toFenChar(piece));
            }
            if (empty > 0)
                fen.append(empty);
            if (rank > 0)
                fen.append('/');
        }
    }

    /**
     * Maps a piece to its FEN letter (uppercase = white, lowercase = black).
     */
    private static char toFenChar(ChessPiece piece) {
        char c;
        switch (piece.getPieceType()) {
            case PAWN -> c = 'p';
            case KNIGHT -> c = 'n';
            case BISHOP -> c = 'b';
            case ROOK -> c = 'r';
            case QUEEN -> c = 'q';
            case KING -> c = 'k';
            default -> throw new IllegalStateException("Unknown piece type: " + piece.getPieceType());
        }
        return piece.isWhite() ? Character.toUpperCase(c) : c;
    }

    /**
     * Appends castling availability, derived from whether kings and rooks have
     * moved. Standard layout: king on file 4, king-side rook on file 7,
     * queen-side rook on file 0. White rank 0, black rank 7.
     */
    private static void appendCastling(StringBuilder fen, GameState state) {
        StringBuilder rights = new StringBuilder(4);

        // White (rank 0)
        if (canCastle(state, 4, 0, 7, 0))
            rights.append('K');
        if (canCastle(state, 4, 0, 0, 0))
            rights.append('Q');

        // Black (rank 7)
        if (canCastle(state, 4, 7, 7, 7))
            rights.append('k');
        if (canCastle(state, 4, 7, 0, 7))
            rights.append('q');

        fen.append(rights.length() == 0 ? "-" : rights.toString());
    }

    /**
     * A side retains a castling right if its king and the relevant rook are both
     * present, unmoved, and of the correct type/colour.
     */
    private static boolean canCastle(GameState state, int kingFile, int kingRank, int rookFile, int rookRank) {
        ChessPiece king = state.getPieceAt(kingFile, kingRank);
        if (king == null || king.getPieceType() != ChessPiece.PieceType.KING || king.hasMoved())
            return false;

        ChessPiece rook = state.getPieceAt(rookFile, rookRank);
        if (rook == null || rook.getPieceType() != ChessPiece.PieceType.ROOK || rook.hasMoved())
            return false;

        return king.isWhite() == rook.isWhite();
    }

    /**
     * Appends the en-passant target square, or "-" if none.
     */
    private static void appendEnPassant(StringBuilder fen, GameState state) {
        Point ep = state.getEnPassantTargetSquare();
        if (ep == null) {
            fen.append('-');
            return;
        }
        fen.append(toSquare(ep.x, ep.y));
    }

    /**
     * Converts internal (file, rank) to algebraic notation.
     */
    static String toSquare(int file, int rank) {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }
}
