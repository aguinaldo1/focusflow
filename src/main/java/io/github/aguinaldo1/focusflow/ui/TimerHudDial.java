package io.github.aguinaldo1.focusflow.ui;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public final class TimerHudDial extends StackPane {

    private static final double OUTER_RADIUS =
            67.0;

    private static final double SEGMENT_RADIUS =
            62.0;

    private static final double INNER_RADIUS =
            55.0;

    private static final double SIZE =
            146.0;

    private static final double CENTER =
            SIZE / 2.0;

    private static final int MARKER_COUNT =
            24;

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
