package com.comp2042.event;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateEvent(MoveEvent event);

    // Handles hard drop event - instantly drops brick to bottom and locks it
    DownData onHardDropEvent(MoveEvent event);

    // Hold piece feature - stores current piece and swaps with held piece
    // First press: holds piece and spawns new one
    // Subsequent press: swaps current with held piece (once per piece)
    ViewData onHoldEvent(MoveEvent event);

    void createNewGame();
}

