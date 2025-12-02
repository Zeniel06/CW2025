package com.comp2042.ui;

import com.comp2042.util.GameAction;
import com.comp2042.util.KeyBindingManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Settings panel for adjusting game volume and customizing key bindings.
 * Players can click any key button to rebind controls and reset to defaults.
 */
public class SettingsPanel extends BorderPane {

    private static final String SECTION_LABEL_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;";
    
    // Button style constants to avoid duplication
    private static final String KEY_BUTTON_NORMAL_STYLE = 
        "-fx-background-color: rgba(50, 70, 100, 0.8);" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 5 15 5 15;" +
        "-fx-border-color: rgba(100, 150, 200, 0.6);" +
        "-fx-border-width: 1;" +
        "-fx-cursor: hand;";
    
    private static final String KEY_BUTTON_HOVER_STYLE = 
        "-fx-background-color: rgba(70, 90, 120, 0.9);" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 5 15 5 15;" +
        "-fx-border-color: rgba(120, 170, 220, 0.8);" +
        "-fx-border-width: 1;" +
        "-fx-cursor: hand;";
    
    private static final String KEY_BUTTON_REBINDING_STYLE = 
        "-fx-background-color: rgba(200, 100, 50, 0.9);" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 11px;" +
        "-fx-font-weight: bold;" +
        "-fx-padding: 5 15 5 15;" +
        "-fx-border-color: rgba(255, 150, 100, 0.8);" +
        "-fx-border-width: 2;" +
        "-fx-cursor: hand;";
    
    private Slider volumeSlider;
    private Button backButton;
    private KeyBindingManager keyBindingManager;
    private GameAction currentlyRebinding = null;
    private Button currentRebindButton = null;

    /**
     * Constructs a new SettingsPanel with volume control and key binding customization.
     */
    public SettingsPanel() {
        keyBindingManager = KeyBindingManager.getInstance();
        
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
        
        VBox volumeBox = new VBox(6);
        volumeBox.setAlignment(Pos.CENTER);
        volumeBox.getChildren().addAll(volumeLabel, volumeSlider);
        
        // Control display
        Label controlsLabel = new Label("KEY BINDINGS");
        controlsLabel.setStyle(SECTION_LABEL_STYLE);
        
        // Create controls box with customizable key bindings
        VBox controlsBox = new VBox(4);
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        controlsBox.setPadding(new Insets(8));
        controlsBox.setStyle("-fx-background-color: rgba(30, 30, 30, 0.8); -fx-border-color: rgba(100, 150, 200, 0.6); -fx-border-width: 1;");
        
        // Add interactive control bindings for each action
        for (GameAction action : GameAction.values()) {
            addKeyBindingControl(controlsBox, action);
        }
        
        VBox controlsSection = new VBox(6);
        controlsSection.setAlignment(Pos.CENTER);
        controlsSection.getChildren().addAll(controlsLabel, controlsBox);
        
        // Buttons row
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Reset to defaults button
        Button resetButton = new Button("RESET");
        resetButton.getStyleClass().add("gameModeButton");
        resetButton.setPrefWidth(150);
        resetButton.setMinWidth(150);
        resetButton.setMaxWidth(150);
        resetButton.setPrefHeight(35);
        resetButton.setOnAction(e -> resetKeybindsToDefaults());
        
        // back button
        backButton = new Button("BACK");
        backButton.getStyleClass().add("gameModeButton");
        backButton.setPrefWidth(150);
        backButton.setPrefHeight(35);
        
        buttonsBox.getChildren().addAll(resetButton, backButton);
        
        // Create main content container
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(12));
        contentBox.getChildren().addAll(titleLabel, volumeBox, controlsSection, buttonsBox);
        
        // Panel styling
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 3;");
        this.setPrefSize(420, 520);
        setCenter(contentBox);
        
        // Setup key event handler for rebinding
        this.setOnKeyPressed(this::handleKeyPress);
        this.setFocusTraversable(true);
    }

    /**
     * Adds an interactive key binding control for a game action.
     * Users can click the button to rebind the key.
     */
    private void addKeyBindingControl(VBox container, GameAction action) {
        HBox line = new HBox(10);
        line.setAlignment(Pos.CENTER_LEFT);
        
        // Action label
        Text actionText = new Text(action.getDisplayName().toUpperCase() + ":");
        actionText.setFill(Color.LIGHTBLUE);
        actionText.setFont(Font.font(null, FontWeight.BOLD, 11));
        actionText.setWrappingWidth(110);
        
        // Spacer to push button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        // Key button - shows current binding and allows rebinding
        Button keyButton = new Button(KeyBindingManager.getKeyDisplayName(keyBindingManager.getPrimaryBinding(action)));
        keyButton.setStyle(KEY_BUTTON_NORMAL_STYLE);
        keyButton.setPrefWidth(100);
        
        // Handle button click to start rebinding
        keyButton.setOnAction(e -> startRebinding(action, keyButton));
        
        // Hover effect
        keyButton.setOnMouseEntered(e -> keyButton.setStyle(KEY_BUTTON_HOVER_STYLE));
        keyButton.setOnMouseExited(e -> {
            if (currentRebindButton != keyButton) {
                keyButton.setStyle(KEY_BUTTON_NORMAL_STYLE);
            }
        });
        
        line.getChildren().addAll(actionText, spacer, keyButton);
        container.getChildren().add(line);
    }
    
    /**
     * Starts the rebinding process for a game action.
     */
    private void startRebinding(GameAction action, Button button) {
        // Cancel any previous rebinding
        if (currentRebindButton != null) {
            KeyCode previousKey = keyBindingManager.getPrimaryBinding(currentlyRebinding);
            currentRebindButton.setText(KeyBindingManager.getKeyDisplayName(previousKey));
            currentRebindButton.setStyle(KEY_BUTTON_NORMAL_STYLE);
        }
        
        // Set up new rebinding
        currentlyRebinding = action;
        currentRebindButton = button;
        button.setText("Press key...");
        button.setStyle(KEY_BUTTON_REBINDING_STYLE);
        
        // Request focus to receive key events
        this.requestFocus();
    }
    
    /**
     * Handles key press events during rebinding.
     */
    private void handleKeyPress(KeyEvent event) {
        if (currentlyRebinding != null && currentRebindButton != null) {
            KeyCode newKey = event.getCode();
            
            // Ignore modifier keys alone
            if (newKey == KeyCode.SHIFT || newKey == KeyCode.CONTROL || 
                newKey == KeyCode.ALT || newKey == KeyCode.META) {
                return;
            }
            
            // Set the new binding
            keyBindingManager.setBinding(currentlyRebinding, newKey);
            
            // Update button text and style
            currentRebindButton.setText(KeyBindingManager.getKeyDisplayName(newKey));
            currentRebindButton.setStyle(KEY_BUTTON_NORMAL_STYLE);
            
            // Clear rebinding state
            currentlyRebinding = null;
            currentRebindButton = null;
            
            event.consume();
        }
    }
    
    /**
     * Resets all key bindings to their default values and updates the UI.
     */
    private void resetKeybindsToDefaults() {
        keyBindingManager.resetToDefaults();
        
        // Refresh all buttons - rebuild the controls section
        // Find the controls box and rebuild it
        VBox contentBox = (VBox) this.getCenter();
        VBox controlsSection = null;
        for (javafx.scene.Node node : contentBox.getChildren()) {
            if (node instanceof VBox) {
                VBox vbox = (VBox) node;
                for (javafx.scene.Node child : vbox.getChildren()) {
                    if (child instanceof Label && ((Label) child).getText().equals("KEY BINDINGS")) {
                        controlsSection = vbox;
                        break;
                    }
                }
            }
        }
        
        if (controlsSection != null) {
            // Find the controls box (second child after label)
            VBox controlsBox = (VBox) controlsSection.getChildren().get(1);
            controlsBox.getChildren().clear();
            
            // Re-add all key binding controls with default values
            for (GameAction action : GameAction.values()) {
                addKeyBindingControl(controlsBox, action);
            }
        }
        
        // Clear any active rebinding
        currentlyRebinding = null;
        currentRebindButton = null;
    }

    /**
     * Gets the volume slider.
     * 
     * @return the volume slider
     */
    public Slider getVolumeSlider() {
        return volumeSlider;
    }

    /**
     * Gets the back button.
     * 
     * @return the back button
     */
    public Button getBackButton() {
        return backButton;
    }
}

