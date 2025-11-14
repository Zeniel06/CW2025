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

/**
 * Main game controller that manages the Tetris game logic and coordinates between the model and view.
 * Implements event handling for player inputs and manages game state transitions.
 */
public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    /**
     * Constructs a new GameController with the specified GUI controller.
     * 
     * @param c the GUI controller for managing the view layer
     */
    public GameController(GuiController c) {
        viewGuiController = c;
        viewGuiController.setEventListener(this);
        // Don't initialize game yet - wait for user to select a mode
    }
    
    /**
     * Initializes the game by creating the first brick and setting up the view bindings.
     */
    public void initializeGame() {
        board.createNewBrick();
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.bindLevel(board.getScore().levelProperty());
        viewGuiController.bindLines(board.getScore().linesProperty());
    }

    /**
     * Handles the down movement event for the falling brick.
     * 
     * @param event the move event containing the source of the movement
     * @return the down data containing cleared row information and updated view data
     */
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

    /**
     * Handles the left movement event for the falling brick.
     * 
     * @param event the move event
     * @return the updated view data after the movement
     */
    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    /**
     * Handles the right movement event for the falling brick.
     * 
     * @param event the move event
     * @return the updated view data after the movement
     */
    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    /**
     * Handles the rotation event for the falling brick.
     * 
     * @param event the move event
     * @return the updated view data after the rotation
     */
    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }

    /**
     * Handles the hard drop event, instantly dropping the brick to the bottom position.
     * 
     * @param event the move event
     * @return the down data containing cleared row information and updated view data
     */
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

    /**
     * Handles the hold event, storing or swapping the current brick with the held brick.
     * 
     * @param event the move event
     * @return the updated view data after the hold operation
     */
    @Override
    public ViewData onHoldEvent(MoveEvent event) {
        // Handle hold piece action - stores/swaps current piece with held piece
        board.holdCurrentBrick();
        return board.getViewData();
    }

    /**
     * Creates a new game by resetting the board and refreshing the view.
     */
    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.refreshBrick(board.getViewData());
    }

    // Locks brick and handles row clearing, returns null if game over
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

