package com.comp2042.event;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;

/**
 * Interface for handling player input events and game actions.
 * Defines callbacks for all possible player interactions with the game.
 */
public interface InputEventListener {

    /**
     * Handles the downward movement event.
     * 
     * @param event the move event
     * @return the down data containing cleared row information and updated view data
     */
    DownData onDownEvent(MoveEvent event);

    /**
     * Handles the left movement event.
     * 
     * @param event the move event
     * @return the updated view data
     */
    ViewData onLeftEvent(MoveEvent event);

    /**
     * Handles the right movement event.
     * 
     * @param event the move event
     * @return the updated view data
     */
    ViewData onRightEvent(MoveEvent event);

    /**
     * Handles the rotation event.
     * 
     * @param event the move event
     * @return the updated view data
     */
    ViewData onRotateEvent(MoveEvent event);

    /**
     * Handles the hard drop event, instantly dropping the brick to the bottom.
     * 
     * @param event the move event
     * @return the down data containing cleared row information and updated view data
     */
    DownData onHardDropEvent(MoveEvent event);

    /**
     * Handles the hold event, storing or swapping the current brick with the held brick.
     * 
     * @param event the move event
     * @return the updated view data
     */
    ViewData onHoldEvent(MoveEvent event);

    /**
     * Creates and initializes a new game.
     */
    void createNewGame();
}

