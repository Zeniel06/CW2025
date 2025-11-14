package com.comp2042.model.bricks;

/**
 * Interface for generating random Tetris bricks.
 * Manages the sequence of bricks that will appear in the game.
 */
public interface BrickGenerator {

    /**
     * Gets the next brick in the sequence and advances the generator.
     * 
     * @return the next brick to be used in the game
     */
    Brick getBrick();

    /**
     * Previews the next brick without advancing the generator.
     * 
     * @return the brick that will appear after the current one
     */
    Brick getNextBrick();
}

