package com.comp2042.model.bricks;

import java.util.List;

/**
 * Interface representing a Tetris brick with its rotation states.
 * Each brick can have multiple rotation orientations represented as shape matrices.
 */
public interface Brick {

    /**
     * Gets the list of shape matrices representing all rotation states of the brick.
     * 
     * @return a list of 2D integer arrays, each representing a rotation state
     */
    List<int[][]> getShapeMatrix();
}

