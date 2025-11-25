package com.comp2042.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * UI panel displayed when the game ends.
 * Shows a "GAME OVER" message, final score, and restart instructions to the player.
 */
public class GameOverPanel extends BorderPane {

    private Label scoreLabel;

    /**
     * Constructs a new GameOverPanel with centered "GAME OVER" text,
     * score display, and restart instructions.
     */
    public GameOverPanel() {
        // Create the GAME OVER label
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        // Create the score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.getStyleClass().add("gameOverScoreStyle");
        
        // Create the restart instruction label
        final Label restartLabel = new Label("Press \"N\" to Restart");
        restartLabel.getStyleClass().add("restartInstructionStyle");
        
        // Container to hold all labels vertically
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.getChildren().addAll(gameOverLabel, scoreLabel, restartLabel);
        
        // Center the entire message box
        setCenter(messageBox);
        setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);");
    }

    /**
     * Updates the displayed score on the game over screen.
     * 
     * @param score the final score to display
     */
    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

}

