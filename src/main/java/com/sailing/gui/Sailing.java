package com.sailing.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * This acts as an entry point to the entire application
 */
public class Sailing extends Application {

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Pane());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}