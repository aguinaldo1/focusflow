package io.github.aguinaldo1.focusflow.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

public final class TimerHudDial extends StackPane {

    private static final double OUTER_RADIUS =
            67.0;

    private static final double SEGMENT_RADIUS =
            62.0;

    private static final double INNER_RADIUS =
            55.0;

    private static final double SIZE =
            146.0;

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

        getChildren().addAll(
                outerRing,
                segmentedRing,
                innerRing
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
}
