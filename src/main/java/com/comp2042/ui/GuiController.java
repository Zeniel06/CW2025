package com.comp2042.ui;

import com.comp2042.data.DownData;
import com.comp2042.data.ViewData;
import com.comp2042.event.EventSource;
import com.comp2042.event.EventType;
import com.comp2042.event.InputEventListener;
import com.comp2042.event.MoveEvent;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main GUI controller for the Tetris game view.
 * Manages all visual elements, user input handling, animations, and game state rendering.
 */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final double BASE_WIDTH = 400.0; // Base window dimensions for scaling
    private static final double BASE_HEIGHT = 500.0;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;
    
    @FXML
    private BorderPane gameBoard;

    @FXML
    private GameOverPanel gameOverPanel;

    private PauseMenuPanel pauseMenuPanel;
    
    private MainMenuPanel mainMenuPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    
    private boolean isGameInitialized = false;
    
    // Scaling-related fields
    private javafx.scene.layout.Pane rootPane;
    private javafx.scene.Scene scene;

    // Ghost brick preview - shows where the block will land
    private GridPane ghostBrickPanel;
    private Rectangle[][] ghostRectangles;

    // Hold piece feature - UI components to display held piece
    private GridPane heldBrickPanel;      // Panel containing the held brick visualization
    private Rectangle[][] heldRectangles; // 4x4 grid to display held brick shape
    private VBox heldBrickContainer;      // Container with "HOLD" label and panel
    
    // Next piece preview - UI components to display upcoming piece
    private GridPane nextBrickPanel;      // Panel containing the next brick visualization
    private Rectangle[][] nextRectangles; // 4x4 grid to display next brick shape
    private VBox nextBrickContainer;      // Container with "NEXT" label and panel

    // Game statistics - displays score, level, and lines cleared
    private VBox statsContainer;          // Statistics container panel
    private Text scoreText;
    private Text levelText;
    private Text linesText;
    private IntegerProperty levelProperty;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();
    
    // Sound effects
    private MediaPlayer backgroundMusic;

    /**
     * Initializes scaling for the game window to allow resizing.
     * This method sets up listeners that automatically scale all game elements
     * when the window is resized.
     * 
     * @param scene the game scene to apply scaling to
     */
    public void initializeScaling(javafx.scene.Scene scene) {
        this.scene = scene;
        javafx.scene.layout.Pane root = (javafx.scene.layout.Pane) scene.getRoot();
        this.rootPane = root;
        
        // Set initial scene background (will be updated based on game state)
        scene.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        
        // Set the root pane size (base size before scaling)
        root.setPrefWidth(BASE_WIDTH);
        root.setPrefHeight(BASE_HEIGHT);
        
        // Create a scale transform for the root pane that scales from top-left (0,0)
        javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale();
        scale.setPivotX(0);
        scale.setPivotY(0);
        root.getTransforms().add(scale);
        
        // Update scale based on window size - elements stay in place but get bigger
        javafx.beans.value.ChangeListener<Number> sizeListener = (obs, oldVal, newVal) -> {
            double scaleX = scene.getWidth() / BASE_WIDTH;
            double scaleY = scene.getHeight() / BASE_HEIGHT;
            
            // Apply independent X and Y scaling to fill the window completely
            scale.setX(scaleX);
            scale.setY(scaleY);
        };
        
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);
        
        // Trigger initial scaling
        sizeListener.changed(null, 0, 0);
    }

    /**
     * Initializes the GUI controller and sets up all UI components and event handlers.
     * 
     * @param location the location used to resolve relative paths
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        
        // Hide all game elements on startup (they'll be shown when game starts)
        gamePanel.setVisible(false);
        gamePanel.setManaged(false); // Also remove from layout calculations
        brickPanel.setVisible(false);
        brickPanel.setManaged(false);
        if (gameBoard != null) {
            gameBoard.setVisible(false);
            gameBoard.setManaged(false);
        }
        gameOverPanel.setVisible(false);
        groupNotification.setVisible(false);
        groupNotification.setManaged(false);
        
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isGameInitialized && isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
                    if (keyEvent.getCode() == KeyCode.LEFT || keyEvent.getCode() == KeyCode.A) {
                        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.RIGHT || keyEvent.getCode() == KeyCode.D) {
                        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.UP || keyEvent.getCode() == KeyCode.W) {
                        refreshBrick(eventListener.onRotateEvent(new MoveEvent(EventType.ROTATE, EventSource.USER)));
                        keyEvent.consume();
                    }
                    if (keyEvent.getCode() == KeyCode.DOWN || keyEvent.getCode() == KeyCode.S) {
                        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
                        keyEvent.consume();
                    }
                    // Space bar triggers hard drop instantly 
                    if (keyEvent.getCode() == KeyCode.SPACE) {
                        hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
                        keyEvent.consume();
                    }
                    // Shift key triggers hold/swap piece (standard Tetris hold mechanic)
                    if (keyEvent.getCode() == KeyCode.SHIFT) {
                        holdPiece(new MoveEvent(EventType.HOLD, EventSource.USER));
                        keyEvent.consume();
                    }
                }
                // Escape key toggles pause menu (only when game is initialized)
                if (keyEvent.getCode() == KeyCode.ESCAPE && isGameInitialized) {
                    togglePause();
                    keyEvent.consume();
                }
                if (keyEvent.getCode() == KeyCode.N && isGameInitialized) {
                    newGame(null);
                }
            }
        });
        
        // Initialize pause menu panel
        pauseMenuPanel = new PauseMenuPanel();
        pauseMenuPanel.setVisible(false);
        
        // Set up resume button action
        pauseMenuPanel.getResumeButton().setOnAction(event -> resumeGame());
        
        // Set up main menu button action
        pauseMenuPanel.getMainMenuButton().setOnAction(event -> returnToMainMenu());
        
        // Initialize main menu panel
        mainMenuPanel = new MainMenuPanel();
        mainMenuPanel.setVisible(false);
        
        // Set up Start Game button action
        mainMenuPanel.getStartGameButton().setOnAction(event -> startGame());
        
        // Add menus to scene - use Platform.runLater to ensure scene graph is ready
        javafx.application.Platform.runLater(() -> {
            if (gamePanel.getParent() != null && gamePanel.getParent().getParent() != null) {
                javafx.scene.layout.Pane parentPane = (javafx.scene.layout.Pane) gamePanel.getParent().getParent();
                
                // Set initial background to default dark (main menu will show)
                parentPane.setStyle("-fx-background-color: #1e1e1e;");
                
                // Add pause menu (centered on screen: 400x500, menu is 300x230)
                pauseMenuPanel.setLayoutX(50);
                pauseMenuPanel.setLayoutY(135);
                parentPane.getChildren().add(pauseMenuPanel);
                pauseMenuPanel.toFront();
                
                // Add main menu (centered on screen: 400x500, menu is 300x300)
                mainMenuPanel.setLayoutX(50);  // (400 - 300) / 2 = 50
                mainMenuPanel.setLayoutY(100); // (500 - 300) / 2 = 100
                parentPane.getChildren().add(mainMenuPanel);
                mainMenuPanel.toFront();
            }
        });

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
        
        // Initialize sound effects
        initializeSounds();
    }
    
    // Initialize background music
    private void initializeSounds() {
        try {
            // Load background music (loops continuously)
            String musicPath = getClass().getClassLoader().getResource("background_music.mp3").toExternalForm();
            backgroundMusic = new MediaPlayer(new Media(musicPath));
            backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
            backgroundMusic.setVolume(0.15); // 15% volume for softer background music
        } catch (Exception e) {
            System.out.println("Background music not found. Add 'background_music.mp3' to resources folder.");
        }
    }

    /**
     * Initializes the game view with the board and brick display components.
     * 
     * @param boardMatrix the initial state of the game board
     * @param brick the initial brick data for rendering
     */
    public void initGameView(int[][] boardMatrix, ViewData brick) {
        isGameInitialized = true;
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                // Initialize empty cells with filled gridline appearance
                rectangle.setFill(Color.rgb(25, 40, 70, 0.4));
                rectangle.setStroke(Color.rgb(45, 65, 100, 0.5));
                rectangle.setStrokeWidth(0.5);
                rectangle.setStrokeType(StrokeType.INSIDE);  // Keep stroke inside to maintain consistent cell size
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                setBrickPreviewData(brick.getBrickData()[i][j], rectangle);
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * (BRICK_SIZE + 1));
        brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * (BRICK_SIZE + 1));
        // Bring the falling brick panel to front so it's visible above the game board
        brickPanel.toFront();

        // Initialize ghost brick panel - shows outline of where the block will land
        ghostBrickPanel = new GridPane();
        ghostBrickPanel.setVgap(1);
        ghostBrickPanel.setHgap(1);
        ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);  // Transparent fill for outline only
                rectangle.setStroke(Color.LIGHTGRAY);
                rectangle.setStrokeWidth(2);
                rectangle.setStrokeType(StrokeType.INSIDE);  // Prevents overlap with adjacent blocks
                rectangle.setArcHeight(9);  // Match the rounded corners of actual blocks
                rectangle.setArcWidth(9);
                rectangle.setOpacity(0.6);
                ghostRectangles[i][j] = rectangle;
                ghostBrickPanel.add(rectangle, j, i);
            }
        }
        // Position ghost at the calculated landing position
        ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * (BRICK_SIZE + 1));
        ghostBrickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getGhostYPosition() * (BRICK_SIZE + 1));
        
        // Add ghost brick panel to the parent pane
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(ghostBrickPanel);

        // === HOLD PIECE FEATURE - Initialize UI panel to display held piece ===
        heldBrickPanel = new GridPane();
        heldBrickPanel.setVgap(1);
        heldBrickPanel.setHgap(1);
        // Style: semi-transparent dark background with blue border
        heldBrickPanel.setStyle("-fx-background-color: rgba(50, 50, 50, 0.7); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 2; -fx-padding: 5; -fx-pref-width: 97;");
        
        // Create 4x4 grid of rectangles (max Tetris piece size)
        heldRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                heldRectangles[i][j] = rectangle;
                heldBrickPanel.add(rectangle, j, i);
            }
        }
        
        // Create "HOLD" label above the panel
        Text holdLabel = new Text("HOLD");
        holdLabel.setFill(Color.WHITE);
        holdLabel.setFont(Font.font("Arial", 14));
        holdLabel.setStyle("-fx-font-weight: bold;");
        
        // Combine label and panel in vertical container
        heldBrickContainer = new VBox(5);
        heldBrickContainer.getChildren().addAll(holdLabel, heldBrickPanel);
        heldBrickContainer.setStyle("-fx-alignment: center;");
        
        // Position to the right of the game board
        heldBrickContainer.setLayoutX(gamePanel.getLayoutX() + 240);
        heldBrickContainer.setLayoutY(gamePanel.getLayoutY() + 10);
        
        // Add to parent pane (makes it visible on screen)
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(heldBrickContainer);

        // === NEXT PIECE PREVIEW - Initialize UI panel to display upcoming piece ===
        nextBrickPanel = new GridPane();
        nextBrickPanel.setVgap(1);
        nextBrickPanel.setHgap(1);
        // Style: same as hold panel for consistency
        nextBrickPanel.setStyle("-fx-background-color: rgba(50, 50, 50, 0.7); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 2; -fx-padding: 5; -fx-pref-width: 97;");
        
        // Create 4x4 grid of rectangles (max Tetris piece size)
        nextRectangles = new Rectangle[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                nextRectangles[i][j] = rectangle;
                nextBrickPanel.add(rectangle, j, i);
            }
        }
        
        // Create "NEXT" label above the panel
        Text nextLabel = new Text("NEXT");
        nextLabel.setFill(Color.WHITE);
        nextLabel.setFont(Font.font("Arial", 14));
        nextLabel.setStyle("-fx-font-weight: bold;");
        
        // Combine label and panel in vertical container
        nextBrickContainer = new VBox(5);
        nextBrickContainer.getChildren().addAll(nextLabel, nextBrickPanel);
        nextBrickContainer.setStyle("-fx-alignment: center;");
        
        // Position below the hold panel (hold panel height + gap)
        nextBrickContainer.setLayoutX(heldBrickContainer.getLayoutX());
        nextBrickContainer.setLayoutY(heldBrickContainer.getLayoutY() + 130); // Position below hold panel
        
        // Add to parent pane (makes it visible on screen)
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(nextBrickContainer);

        // === STATISTICS PANEL - Display score, level, and lines (compact & thin design) ===
        
        // --- SCORE Section ---
        Text scoreLabel = new Text("SCORE");
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Arial", 11));
        scoreLabel.setStyle("-fx-font-weight: bold;");
        
        scoreText = new Text("0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font("Digital-7", 18));
        scoreText.setStyle("-fx-font-weight: bold;");
        
        StackPane scorePanel = new StackPane(scoreText);
        scorePanel.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.9);" +
            "-fx-padding: 5 5 5 5;" +
            "-fx-pref-width: 77;"
        );
        
        VBox scoreSection = new VBox(1);
        scoreSection.getChildren().addAll(scoreLabel, scorePanel);
        scoreSection.setStyle("-fx-alignment: center;");
        
        // --- LEVEL Section ---
        Text levelLabel = new Text("LEVEL");
        levelLabel.setFill(Color.WHITE);
        levelLabel.setFont(Font.font("Arial", 11));
        levelLabel.setStyle("-fx-font-weight: bold;");
        
        levelText = new Text("1");
        levelText.setFill(Color.WHITE);
        levelText.setFont(Font.font("Digital-7", 18));
        levelText.setStyle("-fx-font-weight: bold;");
        
        StackPane levelPanel = new StackPane(levelText);
        levelPanel.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.9);" +
            "-fx-padding: 5 5 5 5;" +
            "-fx-pref-width: 77;"
        );
        
        VBox levelSection = new VBox(1);
        levelSection.getChildren().addAll(levelLabel, levelPanel);
        levelSection.setStyle("-fx-alignment: center;");
        
        // --- LINES Section ---
        Text linesLabel = new Text("LINES");
        linesLabel.setFill(Color.WHITE);
        linesLabel.setFont(Font.font("Arial", 11));
        linesLabel.setStyle("-fx-font-weight: bold;");
        
        linesText = new Text("0");
        linesText.setFill(Color.WHITE);
        linesText.setFont(Font.font("Digital-7", 18));
        linesText.setStyle("-fx-font-weight: bold;");
        
        StackPane linesPanel = new StackPane(linesText);
        linesPanel.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.9);" +
            "-fx-padding: 5 5 5 5;" +
            "-fx-pref-width: 77;"
        );
        
        VBox linesSection = new VBox(1);
        linesSection.getChildren().addAll(linesLabel, linesPanel);
        linesSection.setStyle("-fx-alignment: center;");
        
        // Combine all sections - thin and compact
        statsContainer = new VBox(1);
        statsContainer.getChildren().addAll(scoreSection, levelSection, linesSection);
        statsContainer.setStyle(
            "-fx-alignment: center;" +
            "-fx-background-color: rgba(50, 50, 50, 0.7);" +
            "-fx-border-color: rgba(100, 150, 200, 0.8);" +
            "-fx-border-width: 2;" +
            "-fx-padding: 8;" +
            "-fx-spacing: 4;" +
            "-fx-pref-width: 97;"
        );
        
        // Position below next panel - aligned properly
        statsContainer.setLayoutX(nextBrickContainer.getLayoutX());
        statsContainer.setLayoutY(nextBrickContainer.getLayoutY() + 150);
        
        // Add statistics panel to screen
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(statsContainer);
        
        // Hide all game elements initially (they'll be shown when game mode starts)
        gamePanel.setVisible(false);
        brickPanel.setVisible(false);
        ghostBrickPanel.setVisible(false);
        heldBrickContainer.setVisible(false);
        nextBrickContainer.setVisible(false);
        statsContainer.setVisible(false);

        // Set initial fall speed - higher value = slower fall (600ms per drop at level 1)
        // Timeline will be created and started in updateFallSpeed
        updateFallSpeed(1);
    }

    private Paint getFillColor(int i) {
        Paint returnPaint;
        switch (i) {
            case 0:
                returnPaint = Color.TRANSPARENT;
                break;
            case 1:
                returnPaint = Color.AQUA;
                break;
            case 2:
                returnPaint = Color.BLUEVIOLET;
                break;
            case 3:
                returnPaint = Color.DARKGREEN;
                break;
            case 4:
                returnPaint = Color.YELLOW;
                break;
            case 5:
                returnPaint = Color.RED;
                break;
            case 6:
                returnPaint = Color.BEIGE;
                break;
            case 7:
                returnPaint = Color.BURLYWOOD;
                break;
            default:
                returnPaint = Color.WHITE;
                break;
        }
        return returnPaint;
    }


    /**
     * Refreshes the brick display with updated position and state information.
     * 
     * @param brick the updated brick view data
     */
    public void refreshBrick(ViewData brick) {
        if (isPause.getValue() == Boolean.FALSE) {
            brickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * (BRICK_SIZE + 1));
            brickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getyPosition() * (BRICK_SIZE + 1));
            // Ensure the falling brick stays visible on top
            brickPanel.toFront();
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    setBrickPreviewData(brick.getBrickData()[i][j], rectangles[i][j]);
                }
            }
            
            // Update ghost brick position when the block moves/rotates
            ghostBrickPanel.setLayoutX(gamePanel.getLayoutX() + brick.getxPosition() * (BRICK_SIZE + 1));
            ghostBrickPanel.setLayoutY(-42 + gamePanel.getLayoutY() + brick.getGhostYPosition() * (BRICK_SIZE + 1));
            // Update ghost outline to match current brick shape
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    if (brick.getBrickData()[i][j] != 0) {
                        ghostRectangles[i][j].setStroke(Color.LIGHTGRAY);
                    } else {
                        ghostRectangles[i][j].setStroke(Color.TRANSPARENT);
                    }
                }
            }
            
            // Update held brick display panel with current held piece
            updateHeldBrickDisplay(brick.getHeldBrickData());
            
            // Update next brick preview panel with upcoming piece
            updateNextBrickDisplay(brick.getNextBrickData());
        }
    }

    /**
     * Refreshes the game board background with the updated board state.
     * 
     * @param board the updated board matrix
     */
    public void refreshGameBackground(int[][] board) {
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    // Sets rectangle appearance for game board cells (adds filled gridlines to empty cells)
    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(getFillColor(color));
        
        if (color != 0) {
            // Colored blocks with rounded corners
            rectangle.setArcHeight(9);
            rectangle.setArcWidth(9);
            rectangle.setStroke(Color.rgb(255, 255, 255, 0.3));
            rectangle.setStrokeWidth(0.5);
            rectangle.setStrokeType(StrokeType.INSIDE);
        } else {
            // Empty cells on game board get filled gridline appearance
            rectangle.setFill(Color.rgb(25, 40, 70, 0.4));
            rectangle.setArcHeight(0);
            rectangle.setArcWidth(0);
            rectangle.setStroke(Color.rgb(45, 65, 100, 0.5));
            rectangle.setStrokeWidth(0.5);
            rectangle.setStrokeType(StrokeType.INSIDE);
        }
    }

    // Sets rectangle appearance for brick preview panels (no gridlines on empty cells)
    private void setBrickPreviewData(int color, Rectangle rectangle) {
        if (color != 0) {
            // Colored blocks with rounded corners
            rectangle.setFill(getFillColor(color));
            rectangle.setArcHeight(9);
            rectangle.setArcWidth(9);
            rectangle.setStroke(Color.rgb(255, 255, 255, 0.3));
            rectangle.setStrokeWidth(0.5);
            rectangle.setStrokeType(StrokeType.INSIDE);
        } else {
            // Empty cells in preview panels are transparent (no gridlines)
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setArcHeight(0);
            rectangle.setArcWidth(0);
            rectangle.setStroke(null);
        }
    }

    private void moveDown(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onDownEvent(event);
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    // Handles the hard drop action (instantly drops brick and locks it in place)
    private void hardDrop(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            DownData downData = eventListener.onHardDropEvent(event);
            // Show score notification if rows were cleared
            if (downData.getClearRow() != null && downData.getClearRow().getLinesRemoved() > 0) {
                NotificationPanel notificationPanel = new NotificationPanel("+" + downData.getClearRow().getScoreBonus());
                groupNotification.getChildren().add(notificationPanel);
                notificationPanel.showScore(groupNotification.getChildren());
            }
            refreshBrick(downData.getViewData());
        }
        gamePanel.requestFocus();
    }

    // Handles the hold piece action (stores/swaps current piece with held piece)
    // Triggered when user presses Shift key
    private void holdPiece(MoveEvent event) {
        if (isPause.getValue() == Boolean.FALSE) {
            ViewData viewData = eventListener.onHoldEvent(event);
            refreshBrick(viewData); // Updates display with new brick state
        }
        gamePanel.requestFocus();
    }

    // Updates the held brick display panel
    private void updateHeldBrickDisplay(int[][] heldBrickData) {
        // Clear all rectangles first
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                heldRectangles[i][j].setFill(Color.TRANSPARENT);
                heldRectangles[i][j].setStroke(null);
                heldRectangles[i][j].setArcHeight(0);
                heldRectangles[i][j].setArcWidth(0);
            }
        }
        
        // Render the held brick if one exists
        if (heldBrickData != null) {
            for (int i = 0; i < heldBrickData.length && i < 4; i++) {
                for (int j = 0; j < heldBrickData[i].length && j < 4; j++) {
                    setBrickPreviewData(heldBrickData[i][j], heldRectangles[i][j]);
                }
            }
        }
    }

    // Updates the next brick preview panel with the upcoming piece
    private void updateNextBrickDisplay(int[][] nextBrickData) {
        // Clear all rectangles first (reset to transparent)
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                nextRectangles[i][j].setFill(Color.TRANSPARENT);
                nextRectangles[i][j].setArcHeight(0);
                nextRectangles[i][j].setArcWidth(0);
            }
        }
        
        // Render the next brick in the panel (there's always a next brick)
        if (nextBrickData != null) {
            for (int i = 0; i < nextBrickData.length && i < 4; i++) {
                for (int j = 0; j < nextBrickData[i].length && j < 4; j++) {
                    setBrickPreviewData(nextBrickData[i][j], nextRectangles[i][j]);
                }
            }
        }
    }

    /**
     * Sets the event listener for handling game events.
     * 
     * @param eventListener the listener to handle input events
     */
    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Binds the score display to the game score property for automatic updates.
     * 
     * @param integerProperty the score property to bind to
     */
    public void bindScore(IntegerProperty integerProperty) {
        // Connect score display to game score - updates automatically
        scoreText.textProperty().bind(integerProperty.asString());
    }

    /**
     * Binds the level display to the game level property and sets up automatic fall speed adjustment.
     * 
     * @param integerProperty the level property to bind to
     */
    public void bindLevel(IntegerProperty integerProperty) {
        // Connect level display to game level - updates automatically
        levelText.textProperty().bind(integerProperty.asString());
        // Store level property and listen for changes to update fall speed
        this.levelProperty = integerProperty;
        integerProperty.addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateFallSpeed(newVal.intValue());
            }
        });
    }
    
    // Updates the fall speed based on level (faster as level increases)
    private void updateFallSpeed(int level) {
        // Formula: 600ms - (level - 1) * 100ms, minimum 50ms
        // Level 1: 600ms, Level 2: 500ms, Level 3: 400ms, Level 4: 300ms, etc.
        int fallDuration = Math.max(50, 600 - (level - 1) * 100);
        
        // Stop current timeline if it exists and is playing
        boolean wasPlaying = (timeLine != null && timeLine.getStatus() == javafx.animation.Animation.Status.RUNNING);
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Create new timeline with updated speed
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(fallDuration),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        
        // Resume playing if game is not paused or over
        // This handles both cases: resuming after level change, and initial game start
        if (!isPause.getValue() && !isGameOver.getValue()) {
            timeLine.play();
        }
    }

    /**
     * Binds the lines display to the game lines cleared property for automatic updates.
     * 
     * @param integerProperty the lines property to bind to
     */
    public void bindLines(IntegerProperty integerProperty) {
        // Connect lines display to game lines - updates automatically
        linesText.textProperty().bind(integerProperty.asString());
    }

    /**
     * Handles the game over state by stopping the game and displaying the game over panel.
     * Dims the game board, hides falling bricks, stops music, and shows the game over overlay with final score.
     */
    public void gameOver() {
        timeLine.stop();
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        // Dim the game board to make game over state clear
        if (gameBoard != null) {
            gameBoard.setOpacity(0.3);
        }
        gamePanel.setOpacity(0.3);
        
        // Hide the falling brick and ghost brick completely when game ends
        brickPanel.setVisible(false);
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(false);
        }
        
        // Get final score and display it on game over screen
        try {
            int finalScore = Integer.parseInt(scoreText.getText());
            gameOverPanel.setScore(finalScore);
        } catch (NumberFormatException e) {
            gameOverPanel.setScore(0);
        }
        
        gameOverPanel.setVisible(true);
        gameOverPanel.toFront();
        isGameOver.setValue(Boolean.TRUE);
    }

    /**
     * Starts a new game by resetting the game state and restarting the timeline.
     * Restores UI element visibility and opacity, clears the hold panel, resets game state,
     * and restarts background music.
     * 
     * @param actionEvent the action event that triggered the new game (may be null)
     */
    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        
        // Restore full opacity to game elements that were dimmed
        if (gameBoard != null) {
            gameBoard.setOpacity(1.0);
        }
        gamePanel.setOpacity(1.0);
        
        // Show the falling brick and ghost brick panels again (they were hidden, not dimmed)
        brickPanel.setVisible(true);
        if (ghostBrickPanel != null) {
            ghostBrickPanel.setVisible(true);
        }
        
        // Clear hold panel for new game
        updateHeldBrickDisplay(null);
        
        // Set game state flags before resetting score/level (so listener can start timeline)
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
        
        // Reset game state (also resets score/level/lines to initial values)
        eventListener.createNewGame();
        
        // Explicitly restart the timeline to ensure blocks start falling
        // (needed in case level was already 1, which wouldn't trigger the listener)
        updateFallSpeed(1);
        
        // Restart background music when starting a new game
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
        
        gamePanel.requestFocus();
    }

    
    // Toggle pause state (called when Escape is pressed)
    private void togglePause() {
        if (isPause.getValue() == Boolean.FALSE) {
            // Pause the game
            isPause.setValue(Boolean.TRUE);
            timeLine.pause();
            if (backgroundMusic != null) {
                backgroundMusic.pause();
            }
            // Hide game over panel if visible, show pause menu
            if (isGameOver.getValue() == Boolean.TRUE) {
                gameOverPanel.setVisible(false);
            }
            pauseMenuPanel.setVisible(true);
            pauseMenuPanel.toFront();
        } else {
            // Resume the game
            resumeGame();
        }
    }
    
    // Resume the game from pause
    private void resumeGame() {
        isPause.setValue(Boolean.FALSE);
        pauseMenuPanel.setVisible(false);
        
        // If game is over, show game over panel instead
        if (isGameOver.getValue() == Boolean.TRUE) {
            gameOverPanel.setVisible(true);
            gameOverPanel.toFront();
        } else {
            // Normal resume 
            timeLine.play();
            if (backgroundMusic != null) {
                backgroundMusic.play();
            }
        }
        gamePanel.requestFocus();
    }
    
    /**
     * Shows the main menu by hiding game elements and displaying the menu panel.
     */
    public void showMainMenu() {
        // Stop the game if running
        if (timeLine != null) {
            timeLine.stop();
        }
        
        // Stop background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        
        // Hide all game-related panels
        pauseMenuPanel.setVisible(false);
        gameOverPanel.setVisible(false);
        
        // Hide game elements if initialized
        if (isGameInitialized) {
            setGameElementsVisible(false);
        }
        
        // Set background to default dark (no gradient) - both scene and root pane
        if (scene != null) {
            scene.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        }
        setRootPaneStyle("-fx-background-color: #1e1e1e;");
        
        // Show main menu
        mainMenuPanel.setVisible(true);
        mainMenuPanel.toFront();
        
        // Reset game state
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }
    
    // Return to main menu from pause
    private void returnToMainMenu() {
        showMainMenu();
    }
    
    // Start the game
    private void startGame() {
        // Hide main menu
        mainMenuPanel.setVisible(false);
        
        // Restore gradient background when starting game - both scene and root pane
        // Scene gets solid dark background, root pane gets gradient
        if (scene != null) {
            scene.setFill(javafx.scene.paint.Color.rgb(8, 25, 24)); // Dark color matching gradient bottom
        }
        setRootPaneStyle("-fx-background-color: linear-gradient(to bottom, #2d6a65 0%, #081918 100%);");
        
        // Initialize game if first time
        if (!isGameInitialized) {
            ((com.comp2042.controller.GameController) eventListener).initializeGame();
        }
        
        // Show all game elements
        if (isGameInitialized) {
            setGameElementsVisible(true);
        }
        
        // Start background music
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
        
        // Start new game
        newGame(null);
    }

    // Shows or hides all game UI elements
    private void setGameElementsVisible(boolean visible) {
        gamePanel.setVisible(visible);
        gamePanel.setManaged(visible);
        brickPanel.setVisible(visible);
        brickPanel.setManaged(visible);
        ghostBrickPanel.setVisible(visible);
        heldBrickContainer.setVisible(visible);
        nextBrickContainer.setVisible(visible);
        groupNotification.setVisible(visible);
        groupNotification.setManaged(visible);
        
        if (statsContainer != null) {
            statsContainer.setVisible(visible);
        }
        if (gameBoard != null) {
            gameBoard.setVisible(visible);
            gameBoard.setManaged(visible);
        }
    }

    // Sets the background style of the root pane
    private void setRootPaneStyle(String style) {
        if (rootPane != null) {
            rootPane.setStyle(style);
        }
    }
}

