package com.comp2042.model.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of BrickGenerator that randomly selects bricks from all available types.
 * Maintains a queue of upcoming bricks for preview functionality.
 */
public class RandomBrickGenerator implements BrickGenerator {

    private final List<Brick> brickList;

    private final Deque<Brick> nextBricks = new ArrayDeque<>();

    /**
     * Constructs a new RandomBrickGenerator with all seven standard Tetris brick types.
     * Initializes the next bricks queue with five random bricks (current + next 4 preview).
     */
    public RandomBrickGenerator() {
        brickList = new ArrayList<>();
        brickList.add(new IBrick());
        brickList.add(new JBrick());
        brickList.add(new LBrick());
        brickList.add(new OBrick());
        brickList.add(new SBrick());
        brickList.add(new TBrick());
        brickList.add(new ZBrick());
        // Initialize queue with 5 bricks (1 current + 4 for preview)
        for (int i = 0; i < 5; i++) {
            nextBricks.add(getRandomBrick());
        }
    }

    /**
     * Gets the next brick in the sequence and advances the generator.
     * Maintains a queue of 5 bricks (current + 4 preview).
     * 
     * @return the next brick to be used in the game
     */
    @Override
    public Brick getBrick() {
        // Keep queue at 5 bricks (1 current + 4 preview)
        if (nextBricks.size() <= 4) {
            nextBricks.add(getRandomBrick());
        }
        return nextBricks.poll();
    }

    /**
     * Previews the next brick without advancing the generator.
     * 
     * @return the brick that will appear after the current one
     */
    @Override
    public Brick getNextBrick() {
        return nextBricks.peek();
    }

    /**
     * Gets the next 4 bricks for preview without advancing the generator.
     * 
     * @return list of the next 4 upcoming bricks
     */
    public List<Brick> getNext4Bricks() {
        List<Brick> preview = new ArrayList<>();
        int count = 0;
        for (Brick brick : nextBricks) {
            if (count >= 4) break;
            preview.add(brick);
            count++;
        }
        return preview;
    }

    // Returns a random brick from the brick list
    private Brick getRandomBrick() {
        return brickList.get(ThreadLocalRandom.current().nextInt(brickList.size()));
    }
}

