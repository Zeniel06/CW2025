package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.RandomBrickGenerator;
import com.comp2042.util.MatrixOperations;

import java.awt.*;

/**
 * Implementation of the Board interface representing the Tetris game board.
 * Manages the game state, brick movements, collision detection, and game mechanics.
 */
public class SimpleBoard implements Board {

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;
    
    // Hold piece feature - stores the brick being held
    private Brick heldBrick;
    // Prevents holding/swapping multiple times per piece (standard Tetris rule)
    private boolean canHold;

    /**
     * Constructs a new SimpleBoard with the specified dimensions.
     * 
     * @param width the number of rows on the board
     * @param height the number of columns on the board
     */
    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    /**
     * Attempts to move the current brick down one row.
     * 
     * @return true if the movement was successful, false if the brick cannot move down
     */
    @Override
    public boolean moveBrickDown() {
        return tryMoveBrick(0, 1);
    }

    /**
     * Attempts to move the current brick left one column.
     * 
     * @return true if the movement was successful, false if blocked
     */
    @Override
    public boolean moveBrickLeft() {
        return tryMoveBrick(-1, 0);
    }

    /**
     * Attempts to move the current brick right one column.
     * 
     * @return true if the movement was successful, false if blocked
     */
    @Override
    public boolean moveBrickRight() {
        return tryMoveBrick(1, 0);
    }

    // Attempts to move brick by offset (dx, dy), returns true if successful
    private boolean tryMoveBrick(int dx, int dy) {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point newPosition = new Point(currentOffset);
        newPosition.translate(dx, dy);
        
        boolean conflict = MatrixOperations.intersect(
            currentMatrix, 
            brickRotator.getCurrentShape(), 
            (int) newPosition.getX(), 
            (int) newPosition.getY()
        );
        
        if (!conflict) {
            currentOffset = newPosition;
            return true;
        }
        return false;
    }

    /**
     * Attempts to rotate the current brick clockwise.
     * 
     * @return true if the rotation was successful, false if blocked
     */
    @Override
    public boolean rotateLeftBrick() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        NextShapeInfo nextShape = brickRotator.getNextShape();
        boolean conflict = MatrixOperations.intersect(currentMatrix, nextShape.getShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
        if (conflict) {
            return false;
        } else {
            brickRotator.setCurrentShape(nextShape.getPosition());
            return true;
        }
    }

    /**
     * Instantly drops the brick to the bottom position.
     * 
     * @return the number of rows the brick dropped
     */
    @Override
    public int hardDropBrick() {
        // Store the starting position
        int initialY = (int) currentOffset.getY();
        // Calculate where the brick will land (same as ghost brick position)
        int ghostY = calculateGhostPosition();
        // Instantly move the brick to the landing position
        currentOffset.setLocation(currentOffset.getX(), ghostY);
        // Return the distance dropped for scoring purposes
        return ghostY - initialY;
    }

    /**
     * Creates and spawns a new brick at the top of the board.
     * 
     * @return true if successful, false otherwise
     */
    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        // Spawn above the visible area (y=0 is hidden, top of visible area is y=2)
        currentOffset = new Point(4, 0);
        // Reset hold ability for the new piece (one hold/swap allowed per piece)
        canHold = true;
        return false; // Return value maintained for interface compatibility
    }

    /**
     * Gets the current state of the game board matrix.
     * 
     * @return the board matrix
     */
    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    /**
     * Gets the current view data for rendering the game state.
     * 
     * @return the view data containing brick positions and states
     */
    @Override
    public ViewData getViewData() {
        int ghostY = calculateGhostPosition();  // Calculate where the brick will land for ghost preview
        // Include held brick shape in view data so UI can display it
        return new ViewData(brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY(), brickGenerator.getNextBrick().getShapeMatrix().get(0), ghostY, getHeldBrickShape());
    }

    // Calculates the Y position where the current brick would land if dropped straight down
    private int calculateGhostPosition() {
        int ghostY = (int) currentOffset.getY();
        int ghostX = (int) currentOffset.getX();
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        int[][] currentShape = brickRotator.getCurrentShape();
        
        // Keep incrementing Y until the NEXT position would collide
        // This matches exactly how moveBrickDown works
        while (!MatrixOperations.intersect(currentMatrix, currentShape, ghostX, ghostY + 1)) {
            ghostY++;
        }
        
        return ghostY;
    }

    /**
     * Merges the current falling brick into the background board matrix.
     */
    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    /**
     * Clears all completed rows and returns information about the clearing operation.
     * 
     * @return the clear row data containing lines removed and score bonus
     */
    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    /**
     * Gets the score tracking object for the current game.
     * 
     * @return the score object
     */
    @Override
    public Score getScore() {
        return score;
    }


    /**
     * Resets the board and starts a new game.
     */
    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        // Reset hold piece state for new game
        heldBrick = null;
        canHold = true;
        createNewBrick();
    }

    /**
     * Checks if blocks have reached the danger line at the top of the play area.
     * 
     * @return true if game over condition is met, false otherwise
     */
    @Override
    public boolean isDangerLineReached() {
        // Check if any blocks exist at row 2 (top of visible play area)
        // Game over if blocks reach the top
        for (int col = 0; col < height; col++) {
            if (currentGameMatrix[2][col] != 0) {
                return true;  // Game over - blocks have reached the top
            }
        }
        return false;
    }

    /**
     * Stores or swaps the current brick with the held brick.
     * 
     * @return true if hold was successful, false if already used for this brick
     */
    @Override
    public boolean holdCurrentBrick() {
        // Check if player already used hold/swap for this piece
        if (!canHold) {
            return false; // Already held/swapped - must wait until piece locks
        }
        
        // Mark hold as used before performing operation
        canHold = false;
        Brick currentBrick = brickRotator.getBrick();
        
        if (heldBrick == null) {
            // First time holding - store current piece and spawn a new random piece
            heldBrick = currentBrick;
            createNewBrick(); // This will set canHold = true for the new piece
        } else {
            // Swap current piece with the held piece
            Brick temp = heldBrick;
            heldBrick = currentBrick;
            brickRotator.setBrick(temp);
            currentOffset = new Point(4, 0); // Reset position to spawn location
            // canHold stays false - can't swap again until this piece locks in place
        }
        
        return true;
    }

    /**
     * Gets the shape matrix of the currently held brick.
     * 
     * @return the held brick shape, or null if no brick is held
     */
    @Override
    public int[][] getHeldBrickShape() {
        // Return null if no piece is held, otherwise return its shape matrix
        if (heldBrick == null) {
            return null;
        }
        return heldBrick.getShapeMatrix().get(0);
    }
}

