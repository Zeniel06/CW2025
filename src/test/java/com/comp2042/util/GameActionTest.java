package com.comp2042.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests GameAction enum
class GameActionTest {

    @Test
    void testAllActionsExist() {
        GameAction[] actions = GameAction.values();
        assertEquals(8, actions.length);
    }

    @Test
    void testDisplayNames() {
        assertEquals("Move Left", GameAction.MOVE_LEFT.getDisplayName());
        assertEquals("Move Right", GameAction.MOVE_RIGHT.getDisplayName());
        assertEquals("Rotate", GameAction.ROTATE.getDisplayName());
        assertEquals("Hard Drop", GameAction.HARD_DROP.getDisplayName());
    }

    @Test
    void testAllActionsHaveDisplayNames() {
        for (GameAction action : GameAction.values()) {
            assertNotNull(action.getDisplayName());
            assertFalse(action.getDisplayName().isEmpty());
        }
    }

    @Test
    void testEnumValueOf() {
        GameAction action = GameAction.valueOf("MOVE_LEFT");
        assertEquals(GameAction.MOVE_LEFT, action);
    }
}

