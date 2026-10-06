package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.objective.Objective;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Objects;
import java.util.Optional;

public final class ObjectiveLifecycleDialogs {

    private ObjectiveLifecycleDialogs() {
    }

    public static boolean confirmCompletion(
            Objective objective
    ) {

        Objects.requireNonNull(
                objective,
                "objective"
        );

        return showConfirmation(
                objective,
                "Finalizar objetivo",
                "Finalizar",
                "O objetivo será marcado como concluído, "
                        + "sairá da lista ativa e seu progresso "
                        + "será preservado.",
                "Finalizar",
                true
        );
    }

    public static boolean confirmNotFinished(
            Objective objective
    ) {

        Objects.requireNonNull(
                objective,
                "objective"
        );

        return showConfirmation(
                objective,
                "Objetivo não finalizado",
                "Marcar como não finalizado",
                "O objetivo sairá da lista ativa, mas ficará "
                        + "registrado como não finalizado.",
                "Marcar",
                true
        );
    }

    public static boolean confirmDiscard(
            Objective objective
    ) {

        Objects.requireNonNull(
                objective,
                "objective"
        );

        return showConfirmation(
                objective,
                "Remover objetivo",
                "Remover",
                "O objetivo sairá da lista ativa e ficará "
                        + "marcado como descartado.",
                "Remover",
                false
        );
    }

    private static boolean showConfirmation(
            Objective objective,
            String title,
            String headerAction,
            String content,
            String confirmationButtonText,
            boolean showProgress
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                title
        );

        confirmation.setHeaderText(
                headerAction
                        + " \""
                        + objective.getName()
                        + "\"?"
        );

        String contentText =
                content;

        if (showProgress) {

            contentText +=
                    "\n\nProgresso atual: "
                            + objective.getProgressPercentage()
                            + "%.";
        }

        confirmation.setContentText(
                contentText
        );

        ButtonType confirmationButton =
                new ButtonType(
                        confirmationButtonText
                );

        confirmation
                .getButtonTypes()
                .setAll(
                        confirmationButton,
                        ButtonType.CANCEL
                );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        return result.isPresent()
                && result.get()
                == confirmationButton;
    }
}
