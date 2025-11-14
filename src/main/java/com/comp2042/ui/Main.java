package com.comp2042.ui;

import com.comp2042.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main application class for the Tetris game.
 * Initializes and launches the JavaFX application window.
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application and sets up the main game window.
     * 
     * @param primaryStage the primary stage for the application
     * @throws Exception if there is an error loading the FXML layout
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent root = fxmlLoader.load();
        GuiController controller = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, 400, 500); //resize window
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        new GameController(controller);
        controller.showMainMenu();
    }

    /**
     * Main entry point for the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}

