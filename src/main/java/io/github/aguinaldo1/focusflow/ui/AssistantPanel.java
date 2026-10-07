package io.github.aguinaldo1.focusflow.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.util.Objects;

public final class AssistantPanel extends HBox {

    private static final String DEFAULT_MESSAGE =
            "Tudo pronto. Vamos focar?";

    private final Label messageLabel;

    public AssistantPanel() {

        super(12);

        getStyleClass().addAll(
                "hud-panel",
                "assistant-panel"
        );

        StackPane assistantCore =
                createAssistantCore();

        Label identityLabel =
                new Label(
                        "FOCUSFLOW"
                );

        identityLabel
                .getStyleClass()
                .add(
                        "assistant-identity"
                );

        messageLabel =
                new Label();

        messageLabel.setWrapText(
                true
        );

        messageLabel.setMaxWidth(
                250
        );

        messageLabel
                .getStyleClass()
                .add(
                        "assistant-message"
                );

        VBox messageArea =
                new VBox(
                        2,
                        identityLabel,
                        messageLabel
                );

        messageArea.setAlignment(
                Pos.CENTER_LEFT
        );

        getChildren().addAll(
                assistantCore,
                messageArea
        );

        setAlignment(
                Pos.CENTER_LEFT
        );

        setPrefWidth(
                360
        );

        setMinHeight(
                68
        );

        setMaxWidth(
                360
        );

        showDefaultMessage();
    }

    public void showDefaultMessage() {

        setMessage(
                DEFAULT_MESSAGE
        );
    }

    public void setMessage(
            String message
    ) {

        Objects.requireNonNull(
                message,
                "message"
        );

        String normalized =
                message.trim();

        if (normalized.isEmpty()) {

            throw new IllegalArgumentException(
                    "Assistant message cannot be blank."
            );
        }

        messageLabel.setText(
                normalized
        );
    }

    private static StackPane createAssistantCore() {

        Circle outerRing =
                new Circle(
                        25
                );

        outerRing
                .getStyleClass()
                .add(
                        "assistant-core-outer"
                );

        Circle innerRing =
                new Circle(
                        17
                );

        innerRing
                .getStyleClass()
                .add(
                        "assistant-core-inner"
                );

        Circle energyCore =
                new Circle(
                        7
                );

        energyCore
                .getStyleClass()
                .add(
                        "assistant-core-energy"
                );

        StackPane core =
                new StackPane(
                        outerRing,
                        innerRing,
                        energyCore
                );

        core.setMinSize(
                56,
                56
        );

        core.setPrefSize(
                56,
                56
        );

        core.setMaxSize(
                56,
                56
        );

        core.setMouseTransparent(
                true
        );

        return core;
    }
}
