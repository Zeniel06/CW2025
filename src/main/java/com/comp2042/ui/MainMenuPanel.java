package com.comp2042.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

/**
 * UI panel for the main menu displayed when the application starts.
 * Provides options to start a new game.
 */
public class MainMenuPanel extends BorderPane {

    private Button startGameButton;

    /**
     * Constructs a new MainMenuPanel with the title and game start button.
     */
    public MainMenuPanel() {
        // Create the title label
        final Label titleLabel = new Label("TETRIS");
        titleLabel.getStyleClass().add("mainMenuTitle");
        
        // Create Start Game button
        startGameButton = new Button("START GAME");
        startGameButton.getStyleClass().add("gameModeButton");
        startGameButton.setPrefWidth(250);
        startGameButton.setPrefHeight(50);
        
        // Create a VBox to hold all elements
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.getChildren().addAll(titleLabel, startGameButton);
        
        // No background - completely transparent
        this.setStyle("-fx-background-color: transparent;");
        this.setPrefSize(300, 300);
        setCenter(contentBox);
    }

    /**
     * Gets the start game button.
     * 
     * @return the button that starts a new game
     */
    public Button getStartGameButton() {
        return startGameButton;
    }
}

