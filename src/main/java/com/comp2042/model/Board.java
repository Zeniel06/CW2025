package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.data.ViewData;

/**
 * Interface defining the core game board functionality for a Tetris game.
 * Manages brick movements, rotations, collision detection, and row clearing.
 */
public interface Board {

    /**
     * Attempts to move the current brick down one row.
     * 
     * @return true if the movement was successful, false if the brick cannot move down
     */
    boolean moveBrickDown();

    /**
     * Attempts to move the current brick left one column.
     * 
     * @return true if the movement was successful, false if blocked
     */
    boolean moveBrickLeft();

    /**
     * Attempts to move the current brick right one column.
     * 
     * @return true if the movement was successful, false if blocked
     */
    boolean moveBrickRight();

    /**
     * Attempts to rotate the current brick clockwise.
     * 
     * @return true if the rotation was successful, false if blocked
     */
    boolean rotateLeftBrick();

    /**
     * Instantly drops the brick to the bottom position.
     * 
     * @return the number of rows the brick dropped
     */
    int hardDropBrick();

    /**
     * Creates and spawns a new brick at the top of the board.
     * 
     * @return true if successful, false otherwise
     */
    boolean createNewBrick();

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return the board matrix
     */
    int[][] getBoardMatrix();

    /**
     * Gets the current view data for rendering the game state.
     * 
     * @return the view data containing brick positions and states
     */
    ViewData getViewData();

    /**
     * Merges the current falling brick into the background board matrix.
     */
    void mergeBrickToBackground();

    /**
     * Clears all completed rows and returns information about the clearing operation.
     * 
     * @return the clear row data containing lines removed and score bonus
     */
    ClearRow clearRows();

    /**
     * Gets the score tracking object for the current game.
     * 
     * @return the score object
     */
    Score getScore();

    /**
     * Resets the board and starts a new game.
     */
    void newGame();

    /**
     * Checks if blocks have reached the danger line at the top of the play area.
     * 
     * @return true if game over condition is met, false otherwise
     */
    boolean isDangerLineReached();
    
    /**
     * Stores or swaps the current brick with the held brick.
     * 
     * @return true if hold was successful, false if already used for this brick
     */
    boolean holdCurrentBrick();
    
    /**
     * Gets the shape matrix of the currently held brick.
     * 
     * @return the held brick shape, or null if no brick is held
     */
    int[][] getHeldBrickShape();
}

