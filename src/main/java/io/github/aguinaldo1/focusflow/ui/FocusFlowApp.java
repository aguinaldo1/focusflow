package io.github.aguinaldo1.focusflow.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public final class FocusFlowApp extends Application {

    @Override
    public void start(Stage stage) {

        Label title = new Label("FocusFlow");
        Label message = new Label("Fundação JavaFX ativa");

        VBox root = new VBox(
                12,
                title,
                message
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(
                root,
                360,
                220
        );

        stage.setTitle("FocusFlow");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
