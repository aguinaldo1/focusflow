package io.github.aguinaldo1.focusflow.ui;

import java.net.URL;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
        ImageView assistantMascot = createAssistantMascot();

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
                assistantMascot,
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

    private static ImageView createAssistantMascot() {
        ImageView mascot = new ImageView();

        mascot.getStyleClass().add(
                "assistant-mascot-image"
        );

        mascot.setFitWidth(72);
        mascot.setFitHeight(72);
        mascot.setPreserveRatio(true);
        mascot.setSmooth(true);
        mascot.setMouseTransparent(true);

        URL resource = AssistantPanel.class.getResource(
                "/assets/assistant/bust/"
                + "focusflow-assistant-bust.png"
        );

        if (resource == null) {
            System.err.println(
                    "FocusFlow: asset do assistente "
                    + "não encontrado."
            );
            return mascot;
        }

        mascot.setImage(
                new Image(resource.toExternalForm())
        );

        return mascot;
    }
}
