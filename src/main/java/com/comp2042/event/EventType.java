package com.comp2042.event;

/**
 * Enumeration representing different types of game events or player actions.
 * Defines all possible movements and special actions for bricks in the game.
 */
public enum EventType {
    /** Move brick down one row */
    DOWN, 
    /** Move brick left one column */
    LEFT, 
    /** Move brick right one column */
    RIGHT, 
    /** Rotate brick clockwise */
    ROTATE, 
    /** Instantly drop brick to the bottom */
    HARD_DROP, 
    /** Store or swap current brick with held brick */
    HOLD
}

