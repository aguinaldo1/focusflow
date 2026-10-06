package io.github.aguinaldo1.focusflow.ui;

import javafx.scene.control.Label;
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
                Color.web(
                        "#E2E8F0"
                )
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
                Color.web(
                        "#4F46E5"
                )
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

        percentageLabel =
                new Label(
                        "0%"
                );

        percentageLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
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
