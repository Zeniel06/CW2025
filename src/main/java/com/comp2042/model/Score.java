package com.comp2042.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class Score {

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty level = new SimpleIntegerProperty(1);
    private final IntegerProperty lines = new SimpleIntegerProperty(0);

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty linesProperty() {
        return lines;
    }

    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    public void addLines(int linesCleared) {
        lines.setValue(lines.getValue() + linesCleared);
        // Update level based on lines cleared (every 3 lines = new level)
        updateLevel();
    }

    private void updateLevel() {
        int newLevel = (lines.getValue() / 3) + 1;
        level.setValue(newLevel);
    }

    public void reset() {
        score.setValue(0);
        level.setValue(1);
        lines.setValue(0);
    }
}

