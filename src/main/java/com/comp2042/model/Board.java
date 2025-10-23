package com.comp2042.model;

import com.comp2042.data.ClearRow;
import com.comp2042.data.ViewData;

public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateLeftBrick();

    // Instantly drops the brick to the bottom position and returns the distance dropped
    int hardDropBrick();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();
}

