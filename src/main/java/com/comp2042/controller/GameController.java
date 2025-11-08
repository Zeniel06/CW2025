package com.comp2042.controller;

import com.comp2042.data.ClearRow;
import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;
import com.comp2042.event.EventSource;
import com.comp2042.event.InputEventListener;
import com.comp2042.event.MoveEvent;
import com.comp2042.model.Board;
import com.comp2042.model.SimpleBoard;
import com.comp2042.ui.GuiController;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
        // Don't initialize game yet - wait for user to select a mode
    }
    
    // Initialize the game when user selects a mode
    public void initializeGame() {
        board.createNewBrick();
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
        viewGuiController.bindLines(board.getScore().linesProperty());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            clearRow = lockBrickAndHandleClearing();
            if (clearRow == null) {
                // Game over occurred
                return new DownData(null, board.getViewData());
            }
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    @Override
    public DownData onHardDropEvent(MoveEvent event) {
        // Drop the brick instantly to the bottom and award points
        int distanceDropped = board.hardDropBrick();
        board.getScore().add(distanceDropped * 2);
        
        // Lock brick and handle row clearing (same as normal drop)
        ClearRow clearRow = lockBrickAndHandleClearing();
        if (clearRow == null) {
            // Game over occurred
            return new DownData(null, board.getViewData());
        }
        
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        // Handle hold piece action - stores/swaps current piece with held piece
        board.holdCurrentBrick();
        return board.getViewData();
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }

    /**
     * Common logic for locking a brick and handling row clearing.
     * @return ClearRow if successful, null if game over occurred
     */
    private ClearRow lockBrickAndHandleClearing() {
        // Lock the brick in place
        board.mergeBrickToBackground();
        
        // Clear any completed rows
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }
        
        // Check if blocks have reached the top (game over condition)
        if (board.isDangerLineReached()) {
            viewGuiController.gameOver();
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            return null; // Signal game over
        }
        
        // Spawn a new brick
        board.createNewBrick();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        
        return clearRow;
    }
}

