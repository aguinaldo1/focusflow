package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

import java.time.Duration;
import java.util.Objects;

public final class TimerHudDial extends StackPane {

    private static final double OUTER_RADIUS =
            67.0;

    private static final double SEGMENT_RADIUS =
            62.0;

    private static final double SESSION_PROGRESS_RADIUS =
            59.0;

    private static final double INNER_RADIUS =
            55.0;

    private static final double SIZE =
            146.0;

    private static final double CENTER =
            SIZE / 2.0;

    private static final int MARKER_COUNT =
            24;

    private static final double SESSION_PROGRESS_CIRCUMFERENCE =
            2.0
                    * Math.PI
                    * SESSION_PROGRESS_RADIUS;

    private final Circle sessionProgressRing;

    public TimerHudDial() {

        getStyleClass().add(
                "timer-hud-dial"
        );

        Circle outerRing =
                createRing(
                        OUTER_RADIUS,
                        "timer-hud-outer-ring"
                );

        Circle segmentedRing =
                createRing(
                        SEGMENT_RADIUS,
                        "timer-hud-segment-ring"
                );

        sessionProgressRing =
                createRing(
                        SESSION_PROGRESS_RADIUS,
                        "timer-hud-session-progress"
                );

        sessionProgressRing.setStrokeLineCap(
                StrokeLineCap.ROUND
        );

        sessionProgressRing
                .getStrokeDashArray()
                .setAll(
                        SESSION_PROGRESS_CIRCUMFERENCE,
                        SESSION_PROGRESS_CIRCUMFERENCE
                );

        sessionProgressRing.setStrokeDashOffset(
                SESSION_PROGRESS_CIRCUMFERENCE
        );

        sessionProgressRing.setRotate(
                -90
        );

        Circle innerRing =
                createRing(
                        INNER_RADIUS,
                        "timer-hud-inner-ring"
                );

        Pane markerLayer =
                createMarkerLayer();

        getChildren().addAll(
                outerRing,
                segmentedRing,
                sessionProgressRing,
                innerRing,
                markerLayer
        );

        setMinSize(
                SIZE,
                SIZE
        );

        setPrefSize(
                SIZE,
                SIZE
        );

        setMaxSize(
                SIZE,
                SIZE
        );

        setMouseTransparent(
                true
        );

        showEmptyState();
    }

    public void showEmptyState() {

        setSessionProgress(
                0.0
        );

        updateProgressAppearance(
                null
        );
    }

    public void updateSessionProgress(
            Duration remainingTime,
            Duration intervalDuration,
            PomodoroPhase phase
    ) {

        Objects.requireNonNull(
                remainingTime,
                "remainingTime"
        );

        Objects.requireNonNull(
                intervalDuration,
                "intervalDuration"
        );

        Objects.requireNonNull(
                phase,
                "phase"
        );

        long totalMillis =
                intervalDuration.toMillis();

        if (totalMillis <= 0) {

            throw new IllegalArgumentException(
                    "Interval duration must be positive."
            );
        }

        long remainingMillis =
                Math.max(
                        0,
                        Math.min(
                                totalMillis,
                                remainingTime.toMillis()
                        )
                );

        double elapsedMillis =
                totalMillis
                        - remainingMillis;

        double progress =
                elapsedMillis
                        / totalMillis;

        setSessionProgress(
                progress
        );

        updateProgressAppearance(
                phase
        );
    }

    private void setSessionProgress(
            double progress
    ) {

        double normalizedProgress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                );

        double offset =
                SESSION_PROGRESS_CIRCUMFERENCE
                        * (
                        1.0
                                - normalizedProgress
                );

        sessionProgressRing.setStrokeDashOffset(
                offset
        );
    }

    private void updateProgressAppearance(
            PomodoroPhase phase
    ) {

        sessionProgressRing
                .getStyleClass()
                .removeAll(
                        "timer-hud-session-progress-focus",
                        "timer-hud-session-progress-break",
                        "timer-hud-session-progress-inactive"
                );

        if (phase == PomodoroPhase.FOCUS) {

            sessionProgressRing
                    .getStyleClass()
                    .add(
                            "timer-hud-session-progress-focus"
                    );

            return;
        }

        if (
                phase == PomodoroPhase.SHORT_BREAK
                        || phase == PomodoroPhase.LONG_BREAK
        ) {

            sessionProgressRing
                    .getStyleClass()
                    .add(
                            "timer-hud-session-progress-break"
                    );

            return;
        }

        sessionProgressRing
                .getStyleClass()
                .add(
                        "timer-hud-session-progress-inactive"
                );
    }

    private static Circle createRing(
            double radius,
            String styleClass
    ) {

        Circle ring =
                new Circle(
                        radius
                );

        ring.getStyleClass().add(
                styleClass
        );

        return ring;
    }

    private static Pane createMarkerLayer() {

        Pane layer =
                new Pane();

        layer.setMinSize(
                SIZE,
                SIZE
        );

        layer.setPrefSize(
                SIZE,
                SIZE
        );

        layer.setMaxSize(
                SIZE,
                SIZE
        );

        layer.setMouseTransparent(
                true
        );

        for (
                int index = 0;
                index < MARKER_COUNT;
                index++
        ) {

            boolean majorMarker =
                    index % 6 == 0;

            double angleDegrees =
                    index
                            * (
                            360.0
                                    / MARKER_COUNT
                    )
                            - 90.0;

            double angleRadians =
                    Math.toRadians(
                            angleDegrees
                    );

            double innerMarkerRadius =
                    majorMarker
                            ? 64.0
                            : 68.0;

            double outerMarkerRadius =
                    majorMarker
                            ? 72.0
                            : 71.0;

            double startX =
                    CENTER
                            + Math.cos(
                            angleRadians
                    )
                            * innerMarkerRadius;

            double startY =
                    CENTER
                            + Math.sin(
                            angleRadians
                    )
                            * innerMarkerRadius;

            double endX =
                    CENTER
                            + Math.cos(
                            angleRadians
                    )
                            * outerMarkerRadius;

            double endY =
                    CENTER
                            + Math.sin(
                            angleRadians
                    )
                            * outerMarkerRadius;

            Line marker =
                    new Line(
                            startX,
                            startY,
                            endX,
                            endY
                    );

            marker
                    .getStyleClass()
                    .add(
                            majorMarker
                                    ? "timer-hud-major-marker"
                                    : "timer-hud-minor-marker"
                    );

            layer
                    .getChildren()
                    .add(
                            marker
                    );
        }

        return layer;
    }
}
