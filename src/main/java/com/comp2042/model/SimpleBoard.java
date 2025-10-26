package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.data.NextShapeInfo;
import com.comp2042.data.ViewData;
import com.comp2042.model.bricks.Brick;
import com.comp2042.model.bricks.BrickGenerator;
import com.comp2042.model.bricks.RandomBrickGenerator;
import com.comp2042.util.MatrixOperations;

import java.awt.*;

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

    public SimpleBoard(int width, int height) {
        this.width = width;
        this.height = height;
        currentGameMatrix = new int[width][height];
        brickGenerator = new RandomBrickGenerator();
        brickRotator = new BrickRotator();
        score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }


    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(), (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

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

    @Override
    public int hardDropBrick() {
        // Store the starting position
        int initialY = (int) currentOffset.getY();
        // Calculate where the brick will land (same as ghost brick position)
        int ghostY = calculateGhostPosition();
        // Instantly move the brick to the landing position
        currentOffset.setLocation(currentOffset.getX(), ghostY);
        // Return the distance dropped for scoring purposes
        int distanceDropped = ghostY - initialY;
        return distanceDropped;
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        // Spawn above the visible area (y=0 is hidden, top of visible area is y=2)
        // This allows blocks to fall into view and potentially reach the top
        currentOffset = new Point(4, 0);
        // Reset hold ability for the new piece (one hold/swap allowed per piece)
        canHold = true;
        // Return false since game over is now checked via isDangerLineReached()
        return false;
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

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

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }


    @Override
    public void newGame() {
        currentGameMatrix = new int[width][height];
        score.reset();
        // Reset hold piece state for new game
        heldBrick = null;
        canHold = true;
        createNewBrick();
    }

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

    @Override
    public int[][] getHeldBrickShape() {
        // Return null if no piece is held, otherwise return its shape matrix
        if (heldBrick == null) {
            return null;
        }
        return heldBrick.getShapeMatrix().get(0);
    }
}

