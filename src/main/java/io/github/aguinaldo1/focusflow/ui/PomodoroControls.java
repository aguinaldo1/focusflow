package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Objects;

public final class PomodoroControls extends HBox {

    private final Button startButton;
    private final Button pauseButton;
    private final Button resumeButton;
    private final Button restartButton;

    public PomodoroControls(
            Runnable onStart,
            Runnable onPause,
            Runnable onResume,
            Runnable onRestart
    ) {

        super(6);

        getStyleClass().add(
                "hud-controls"
        );

        Objects.requireNonNull(
                onStart,
                "onStart"
        );

        Objects.requireNonNull(
                onPause,
                "onPause"
        );

        Objects.requireNonNull(
                onResume,
                "onResume"
        );

        Objects.requireNonNull(
                onRestart,
                "onRestart"
        );

        startButton =
                new Button(
                        "Iniciar"
                );

        pauseButton =
                new Button(
                        "Pausar"
                );

        resumeButton =
                new Button(
                        "Continuar"
                );

        restartButton =
                new Button(
                        "Reiniciar"
                );

        startButton
                .getStyleClass()
                .add(
                        "hud-success-button"
                );

        pauseButton
                .getStyleClass()
                .add(
                        "hud-warning-button"
                );

        resumeButton
                .getStyleClass()
                .add(
                        "hud-primary-button"
                );

        restartButton
                .getStyleClass()
                .add(
                        "hud-secondary-button"
                );

        startButton.setOnAction(
                event ->
                        onStart.run()
        );

        pauseButton.setOnAction(
                event ->
                        onPause.run()
        );

        resumeButton.setOnAction(
                event ->
                        onResume.run()
        );

        restartButton.setOnAction(
                event ->
                        onRestart.run()
        );

        setAlignment(
                Pos.CENTER
        );

        getChildren().addAll(
                startButton,
                pauseButton,
                resumeButton,
                restartButton
        );

        showWithoutObjective();
    }

    public void showWithoutObjective() {

        startButton.setDisable(
                true
        );

        pauseButton.setDisable(
                true
        );

        resumeButton.setDisable(
                true
        );

        restartButton.setDisable(
                true
        );
    }

    public void update(
            PomodoroStatus status
    ) {

        Objects.requireNonNull(
                status,
                "status"
        );

        startButton.setDisable(
                status != PomodoroStatus.IDLE
        );

        pauseButton.setDisable(
                status != PomodoroStatus.RUNNING
        );

        resumeButton.setDisable(
                status != PomodoroStatus.PAUSED
        );

        restartButton.setDisable(
                false
        );
    }
}
