package com.comp2042.ui;

import com.comp2042.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getClassLoader().getResource("gameLayout.fxml");
        ResourceBundle resources = null;
        FXMLLoader fxmlLoader = new FXMLLoader(location, resources);
        Parent root = fxmlLoader.load();
        GuiController c = fxmlLoader.getController();

        primaryStage.setTitle("TetrisJFX");
        // To increase window size
        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Keep consistent aspect ratio
        primaryStage.show();
        new GameController(c);
        
        // Show main menu on startup
        c.showMainMenu();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

