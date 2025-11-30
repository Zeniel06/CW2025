package com.comp2042.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

/**
 * UI panel for the main menu displayed when the application starts.
 * Provides options to start a new game or access the settings menu.
 */
public class MainMenuPanel extends BorderPane {

    private Button startGameButton;
    private Button settingsButton;

    /**
     * Constructs a new MainMenuPanel with the title, start game button, and settings button.
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
        
        // Create Settings button
        settingsButton = new Button("SETTINGS");
        settingsButton.getStyleClass().add("gameModeButton");
        settingsButton.setPrefWidth(250);
        settingsButton.setPrefHeight(50);
        
        // Create a VBox to hold all elements
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.getChildren().addAll(titleLabel, startGameButton, settingsButton);
        
        // No background - completely transparent
        this.setStyle("-fx-background-color: transparent;");
        this.setPrefSize(300, 360);
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

    /**
     * Gets the settings button.
     * 
     * @return the button that opens the settings menu
     */
    public Button getSettingsButton() {
        return settingsButton;
    }
}

