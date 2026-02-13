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

        // White Pawn e2 -> e4
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
}
