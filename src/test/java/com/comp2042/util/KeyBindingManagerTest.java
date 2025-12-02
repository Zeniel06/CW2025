package com.comp2042.util;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Tests KeyBindingManager
// Tests binding, conflict resolution, and reset functionality
class KeyBindingManagerTest {

    private KeyBindingManager manager;

    @BeforeEach
    void setUp() {
        manager = KeyBindingManager.getInstance();
        manager.resetToDefaults();
    }

    @Test
    void testSingletonInstance() {
        KeyBindingManager instance1 = KeyBindingManager.getInstance();
        KeyBindingManager instance2 = KeyBindingManager.getInstance();
        
        assertSame(instance1, instance2);
    }

    @Test
    void testDefaultBindings() {
        assertEquals(KeyCode.LEFT, manager.getPrimaryBinding(GameAction.MOVE_LEFT));
        assertEquals(KeyCode.RIGHT, manager.getPrimaryBinding(GameAction.MOVE_RIGHT));
        assertEquals(KeyCode.SPACE, manager.getPrimaryBinding(GameAction.HARD_DROP));
    }

    @Test
    void testSetBinding() {
        manager.setBinding(GameAction.MOVE_LEFT, KeyCode.A);
        
        assertEquals(KeyCode.A, manager.getPrimaryBinding(GameAction.MOVE_LEFT));
    }

    @Test
    void testConflictResolution() {
        manager.setBinding(GameAction.MOVE_LEFT, KeyCode.A);
        manager.setBinding(GameAction.MOVE_RIGHT, KeyCode.A);
        
        // A now bound to MOVE_RIGHT, not MOVE_LEFT
        assertEquals(KeyCode.A, manager.getPrimaryBinding(GameAction.MOVE_RIGHT));
        assertNotEquals(KeyCode.A, manager.getPrimaryBinding(GameAction.MOVE_LEFT));
    }

    @Test
    void testGetAction() {
        GameAction action = manager.getAction(KeyCode.LEFT);
        assertEquals(GameAction.MOVE_LEFT, action);
    }

    @Test
    void testGetActionUnbound() {
        GameAction action = manager.getAction(KeyCode.F12);
        assertNull(action);
    }

    @Test
    void testResetToDefaults() {
        manager.setBinding(GameAction.MOVE_LEFT, KeyCode.A);
        manager.setBinding(GameAction.MOVE_RIGHT, KeyCode.D);
        
        manager.resetToDefaults();
        
        assertEquals(KeyCode.LEFT, manager.getPrimaryBinding(GameAction.MOVE_LEFT));
        assertEquals(KeyCode.RIGHT, manager.getPrimaryBinding(GameAction.MOVE_RIGHT));
    }

    @Test
    void testKeyDisplayName() {
        assertEquals("←", KeyBindingManager.getKeyDisplayName(KeyCode.LEFT));
        assertEquals("→", KeyBindingManager.getKeyDisplayName(KeyCode.RIGHT));
        assertEquals("SPACE", KeyBindingManager.getKeyDisplayName(KeyCode.SPACE));
        assertEquals("None", KeyBindingManager.getKeyDisplayName(null));
    }

    @Test
    void testAllActionsHaveBindings() {
        for (GameAction action : GameAction.values()) {
            assertNotNull(manager.getPrimaryBinding(action));
        }
    }
}

