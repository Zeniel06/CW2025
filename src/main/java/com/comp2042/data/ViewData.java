package com.comp2042.data;

import com.comp2042.util.MatrixOperations;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int[][] nextBrickData;
    private final int ghostYPosition;  // Y position where the brick will land (for ghost preview)
    private final int[][] heldBrickData;  // Hold piece feature - the held brick shape (null if no brick is held)

    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData, int ghostYPosition, int[][] heldBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
        this.ghostYPosition = ghostYPosition;
        this.heldBrickData = heldBrickData;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    // Returns a copy of the held brick data for UI display (null if no brick is held)
    public int[][] getHeldBrickData() {
        return heldBrickData == null ? null : MatrixOperations.copy(heldBrickData);
    }
}

