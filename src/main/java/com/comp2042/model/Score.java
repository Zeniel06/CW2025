package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Manages the game scoring system including score, level, and lines cleared.
 * Uses JavaFX properties to enable automatic UI binding and updates.
 */
public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);

    /**
     * Gets the score property for binding to UI components.
     * 
     * @return the score integer property
     */
    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Gets the level property for binding to UI components.
     * 
     * @return the level integer property
     */
    public IntegerProperty levelProperty() {
        return level;
    }

    /**
     * Gets the lines property for binding to UI components.
     * 
     * @return the lines cleared integer property
     */
    public IntegerProperty linesProperty() {
        return lines;
    }

    /**
     * Adds points to the current score.
     * 
     * @param i the number of points to add
     */
    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    /**
     * Adds cleared lines to the total and updates the level accordingly.
     * 
     * @param linesCleared the number of lines cleared
     */
    public void addLines(int linesCleared) {
        lines.setValue(lines.getValue() + linesCleared);
        // Update level based on lines cleared (every 3 lines = new level)
        updateLevel();
    }

    /**
     * Resets all score values to their initial state for a new game.
     */
    public void reset() {
        score.setValue(0);
        level.setValue(1);
        lines.setValue(0);
    }
}

