package io.github.aguinaldo1.focusflow.ui;

import io.github.aguinaldo1.focusflow.desktop.SystemAlert;
import io.github.aguinaldo1.focusflow.desktop.SystemTrayIntegration;
import io.github.aguinaldo1.focusflow.objective.Objective;
import io.github.aguinaldo1.focusflow.objective.ObjectiveComplexity;
import io.github.aguinaldo1.focusflow.objective.ObjectiveManager;
import io.github.aguinaldo1.focusflow.persistence.FocusFlowStorage;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroClock;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroPhase;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.awt.AWTException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class FocusFlowApp extends Application {

    private static final double PROGRESS_RADIUS =
            27.0;

    private static final double PROGRESS_CIRCUMFERENCE =
            2.0
                    * Math.PI
                    * PROGRESS_RADIUS;

    private ObjectiveManager objectiveManager;

    private FocusFlowStorage storage;
    private boolean storageAvailable;

    private PomodoroClock clock;
    private Timeline uiRefreshTimeline;

    private SystemAlert systemAlert;
    private SystemTrayIntegration systemTrayIntegration;

    private TextField objectiveNameField;

    private ComboBox<ComplexityOption> complexityComboBox;
    private ComboBox<Objective> objectiveSelector;

    private Label planningLabel;
    private Label plannedCyclesLabel;
    private Label phaseLabel;
    private Label timeLabel;
    private Label statusLabel;
    private Label cyclesLabel;
    private Label feedbackLabel;

    private Label progressPercentageLabel;
    private Circle progressRing;
    private StackPane progressDonut;

    private Button addObjectiveButton;

    private Button completeObjectiveButton;
    private Button notFinishedObjectiveButton;
    private Button removeObjectiveButton;

    private Button decreaseCyclesButton;
    private Button increaseCyclesButton;

    private Button startButton;
    private Button pauseButton;
    private Button resumeButton;
    private Button restartButton;

    @Override
    public void start(
            Stage stage
    ) {

        initializeStorage();

        systemAlert =
                new SystemAlert();

        systemTrayIntegration =
                new SystemTrayIntegration();

        createObjectiveControls();
        createPomodoroControls();
        createProgressDonut();

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

        VBox timerInformation =
                new VBox(
                        0,
                        phaseLabel,
                        timeLabel,
                        statusLabel,
                        cyclesLabel
                );

        timerInformation.setAlignment(
                Pos.CENTER
        );

        StackPane timerArea =
                new StackPane(
                        timerInformation,
                        progressDonut
                );

        timerArea.setPrefSize(
                360,
                112
        );

        timerArea.setMinHeight(
                112
        );

        timerArea.setMaxWidth(
                360
        );

        StackPane.setAlignment(
                timerInformation,
                Pos.CENTER
        );

        StackPane.setAlignment(
                progressDonut,
                Pos.TOP_LEFT
        );

        StackPane.setMargin(
                progressDonut,
                new Insets(
                        3,
                        0,
                        0,
                        20
                )
        );

        HBox pomodoroControls =
                new HBox(
                        6,
                        startButton,
                        pauseButton,
                        resumeButton,
                        restartButton
                );

        pomodoroControls.setAlignment(
                Pos.CENTER
        );

        Label shortcutsLabel =
                new Label(
                        "Ctrl+I iniciar  •  Ctrl+P pausar  •  "
                                + "Ctrl+C continuar  •  Ctrl+R reiniciar"
                );

        shortcutsLabel.setStyle(
                "-fx-font-size: 9px;"
                        + "-fx-opacity: 0.65;"
        );

        VBox root =
                new VBox(
                        7,
                        objectiveInput,
                        objectiveSelection,
                        objectiveLifecycle,
                        planningLabel,
                        cycleAdjustment,
                        timerArea,
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
                        380
                );

        configureKeyboardShortcuts(
                scene
        );

        refreshObjectiveSelector();
        bindClockToSelectedObjective();
        refreshView();

        if (!storageAvailable) {

            feedbackLabel.setText(
                    "Armazenamento local indisponível. "
                            + "A sessão atual não será persistida."
            );
        }

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

    private void initializeStorage() {

        Path databasePath =
                Path.of(
                        System.getProperty(
                                "user.home"
                        ),
                        ".focusflow",
                        "focusflow.db"
                );

        storage =
                new FocusFlowStorage(
                        databasePath
                );

        try {

            objectiveManager =
                    storage.load();

            storageAvailable =
                    true;

        } catch (SQLException exception) {

            objectiveManager =
                    new ObjectiveManager();

            storageAvailable =
                    false;
        }
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
                            objectiveSelector.getValue();

                    if (selectedObjective == null) {
                        return;
                    }

                    selectObjectiveFromInterface(
                            selectedObjective
                    );
                }
        );

        completeObjectiveButton =
                new Button(
                        "Finalizar"
                );

        completeObjectiveButton.setDisable(
                true
        );

        completeObjectiveButton.setOnAction(
                event ->
                        completeSelectedObjective()
        );

        notFinishedObjectiveButton =
                new Button(
                        "Não finalizado"
                );

        notFinishedObjectiveButton.setDisable(
                true
        );

        notFinishedObjectiveButton.setOnAction(
                event ->
                        markSelectedObjectiveNotFinished()
        );

        removeObjectiveButton =
                new Button(
                        "Remover"
                );

        removeObjectiveButton.setDisable(
                true
        );

        removeObjectiveButton.setOnAction(
                event ->
                        removeSelectedObjective()
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

        increaseCyclesButton =
                new Button(
                        "+"
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
                        adjustPlannedCycles(
                                -1
                        )
        );

        increaseCyclesButton.setOnAction(
                event ->
                        adjustPlannedCycles(
                                1
                        )
        );
    }

    private void createPomodoroControls() {

        phaseLabel =
                new Label("-");

        phaseLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-opacity: 0.72;"
        );

        timeLabel =
                new Label("--:--");

        statusLabel =
                new Label(
                        "SEM OBJETIVO"
                );

        statusLabel.setStyle(
                "-fx-font-size: 10px;"
                        + "-fx-opacity: 0.75;"
        );

        cyclesLabel =
                new Label(
                        "Ciclos concluídos: 0"
                );

        cyclesLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-opacity: 0.85;"
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

        restartButton =
                new Button(
                        "Reiniciar"
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

        restartButton.setOnAction(
                event ->
                        executeTimerAction(
                                PomodoroTimer::restartCurrentInterval,
                                "Intervalo reiniciado."
                        )
        );

        updateTimerAppearance(
                null
        );
    }

    private void createProgressDonut() {

        Circle backgroundRing =
                new Circle(
                        PROGRESS_RADIUS
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
                        PROGRESS_RADIUS
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
                        PROGRESS_CIRCUMFERENCE,
                        PROGRESS_CIRCUMFERENCE
                );

        progressRing.setStrokeDashOffset(
                PROGRESS_CIRCUMFERENCE
        );

        progressRing.setRotate(
                -90
        );

        progressPercentageLabel =
                new Label(
                        "0%"
                );

        progressPercentageLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #334155;"
        );

        progressDonut =
                new StackPane(
                        backgroundRing,
                        progressRing,
                        progressPercentageLabel
                );

        progressDonut.setMinSize(
                66,
                66
        );

        progressDonut.setPrefSize(
                66,
                66
        );

        progressDonut.setMaxSize(
                66,
                66
        );

        progressDonut.setTranslateY(
                -12
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
            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        "Objetivo adicionado: "
                                + objective.getName()
                );
            }

        } catch (
                IllegalArgumentException
                        | IllegalStateException exception
        ) {

            feedbackLabel.setText(
                    exception.getMessage()
            );
        }
    }

    private void completeSelectedObjective() {

        closeSelectedObjective(
                "Finalizar objetivo",
                "Finalizar",
                "O objetivo será marcado como concluído, "
                        + "sairá da lista ativa e seu progresso "
                        + "será preservado.",
                "Finalizar",
                objectiveManager::completeObjective,
                "Objetivo finalizado: "
        );
    }

    private void markSelectedObjectiveNotFinished() {

        closeSelectedObjective(
                "Objetivo não finalizado",
                "Marcar como não finalizado",
                "O objetivo sairá da lista ativa, mas ficará "
                        + "registrado como não finalizado.",
                "Marcar",
                objectiveManager::markObjectiveNotFinished,
                "Objetivo marcado como não finalizado: "
        );
    }

    private void closeSelectedObjective(
            String title,
            String headerAction,
            String content,
            String confirmationButtonText,
            Consumer<UUID> closeAction,
            String successMessagePrefix
    ) {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

            feedbackLabel.setText(
                    "Selecione um objetivo primeiro."
            );

            return;
        }

        Objective objective =
                selected.orElseThrow();

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

        confirmation.setContentText(
                content
                        + "\n\nProgresso atual: "
                        + objective.getProgressPercentage()
                        + "%."
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

        if (
                result.isEmpty()
                        || result.get()
                        != confirmationButton
        ) {

            return;
        }

        try {

            closeAction.accept(
                    objective.getId()
            );

            refreshObjectiveSelector();
            bindClockToSelectedObjective();
            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        successMessagePrefix
                                + objective.getName()
                );
            }

        } catch (
                IllegalArgumentException
                        | IllegalStateException exception
        ) {

            feedbackLabel.setText(
                    exception.getMessage()
            );
        }
    }

    private void removeSelectedObjective() {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

            feedbackLabel.setText(
                    "Selecione um objetivo primeiro."
            );

            return;
        }

        Objective objective =
                selected.orElseThrow();

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );

        confirmation.setTitle(
                "Remover objetivo"
        );

        confirmation.setHeaderText(
                "Remover \""
                        + objective.getName()
                        + "\"?"
        );

        confirmation.setContentText(
                "O objetivo sairá da lista ativa e ficará "
                        + "marcado como descartado."
        );

        ButtonType removeButton =
                new ButtonType(
                        "Remover"
                );

        confirmation
                .getButtonTypes()
                .setAll(
                        removeButton,
                        ButtonType.CANCEL
                );

        Optional<ButtonType> result =
                confirmation.showAndWait();

        if (
                result.isEmpty()
                        || result.get()
                        != removeButton
        ) {

            return;
        }

        try {

            objectiveManager.discardObjective(
                    objective.getId()
            );

            refreshObjectiveSelector();
            bindClockToSelectedObjective();
            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        "Objetivo removido: "
                                + objective.getName()
                );
            }

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
            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        "Objetivo selecionado: "
                                + objective.getName()
                );
            }

        } catch (
                IllegalArgumentException
                        | IllegalStateException exception
        ) {

            feedbackLabel.setText(
                    exception.getMessage()
            );
        }
    }

    private void adjustPlannedCycles(
            int adjustment
    ) {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

            feedbackLabel.setText(
                    "Selecione um objetivo primeiro."
            );

            return;
        }

        Objective objective =
                selected.orElseThrow();

        try {

            if (adjustment > 0) {

                objective
                        .increasePlannedFocusCycles();

            } else {

                objective
                        .decreasePlannedFocusCycles();
            }

            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        "Planejamento ajustado para "
                                + objective
                                .getPlannedFocusCycles()
                                + " ciclos."
                );
            }

        } catch (IllegalStateException exception) {

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

        Objective clockObjective =
                selected.orElseThrow();

        PomodoroTimer selectedTimer =
                clockObjective.getTimer();

        clock =
                new PomodoroClock(
                        selectedTimer,
                        () ->
                                Platform.runLater(
                                        () ->
                                                handleIntervalCompleted(
                                                        clockObjective,
                                                        selectedTimer
                                                )
                                )
                );

        clock.start();
    }

    private void handleIntervalCompleted(
            Objective completedObjective,
            PomodoroTimer completedTimer
    ) {

        systemAlert
                .playTimerFinished();

        boolean stillSelected =
                objectiveManager
                        .getSelectedObjective()
                        .map(
                                objective ->
                                        objective
                                                .getId()
                                                .equals(
                                                        completedObjective
                                                                .getId()
                                                )
                        )
                        .orElse(
                                false
                        );

        if (stillSelected) {

            PomodoroSnapshot snapshot =
                    completedTimer.snapshot();

            boolean breakStarted =
                    completedTimer
                            .startBreakIfReady();

            if (breakStarted) {

                feedbackLabel.setText(
                        "Foco concluído. Pausa iniciada automaticamente."
                );

            } else if (
                    snapshot.phase()
                            == PomodoroPhase.FOCUS
            ) {

                feedbackLabel.setText(
                        "Pausa concluída. Inicie o próximo ciclo quando estiver pronto."
                );

            } else {

                feedbackLabel.setText(
                        "Intervalo concluído."
                );
            }
        }

        persistState();
        refreshView();
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

            refreshView();

            if (persistState()) {

                feedbackLabel.setText(
                        successMessage
                );
            }

        } catch (IllegalStateException exception) {

            feedbackLabel.setText(
                    "Operação não permitida: "
                            + exception.getMessage()
            );
        }
    }

    private boolean persistState() {

        if (
                !storageAvailable
                        || storage == null
        ) {

            feedbackLabel.setText(
                    "Armazenamento local indisponível."
            );

            return false;
        }

        try {

            storage.save(
                    objectiveManager
            );

            return true;

        } catch (SQLException exception) {

            feedbackLabel.setText(
                    "Não foi possível salvar os dados."
            );

            return false;
        }
    }

    private void refreshView() {

        Optional<Objective> selected =
                objectiveManager
                        .getSelectedObjective();

        if (selected.isEmpty()) {

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

            updateProgressDonut(
                    0
            );

            updateTimerAppearance(
                    null
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
                        snapshot.completedFocusCycles()
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

        phaseLabel.setText(
                phaseDisplayName(
                        snapshot.phase()
                )
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

        updateProgressDonut(
                objective.getProgressPercentage()
        );

        updateTimerAppearance(
                snapshot.phase()
        );

        updateButtons(
                snapshot.status()
        );

        updateAddObjectiveControls();
    }

    private void updateProgressDonut(
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

        progressPercentageLabel.setText(
                normalizedPercentage
                        + "%"
        );

        double offset =
                PROGRESS_CIRCUMFERENCE
                        * (
                        1.0
                                - normalizedPercentage
                                / 100.0
                );

        progressRing.setStrokeDashOffset(
                offset
        );
    }

    private void updateTimerAppearance(
            PomodoroPhase phase
    ) {

        String timerColor;
        String phaseColor;

        if (
                phase == PomodoroPhase.SHORT_BREAK
                        || phase == PomodoroPhase.LONG_BREAK
        ) {

            timerColor =
                    "#0F766E";

            phaseColor =
                    "#0F766E";

        } else if (
                phase == PomodoroPhase.FOCUS
        ) {

            timerColor =
                    "#0F172A";

            phaseColor =
                    "#475569";

        } else {

            timerColor =
                    "#64748B";

            phaseColor =
                    "#64748B";
        }

        timeLabel.setStyle(
                "-fx-font-size: 50px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + timerColor
                        + ";"
        );

        phaseLabel.setStyle(
                "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: "
                        + phaseColor
                        + ";"
        );
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

        restartButton.setDisable(
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

        restartButton.setDisable(
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
                                PomodoroTimer::restartCurrentInterval,
                                "Intervalo reiniciado."
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

    private static String phaseDisplayName(
            PomodoroPhase phase
    ) {

        return switch (phase) {

            case FOCUS ->
                    "FOCO";

            case SHORT_BREAK ->
                    "PAUSA CURTA";

            case LONG_BREAK ->
                    "PAUSA LONGA";
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

        if (
                storageAvailable
                        && storage != null
                        && objectiveManager != null
        ) {

            try {

                storage.save(
                        objectiveManager
                );

            } catch (SQLException exception) {

                System.err.println(
                        "Não foi possível salvar o estado final do FocusFlow: "
                                + exception.getMessage()
                );
            }
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
