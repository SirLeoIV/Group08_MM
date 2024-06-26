package com.sailing.gui;

import com.sailing.math.data_structures.Vector2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.List;

import static com.sailing.gui.Sailing.HEIGHT;
import static com.sailing.gui.Sailing.WIDTH;

public class WinningScreen extends Pane {

    WinningScreen(String message, List<Vector2D> targetPositions) {

        Text text = new Text(message);
        text.setTranslateX(WIDTH / 2 - 200);
        text.setTranslateY(HEIGHT / 2 - 190);
        text.setStyle("-fx-font-size: 40px;");
        getChildren().add(text);

        Button retry = new Button("Retry!");
        retry.setPrefWidth(400);
        retry.setPrefHeight(70);
        retry.setTranslateX(WIDTH / 2 - 200);
        retry.setTranslateY(HEIGHT / 2 - 100);
        retry.setOnAction(e -> {
            System.out.println("retry");
            Sailing.window.getScene().setRoot(new Parkour(targetPositions));
            Sailing.window.getScene().getRoot().requestFocus();
        });

        Button parkour = new Button("Back to Menu");
        parkour.setPrefWidth(400);
        parkour.setPrefHeight(70);
        parkour.setTranslateX(WIDTH / 2 - 200);
        parkour.setTranslateY(HEIGHT / 2 );
        parkour.setOnAction(e -> {
            System.out.println("Menu");
            Sailing.window.getScene().setRoot(new Menu());
            Sailing.window.getScene().getRoot().requestFocus();
        });

        getChildren().addAll(retry, parkour);

    }

}
