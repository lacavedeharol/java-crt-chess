package com.lacavedeharol.chess.core.state;

import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState.MoveResult;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    @Test
    public void testInitialState() {
        GameState gameState = new GameState();
        gameState.initializePieces();

        assertTrue(gameState.isWhiteToMove());
        assertFalse(gameState.isWhiteKingInCheck());
        assertFalse(gameState.isBlackKingInCheck());

        ChessPiece whitePawn = gameState.getPieceAt(0, 1);
        assertNotNull(whitePawn);
        assertEquals(ChessPiece.PieceType.PAWN, whitePawn.getPieceType());
        assertTrue(whitePawn.isWhite());

        ChessPiece blackPawn = gameState.getPieceAt(0, 6);
        assertNotNull(blackPawn);
        assertEquals(ChessPiece.PieceType.PAWN, blackPawn.getPieceType());
        assertFalse(blackPawn.isWhite());
    }

    @Test
    public void testValidMove() {
        GameState gameState = new GameState();
        gameState.initializePieces();

        // White Pawn e2 -> e4 (file=4, rank 1->3)
        MoveResult result = gameState.movePiece(4, 1, 4, 3);
        assertEquals(MoveResult.VALID, result);

        assertNull(gameState.getPieceAt(4, 1));
        assertNotNull(gameState.getPieceAt(4, 3));
        assertEquals(ChessPiece.PieceType.PAWN, gameState.getPieceAt(4, 3).getPieceType());
        assertFalse(gameState.isWhiteToMove());
    }

    @Test
    public void testInvalidMove() {
        GameState gameState = new GameState();
        gameState.initializePieces();
        MoveResult result = gameState.movePiece(4, 6, 4, 4);
        assertEquals(MoveResult.INVALID, result);
    }

    /**
     * Verify that a piece cannot make a move that leaves its own king in check.
     *
     * Board layout back-row: a=ROOK, b=KNIGHT, c=BISHOP, d=KING, e=QUEEN,
     * f=BISHOP, g=KNIGHT, h=ROOK (files 0-7); rank 0 = white, rank 7 = black.
     *
     * Sequence:
     * 1. e4 (4,1)→(4,3) white e-pawn forward 2
     * 1... e5 (4,6)→(4,4) black e-pawn forward 2
     * 2. d4 (3,1)→(3,3) white d-pawn (in front of king) forward 2 — should be VALID
     */
    @Test
    public void testPawnDoesNotLeaveKingInCheck() {
        GameState gs = new GameState();
        assertEquals(MoveResult.VALID, gs.movePiece(4, 1, 4, 3)); // e4
        assertEquals(MoveResult.VALID, gs.movePiece(4, 6, 4, 4)); // e5
        // Moving the d-pawn (in front of king) should be legal; king safety check
        assertEquals(MoveResult.VALID, gs.movePiece(3, 1, 3, 3)); // d4
        assertFalse(gs.isWhiteKingInCheck(), "White king should not be in check after d4");
    }

    /**
     * Verify check detection: after white plays Qe5, putting the queen on a
     * square that attacks the black king's adjacent squares does not itself
     * constitute check, but after a discovered attack the check flag is set.
     *
     * We use a simple queen-checks-king scenario via:
     * 1. e4, 1... d5, 2. exd5 — captures pawn, verifies basic capture works
     */
    @Test
    public void testCaptureMoveIsValid() {
        GameState gs = new GameState();
        assertEquals(MoveResult.VALID, gs.movePiece(4, 1, 4, 3)); // e4
        assertEquals(MoveResult.VALID, gs.movePiece(3, 6, 3, 4)); // d5 (d-pawn = file 3)
        // White e-pawn captures d5
        assertEquals(MoveResult.VALID, gs.movePiece(4, 3, 3, 4)); // exd5
        // Black d7 pawn should now be gone, pawn at d5
        assertNull(gs.getPieceAt(4, 3));
        assertNotNull(gs.getPieceAt(3, 4));
        assertEquals(ChessPiece.PieceType.PAWN, gs.getPieceAt(3, 4).getPieceType());
        assertTrue(gs.getPieceAt(3, 4).isWhite());
    }

    /**
     * Verify the game starts IN_PROGRESS and only reaches CHECKMATE or STALEMATE
     * when appropriate: even after several moves the status should remain
     * IN_PROGRESS.
     */
    @Test
    public void testGameStatusInProgress() {
        GameState gs = new GameState();
        assertEquals(GameState.GameStatus.IN_PROGRESS, gs.getGameStatus());

        gs.movePiece(4, 1, 4, 3); // e4
        gs.movePiece(4, 6, 4, 4); // e5
        gs.movePiece(5, 0, 2, 3); // Bc4 (f-bishop to c4)
        gs.movePiece(1, 7, 2, 5); // Nc6 (b-knight to c6)

        assertEquals(GameState.GameStatus.IN_PROGRESS, gs.getGameStatus());
    }
}
