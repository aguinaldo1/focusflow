package io.github.aguinaldo1.focusflow.objective;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class ObjectiveManager {

    public static final int MAX_ACTIVE_OBJECTIVES = 5;

    private final List<Objective> objectives =
            new ArrayList<>();

    private Objective selectedObjective;

    public synchronized Objective addObjective(
            String name,
            String description
    ) {

        if (getActiveObjectiveCount()
                >= MAX_ACTIVE_OBJECTIVES) {

            throw new IllegalStateException(
                    "Maximum number of active objectives reached."
            );
        }

        Objective objective =
                new Objective(
                        name,
                        description
                );

        objectives.add(
                objective
        );

        if (selectedObjective == null
                || !selectedObjective.isActive()) {

            selectedObjective =
                    objective;
        }

        return objective;
    }

    public synchronized void selectObjective(
            UUID objectiveId
    ) {

        Objects.requireNonNull(
                objectiveId,
                "objectiveId"
        );

        Objective target =
                findObjective(
                        objectiveId
                );

        if (!target.isActive()) {

            throw new IllegalStateException(
                    "Cannot select a closed objective."
            );
        }

        if (target == selectedObjective) {
            return;
        }

        pauseIfRunning(
                selectedObjective
        );

        selectedObjective =
                target;
    }


	public synchronized void completeObjective(
        UUID objectiveId
) {

    closeObjective(
            objectiveId,
            Objective::complete
    );
}

public synchronized void markObjectiveNotFinished(
        UUID objectiveId
) {

    closeObjective(
            objectiveId,
            Objective::markNotFinished
    );
}

public synchronized void discardObjective(
        UUID objectiveId
) {

    closeObjective(
            objectiveId,
            Objective::discard
    );
}

    public synchronized Optional<Objective>
    getSelectedObjective() {

        return Optional.ofNullable(
                selectedObjective
        );
    }

    public synchronized List<Objective>
    getActiveObjectives() {

        return objectives.stream()
                .filter(
                        Objective::isActive
                )
                .toList();
    }

    public synchronized List<Objective>
    getAllObjectives() {

        return List.copyOf(
                objectives
        );
    }

    public synchronized int getActiveObjectiveCount() {

        return (int) objectives.stream()
                .filter(
                        Objective::isActive
                )
                .count();
    }

	private void closeObjective(
        UUID objectiveId,
        Consumer<Objective> closeAction
) {

    Objects.requireNonNull(
            objectiveId,
            "objectiveId"
    );

    Objects.requireNonNull(
            closeAction,
            "closeAction"
    );

    Objective objective =
            findObjective(
                    objectiveId
            );

    closeAction.accept(
            objective
    );

    if (objective == selectedObjective) {
        selectNextActiveObjective();
    }
}

private void selectNextActiveObjective() {

    selectedObjective =
            objectives.stream()
                    .filter(
                            Objective::isActive
                    )
                    .findFirst()
                    .orElse(null);
}

    private Objective findObjective(
            UUID objectiveId
    ) {

        return objectives.stream()
                .filter(
                        objective ->
                                objective
                                        .getId()
                                        .equals(
                                                objectiveId
                                        )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "Objective not found."
                                )
                );
    }

    private void pauseIfRunning(
            Objective objective
    ) {

        if (objective == null) {
            return;
        }

        PomodoroStatus status =
                objective
                        .getTimer()
                        .snapshot()
                        .status();

        if (status
                == PomodoroStatus.RUNNING) {

            objective
                    .getTimer()
                    .pause();
        }
    }
}
