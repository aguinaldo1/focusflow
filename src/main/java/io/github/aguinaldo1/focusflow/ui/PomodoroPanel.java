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

    private final Label phaseLabel;
    private final Label timeLabel;
    private final Label statusLabel;
    private final Label cyclesLabel;
    private final ProgressDonut progressDonut;

    public PomodoroPanel() {

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
                        + "-fx-opacity: 0.75;"
        );

        cyclesLabel =
                new Label(
                        "Ciclos concluídos: 0"
                );

        cyclesLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-opacity: 0.85;"
        );

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
                timerInformation,
                progressDonut
        );

        setPrefSize(
                360,
                112
        );

        setMinHeight(
                112
        );

        setMaxWidth(
                360
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
                        3,
                        0,
                        0,
                        20
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
                    "#0F766E";

            phaseColor =
                    "#0F766E";

        } else if (
                phase == PomodoroPhase.FOCUS
        ) {

            timerColor =
                    "#0F172A";

            phaseColor =
                    "#475569";

        } else {

            timerColor =
                    "#64748B";

            phaseColor =
                    "#64748B";
        }

        timeLabel.setStyle(
                "-fx-font-size: 50px;"
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
