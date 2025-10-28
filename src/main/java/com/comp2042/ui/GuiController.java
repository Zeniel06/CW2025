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
import javafx.scene.control.Button;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GameOverPanel gameOverPanel;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;

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

    // Scoreboard - displays the current game score
    private Text scoreText;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(getClass().getClassLoader().getResource("digital.ttf").toExternalForm(), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (isPause.getValue() == Boolean.FALSE && isGameOver.getValue() == Boolean.FALSE) {
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
                if (keyEvent.getCode() == KeyCode.N) {
                    newGame(null);
                }
            }
        });
        gameOverPanel.setVisible(false);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
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
        heldBrickPanel.setStyle("-fx-background-color: rgba(50, 50, 50, 0.7); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 2; -fx-padding: 5;");
        
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
        
        // Position to the right of game panel (gamePanel width ≈ 10 cells * 21px = 210px)
        heldBrickContainer.setLayoutX(gamePanel.getLayoutX() + 220);
        heldBrickContainer.setLayoutY(gamePanel.getLayoutY() + 10);
        
        // Add to parent pane (makes it visible on screen)
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(heldBrickContainer);

        // === NEXT PIECE PREVIEW - Initialize UI panel to display upcoming piece ===
        nextBrickPanel = new GridPane();
        nextBrickPanel.setVgap(1);
        nextBrickPanel.setHgap(1);
        // Style: same as hold panel for consistency
        nextBrickPanel.setStyle("-fx-background-color: rgba(50, 50, 50, 0.7); -fx-border-color: rgba(100, 150, 200, 0.8); -fx-border-width: 2; -fx-padding: 5;");
        
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

        // === RESTART BUTTON - Add restart button with icon below next panel ===
        Button restartButton = createRestartButton();
        restartButton.setLayoutX(nextBrickContainer.getLayoutX() + 20);
        restartButton.setLayoutY(nextBrickContainer.getLayoutY() + 150); // Position below next panel
        restartButton.setOnAction(e -> newGame(null));
        
        // Add restart button to parent pane
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(restartButton);

        // === SCOREBOARD - Display score below restart button ===
        VBox scoreContainer = new VBox(5);
        
        // "SCORE" label
        Text scoreLabel = new Text("SCORE");
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setFont(Font.font("Arial", 14));
        scoreLabel.setStyle("-fx-font-weight: bold;");
        
        // Score number display (starts at 0)
        scoreText = new Text("0");
        scoreText.setFill(Color.rgb(255, 215, 0)); // Gold color
        scoreText.setFont(Font.font("Digital-7", 32));
        scoreText.setStyle("-fx-font-weight: bold;");
        
        // Panel container for score number
        StackPane scorePanel = new StackPane(scoreText);
        scorePanel.setStyle(
            "-fx-background-color: rgba(50, 50, 50, 0.7);" +
            "-fx-border-color: rgba(100, 150, 200, 0.8);" +
            "-fx-border-width: 2;" +
            "-fx-padding: 15;" +
            "-fx-min-width: 100;"
        );
        
        // Combine label and panel, position below restart button
        scoreContainer.getChildren().addAll(scoreLabel, scorePanel);
        scoreContainer.setStyle("-fx-alignment: center;");
        scoreContainer.setLayoutX(nextBrickContainer.getLayoutX());
        scoreContainer.setLayoutY(restartButton.getLayoutY() + 80);
        
        // Add scoreboard to screen
        ((javafx.scene.layout.Pane) gamePanel.getParent().getParent()).getChildren().add(scoreContainer);

        // Set fall speed - higher value = slower fall (600ms per drop)
        timeLine = new Timeline(new KeyFrame(
                Duration.millis(600),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))
        ));
        timeLine.setCycleCount(Timeline.INDEFINITE);
        timeLine.play();
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


    private void refreshBrick(ViewData brick) {
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

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(IntegerProperty integerProperty) {
        // Connect score display to game score - updates automatically
        scoreText.textProperty().bind(integerProperty.asString());
    }

    // Creates a restart button with a circular arrow icon in a circular container
    private Button createRestartButton() {
        Button button = new Button();
        
        // Create circular arrow icon using SVG-like path (larger scale)
        Path arrow = new Path();
        
        // Draw circular arrow path (clockwise arrow) - scaled up
        MoveTo moveTo = new MoveTo(15, 3);
        ArcTo arcTo = new ArcTo();
        arcTo.setX(27);
        arcTo.setY(15);
        arcTo.setRadiusX(12);
        arcTo.setRadiusY(12);
        arcTo.setSweepFlag(true);
        arcTo.setLargeArcFlag(true);
        
        // Arrow head
        LineTo line1 = new LineTo(27, 6);
        MoveTo move2 = new MoveTo(27, 15);
        LineTo line2 = new LineTo(33, 15);
        
        arrow.getElements().addAll(moveTo, arcTo, line1, move2, line2);
        arrow.setStroke(Color.rgb(200, 230, 255));
        arrow.setStrokeWidth(3.5);
        arrow.setStrokeLineCap(StrokeLineCap.ROUND);
        arrow.setStrokeLineJoin(StrokeLineJoin.ROUND);
        arrow.setFill(Color.TRANSPARENT);
        
        // Create a container for the icon
        StackPane iconContainer = new StackPane(arrow);
        iconContainer.setPrefSize(50, 50);
        
        button.setGraphic(iconContainer);
        button.setPrefSize(60, 60);
        button.setStyle(
            "-fx-background-color: rgba(45, 85, 90, 0.6);" +
            "-fx-border-color: rgba(100, 200, 220, 0.5);" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 30;" +
            "-fx-background-radius: 30;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0.3, 0, 2);"
        );
        
        // Hover effect - brighten and make more opaque
        button.setOnMouseEntered(e -> {
            arrow.setStroke(Color.rgb(220, 250, 255));
            button.setStyle(
                "-fx-background-color: rgba(60, 110, 120, 0.8);" +
                "-fx-border-color: rgba(120, 220, 240, 0.8);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-background-radius: 30;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.5), 6, 0.4, 0, 3);"
            );
        });
        
        button.setOnMouseExited(e -> {
            arrow.setStroke(Color.rgb(200, 230, 255));
            button.setStyle(
                "-fx-background-color: rgba(45, 85, 90, 0.6);" +
                "-fx-border-color: rgba(100, 200, 220, 0.5);" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 30;" +
                "-fx-background-radius: 30;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 4, 0.3, 0, 2);"
            );
        });
        
        return button;
    }

    public void gameOver() {
        timeLine.stop();
        gameOverPanel.setVisible(true);
        isGameOver.setValue(Boolean.TRUE);
    }

    public void newGame(ActionEvent actionEvent) {
        timeLine.stop();
        gameOverPanel.setVisible(false);
        
        // Clear hold panel for new game
        updateHeldBrickDisplay(null);
        
        // Reset game state (also resets score to 0)
        eventListener.createNewGame();
        gamePanel.requestFocus();
        timeLine.play();
        isPause.setValue(Boolean.FALSE);
        isGameOver.setValue(Boolean.FALSE);
    }

    public void pauseGame(ActionEvent actionEvent) {
        gamePanel.requestFocus();
    }
}

