package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests Score system
// Tests score addition, line clearing, and level progression
class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() {
        score = new Score();
    }

    // SCORE TESTS

    @Test
    void testInitialScore() {
        assertEquals(0, score.scoreProperty().get());
    }

    @Test
    void testAddScore() {
        score.add(100);
        assertEquals(100, score.scoreProperty().get());
        
        score.add(50);
        assertEquals(150, score.scoreProperty().get());
    }

    // LINE CLEARING TESTS

    @Test
    void testInitialLines() {
        assertEquals(0, score.linesProperty().get());
    }

    @Test
    void testAddLines() {
        score.addLines(1);
        assertEquals(1, score.linesProperty().get());
        
        score.addLines(3);
        assertEquals(4, score.linesProperty().get());
    }

    // LEVEL PROGRESSION TESTS

    @Test
    void testInitialLevel() {
        assertEquals(1, score.levelProperty().get());
    }

    @Test
    void testLevelProgression() {
        // Level formula: (lines / 3) + 1
        
        score.addLines(2); // 2 lines
        assertEquals(1, score.levelProperty().get()); // (2/3) + 1 = 1
        
        score.addLines(1); // 3 lines total
        assertEquals(2, score.levelProperty().get()); // (3/3) + 1 = 2
        
        score.addLines(3); // 6 lines total
        assertEquals(3, score.levelProperty().get()); // (6/3) + 1 = 3
    }

    @Test
    void testLevelFormulaWithTetris() {
        // TETRIS (4 lines at once)
        score.addLines(4);
        assertEquals(2, score.levelProperty().get()); // (4/3) + 1 = 2
    }

    // RESET TEST

    @Test
    void testReset() {
        score.add(1000);
        score.addLines(10);
        
        score.reset();
        
        // All values reset
        assertEquals(0, score.scoreProperty().get());
        assertEquals(0, score.linesProperty().get());
        assertEquals(1, score.levelProperty().get());
    }

    // GAME SCENARIO TEST

    @Test
    void testRealisticGameScenario() {
        // Player clears 1 line
        score.add(50);
        score.addLines(1);
        
        assertEquals(50, score.scoreProperty().get());
        assertEquals(1, score.linesProperty().get());
        assertEquals(1, score.levelProperty().get());
        
        // Player gets TETRIS (4 lines)
        score.add(800);
        score.addLines(4);
        
        assertEquals(850, score.scoreProperty().get());
        assertEquals(5, score.linesProperty().get());
        assertEquals(2, score.levelProperty().get()); // (5/3) + 1 = 2
    }

    @Test
    void testLargeScore() {
        score.add(999999);
        assertEquals(999999, score.scoreProperty().get());
    }

    @Test
    void testMultipleLevels() {
        score.addLines(12); // Should reach level 5
        assertEquals(5, score.levelProperty().get());
    }

    @Test
    void testZeroAddition() {
        score.add(0);
        assertEquals(0, score.scoreProperty().get());
    }
}
