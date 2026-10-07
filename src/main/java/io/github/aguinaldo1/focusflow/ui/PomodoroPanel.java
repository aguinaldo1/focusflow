package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.util.Objects;

public final class PomodoroPanel extends StackPane {

    private static final String FOCUS_TIMER_COLOR =
            "#EAFBFF";

    private static final String FOCUS_PHASE_COLOR =
            "#00D9FF";

    private static final String BREAK_TIMER_COLOR =
            "#B8FFF0";

    private static final String BREAK_PHASE_COLOR =
            "#23E6B1";

    private static final String INACTIVE_COLOR =
            "#7797AA";

    private final Label phaseLabel;
    private final Label timeLabel;
    private final Label statusLabel;
    private final Label cyclesLabel;

    private final ProgressDonut progressDonut;
    private final TimerHudDial timerHudDial;

    public PomodoroPanel() {

        getStyleClass().addAll(
                "hud-panel",
                "hud-pomodoro-panel"
        );

        phaseLabel =
                new Label("-");

        timeLabel =
                new Label("--:--");

        statusLabel =
                new Label(
                        "SEM OBJETIVO"
                );

        statusLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-opacity: 0.78;"
                        + "-fx-text-fill: #8FB7C8;"
        );

        cyclesLabel =
                new Label(
                        "Ciclos concluídos: 0"
                );

        cyclesLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-opacity: 0.90;"
                        + "-fx-text-fill: #A9C8D8;"
        );

        timerHudDial =
                new TimerHudDial();

        progressDonut =
                new ProgressDonut();

        VBox timerInformation =
                new VBox(
                        0,
                        phaseLabel,
                        timeLabel,
                        statusLabel,
                        cyclesLabel
                );

        timerInformation.setAlignment(
                Pos.CENTER
        );

        getChildren().addAll(
                timerHudDial,
                timerInformation,
                progressDonut
        );

        setPrefSize(
                360,
                145
        );

        setMinHeight(
                145
        );

        setMaxWidth(
                360
        );

        StackPane.setAlignment(
                timerHudDial,
                Pos.CENTER
        );

        StackPane.setAlignment(
                timerInformation,
                Pos.CENTER
        );

        StackPane.setAlignment(
                progressDonut,
                Pos.TOP_LEFT
        );

        StackPane.setMargin(
                progressDonut,
                new Insets(
                        4,
                        0,
                        0,
                        16
                )
        );

        showEmptyState();
    }

    public void showEmptyState() {

        phaseLabel.setText(
                "-"
        );

        timeLabel.setText(
                "--:--"
        );

        statusLabel.setText(
                "SEM OBJETIVO"
        );

        cyclesLabel.setText(
                "Ciclos concluídos: 0"
        );

        progressDonut
                .setProgressPercentage(
                        0
                );

        timerHudDial
                .showEmptyState();

        updateAppearance(
                null
        );
    }

    public void update(
            PomodoroSnapshot snapshot,
            int progressPercentage
    ) {

        Objects.requireNonNull(
                snapshot,
                "snapshot"
        );

        phaseLabel.setText(
                phaseDisplayName(
                        snapshot.phase()
                )
        );

        timeLabel.setText(
                formatDuration(
                        snapshot.remainingTime()
                )
        );

        statusLabel.setText(
                snapshot
                        .status()
                        .name()
        );

        cyclesLabel.setText(
                "Ciclos concluídos: "
                        + snapshot.completedFocusCycles()
        );

        progressDonut
                .setProgressPercentage(
                        progressPercentage
                );

        timerHudDial
                .updateSessionProgress(
                        snapshot.remainingTime(),
                        snapshot.intervalDuration(),
                        snapshot.phase()
                );

        updateAppearance(
                snapshot.phase()
        );
    }

    private void updateAppearance(
            PomodoroPhase phase
    ) {

        String timerColor;
        String phaseColor;

        if (
                phase == PomodoroPhase.SHORT_BREAK
                        || phase == PomodoroPhase.LONG_BREAK
        ) {

            timerColor =
                    BREAK_TIMER_COLOR;

            phaseColor =
                    BREAK_PHASE_COLOR;

        } else if (
                phase == PomodoroPhase.FOCUS
        ) {

            timerColor =
                    FOCUS_TIMER_COLOR;

            phaseColor =
                    FOCUS_PHASE_COLOR;

        } else {

            timerColor =
                    INACTIVE_COLOR;

            phaseColor =
                    INACTIVE_COLOR;
        }

        timeLabel.setStyle(
                "-fx-font-size: 46px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + timerColor
                        + ";"
        );

        phaseLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + phaseColor
                        + ";"
        );
    }

    private static String phaseDisplayName(
            PomodoroPhase phase
    ) {

        return switch (phase) {

            case FOCUS ->
                    "FOCO";

            case SHORT_BREAK ->
                    "PAUSA CURTA";

            case LONG_BREAK ->
                    "PAUSA LONGA";
        };
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
}
