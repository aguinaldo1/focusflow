package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public final class ObjectivePanel extends VBox {

    private final TextField objectiveNameField;

    private final ComboBox<ComplexityOption> complexityComboBox;
    private final ComboBox<Objective> objectiveSelector;

    private final Label planningLabel;
    private final Label plannedCyclesLabel;

    private final Button addObjectiveButton;

    private final Button completeObjectiveButton;
    private final Button notFinishedObjectiveButton;
    private final Button removeObjectiveButton;

    private final Button decreaseCyclesButton;
    private final Button increaseCyclesButton;

    public ObjectivePanel(
            Runnable onAddObjective,
            Consumer<Objective> onSelectObjective,
            Runnable onCompleteObjective,
            Runnable onNotFinishedObjective,
            Runnable onRemoveObjective,
            IntConsumer onAdjustPlannedCycles
    ) {

        super(7);

        getStyleClass().addAll(
                "hud-panel",
                "hud-objective-panel"
        );

        Objects.requireNonNull(
                onAddObjective,
                "onAddObjective"
        );

        Objects.requireNonNull(
                onSelectObjective,
                "onSelectObjective"
        );

        Objects.requireNonNull(
                onCompleteObjective,
                "onCompleteObjective"
        );

        Objects.requireNonNull(
                onNotFinishedObjective,
                "onNotFinishedObjective"
        );

        Objects.requireNonNull(
                onRemoveObjective,
                "onRemoveObjective"
        );

        Objects.requireNonNull(
                onAdjustPlannedCycles,
                "onAdjustPlannedCycles"
        );

        objectiveNameField =
                new TextField();

        objectiveNameField.setPromptText(
                "Digite seu objetivo..."
        );

        objectiveNameField.setPrefColumnCount(
                14
        );

        complexityComboBox =
                new ComboBox<>();

        complexityComboBox
                .getItems()
                .addAll(
                        ComplexityOption.values()
                );

        complexityComboBox.setValue(
                ComplexityOption.MEDIUM
        );

        addObjectiveButton =
                new Button(
                        "Adicionar"
                );

        addObjectiveButton
                .getStyleClass()
                .add(
                        "hud-primary-button"
                );

        addObjectiveButton.setOnAction(
                event ->
                        onAddObjective.run()
        );

        objectiveSelector =
                new ComboBox<>();

        objectiveSelector.setPromptText(
                "Selecione um objetivo..."
        );

        objectiveSelector.setPrefWidth(
                250
        );

        objectiveSelector.setDisable(
                true
        );

        objectiveSelector.setConverter(
                new StringConverter<>() {

                    @Override
                    public String toString(
                            Objective objective
                    ) {

                        if (objective == null) {

                            return "";
                        }

                        return objective.getName();
                    }

                    @Override
                    public Objective fromString(
                            String value
                    ) {

                        return null;
                    }
                }
        );

        objectiveSelector.setOnAction(
                event -> {

                    Objective selectedObjective =
                            objectiveSelector
                                    .getValue();

                    if (selectedObjective == null) {

                        return;
                    }

                    onSelectObjective.accept(
                            selectedObjective
                    );
                }
        );

        completeObjectiveButton =
                new Button(
                        "Finalizar"
                );

        completeObjectiveButton
                .getStyleClass()
                .add(
                        "hud-success-button"
                );

        completeObjectiveButton.setDisable(
                true
        );

        completeObjectiveButton.setOnAction(
                event ->
                        onCompleteObjective.run()
        );

        notFinishedObjectiveButton =
                new Button(
                        "Não finalizado"
                );

        notFinishedObjectiveButton
                .getStyleClass()
                .add(
                        "hud-warning-button"
                );

        notFinishedObjectiveButton.setDisable(
                true
        );

        notFinishedObjectiveButton.setOnAction(
                event ->
                        onNotFinishedObjective.run()
        );

        removeObjectiveButton =
                new Button(
                        "Remover"
                );

        removeObjectiveButton
                .getStyleClass()
                .add(
                        "hud-danger-button"
                );

        removeObjectiveButton.setDisable(
                true
        );

        removeObjectiveButton.setOnAction(
                event ->
                        onRemoveObjective.run()
        );

        planningLabel =
                new Label(
                        "Cadastre um objetivo para começar."
                );

        planningLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-opacity: 0.75;"
        );

        decreaseCyclesButton =
                new Button(
                        "−"
                );

        decreaseCyclesButton
                .getStyleClass()
                .add(
                        "hud-cycle-button"
                );

        increaseCyclesButton =
                new Button(
                        "+"
                );

        increaseCyclesButton
                .getStyleClass()
                .add(
                        "hud-cycle-button"
                );

        plannedCyclesLabel =
                new Label(
                        "0 ciclos"
                );

        plannedCyclesLabel.setStyle(
                "-fx-font-size: 12px;"
                        + "-fx-font-weight: bold;"
        );

        decreaseCyclesButton.setOnAction(
                event ->
                        onAdjustPlannedCycles.accept(
                                -1
                        )
        );

        increaseCyclesButton.setOnAction(
                event ->
                        onAdjustPlannedCycles.accept(
                                1
                        )
        );

        Label currentObjectiveTitle =
                new Label(
                        "OBJETIVO ATUAL"
                );

        currentObjectiveTitle
                .getStyleClass()
                .add(
                        "hud-section-label"
                );

        planningLabel
                .getStyleClass()
                .add(
                        "hud-muted"
                );

        plannedCyclesLabel
                .getStyleClass()
                .add(
                        "hud-cycle-count"
                );

        HBox objectiveInput =
                new HBox(
                        6,
                        objectiveNameField,
                        complexityComboBox,
                        addObjectiveButton
                );

        objectiveInput.setAlignment(
                Pos.CENTER
        );

        HBox objectiveSelection =
                new HBox(
                        6,
                        objectiveSelector
                );

        objectiveSelection.setAlignment(
                Pos.CENTER
        );

        HBox objectiveLifecycle =
                new HBox(
                        6,
                        completeObjectiveButton,
                        notFinishedObjectiveButton,
                        removeObjectiveButton
                );

        objectiveLifecycle.setAlignment(
                Pos.CENTER
        );

        HBox cycleAdjustment =
                new HBox(
                        8,
                        decreaseCyclesButton,
                        plannedCyclesLabel,
                        increaseCyclesButton
                );

        cycleAdjustment.setAlignment(
                Pos.CENTER
        );

        setAlignment(
                Pos.CENTER
        );

        getChildren().addAll(
                objectiveInput,
                currentObjectiveTitle,
                objectiveSelection,
                objectiveLifecycle,
                planningLabel,
                cycleAdjustment
        );
    }

    public String getObjectiveName() {

        return objectiveNameField
                .getText();
    }

    public ObjectiveComplexity getSelectedComplexity() {

        ComplexityOption selected =
                complexityComboBox
                        .getValue();

        if (selected == null) {

            return null;
        }

        return selected.complexity();
    }

    public void clearObjectiveName() {

        objectiveNameField.clear();
    }

    public void refreshObjectives(
            List<Objective> activeObjectives,
            Objective selectedObjective
    ) {

        Objects.requireNonNull(
                activeObjectives,
                "activeObjectives"
        );

        objectiveSelector
                .getItems()
                .setAll(
                        activeObjectives
                );

        objectiveSelector.setValue(
                selectedObjective
        );

        objectiveSelector.setDisable(
                activeObjectives.isEmpty()
        );
    }

    public void showEmptyState() {

        planningLabel.setText(
                "Cadastre um objetivo para começar."
        );

        plannedCyclesLabel.setText(
                "0 ciclos"
        );

        decreaseCyclesButton.setDisable(
                true
        );

        increaseCyclesButton.setDisable(
                true
        );

        completeObjectiveButton.setDisable(
                true
        );

        notFinishedObjectiveButton.setDisable(
                true
        );

        removeObjectiveButton.setDisable(
                true
        );
    }

    public void showObjective(
            Objective objective,
            int completedFocusCycles
    ) {

        Objects.requireNonNull(
                objective,
                "objective"
        );

        planningLabel.setText(
                complexityLabel(
                        objective.getComplexity()
                )
                        + " • planejamento"
        );

        plannedCyclesLabel.setText(
                objective
                        .getPlannedFocusCycles()
                        + " ciclos"
        );

        int minimumPlannedCycles =
                Math.max(
                        1,
                        completedFocusCycles
                );

        decreaseCyclesButton.setDisable(
                objective
                        .getPlannedFocusCycles()
                        <= minimumPlannedCycles
        );

        increaseCyclesButton.setDisable(
                false
        );

        completeObjectiveButton.setDisable(
                false
        );

        notFinishedObjectiveButton.setDisable(
                false
        );

        removeObjectiveButton.setDisable(
                false
        );
    }

    public void setAddControlsDisabled(
            boolean disabled
    ) {

        addObjectiveButton.setDisable(
                disabled
        );

        objectiveNameField.setDisable(
                disabled
        );

        complexityComboBox.setDisable(
                disabled
        );
    }

    private static String complexityLabel(
            ObjectiveComplexity complexity
    ) {

        return switch (complexity) {

            case EASY ->
                    "Fácil";

            case MEDIUM ->
                    "Médio";

            case HARD ->
                    "Difícil";
        };
    }

    private enum ComplexityOption {

        EASY(
                "Fácil",
                ObjectiveComplexity.EASY
        ),

        MEDIUM(
                "Médio",
                ObjectiveComplexity.MEDIUM
        ),

        HARD(
                "Difícil",
                ObjectiveComplexity.HARD
        );

        private final String label;

        private final ObjectiveComplexity complexity;

        ComplexityOption(
                String label,
                ObjectiveComplexity complexity
        ) {

            this.label =
                    label;

            this.complexity =
                    complexity;
        }

        public ObjectiveComplexity complexity() {

            return complexity;
        }

        @Override
        public String toString() {

            return label;
        }
    }
}
