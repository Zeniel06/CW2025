package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * UI panel that displays score notifications with animations.
 * Shows score bonuses earned from clearing lines, then fades out and removes itself.
 */
public class NotificationPanel extends BorderPane {

    /**
     * Constructs a new NotificationPanel with the specified text to display.
     * 
     * @param text the text to display in the notification
     */
    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        score.setEffect(new Glow(0.6));
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    /**
     * Displays the score notification with fade and translate animations, then removes it.
     * 
     * @param list the list of nodes to remove this notification from after animation completes
     */
    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(event -> list.remove(this));
        transition.play();
    }
}

