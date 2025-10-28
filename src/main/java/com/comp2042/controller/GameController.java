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
        board.createNewBrick();
        viewGuiController.setEventListener(this);
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
            // Lock the brick in place
            board.mergeBrickToBackground();
            
            // Clear any completed rows first
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
                board.getScore().addLines(clearRow.getLinesRemoved());
            }
            
            // Check if blocks have reached the top AFTER clearing (game over condition)
            if (board.isDangerLineReached()) {
                viewGuiController.gameOver();
                viewGuiController.refreshGameBackground(board.getBoardMatrix());
                return new DownData(clearRow, board.getViewData());
            }
            
            // Spawn a new brick
            board.createNewBrick();

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

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
        // Drop the brick instantly to the bottom
        int distanceDropped = board.hardDropBrick();
        // Award 2 points per cell dropped (typical Tetris scoring)
        board.getScore().add(distanceDropped * 2);
        
        // Lock the brick in place
        board.mergeBrickToBackground();
        
        // Clear any completed rows first
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
            board.getScore().addLines(clearRow.getLinesRemoved());
        }
        
        // Check if blocks have reached the top AFTER clearing (game over condition)
        if (board.isDangerLineReached()) {
            viewGuiController.gameOver();
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
            return new DownData(clearRow, board.getViewData());
        }
        
        // Spawn a new brick
        board.createNewBrick();
        
        // Update the game display
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
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
}

