package com.comp2042.ui;

import com.comp2042.util.GameAction;
import com.comp2042.util.KeyBindingManager;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Game over panel displaying the final score and restart instructions.
 * The restart key shown is dynamically updated based on user's key bindings.
 */
public class GameOverPanel extends BorderPane {

    private Label scoreLabel;
    private Label restartLabel;
    private KeyBindingManager keyBindingManager;

    /**
     * Constructs a new GameOverPanel with game over message, score, and restart instructions.
     */
    public GameOverPanel() {
        keyBindingManager = KeyBindingManager.getInstance();
        
        // Create the GAME OVER label
        final Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverStyle");
        
        // Create the score label
        scoreLabel = new Label("Score: 0");
        scoreLabel.getStyleClass().add("gameOverScoreStyle");
        
        // Create the restart instruction label with dynamic key binding
        restartLabel = new Label(getRestartInstructionText());
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
     * Gets the restart instruction text with the current key binding.
     * 
     * @return the restart instruction text
     */
    private String getRestartInstructionText() {
        String keyName = KeyBindingManager.getKeyDisplayName(
            keyBindingManager.getPrimaryBinding(GameAction.NEW_GAME)
        );
        return "Press \"" + keyName + "\" to Restart";
    }

    /**
     * Sets the score and refreshes the restart instruction.
     * 
     * @param score the final score
     */
    public void setScore(int score) {
        scoreLabel.setText("Score: " + score);
        // Update restart instruction in case key bindings have changed
        restartLabel.setText(getRestartInstructionText());
    }

}

