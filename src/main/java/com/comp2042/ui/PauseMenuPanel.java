package com.comp2042.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;

public class PauseMenuPanel extends BorderPane {

    private Button resumeButton;
    private Button mainMenuButton;

    public PauseMenuPanel() {
        // Create the "PAUSED" label
        final Label pausedLabel = new Label("PAUSED");
        pausedLabel.getStyleClass().add("pausedStyle");
        
        // Create Resume button
        resumeButton = new Button("RESUME");
        resumeButton.getStyleClass().add("pauseButton");
        resumeButton.setPrefWidth(150);
        
        // Create Main Menu button
        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("pauseButton");
        mainMenuButton.setPrefWidth(150);
        
        // Create a VBox to hold all elements
        VBox contentBox = new VBox(15);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.getChildren().addAll(pausedLabel, resumeButton, mainMenuButton);
        
        // Add semi-transparent background
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 3;");
        this.setPrefSize(300, 230);
        setCenter(contentBox);
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getMainMenuButton() {
        return mainMenuButton;
    }
}

