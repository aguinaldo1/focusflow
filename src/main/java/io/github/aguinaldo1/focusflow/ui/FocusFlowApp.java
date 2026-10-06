package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.desktop.SystemTrayIntegration;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroClock;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.AWTException;
import java.time.Duration;

public final class FocusFlowApp extends Application {

    private PomodoroTimer timer;
    private PomodoroClock clock;
    private Timeline uiRefreshTimeline;
    private SystemTrayIntegration systemTrayIntegration;

    private Label phaseLabel;
    private Label timeLabel;
    private Label statusLabel;
    private Label cyclesLabel;
    private Label feedbackLabel;

    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button resetButton;

    @Override
    public void start(Stage stage) {

        PomodoroSession session =
                new PomodoroSession();

        timer =
                new PomodoroTimer(session);

        clock =
                new PomodoroClock(timer);

        systemTrayIntegration =
                new SystemTrayIntegration();

        Label titleLabel =
                new Label("FOCUSFLOW");

        titleLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
        );

        phaseLabel =
                new Label();

        phaseLabel.setStyle(
                "-fx-font-size: 12px;"
        );

        timeLabel =
                new Label();

        timeLabel.setStyle(
                "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );

        statusLabel =
                new Label();

        statusLabel.setStyle(
                "-fx-font-size: 11px;"
        );

        cyclesLabel =
                new Label();

        cyclesLabel.setStyle(
                "-fx-font-size: 11px;"
        );

        feedbackLabel =
                new Label();

        feedbackLabel.setStyle(
                "-fx-font-size: 10px;"
        );

        Label shortcutsLabel =
                new Label(
                        "Ctrl+I iniciar  •  Ctrl+P pausar  •  "
                                + "Ctrl+C continuar  •  Ctrl+R resetar"
                );

        shortcutsLabel.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-opacity: 0.65;"
        );

        startButton =
                new Button("Iniciar");

        pauseButton =
                new Button("Pausar");

        resumeButton =
                new Button("Continuar");

        resetButton =
                new Button("Resetar");

        startButton.setOnAction(event ->
                executeAction(
                        timer::start,
                        "Intervalo iniciado."
                )
        );

        pauseButton.setOnAction(event ->
                executeAction(
                        timer::pause,
                        "Intervalo pausado."
                )
        );

        resumeButton.setOnAction(event ->
                executeAction(
                        timer::resume,
                        "Intervalo retomado."
                )
        );

        resetButton.setOnAction(event ->
                executeAction(
                        timer::reset,
                        "Pomodoro reiniciado."
                )
        );

        HBox controls = new HBox(
                6,
                startButton,
                pauseButton,
                resumeButton,
                resetButton
        );

        controls.setAlignment(Pos.CENTER);

        VBox root = new VBox(
                6,
                titleLabel,
                phaseLabel,
                timeLabel,
                statusLabel,
                cyclesLabel,
                controls,
                feedbackLabel,
                shortcutsLabel
        );

        root.setAlignment(Pos.CENTER);

        root.setPadding(
                new Insets(14)
        );

        Scene scene = new Scene(
                root,
                390,
                245
        );

        configureKeyboardShortcuts(scene);

        refreshView();
        startUiRefresh();
        clock.start();

        stage.setTitle("FocusFlow");
        stage.setAlwaysOnTop(true);
        stage.setResizable(false);
        stage.setScene(scene);

        configureSystemTray(stage);

        stage.show();
    }

    private void configureSystemTray(
            Stage stage
    ) {

        try {

            boolean installed =
                    systemTrayIntegration.install(
                            () -> Platform.runLater(
                                    () -> restoreWindow(stage)
                            ),
                            () -> Platform.runLater(
                                    Platform::exit
                            )
                    );

            if (installed) {

                Platform.setImplicitExit(false);

                stage.setOnCloseRequest(event -> {
                    event.consume();
                    stage.hide();
                });
            }

        } catch (AWTException exception) {

            feedbackLabel.setText(
                    "System Tray indisponível."
            );
        }
    }

    private void restoreWindow(
            Stage stage
    ) {

        if (!stage.isShowing()) {
            stage.show();
        }

        stage.setIconified(false);
        stage.toFront();
        stage.requestFocus();
    }

    private void configureKeyboardShortcuts(
            Scene scene
    ) {

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.I,
                        KeyCombination.CONTROL_DOWN
                ),
                () -> executeAction(
                        timer::start,
                        "Intervalo iniciado."
                )
        );

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.P,
                        KeyCombination.CONTROL_DOWN
                ),
                () -> executeAction(
                        timer::pause,
                        "Intervalo pausado."
                )
        );

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.C,
                        KeyCombination.CONTROL_DOWN
                ),
                () -> executeAction(
                        timer::resume,
                        "Intervalo retomado."
                )
        );

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.R,
                        KeyCombination.CONTROL_DOWN
                ),
                () -> executeAction(
                        timer::reset,
                        "Pomodoro reiniciado."
                )
        );
    }

    private void startUiRefresh() {

        uiRefreshTimeline =
                new Timeline(
                        new KeyFrame(
                                javafx.util.Duration.millis(200),
                                event -> refreshView()
                        )
                );

        uiRefreshTimeline.setCycleCount(
                Animation.INDEFINITE
        );

        uiRefreshTimeline.play();
    }

    private void executeAction(
            Runnable action,
            String successMessage
    ) {

        try {

            action.run();

            feedbackLabel.setText(
                    successMessage
            );

        } catch (IllegalStateException exception) {

            feedbackLabel.setText(
                    "Operação não permitida: "
                            + exception.getMessage()
            );
        }

        refreshView();
    }

    private void refreshView() {

        PomodoroSnapshot snapshot =
                timer.snapshot();

        phaseLabel.setText(
                snapshot.phase().name()
        );

        timeLabel.setText(
                formatDuration(
                        snapshot.remainingTime()
                )
        );

        statusLabel.setText(
                snapshot.status().name()
        );

        cyclesLabel.setText(
                "Ciclos: "
                        + snapshot.completedFocusCycles()
        );

        updateButtons(
                snapshot.status()
        );
    }

    private void updateButtons(
            PomodoroStatus status
    ) {

        startButton.setDisable(
                status != PomodoroStatus.IDLE
        );

        pauseButton.setDisable(
                status != PomodoroStatus.RUNNING
        );

        resumeButton.setDisable(
                status != PomodoroStatus.PAUSED
        );
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

    @Override
    public void stop() {

        if (uiRefreshTimeline != null) {
            uiRefreshTimeline.stop();
        }

        if (clock != null) {
            clock.close();
        }

        if (systemTrayIntegration != null) {
            systemTrayIntegration.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
