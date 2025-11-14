package com.comp2042.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * UI panel displayed when the game ends.
 * Shows a "GAME OVER" message to the player.
 */
public class GameOverPanel extends BorderPane {

    /**
     * Constructs a new GameOverPanel with a centered "GAME OVER" label.
     */
    public GameOverPanel() {
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        setCenter(gameOverLabel);
    }

}

