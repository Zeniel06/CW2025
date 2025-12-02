package com.comp2042.util;

/**
 * Represents all customizable game actions that can be mapped to key bindings.
 */
public enum GameAction {
    /** Move piece left */
    MOVE_LEFT("Move Left"),
    
    /** Move piece right */
    MOVE_RIGHT("Move Right"),
    
    /** Rotate piece */
    ROTATE("Rotate"),
    
    /** Soft drop */
    SOFT_DROP("Soft Drop"),
    
    /** Hard drop */
    HARD_DROP("Hard Drop"),
    
    /** Hold piece */
    HOLD_PIECE("Hold Piece"),
    
    /** Pause game */
    PAUSE("Pause"),
    
    /** Start new game */
    NEW_GAME("New Game");

    private final String displayName;

    GameAction(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the display name for this action.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
}

