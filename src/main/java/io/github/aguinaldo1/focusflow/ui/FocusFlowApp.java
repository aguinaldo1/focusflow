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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.AWTException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class FocusFlowApp extends Application {

    private ObjectiveManager objectiveManager;

    private FocusFlowStorage storage;
    private boolean storageAvailable;

    private PomodoroClock clock;
    private Timeline uiRefreshTimeline;

    private SystemAlert systemAlert;
    private SystemTrayIntegration systemTrayIntegration;

    private Label feedbackLabel;

    private ObjectivePanel objectivePanel;
    private PomodoroPanel pomodoroPanel;
    private PomodoroControls pomodoroControls;

    private Button historyButton;

    @Override
    public void start(
            Stage stage
    ) {

        initializeStorage();

        systemAlert =
                new SystemAlert();

        systemTrayIntegration =
                new SystemTrayIntegration();

        createObjectivePanel();
        createPomodoroControls();
        createPomodoroPanel();
        createHistoryControl(
                stage
        );

        HBox historyControls =
                new HBox(
                        6,
                        historyButton
                );

        historyControls.setAlignment(
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

        shortcutsLabel
                .getStyleClass()
                .add(
                        "hud-shortcuts"
                );

        VBox root =
                new VBox(
                        7,
                        objectivePanel,
                        pomodoroPanel,
                        pomodoroControls,
                        historyControls,
                        feedbackLabel,
                        shortcutsLabel
                );

        root.setAlignment(
                Pos.CENTER
        );

        root.setPadding(
                new Insets(14)
        );

        root
                .getStyleClass()
                .add(
                        "focusflow-root"
                );

        Scene scene =
                new Scene(
                        root,
                        430,
                        520
                );

        var hudStylesheet =
                FocusFlowApp.class
                        .getResource(
                                "/styles/focusflow-hud.css"
                        );

        if (hudStylesheet == null) {

            throw new IllegalStateException(
                    "CSS HUD não encontrado: "
                            + "/styles/focusflow-hud.css"
            );
        }

        scene
                .getStylesheets()
                .add(
                        hudStylesheet
                                .toExternalForm()
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

    private void createObjectivePanel() {

        objectivePanel =
                new ObjectivePanel(
                        this::addObjective,
                        this::selectObjectiveFromInterface,
                        this::completeSelectedObjective,
                        this::markSelectedObjectiveNotFinished,
                        this::removeSelectedObjective,
                        this::adjustPlannedCycles
                );
    }

    private void createPomodoroControls() {

        feedbackLabel =
                new Label();

        feedbackLabel.setStyle(
                "-fx-font-size: 10px;"
        );

        feedbackLabel
                .getStyleClass()
                .add(
                        "hud-feedback"
                );

        pomodoroControls =
                new PomodoroControls(
                        () ->
                                executeTimerAction(
                                        PomodoroTimer::start,
                                        "Intervalo iniciado."
                                ),
                        () ->
                                executeTimerAction(
                                        PomodoroTimer::pause,
                                        "Intervalo pausado."
                                ),
                        () ->
                                executeTimerAction(
                                        PomodoroTimer::resume,
                                        "Intervalo retomado."
                                ),
                        () ->
                                executeTimerAction(
                                        PomodoroTimer::restartCurrentInterval,
                                        "Intervalo reiniciado."
                                )
                );
    }

    private void createHistoryControl(
            Stage stage
    ) {

        historyButton =
                new Button(
                        "Histórico"
                );

        historyButton
                .getStyleClass()
                .add(
                        "hud-secondary-button"
                );

        historyButton.setOnAction(
                event ->
                        HistoryWindow.show(
                                stage,
                                objectiveManager
                                        .getClosedObjectives()
                        )
        );
    }

    private void createPomodoroPanel() {

        pomodoroPanel =
                new PomodoroPanel();
    }

    private void addObjective() {

        try {

            ObjectiveComplexity selectedComplexity =
                    objectivePanel
                            .getSelectedComplexity();

            if (selectedComplexity == null) {

                feedbackLabel.setText(
                        "Selecione uma complexidade."
                );

                return;
            }

            Objective objective =
                    objectiveManager.addObjective(
                            objectivePanel
                                    .getObjectiveName(),
                            "",
                            selectedComplexity
                    );

            objectiveManager.selectObjective(
                    objective.getId()
            );

            objectivePanel
                    .clearObjectiveName();

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
                ObjectiveLifecycleDialogs::confirmCompletion,
                objectiveManager::completeObjective,
                "Objetivo finalizado: "
        );
    }

    private void markSelectedObjectiveNotFinished() {

        closeSelectedObjective(
                ObjectiveLifecycleDialogs::confirmNotFinished,
                objectiveManager::markObjectiveNotFinished,
                "Objetivo marcado como não finalizado: "
        );
    }

    private void removeSelectedObjective() {

        closeSelectedObjective(
                ObjectiveLifecycleDialogs::confirmDiscard,
                objectiveManager::discardObjective,
                "Objetivo removido: "
        );
    }

    private void closeSelectedObjective(
            Predicate<Objective> confirmation,
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

        if (!confirmation.test(objective)) {

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

        Objective selectedObjective =
                objectiveManager
                        .getSelectedObjective()
                        .orElse(null);

        objectivePanel.refreshObjectives(
                objectiveManager
                        .getActiveObjectives(),
                selectedObjective
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

            objectivePanel
                    .showEmptyState();

            pomodoroPanel
                    .showEmptyState();

            pomodoroControls
                    .showWithoutObjective();

            updateAddObjectiveControls();

            return;
        }

        Objective objective =
                selected.orElseThrow();

        PomodoroSnapshot snapshot =
                objective
                        .getTimer()
                        .snapshot();

        objectivePanel.showObjective(
                objective,
                snapshot.completedFocusCycles()
        );

        pomodoroPanel.update(
                snapshot,
                objective.getProgressPercentage()
        );

        pomodoroControls.update(
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

        objectivePanel
                .setAddControlsDisabled(
                        limitReached
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

}
