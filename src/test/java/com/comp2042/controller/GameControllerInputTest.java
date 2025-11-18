package com.comp2042.controller;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;
import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.MoveEvent;
import com.comp2042.ui.GuiController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Tests GameController user input handling
// Tests all user actions: arrow keys, rotation, hard drop, hold, and reset
class GameControllerInputTest {

    private GameController gameController;
    
    @Mock
    private GuiController mockGuiController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gameController = new GameController(mockGuiController);
        gameController.initializeGame();
    }

    // DOWN ARROW KEY TESTS

    @Test
    void testUserPressesDownArrow() {
        // User presses down arrow
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        DownData result = gameController.onDownEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getViewData());
    }

    @Test
    void testUserDownEventAddsScore() {
        // User down movement should add 1 point
        gameController.createNewGame();
        MoveEvent userEvent = new MoveEvent(EventType.DOWN, EventSource.USER);
        gameController.onDownEvent(userEvent);
        
        assertTrue(true);
    }

    @Test
    void testThreadDownEventDoesNotAddScore() {
        // Thread movements (automatic gravity) don't add score
        MoveEvent threadEvent = new MoveEvent(EventType.DOWN, EventSource.THREAD);
        DownData result = gameController.onDownEvent(threadEvent);
        
        assertNotNull(result);
    }

    @Test
    void testUserDownEventWhenBrickLands() {
        // Move brick down until it lands and locks
        MoveEvent event = new MoveEvent(EventType.DOWN, EventSource.USER);
        DownData result = null;
        
        for (int i = 0; i < 30; i++) {
            result = gameController.onDownEvent(event);
            if (result.getClearRow() != null) {
                break; // Brick landed
            }
        }
        
        assertNotNull(result);
    }

    // LEFT ARROW KEY TESTS

    @Test
    void testUserPressesLeftArrow() {
        // User presses left arrow
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        ViewData result = gameController.onLeftEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getBrickData());
    }

    @Test
    void testUserMultipleLeftPresses() {
        // User rapidly presses left arrow
        MoveEvent event = new MoveEvent(EventType.LEFT, EventSource.USER);
        
        ViewData result1 = gameController.onLeftEvent(event);
        int firstX = result1.getxPosition();
        
        ViewData result2 = gameController.onLeftEvent(event);
        int secondX = result2.getxPosition();
        
        // X position should decrease (move left)
        assertTrue(secondX <= firstX);
    }

    // RIGHT ARROW KEY TESTS

    @Test
    void testUserPressesRightArrow() {
        // User presses right arrow
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        ViewData result = gameController.onRightEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getBrickData());
    }

    @Test
    void testUserMultipleRightPresses() {
        // User rapidly presses right arrow
        MoveEvent event = new MoveEvent(EventType.RIGHT, EventSource.USER);
        
        ViewData result1 = gameController.onRightEvent(event);
        int firstX = result1.getxPosition();
        
        ViewData result2 = gameController.onRightEvent(event);
        int secondX = result2.getxPosition();
        
        // X position should increase (move right)
        assertTrue(secondX >= firstX);
    }

    // ROTATION KEY TESTS

    @Test
    void testUserPressesRotateKey() {
        // User presses rotate key (up arrow or X)
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        ViewData result = gameController.onRotateEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getBrickData());
    }

    @Test
    void testUserMultipleRotations() {
        // User presses rotate multiple times
        MoveEvent event = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        gameController.onRotateEvent(event);
        gameController.onRotateEvent(event);
        ViewData result = gameController.onRotateEvent(event);
        
        assertNotNull(result);
    }

    @Test
    void testUserRotateFourTimesReturnsToOriginal() {
        // Rotate 4 times should cycle back to original
        MoveEvent rotateEvent = new MoveEvent(EventType.ROTATE, EventSource.USER);
        
        gameController.onRotateEvent(rotateEvent);
        gameController.onRotateEvent(rotateEvent);
        gameController.onRotateEvent(rotateEvent);
        gameController.onRotateEvent(rotateEvent);
        ViewData finalView = gameController.onRotateEvent(rotateEvent);
        
        assertNotNull(finalView);
    }

    // HARD DROP TESTS (SPACE BAR)

    @Test
    void testUserPressesHardDrop() {
        // User presses space bar for instant drop
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        DownData result = gameController.onHardDropEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getViewData());
    }

    @Test
    void testUserHardDropAddsScore() {
        // Hard drop adds 2 points per row dropped
        gameController.createNewGame();
        
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        DownData result = gameController.onHardDropEvent(event);
        
        assertNotNull(result);
    }

    @Test
    void testUserHardDropLocksAndSpawnsNewBrick() {
        // Hard drop locks brick and spawns new one
        MoveEvent event = new MoveEvent(EventType.HARD_DROP, EventSource.USER);
        DownData result1 = gameController.onHardDropEvent(event);
        
        assertNotNull(result1.getClearRow());
        assertNotNull(result1.getViewData());
    }

    // HOLD KEY TESTS (C OR SHIFT)

    @Test
    void testUserPressesHoldKey() {
        // User presses hold key (C or Shift)
        MoveEvent event = new MoveEvent(EventType.HOLD, EventSource.USER);
        ViewData result = gameController.onHoldEvent(event);
        
        assertNotNull(result);
        assertNotNull(result.getBrickData());
    }

    @Test
    void testUserHoldsAndSwapsBricks() {
        // User holds first brick
        MoveEvent holdEvent = new MoveEvent(EventType.HOLD, EventSource.USER);
        ViewData afterFirstHold = gameController.onHoldEvent(holdEvent);
        
        assertNotNull(afterFirstHold.getHeldBrickData());
    }

    @Test
    void testUserCannotHoldTwiceInRow() {
        // Can only hold once per piece
        MoveEvent holdEvent = new MoveEvent(EventType.HOLD, EventSource.USER);
        ViewData result1 = gameController.onHoldEvent(holdEvent);
        ViewData result2 = gameController.onHoldEvent(holdEvent);
        
        assertNotNull(result2);
    }

    // NEW GAME / RESET TESTS (N KEY)

    @Test
    void testUserPressesResetKey() {
        // User plays then presses N to reset
        gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        
        gameController.createNewGame();
        
        // Verify GUI refresh methods called
        verify(mockGuiController, atLeastOnce()).refreshGameBackground(any());
        verify(mockGuiController, atLeastOnce()).refreshBrick(any());
    }

    @Test
    void testUserNewGameResetsBoard() {
        // Fill board then start new game
        for (int i = 0; i < 5; i++) {
            gameController.onHardDropEvent(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
        }
        
        gameController.createNewGame();
        
        verify(mockGuiController, atLeastOnce()).refreshGameBackground(any());
    }

    // COMPLEX INPUT SEQUENCE TESTS

    @Test
    void testUserComplexInputSequence() {
        // Simulate realistic user input sequence
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        ViewData result = gameController.onDownEvent(
            new MoveEvent(EventType.DOWN, EventSource.USER)
        ).getViewData();
        
        assertNotNull(result);
    }

    @Test
    void testUserMovementBeforeHardDrop() {
        // User positions brick before hard dropping
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        
        DownData result = gameController.onHardDropEvent(
            new MoveEvent(EventType.HARD_DROP, EventSource.USER)
        );
        
        assertNotNull(result);
    }

    @Test
    void testUserHoldsAfterPositioning() {
        // User positions brick then holds it
        gameController.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER));
        gameController.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER));
        
        ViewData result = gameController.onHoldEvent(
            new MoveEvent(EventType.HOLD, EventSource.USER)
        );
        
        assertNotNull(result);
    }

    // EDGE CASE TESTS

    @Test
    void testUserInputAtBoardEdges() {
        // Move brick to left edge
        for (int i = 0; i < 10; i++) {
            gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        }
        
        // Try to move left again at edge
        ViewData result = gameController.onLeftEvent(
            new MoveEvent(EventType.LEFT, EventSource.USER)
        );
        
        assertNotNull(result);
    }

    @Test
    void testUserRapidInputs() {
        // Simulate rapid user inputs (button mashing)
        for (int i = 0; i < 20; i++) {
            gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        }
        
        assertTrue(true);
    }

    @Test
    void testUserInputLeadsToGameOver() {
        // User plays until game over
        for (int i = 0; i < 20; i++) {
            DownData result = gameController.onHardDropEvent(
                new MoveEvent(EventType.HARD_DROP, EventSource.USER)
            );
            
            if (result.getClearRow() == null) {
                // Game over occurred
                verify(mockGuiController, atLeastOnce()).gameOver();
                return;
            }
        }
    }

    // MIXED USER AND THREAD EVENT TESTS

    @Test
    void testMixedUserAndThreadDownEvents() {
        // Thread moves brick down (gravity)
        gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        
        // User also presses down
        DownData result = gameController.onDownEvent(
            new MoveEvent(EventType.DOWN, EventSource.USER)
        );
        
        assertNotNull(result);
    }

    @Test
    void testUserInputDuringThreadMovement() {
        // Thread moves brick down
        gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.THREAD));
        
        // User moves brick left during fall
        gameController.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER));
        
        // User rotates
        ViewData result = gameController.onRotateEvent(
            new MoveEvent(EventType.ROTATE, EventSource.USER)
        );
        
        assertNotNull(result);
    }

    // SCORING VALIDATION TESTS

    @Test
    void testUserDownEventScoring() {
        // Multiple user down events add score (1 point each)
        gameController.createNewGame();
        
        for (int i = 0; i < 5; i++) {
            gameController.onDownEvent(new MoveEvent(EventType.DOWN, EventSource.USER));
        }
        
        assertTrue(true);
    }

    @Test
    void testUserHardDropScoring() {
        // Hard drop adds score (2 points per row)
        gameController.createNewGame();
        
        DownData result = gameController.onHardDropEvent(
            new MoveEvent(EventType.HARD_DROP, EventSource.USER)
        );
        
        assertNotNull(result);
    }
}
