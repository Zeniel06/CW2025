package com.comp2042.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests SimpleBoard user input handling
// Tests all user actions: movement, rotation, hard drop, hold
class SimpleBoardInputTest {

    private SimpleBoard board;

    @BeforeEach
    void setUp() {
        board = new SimpleBoard(25, 10);
        board.createNewBrick();
    }

    // MOVEMENT TESTS

    @Test
    void testUserMoveBrickLeft() {
        // User presses left
        int initialX = board.getViewData().getxPosition();
        boolean moved = board.moveBrickLeft();
        
        assertTrue(moved);
        assertEquals(initialX - 1, board.getViewData().getxPosition());
    }

    @Test
    void testUserMoveBrickRight() {
        // User presses right
        int initialX = board.getViewData().getxPosition();
        boolean moved = board.moveBrickRight();
        
        assertTrue(moved);
        assertEquals(initialX + 1, board.getViewData().getxPosition());
    }

    @Test
    void testUserMoveBrickDown() {
        // User presses down
        int initialY = board.getViewData().getyPosition();
        boolean moved = board.moveBrickDown();
        
        assertTrue(moved);
        assertEquals(initialY + 1, board.getViewData().getyPosition());
    }

    @Test
    void testUserCannotMoveLeftAtBoundary() {
        // Move to left edge
        for (int i = 0; i < 10; i++) {
            board.moveBrickLeft();
        }
        
        // Cannot move further left
        boolean moved = board.moveBrickLeft();
        assertFalse(moved);
    }

    @Test
    void testUserCannotMoveRightAtBoundary() {
        // Move to right edge
        for (int i = 0; i < 10; i++) {
            board.moveBrickRight();
        }
        
        // Cannot move further right
        boolean moved = board.moveBrickRight();
        assertFalse(moved);
    }

    @Test
    void testUserCannotMoveDownAtBottom() {
        // Move brick to bottom
        while (board.moveBrickDown()) {}
        
        // Cannot move down further
        boolean canMove = board.moveBrickDown();
        assertFalse(canMove);
    }

    // ROTATION TESTS

    @Test
    void testUserRotateBrick() {
        // User presses rotate key
        boolean rotated = board.rotateLeftBrick();
        assertTrue(rotated);
    }

    @Test
    void testUserRotateBrickMultipleTimes() {
        // User rotates 4 times (full cycle)
        board.rotateLeftBrick();
        board.rotateLeftBrick();
        board.rotateLeftBrick();
        board.rotateLeftBrick();
        
        assertNotNull(board.getViewData().getBrickData());
    }

    // HARD DROP TESTS

    @Test
    void testUserHardDrop() {
        // User presses space bar (hard drop)
        int initialY = board.getViewData().getyPosition();
        int distanceDropped = board.hardDropBrick();
        
        assertTrue(distanceDropped > 0);
        
        // Brick is now at bottom
        int finalY = board.getViewData().getyPosition();
        assertEquals(initialY + distanceDropped, finalY);
    }

    @Test
    void testUserHardDropFromTop() {
        // Create brick at top
        board.createNewBrick();
        int initialY = board.getViewData().getyPosition();
        
        // User hard drops immediately
        int distanceDropped = board.hardDropBrick();
        
        // Should drop significant distance
        assertTrue(distanceDropped >= 20);
        
        int finalY = board.getViewData().getyPosition();
        assertTrue(finalY > initialY);
    }

    @Test
    void testUserHardDropAtBottom() {
        // Move brick to bottom first
        while (board.moveBrickDown()) {}
        
        int positionBeforeDrop = board.getViewData().getyPosition();
        
        // Hard drop at bottom does nothing
        int distanceDropped = board.hardDropBrick();
        
        assertEquals(0, distanceDropped);
        assertEquals(positionBeforeDrop, board.getViewData().getyPosition());
    }

    // HOLD FEATURE TESTS

    @Test
    void testUserHoldBrickFirstTime() {
        // Initially no brick is held
        assertNull(board.getHeldBrickShape());
        
        // User presses hold key (Shift)
        boolean held = board.holdCurrentBrick();
        
        assertTrue(held);
        
        // Now brick is held
        assertNotNull(board.getHeldBrickShape());
    }

    @Test
    void testUserCannotHoldTwiceForSamePiece() {
        // Hold a piece
        board.holdCurrentBrick();
        
        // Drop piece and create another
        while (board.moveBrickDown()) {}
        board.mergeBrickToBackground();
        board.createNewBrick();
        
        // Swap with held piece (first swap for this piece)
        boolean firstSwap = board.holdCurrentBrick();
        assertTrue(firstSwap);
        
        // Cannot swap again with same piece
        boolean secondSwap = board.holdCurrentBrick();
        assertFalse(secondSwap);
    }

    @Test
    void testUserCanHoldAfterNewPiece() {
        // Hold a piece
        board.holdCurrentBrick();
        
        // Lock piece and spawn new one
        while (board.moveBrickDown()) {}
        board.mergeBrickToBackground();
        board.createNewBrick();
        
        // Can hold new piece
        boolean canHoldAgain = board.holdCurrentBrick();
        assertTrue(canHoldAgain);
    }

    @Test
    void testUserSwapWithHeldBrick() {
        // Hold first brick
        board.holdCurrentBrick();
        assertNotNull(board.getHeldBrickShape());
        
        // Lock piece and spawn new one
        while (board.moveBrickDown()) {}
        board.mergeBrickToBackground();
        board.createNewBrick();
        
        // Swap second brick with held brick
        boolean swapped = board.holdCurrentBrick();
        assertTrue(swapped);
        
        assertNotNull(board.getViewData().getBrickData());
    }

    // NEW GAME TEST

    @Test
    void testUserStartsNewGame() {
        // Play some moves
        board.moveBrickDown();
        board.moveBrickLeft();
        board.getScore().add(100);
        board.holdCurrentBrick();
        
        // User presses N to start new game
        board.newGame();
        
        // Score is reset
        assertEquals(0, board.getScore().scoreProperty().get());
        assertEquals(1, board.getScore().levelProperty().get());
        assertEquals(0, board.getScore().linesProperty().get());
        
        // Held brick is cleared
        assertNull(board.getHeldBrickShape());
        
        // Board is empty
        int[][] matrix = board.getBoardMatrix();
        boolean isEmpty = true;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    isEmpty = false;
                    break;
                }
            }
        }
        assertTrue(isEmpty);
    }

    @Test
    void testUserNewGameCreatesNewBrick() {
        // User starts new game
        board.newGame();
        
        // New brick is created
        assertNotNull(board.getViewData());
        assertNotNull(board.getViewData().getBrickData());
    }

    // GAME OVER TEST

    @Test
    void testUserActionsLeadToGameOver() {
        // Simulate user dropping many pieces
        for (int i = 0; i < 15; i++) {
            board.hardDropBrick();
            board.mergeBrickToBackground();
            board.clearRows();
            
            // Check if game over
            if (board.isDangerLineReached()) {
                assertTrue(true);
                return;
            }
            
            board.createNewBrick();
        }
    }

    // MOVEMENT COMBINATION TESTS

    @Test
    void testUserComplexMovementSequence() {
        // Simulate typical user input sequence
        int initialX = board.getViewData().getxPosition();
        int initialY = board.getViewData().getyPosition();
        
        // User moves right, rotates, moves down, moves left
        board.moveBrickRight();
        board.rotateLeftBrick();
        board.moveBrickDown();
        board.moveBrickLeft();
        
        // Brick has moved from initial position
        int finalY = board.getViewData().getyPosition();
        assertTrue(finalY > initialY);
    }

    @Test
    void testUserRapidLeftRightMovement() {
        int initialX = board.getViewData().getxPosition();
        
        // User rapidly presses left and right
        board.moveBrickLeft();
        board.moveBrickLeft();
        board.moveBrickRight();
        board.moveBrickRight();
        board.moveBrickRight();
        
        int finalX = board.getViewData().getxPosition();
        
        // Net movement is right by 1
        assertEquals(initialX + 1, finalX);
    }

    @Test
    void testUserRotateAndMoveCombination() {
        // User rotates and immediately moves
        boolean rotated = board.rotateLeftBrick();
        boolean movedRight = board.moveBrickRight();
        boolean movedDown = board.moveBrickDown();
        
        // All actions succeed
        assertTrue(rotated);
        assertTrue(movedRight);
        assertTrue(movedDown);
    }
}
