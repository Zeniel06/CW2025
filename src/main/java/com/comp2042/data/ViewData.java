package com.comp2042.data;

import com.comp2042.util.MatrixOperations;

/**
 * Data class encapsulating all information needed to render the game view.
 * Contains current brick data, position, ghost position, next brick preview, and held brick data.
 */
public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final int ghostYPosition;  // Y position where the brick will land
    private final int[][] heldBrickData;  // Hold piece feature

    /**
     * Constructs a new ViewData object with all view-related information.
     * 
     * @param brickData the shape matrix of the current falling brick
     * @param xPosition the x-coordinate of the current brick on the board
     * @param yPosition the y-coordinate of the current brick on the board
     * @param nextBrickData the shape matrix of the next brick to appear
     * @param ghostYPosition the y-coordinate where the current brick would land if dropped
     * @param heldBrickData the shape matrix of the held brick, or null if no brick is held
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int ghostYPosition, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostYPosition = ghostYPosition;
        this.heldBrickData = heldBrickData;
    }

    /**
     * Gets a copy of the current brick's shape matrix.
     * 
     * @return a copy of the brick data matrix
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Gets the x-coordinate of the current brick.
     * 
     * @return the x-position on the board
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Gets the y-coordinate of the current brick.
     * 
     * @return the y-position on the board
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Gets a copy of the next brick's shape matrix.
     * 
     * @return a copy of the next brick data matrix
     */
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    /**
     * Gets the y-coordinate where the current brick would land if dropped.
     * 
     * @return the ghost y-position
     */
    public int getGhostYPosition() {
        return ghostYPosition;
    }

    /**
     * Gets a copy of the held brick's shape matrix.
     * 
     * @return a copy of the held brick data, or null if no brick is held
     */
    public int[][] getHeldBrickData() {
        return heldBrickData == null ? null : MatrixOperations.copy(heldBrickData);
    }
}

