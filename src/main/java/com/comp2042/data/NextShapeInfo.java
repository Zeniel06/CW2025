package com.comp2042.data;

import com.comp2042.util.MatrixOperations;

/**
 * Data class representing information about the next rotation state of a brick.
 * Contains the shape matrix and the rotation position index.
 */
public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;

    /**
     * Constructs a new NextShapeInfo object with the specified shape and position.
     * 
     * @param shape the shape matrix representing the next rotation state
     * @param position the index of the rotation state in the brick's rotation list
     */
    public NextShapeInfo(final int[][] shape, final int position) {
        this.shape = shape;
        this.position = position;
    }

    /**
     * Gets a copy of the shape matrix for the next rotation state.
     * 
     * @return a copy of the shape matrix
     */
    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    /**
     * Gets the rotation position index.
     * 
     * @return the position index in the brick's rotation list
     */
    public int getPosition() {
        return position;
    }
}

