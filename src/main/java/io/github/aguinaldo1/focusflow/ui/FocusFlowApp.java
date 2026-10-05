package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Duration;

public final class FocusFlowApp extends Application {

    @Override
    public void start(Stage stage) {

        PomodoroSession session =
                new PomodoroSession();

        PomodoroTimer timer =
                new PomodoroTimer(session);

        PomodoroSnapshot snapshot =
                timer.snapshot();

        Label titleLabel =
                new Label("FocusFlow");

        Label phaseLabel =
                new Label(snapshot.phase().name());

        Label timeLabel =
                new Label(
                        formatDuration(
                                snapshot.remainingTime()
                        )
                );

        Label statusLabel =
                new Label(snapshot.status().name());

        Label cyclesLabel =
                new Label(
                        "Ciclos concluídos: "
                                + snapshot.completedFocusCycles()
                );

        VBox root = new VBox(
                12,
                titleLabel,
                phaseLabel,
                timeLabel,
                statusLabel,
                cyclesLabel
        );

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(24));

        Scene scene = new Scene(
                root,
                360,
                260
        );

        stage.setTitle("FocusFlow");
        stage.setScene(scene);
        stage.show();
    }

    private static String formatDuration(
            Duration duration
    ) {

        long totalSeconds =
                duration.toSeconds();

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
