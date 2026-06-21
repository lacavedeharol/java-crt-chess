package com.lacavedeharol.chess.app.game;

import java.awt.Point;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.lacavedeharol.chess.ai.AI;
import com.lacavedeharol.chess.app.components.renderer.*;
import com.lacavedeharol.chess.app.components.renderer.context.TimerMode;
import com.lacavedeharol.chess.app.components.renderer.projection.Vector3;
import com.lacavedeharol.chess.core.ChessPiece;
import com.lacavedeharol.chess.core.state.GameState;

/**
 * The game controller.
 */
public class GameController {

    private final GameState gameState;

    /**
     * The renderer component.
     */
    protected final GameRendererComponent rendererComponent;

    /**
     * The white AI.
     */
    private final AI whiteAI;

    /**
     * The black AI.
     */
    private final AI blackAI;

    /**
     * The game timer.
     */
    private final GameTimer gameTimer;

    /**
     * The dragged piece.
     */
    private ChessPiece draggedPiece;

    /**
     * The legal moves.
     */
    private List<Point> legalMoves;

    /**
     * Constructor.
     * 
     * @param gameState         the game state
     * @param rendererComponent the renderer component
     * @param whiteAI           the white AI
     * @param blackAI           the black AI
     * @param timerMode         the timer mode
     */
    public GameController(GameState gameState, GameRendererComponent rendererComponent, AI whiteAI,
            AI blackAI, TimerMode timerMode) {
        this.gameState = gameState;
        this.rendererComponent = rendererComponent;
        this.whiteAI = whiteAI;
        this.blackAI = blackAI;
        this.gameTimer = new GameTimer(timerMode, this::updateContextMessage, this::handleTimeout);
        initialize();

        boolean playingAsWhite = whiteAI != null && blackAI == null;
        boolean playingAsBlack = blackAI != null && whiteAI == null;
        if (playingAsWhite)
            rendererComponent.setRotationY(0);
        else if (playingAsBlack)
            rendererComponent.setRotationY(Math.PI);
        else
            rendererComponent.setRotationY(0);

        updateContextMessage();
        gameTimer.start(true);
        handleNextTurn();
    }

    private GameInputHandler gameInputHandler;

    /**
     * Initializes the game controller.
     */
    private void initialize() {
        gameInputHandler = new GameInputHandler(this);
        this.rendererComponent.addMouseListener(gameInputHandler);
        this.rendererComponent.addMouseMotionListener(gameInputHandler);
        this.rendererComponent.addMouseWheelListener(gameInputHandler);
        this.rendererComponent.updatePieces(gameState.getPieces());
        this.rendererComponent.setFocusable(true);
        this.rendererComponent.requestFocusInWindow();
        this.rendererComponent.setOnRestart(this::restartGame);
        this.rendererComponent.setOnGameResumed(() -> {
            if (!checkGameStatus()) {
                if (isAITurn())
                    handleNextTurn();
            }
        });
    }

    /**
     * Checks if it's the AI's turn.
     * 
     * @return true if it's the AI's turn, false otherwise
     */
    private boolean isAITurn() {
        return (gameState.isWhiteToMove() && whiteAI != null) || (!gameState.isWhiteToMove() && blackAI != null);
    }

    /**
     * Handles the drag event.
     * 
     * @param square the square
     */
    void onDrag(Point square) {
        if (draggedPiece == null)
            return;

        if (square != null && legalMoves != null && legalMoves.contains(square)) {
            double centerX = square.x + 0.5;
            double zOffset = 0.5 + 0.25 * Math.cos(rendererComponent.getRotationY());
            double centerZ = square.y + zOffset;

            Vector3 snappedPos = new Vector3(centerX, 0.1, centerZ);
            rendererComponent.setDraggedPiece(draggedPiece, snappedPos);
        }
    }

    /**
     * Disposes the game controller.
     */
    public void dispose() {
        if (gameTimer != null)
            gameTimer.stop();
        if (gameInputHandler != null) {
            this.rendererComponent.removeMouseListener(gameInputHandler);
            this.rendererComponent.removeMouseMotionListener(gameInputHandler);
            this.rendererComponent.removeMouseWheelListener(gameInputHandler);
        } /*
           * if (whiteAI != null) // ADD
           * whiteAI.dispose(); // ADDY
           * 
           * if (blackAI != null) // ADD
           * blackAI.dispose();
           */
    }

    /**
     * Restarts the game.
     */
    private void restartGame() {
        gameState.reset();
        gameTimer.stop();
        gameTimer.reset();
        gameTimer.start(true);

        draggedPiece = null;
        legalMoves = null;
        rendererComponent.setDraggedPiece(null, null);
        rendererComponent.setLegalMoves(null);
        rendererComponent.updatePieces(gameState.getPieces());
        rendererComponent.updateCapturedPieces(gameState.getCapturedPieces());
        updateContextMessage();

        rendererComponent.repaint();

        rendererComponent.setRotationY((whiteAI != null && blackAI == null) ? 0 : Math.PI);

        if (!rendererComponent.isMenuOpen() && !rendererComponent.isSettingsOpen())
            handleNextTurn();
    }

    /**
     * Updates the context message.
     */
    private void updateContextMessage() {
        String turn = gameState.isWhiteToMove() ? "white" : "black";
        String time = gameTimer.getFormattedTime(gameState.isWhiteToMove());
        rendererComponent.setContextMessage(turn + " to move with " + time + " left");
    }

    /**
     * Handles the timeout.
     */
    private void handleTimeout() {
        String winner = gameState.isWhiteToMove() ? "black" : "white";
        rendererComponent.showGameOverMenu(winner + " wins on time!");
    }

    /**
     * Handles the square pressed event.
     * 
     * @param square the square
     */
    void onSquarePressed(Point square) {
        if (isAITurn())
            return;
        if (gameState.getGameStatus() != GameState.GameStatus.IN_PROGRESS)
            return;

        if (square == null)
            return;

        ChessPiece piece = gameState.getPieceAt(square.x, square.y);

        if (piece != null && piece.isWhite() == gameState.isWhiteToMove()) {

            legalMoves = gameState.getLegalMovesForPiece(square.x, square.y);
            if (legalMoves.isEmpty())
                return;

            legalMoves.add(square);

            draggedPiece = piece;
            rendererComponent.setLegalMoves(legalMoves);
            SoundManager.getInstance().playSound("menu_click");
        }
    }

    /**
     * Handles the square released event.
     * 
     * @param target the target
     */
    void onSquareReleased(Point target) {
        if (draggedPiece == null)
            return;

        if (target != null && legalMoves.contains(target)) {
            if (target.x != draggedPiece.getFile() || target.y != draggedPiece.getRank()) {

                double centerX = target.x + 0.5;
                double centerZ = target.y + 0.5;
                rendererComponent.setDraggedPiece(draggedPiece, new Vector3(centerX, 0.0, centerZ));
                GameState.MoveResult result = gameState.movePiece(draggedPiece.getFile(), draggedPiece.getRank(),
                        target.x, target.y);

                if (result == GameState.MoveResult.PROMOTION_REQUIRED) {
                    final int endX = target.x;
                    final int endY = target.y;
                    boolean isWhite = draggedPiece.isWhite();

                    rendererComponent.startPromotion(isWhite, (selectedType) -> {
                        gameState.promotePawn(endX, endY, selectedType);
                        finalizeMove(true);
                    });

                    draggedPiece = null;
                    legalMoves = null;
                    rendererComponent.setDraggedPiece(null, null);
                    rendererComponent.setLegalMoves(null);
                    rendererComponent.updatePieces(gameState.getPieces());
                    return;
                }

                if (result == GameState.MoveResult.VALID)
                    finalizeMove(false);

                draggedPiece = null;
                legalMoves = null;
                rendererComponent.setDraggedPiece(null, null);
                rendererComponent.setLegalMoves(null);
                rendererComponent.updatePieces(gameState.getPieces());

                return;
            }
        }

        SoundManager.getInstance().playSound("wrong_move");

        draggedPiece = null;
        legalMoves = null;
        rendererComponent.setDraggedPiece(null, null);
        rendererComponent.setLegalMoves(null);
        rendererComponent.updatePieces(gameState.getPieces());
    }

    /**
     * Finalizes the move.
     * 
     * @param isPromotion true if the move is a promotion, false otherwise
     */
    private void finalizeMove(boolean isPromotion) {
        if (gameState.isCheck())
            SoundManager.getInstance().playSound("check_move");
        else
            playMoveSound();

        rendererComponent.updateCapturedPieces(gameState.getCapturedPieces());
        gameTimer.switchTurn();

        if (!checkGameStatus())
            handleNextTurn();
    }

    /**
     * Handles the next turn.
     */
    private void handleNextTurn() {
        AI currentAI = gameState.isWhiteToMove() ? whiteAI : blackAI;
        updateContextMessage();

        if (currentAI != null) {
            if (rendererComponent.isMenuOpen() || rendererComponent.isSettingsOpen())
                return;

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    return currentAI.makeMove(gameState);
                }

                @Override
                protected void done() {
                    try {
                        if (!get())
                            return;
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    if (gameState.isCheck())
                        SoundManager.getInstance().playSound("check_move");
                    else
                        playMoveSound();
                    rendererComponent.updatePieces(gameState.getPieces());
                    gameTimer.switchTurn();
                    if (!checkGameStatus())
                        handleNextTurn();
                }
            }.execute();
        }
    }

    /**
     * Checks the game status.
     * 
     * @return true if the game is in progress, false otherwise
     */
    private boolean checkGameStatus() {
        GameState.GameStatus status = gameState.getGameStatus();
        if (status == GameState.GameStatus.IN_PROGRESS)
            return false;
        gameTimer.stop();
        String message;
        switch (status) {
            case CHECKMATE_WHITE_WINS ->
                message = "checkmate, white wins";
            case CHECKMATE_BLACK_WINS ->
                message = "checkmate, black wins";
            case STALEMATE ->
                message = "stalemate";
            case DRAW_INSUFFICIENT_MATERIAL ->
                message = "draw, insufficient material";
            default ->
                throw new IllegalStateException("Unexpected game status: " + status);
        }
        rendererComponent.showGameOverMenu(message);
        return true;
    }

    /**
     * Plays a random move sound.
     */
    private void playMoveSound() {
        if (SoundManager.getInstance().isMuted())
            return;

        int index = new Random().nextInt(4) + 1;
        SoundManager.getInstance().playSound("right_move_" + index);
    }

}
