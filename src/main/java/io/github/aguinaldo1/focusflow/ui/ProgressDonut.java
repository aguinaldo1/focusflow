package io.github.aguinaldo1.focusflow.ui;

import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;

public final class ProgressDonut extends StackPane {

    private static final double RADIUS =
            27.0;

    private static final double CIRCUMFERENCE =
            2.0
                    * Math.PI
                    * RADIUS;

    private static final Color BACKGROUND_RING_COLOR =
            Color.web(
                    "#123247"
            );

    private static final Color PROGRESS_RING_COLOR =
            Color.web(
                    "#00D9FF"
            );

    private final Label percentageLabel;
    private final Circle progressRing;

    public ProgressDonut() {

        Circle backgroundRing =
                new Circle(
                        RADIUS
                );

        backgroundRing.setFill(
                Color.TRANSPARENT
        );

        backgroundRing.setStroke(
                BACKGROUND_RING_COLOR
        );

        backgroundRing.setStrokeWidth(
                5
        );

        progressRing =
                new Circle(
                        RADIUS
                );

        progressRing.setFill(
                Color.TRANSPARENT
        );

        progressRing.setStroke(
                PROGRESS_RING_COLOR
        );

        progressRing.setStrokeWidth(
                5
        );

        progressRing.setStrokeLineCap(
                StrokeLineCap.ROUND
        );

        progressRing
                .getStrokeDashArray()
                .setAll(
                        CIRCUMFERENCE,
                        CIRCUMFERENCE
                );

        progressRing.setStrokeDashOffset(
                CIRCUMFERENCE
        );

        progressRing.setRotate(
                -90
        );

        DropShadow glow =
                new DropShadow();

        glow.setColor(
                Color.web(
                        "#00D9FF",
                        0.42
                )
        );

        glow.setRadius(
                6
        );

        glow.setSpread(
                0.12
        );

        progressRing.setEffect(
                glow
        );

        percentageLabel =
                new Label(
                        "0%"
                );

        percentageLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #EAFBFF;"
        );

        getChildren().addAll(
                backgroundRing,
                progressRing,
                percentageLabel
        );

        setMinSize(
                66,
                66
        );

        setPrefSize(
                66,
                66
        );

        setMaxSize(
                66,
                66
        );

        setTranslateY(
                -12
        );
    }

    public void setProgressPercentage(
            int percentage
    ) {

        int normalizedPercentage =
                Math.max(
                        0,
                        Math.min(
                                100,
                                percentage
                        )
                );

        percentageLabel.setText(
                normalizedPercentage
                        + "%"
        );

        double offset =
                CIRCUMFERENCE
                        * (
                        1.0
                                - normalizedPercentage
                                / 100.0
                );

        progressRing.setStrokeDashOffset(
                offset
        );
    }
}
