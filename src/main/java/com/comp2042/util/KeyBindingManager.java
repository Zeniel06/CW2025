package com.comp2042.util;

import javafx.scene.input.KeyCode;

import java.util.*;
import java.util.prefs.Preferences;

/**
 * Manages key bindings for game actions.
 * Provides functionality to get, set, and persist key bindings.
 * Uses the Singleton pattern and supports versioning of default bindings.
 */
public class KeyBindingManager {
    
    private static final KeyBindingManager INSTANCE = new KeyBindingManager();
    private static final String KEYBIND_VERSION_KEY = "keybind_version";
    private static final int CURRENT_KEYBIND_VERSION = 2; // Increment when defaults change
    
    // Maps each action to a list of key codes
    private final Map<GameAction, List<KeyCode>> keyBindings;
    
    // Reverse map for quick lookup: which action is bound to a key
    private final Map<KeyCode, GameAction> reverseBindings;
    
    private final Preferences preferences;
    
    private KeyBindingManager() {
        keyBindings = new EnumMap<>(GameAction.class);
        reverseBindings = new HashMap<>();
        preferences = Preferences.userNodeForPackage(KeyBindingManager.class);
        
        // Check if saved preferences are from an old version
        int savedVersion = preferences.getInt(KEYBIND_VERSION_KEY, 0);
        
        // Initialize with default key bindings
        loadDefaultBindings();
        
        // Only load saved bindings if they're from the current version
        if (savedVersion == CURRENT_KEYBIND_VERSION) {
            loadBindings();
        } else {
            // Old version or first run - save current defaults
            saveBindings();
        }
    }
    
    /**
     * Gets the singleton instance.
     * 
     * @return the instance
     */
    public static KeyBindingManager getInstance() {
        return INSTANCE;
    }
    
    /**
     * Sets the default key bindings for all actions.
     */
    private void loadDefaultBindings() {
        // Clear existing bindings
        keyBindings.clear();
        reverseBindings.clear();
        
        // Load all default bindings
        for (GameAction action : GameAction.values()) {
            KeyCode defaultKey = getDefaultKeyForAction(action);
            if (defaultKey != null) {
                addBinding(action, defaultKey);
            }
        }
    }
    
    /**
     * Gets the default key for a specific action.
     */
    private KeyCode getDefaultKeyForAction(GameAction action) {
        switch (action) {
            case MOVE_LEFT: return KeyCode.LEFT;
            case MOVE_RIGHT: return KeyCode.RIGHT;
            case ROTATE: return KeyCode.UP;
            case SOFT_DROP: return KeyCode.DOWN;
            case HARD_DROP: return KeyCode.SPACE;
            case HOLD_PIECE: return KeyCode.SHIFT;
            case PAUSE: return KeyCode.ESCAPE;
            case NEW_GAME: return KeyCode.N;
            default: return null;
        }
    }
    
    /**
     * Adds a key binding for an action without removing existing bindings.
     */
    private void addBinding(GameAction action, KeyCode keyCode) {
        keyBindings.computeIfAbsent(action, k -> new ArrayList<>()).add(keyCode);
        reverseBindings.put(keyCode, action);
    }
    
    /**
     * Sets a key binding for an action.
     * Automatically resolves conflicts and saves to preferences.
     * 
     * @param action the game action
     * @param keyCode the key code
     */
    public void setBinding(GameAction action, KeyCode keyCode) {
        // Remove old bindings for this action
        List<KeyCode> oldKeys = keyBindings.get(action);
        if (oldKeys != null) {
            for (KeyCode oldKey : oldKeys) {
                reverseBindings.remove(oldKey);
            }
        }
        
        // Remove this key from any other action it might be bound to
        GameAction oldAction = reverseBindings.get(keyCode);
        if (oldAction != null && oldAction != action) {
            keyBindings.get(oldAction).remove(keyCode);
        }
        
        // Set new binding
        List<KeyCode> newKeys = new ArrayList<>();
        newKeys.add(keyCode);
        keyBindings.put(action, newKeys);
        reverseBindings.put(keyCode, action);
        
        // Save to preferences
        saveBindings();
    }
    
    /**
     * Gets the key binding for an action.
     * 
     * @param action the game action
     * @return the key code, or null if not bound
     */
    public KeyCode getPrimaryBinding(GameAction action) {
        List<KeyCode> keys = keyBindings.get(action);
        return (keys != null && !keys.isEmpty()) ? keys.get(0) : null;
    }
    
    /**
     * Gets all key bindings for an action.
     * 
     * @param action the game action
     * @return list of key codes
     */
    public List<KeyCode> getBindings(GameAction action) {
        return new ArrayList<>(keyBindings.getOrDefault(action, Collections.emptyList()));
    }
    
    /**
     * Gets the action bound to a key.
     * 
     * @param keyCode the key code
     * @return the game action, or null if not bound
     */
    public GameAction getAction(KeyCode keyCode) {
        return reverseBindings.get(keyCode);
    }
    
    /**
     * Checks if a key is bound to an action.
     * 
     * @param keyCode the key code
     * @param action the game action
     * @return true if bound
     */
    public boolean isKeyBoundTo(KeyCode keyCode, GameAction action) {
        return action.equals(reverseBindings.get(keyCode));
    }
    
    /**
     * Resets all key bindings to default values.
     */
    public void resetToDefaults() {
        loadDefaultBindings();
        saveBindings();
    }
    
    /**
     * Saves current key bindings to preferences.
     */
    private void saveBindings() {
        for (GameAction action : GameAction.values()) {
            KeyCode primaryKey = getPrimaryBinding(action);
            if (primaryKey != null) {
                preferences.put(action.name(), primaryKey.name());
            }
        }
        // Save version number
        preferences.putInt(KEYBIND_VERSION_KEY, CURRENT_KEYBIND_VERSION);
    }
    
    /**
     * Loads key bindings from preferences.
     * Note: This clears and rebuilds bindings from saved preferences without triggering saves.
     */
    private void loadBindings() {
        // Clear current bindings first
        keyBindings.clear();
        reverseBindings.clear();
        
        // Load each action's binding from preferences
        for (GameAction action : GameAction.values()) {
            String keyName = preferences.get(action.name(), null);
            if (keyName != null) {
                try {
                    KeyCode keyCode = KeyCode.valueOf(keyName);
                    // Use addBinding to avoid triggering save during load
                    List<KeyCode> keys = new ArrayList<>();
                    keys.add(keyCode);
                    keyBindings.put(action, keys);
                    reverseBindings.put(keyCode, action);
                } catch (IllegalArgumentException e) {
                    // Invalid key code in preferences, use default for this action
                    System.err.println("Invalid key code in preferences for " + action + ": " + keyName);
                    // Restore default for this specific action
                    restoreDefaultForAction(action);
                }
            }
        }
    }
    
    /**
     * Restores the default binding for a specific action.
     */
    private void restoreDefaultForAction(GameAction action) {
        KeyCode defaultKey = getDefaultKeyForAction(action);
        if (defaultKey != null) {
            addBinding(action, defaultKey);
        }
    }
    
    /**
     * Gets a display string for a key code.
     * 
     * @param keyCode the key code
     * @return the display string, or "None" if null
     */
    public static String getKeyDisplayName(KeyCode keyCode) {
        if (keyCode == null) {
            return "None";
        }
        
        switch (keyCode) {
            case LEFT: return "←";
            case RIGHT: return "→";
            case UP: return "↑";
            case DOWN: return "↓";
            case SPACE: return "SPACE";
            case SHIFT: return "SHIFT";
            case ESCAPE: return "ESC";
            default: return keyCode.getName().toUpperCase();
        }
    }
}

