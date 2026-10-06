package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.desktop.SystemAlert;
import io.github.aguinaldo1.focusflow.desktop.SystemTrayIntegration;
import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroClock;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.AWTException;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Consumer;

public final class FocusFlowApp extends Application {

    private ObjectiveManager objectiveManager;

    private PomodoroClock clock;
    private Timeline uiRefreshTimeline;

    private SystemAlert systemAlert;
    private SystemTrayIntegration systemTrayIntegration;

    private TextField objectiveNameField;

    private ComboBox<ComplexityOption> complexityComboBox;
    private ComboBox<Objective> objectiveSelector;

    private Label planningLabel;
    private Label phaseLabel;
    private Label timeLabel;
    private Label statusLabel;
    private Label cyclesLabel;
    private Label feedbackLabel;

    private Button addObjectiveButton;
    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button resetButton;

    @Override
    public void start(
            Stage stage
    ) {

        objectiveManager =
                new ObjectiveManager();

        systemAlert =
                new SystemAlert();

        systemTrayIntegration =
                new SystemTrayIntegration();

        createObjectiveControls();
        createPomodoroControls();

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

        HBox pomodoroControls =
                new HBox(
                        6,
                        startButton,
                        pauseButton,
                        resumeButton,
                        resetButton
                );

        pomodoroControls.setAlignment(
                Pos.CENTER
        );

        Label shortcutsLabel =
                new Label(
                        "Ctrl+I iniciar  •  Ctrl+P pausar  •  "
                                + "Ctrl+C continuar  •  Ctrl+R resetar"
                );

        shortcutsLabel.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-opacity: 0.65;"
        );

        VBox root =
                new VBox(
                        7,
                        objectiveInput,
                        objectiveSelector,
                        planningLabel,
                        phaseLabel,
                        timeLabel,
                        statusLabel,
                        cyclesLabel,
                        pomodoroControls,
                        feedbackLabel,
                        shortcutsLabel
                );

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(14)
        );

        Scene scene =
                new Scene(
                        root,
                        430,
                        300
                );

        configureKeyboardShortcuts(
                scene
        );

        refreshView();
        startUiRefresh();

        stage.setTitle(
                "FocusFlow"
        );

        stage.setAlwaysOnTop(
                true
        );

        stage.setResizable(
                false
        );

        stage.setScene(
                scene
        );

        configureSystemTray(
                stage
        );

        stage.show();
    }

    private void createObjectiveControls() {

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

        addObjectiveButton.setOnAction(
                event -> addObjective()
        );

        objectiveSelector =
                new ComboBox<>();

        objectiveSelector.setPromptText(
                "Selecione um objetivo..."
        );

        objectiveSelector.setPrefWidth(
                280
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
                            objectiveSelector.getValue();

                    if (selectedObjective == null) {
                        return;
                    }

                    selectObjectiveFromInterface(
                            selectedObjective
                    );
                }
        );

        planningLabel =
                new Label(
                        "Cadastre um objetivo para começar."
                );

        planningLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-opacity: 0.75;"
        );
    }

    private void createPomodoroControls() {

        phaseLabel =
                new Label("-");

        phaseLabel.setStyle(
                "-fx-font-size: 12px;"
        );

        timeLabel =
                new Label("--:--");

        timeLabel.setStyle(
                "-fx-font-size: 42px;"
                        + "-fx-font-weight: bold;"
        );

        statusLabel =
                new Label(
                        "SEM OBJETIVO"
                );

        statusLabel.setStyle(
                "-fx-font-size: 11px;"
        );

        cyclesLabel =
                new Label(
                        "Ciclos concluídos: 0"
                );

        cyclesLabel.setStyle(
                "-fx-font-size: 11px;"
        );

        feedbackLabel =
                new Label();

        feedbackLabel.setStyle(
                "-fx-font-size: 10px;"
        );

        startButton =
                new Button(
                        "Iniciar"
                );

        pauseButton =
                new Button(
                        "Pausar"
                );

        resumeButton =
                new Button(
                        "Continuar"
                );

        resetButton =
                new Button(
                        "Resetar"
                );

        startButton.setOnAction(
                event ->
                        executeTimerAction(
                                PomodoroTimer::start,
                                "Intervalo iniciado."
                        )
        );

        pauseButton.setOnAction(
                event ->
                        executeTimerAction(
                                PomodoroTimer::pause,
                                "Intervalo pausado."
                        )
        );

        resumeButton.setOnAction(
                event ->
                        executeTimerAction(
                                PomodoroTimer::resume,
                                "Intervalo retomado."
                        )
        );

        resetButton.setOnAction(
                event ->
                        executeTimerAction(
                                PomodoroTimer::reset,
                                "Pomodoro reiniciado."
                        )
        );
    }

    private void addObjective() {

        try {

            ComplexityOption selectedComplexity =
                    complexityComboBox.getValue();

            if (selectedComplexity == null) {

                feedbackLabel.setText(
                        "Selecione uma complexidade."
                );

                return;
            }

            Objective objective =
                    objectiveManager.addObjective(
                            objectiveNameField.getText(),
                            "",
                            selectedComplexity
                                    .complexity()
                    );

            objectiveManager.selectObjective(
                    objective.getId()
            );

            objectiveNameField.clear();

            refreshObjectiveSelector();

            bindClockToSelectedObjective();

            feedbackLabel.setText(
                    "Objetivo adicionado: "
                            + objective.getName()
            );

            refreshView();

        } catch (
                IllegalArgumentException
                        | IllegalStateException exception
        ) {

            feedbackLabel.setText(
                    exception.getMessage()
            );
        }
    }

    private void selectObjectiveFromInterface(
            Objective objective
    ) {

        Optional<Objective> current =
                objectiveManager
                        .getSelectedObjective();

        if (
                current.isPresent()
                        && current
                        .orElseThrow()
                        .getId()
                        .equals(
                                objective.getId()
                        )
        ) {

            return;
        }

        try {

            objectiveManager.selectObjective(
                    objective.getId()
            );

            bindClockToSelectedObjective();

            feedbackLabel.setText(
                    "Objetivo selecionado: "
                            + objective.getName()
            );

            refreshView();

        } catch (
                IllegalArgumentException
                        | IllegalStateException exception
        ) {

            feedbackLabel.setText(
                    exception.getMessage()
            );
        }
    }

    private void refreshObjectiveSelector() {

        objectiveSelector
                .getItems()
                .setAll(
                        objectiveManager
                                .getActiveObjectives()
                );

        Objective selectedObjective =
                objectiveManager
                        .getSelectedObjective()
                        .orElse(null);

        objectiveSelector.setValue(
                selectedObjective
        );

        objectiveSelector.setDisable(
                objectiveManager
                        .getActiveObjectiveCount()
                        == 0
        );
    }

    private void bindClockToSelectedObjective() {

        if (clock != null) {

            clock.close();
            clock = null;
        }

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {
            return;
        }

        PomodoroTimer selectedTimer =
                selected
                        .orElseThrow()
                        .getTimer();

        clock =
                new PomodoroClock(
                        selectedTimer,
                        () ->
                                Platform.runLater(
                                        systemAlert
                                                ::playTimerFinished
                                )
                );

        clock.start();
    }

    private void executeTimerAction(
            Consumer<PomodoroTimer> action,
            String successMessage
    ) {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

            feedbackLabel.setText(
                    "Adicione um objetivo primeiro."
            );

            return;
        }

        try {

            action.accept(
                    selected
                            .orElseThrow()
                            .getTimer()
            );

            feedbackLabel.setText(
                    successMessage
            );

        } catch (IllegalStateException exception) {

            feedbackLabel.setText(
                    "Operação não permitida: "
                            + exception.getMessage()
            );
        }

        refreshView();
    }

    private void refreshView() {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

            planningLabel.setText(
                    "Cadastre um objetivo para começar."
            );

            phaseLabel.setText(
                    "-"
            );

            timeLabel.setText(
                    "--:--"
            );

            statusLabel.setText(
                    "SEM OBJETIVO"
            );

            cyclesLabel.setText(
                    "Ciclos concluídos: 0"
            );

            updateButtonsWithoutObjective();
            updateAddObjectiveControls();

            return;
        }

        Objective objective =
                selected.orElseThrow();

        PomodoroSnapshot snapshot =
                objective
                        .getTimer()
                        .snapshot();

        planningLabel.setText(
                complexityLabel(
                        objective.getComplexity()
                )
                        + " • "
                        + objective.getPlannedFocusCycles()
                        + " ciclos planejados"
        );

        phaseLabel.setText(
                snapshot
                        .phase()
                        .name()
        );

        timeLabel.setText(
                formatDuration(
                        snapshot.remainingTime()
                )
        );

        statusLabel.setText(
                snapshot
                        .status()
                        .name()
        );

        cyclesLabel.setText(
                "Ciclos concluídos: "
                        + snapshot.completedFocusCycles()
        );

        updateButtons(
                snapshot.status()
        );

        updateAddObjectiveControls();
    }

    private void updateAddObjectiveControls() {

        boolean limitReached =
                objectiveManager
                        .getActiveObjectiveCount()
                        >= ObjectiveManager
                        .MAX_ACTIVE_OBJECTIVES;

        addObjectiveButton.setDisable(
                limitReached
        );

        objectiveNameField.setDisable(
                limitReached
        );

        complexityComboBox.setDisable(
                limitReached
        );
    }

    private void updateButtonsWithoutObjective() {

        startButton.setDisable(
                true
        );

        pauseButton.setDisable(
                true
        );

        resumeButton.setDisable(
                true
        );

        resetButton.setDisable(
                true
        );
    }

    private void updateButtons(
            PomodoroStatus status
    ) {

        startButton.setDisable(
                status != PomodoroStatus.IDLE
        );

        pauseButton.setDisable(
                status != PomodoroStatus.RUNNING
        );

        resumeButton.setDisable(
                status != PomodoroStatus.PAUSED
        );

        resetButton.setDisable(
                false
        );
    }

    private void startUiRefresh() {

        uiRefreshTimeline =
                new Timeline(
                        new KeyFrame(
                                javafx.util.Duration.millis(
                                        200
                                ),
                                event -> refreshView()
                        )
                );

        uiRefreshTimeline.setCycleCount(
                Animation.INDEFINITE
        );

        uiRefreshTimeline.play();
    }

    private void configureKeyboardShortcuts(
            Scene scene
    ) {

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.I,
                        KeyCombination.CONTROL_DOWN
                ),
                () ->
                        executeTimerAction(
                                PomodoroTimer::start,
                                "Intervalo iniciado."
                        )
        );

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.P,
                        KeyCombination.CONTROL_DOWN
                ),
                () ->
                        executeTimerAction(
                                PomodoroTimer::pause,
                                "Intervalo pausado."
                        )
        );

        scene.getAccelerators().put(
                new KeyCodeCombination(
                        KeyCode.R,
                        KeyCombination.CONTROL_DOWN
                ),
                () ->
                        executeTimerAction(
                                PomodoroTimer::reset,
                                "Pomodoro reiniciado."
                        )
        );

        scene.addEventFilter(
                KeyEvent.KEY_PRESSED,
                event -> {

                    if (
                            !event.isControlDown()
                                    || event.getCode()
                                    != KeyCode.C
                    ) {

                        return;
                    }

                    if (
                            event.getTarget()
                                    instanceof TextInputControl
                    ) {

                        return;
                    }

                    executeTimerAction(
                            PomodoroTimer::resume,
                            "Intervalo retomado."
                    );

                    event.consume();
                }
        );
    }

    private void configureSystemTray(
            Stage stage
    ) {

        try {

            boolean installed =
                    systemTrayIntegration.install(
                            () ->
                                    Platform.runLater(
                                            () ->
                                                    restoreWindow(
                                                            stage
                                                    )
                                    ),
                            () ->
                                    Platform.runLater(
                                            Platform::exit
                                    )
                    );

            if (installed) {

                Platform.setImplicitExit(
                        false
                );

                stage.setOnCloseRequest(
                        event -> {

                            event.consume();

                            stage.hide();
                        }
                );
            }

        } catch (AWTException exception) {

            feedbackLabel.setText(
                    "System Tray indisponível."
            );
        }
    }

    private void restoreWindow(
            Stage stage
    ) {

        if (!stage.isShowing()) {

            stage.show();
        }

        stage.setIconified(
                false
        );

        stage.toFront();

        stage.requestFocus();
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

    private static String formatDuration(
            Duration duration
    ) {

        long totalSeconds =
                duration.toSeconds();

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );
    }

    @Override
    public void stop() {

        if (uiRefreshTimeline != null) {

            uiRefreshTimeline.stop();
        }

        if (clock != null) {

            clock.close();
        }

        if (systemTrayIntegration != null) {

            systemTrayIntegration.close();
        }
    }

    public static void main(
            String[] args
    ) {

        launch(args);
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
