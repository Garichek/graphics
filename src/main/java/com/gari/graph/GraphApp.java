package com.gari.graph;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class GraphApp extends Application {

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        log.info("Start GRAPH");

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/testscene.fxml"));


        Rectangle2D r = Screen.getPrimary().getBounds();
        Scene scene = new Scene(root, r.getWidth(), r.getHeight() - 100);

        stage.setScene(scene);

        stage.setTitle("Telemetry Analyzer");
        stage.show();
    }
}