package com.comp2042.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;

/**
 * UI panel for game settings.
 * Provides a comprehensive settings interface where players can adjust the background music volume
 * using a slider control and view all available game controls with their key bindings.
 * The panel includes a semi-transparent dark background with a blue border to match the game's aesthetic.
 */
public class SettingsPanel extends BorderPane {

    private static final String SECTION_LABEL_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;";
    
    private Slider volumeSlider;
    private Button backButton;

    /**
     * Constructs a new SettingsPanel with volume control and controls display.
     * The panel includes:
     * - A volume slider (0-100%) for adjusting background music volume
     * - A comprehensive display of all game controls with key bindings
     * - A back button to return to the main menu
     * The panel is styled with a semi-transparent background and positioned centrally.
     */
    public SettingsPanel() {
        // Create the title label
        final Label titleLabel = new Label("SETTINGS");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Volume control
        Label volumeLabel = new Label("MUSIC VOLUME");
        volumeLabel.setStyle(SECTION_LABEL_STYLE);
        
        // Create volume slider (0-100)
        volumeSlider = new Slider(0, 100, 15); // Default to 15% (current volume in GuiController)
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.setPrefWidth(250);
        volumeSlider.setStyle("-fx-control-inner-background: rgba(100, 100, 100, 0.8);");
        
        VBox volumeBox = new VBox(8);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.getChildren().addAll(volumeLabel, volumeSlider);
        
        // Control display
        Label controlsLabel = new Label("CONTROLS");
        controlsLabel.setStyle(SECTION_LABEL_STYLE);
        
        // Create controls text display
        VBox controlsBox = new VBox(5);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setStyle("-fx-background-color: rgba(30, 30, 30, 0.8); -fx-border-color: rgba(100, 150, 200, 0.6); -fx-border-width: 1;");
        
        // Add control descriptions
        addControlLine(controlsBox, "MOVE LEFT:", "← / A");
        addControlLine(controlsBox, "MOVE RIGHT:", "→ / D");
        addControlLine(controlsBox, "ROTATE:", "↑ / W");
        addControlLine(controlsBox, "SOFT DROP:", "↓ / S");
        addControlLine(controlsBox, "HARD DROP:", "SPACE");
        addControlLine(controlsBox, "HOLD PIECE:", "SHIFT");
        addControlLine(controlsBox, "PAUSE:", "ESC");
        addControlLine(controlsBox, "NEW GAME:", "N");
        
        VBox controlsSection = new VBox(8);
        controlsSection.setAlignment(Pos.CENTER);
        controlsSection.getChildren().addAll(controlsLabel, controlsBox);
        
        // back button
        backButton = new Button("BACK");
        backButton.getStyleClass().add("gameModeButton");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(40);
        
        // Create main content container
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(20));
        contentBox.getChildren().addAll(titleLabel, volumeBox, controlsSection, backButton);
        
        // Panel styling
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 3;");
        this.setPrefSize(400, 500);
        setCenter(contentBox);
    }

    private void addControlLine(VBox container, String action, String key) {
        HBox line = new HBox(10);
        line.setAlignment(Pos.CENTER_LEFT);
        
        Text actionText = new Text(action);
        actionText.setFill(Color.LIGHTBLUE);
        actionText.setFont(Font.font(null, FontWeight.BOLD, 12));
        
        Text keyText = new Text(key);
        keyText.setFill(Color.WHITE);
        keyText.setFont(Font.font(12));
        
        line.getChildren().addAll(actionText, keyText);
        container.getChildren().add(line);
    }

    /**
     * Gets the volume slider control.
     * The slider can be bound to a media player's volume property to enable
     * real-time volume adjustment. Values range from 0 (muted) to 100 (maximum volume).
     * 
     * @return the volume slider control
     */
    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    /**
     * Gets the back button.
     * This button should be configured to navigate back to the main menu when clicked.
     * 
     * @return the button that returns to the main menu
     */
    public Button getBackButton() {
        return backButton;
    }
}

