package com.comp2042.model;

import com.comp2042.data.NextShapeInfo;
import com.comp2042.model.bricks.Brick;

/**
 * Manages the rotation states of Tetris bricks.
 * Handles transitioning between different rotation orientations of a brick.
 */
public class BrickRotator {

    private Brick brick;
    private int currentShape = 0;

    /**
     * Gets information about the next rotation state.
     * 
     * @return the next shape information including the shape matrix and position index
     */
    public NextShapeInfo getNextShape() {
        int nextShape = currentShape;
        nextShape = (++nextShape) % brick.getShapeMatrix().size();
        return new NextShapeInfo(brick.getShapeMatrix().get(nextShape), nextShape);
    }

    /**
     * Gets the shape matrix for the current rotation state.
     * 
     * @return the current shape matrix
     */
    public int[][] getCurrentShape() {
        return brick.getShapeMatrix().get(currentShape);
    }

    /**
     * Sets the current rotation state index.
     * 
     * @param currentShape the rotation state index to set
     */
    public void setCurrentShape(int currentShape) {
        this.currentShape = currentShape;
    }

    /**
     * Sets the brick and resets rotation to the initial state.
     * 
     * @param brick the brick to manage
     */
    public void setBrick(Brick brick) {
        this.brick = brick;
        currentShape = 0;
    }

    /**
     * Gets the current brick object.
     * 
     * @return the current brick
     */
    public Brick getBrick() {
        return brick;
    }

}

