package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class HistoryWindow {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter
                    .ofPattern(
                            "dd/MM/yyyy HH:mm"
                    )
                    .withZone(
                            ZoneId.systemDefault()
                    );

    private HistoryWindow() {
    }

    public static void show(
            Window owner,
            List<Objective> objectives
    ) {

        Stage stage =
                new Stage();

        stage.setTitle(
                "Histórico de objetivos"
        );

        stage.initOwner(
                owner
        );

        stage.initModality(
                Modality.NONE
        );

        Label titleLabel =
                new Label(
                        "Histórico de objetivos"
                );

        titleLabel.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        Label summaryLabel =
                new Label(
                        objectives.size()
                                + historyCountLabel(
                                        objectives.size()
                                )
                );

        summaryLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #64748B;"
        );

        VBox historyContent =
                new VBox(
                        8
                );

        historyContent.setFillWidth(
                true
        );

        if (objectives.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "Nenhum objetivo encerrado ainda."
                    );

            emptyLabel.setStyle(
                    "-fx-font-size: 12px;"
                            + "-fx-text-fill: #64748B;"
            );

            historyContent
                    .getChildren()
                    .add(
                            emptyLabel
                    );

        } else {

            for (
                    Objective objective
                            : objectives
            ) {

                historyContent
                        .getChildren()
                        .add(
                                createObjectiveCard(
                                        objective
                                )
                        );
            }
        }

        ScrollPane scrollPane =
                new ScrollPane(
                        historyContent
                );

        scrollPane.setFitToWidth(
                true
        );

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setStyle(
                "-fx-background-color: transparent;"
                        + "-fx-background: transparent;"
        );

        VBox root =
                new VBox(
                        6,
                        titleLabel,
                        summaryLabel,
                        scrollPane
                );

        root.setPadding(
                new Insets(16)
        );

        root.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        Scene scene =
                new Scene(
                        root,
                        420,
                        460
                );

        stage.setScene(
                scene
        );

        stage.setMinWidth(
                360
        );

        stage.setMinHeight(
                320
        );

        stage.show();

        stage.toFront();
    }

    private static VBox createObjectiveCard(
            Objective objective
    ) {

        Label statusIcon =
                new Label(
                        statusIcon(
                                objective.getStatus()
                        )
                );

        statusIcon.setStyle(
                "-fx-font-size: 18px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + statusColor(
                                objective.getStatus()
                        )
                        + ";"
        );

        Label nameLabel =
                new Label(
                        objective.getName()
                );

        nameLabel.setStyle(
                "-fx-font-size: 13px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #0F172A;"
        );

        HBox nameRow =
                new HBox(
                        8,
                        statusIcon,
                        nameLabel
                );

        nameRow.setAlignment(
                Pos.CENTER_LEFT
        );

        Label statusLabel =
                new Label(
                        statusText(
                                objective.getStatus()
                        )
                );

        statusLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + statusColor(
                                objective.getStatus()
                        )
                        + ";"
        );

        int completedCycles =
                objective
                        .getTimer()
                        .snapshot()
                        .completedFocusCycles();

        Label progressLabel =
                new Label(
                        completedCycles
                                + " / "
                                + objective
                                .getPlannedFocusCycles()
                                + " ciclos"
                                + " • "
                                + objective
                                .getProgressPercentage()
                                + "%"
                );

        progressLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-text-fill: #475569;"
        );

        Label closedAtLabel =
                new Label(
                        objective.getClosedAt() == null
                                ? "-"
                                : DATE_FORMAT.format(
                                        objective.getClosedAt()
                                )
                );

        closedAtLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-text-fill: #64748B;"
        );

        VBox card =
                new VBox(
                        4,
                        nameRow,
                        statusLabel,
                        progressLabel,
                        closedAtLabel
                );

        card.setPadding(
                new Insets(10)
        );

        card.setStyle(
                "-fx-background-color: white;"
                        + "-fx-background-radius: 8;"
                        + "-fx-border-color: #E2E8F0;"
                        + "-fx-border-radius: 8;"
        );

        return card;
    }

    private static String statusText(
            ObjectiveStatus status
    ) {

        return switch (status) {

            case COMPLETED ->
                    "Concluído";

            case NOT_FINISHED ->
                    "Não finalizado";

            case DISCARDED ->
                    "Descartado";

            case ACTIVE ->
                    "Ativo";
        };
    }

    private static String statusIcon(
            ObjectiveStatus status
    ) {

        return switch (status) {

            case COMPLETED ->
                    "✓";

            case NOT_FINISHED ->
                    "○";

            case DISCARDED ->
                    "×";

            case ACTIVE ->
                    "•";
        };
    }

    private static String statusColor(
            ObjectiveStatus status
    ) {

        return switch (status) {

            case COMPLETED ->
                    "#15803D";

            case NOT_FINISHED ->
                    "#B45309";

            case DISCARDED ->
                    "#64748B";

            case ACTIVE ->
                    "#4F46E5";
        };
    }

    private static String historyCountLabel(
            int count
    ) {

        if (count == 1) {

            return " objetivo encerrado";
        }

        return " objetivos encerrados";
    }
}
