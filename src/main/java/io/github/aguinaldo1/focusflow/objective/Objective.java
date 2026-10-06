package io.github.aguinaldo1.focusflow.objective;

import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSession;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroSnapshot;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroStatus;
import io.github.aguinaldo1.focusflow.pomodoro.PomodoroTimer;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Objective {

    private final UUID id;
    private final String name;
    private final String description;
    private final Instant createdAt;
    private final PomodoroTimer timer;
    private final ObjectiveComplexity complexity;

    private ObjectiveStatus status;
    private Instant closedAt;
    private int plannedFocusCycles;

    public Objective(
            String name,
            String description
    ) {

        this(
                name,
                description,
                ObjectiveComplexity.MEDIUM
        );
    }

    public Objective(
            String name,
            String description,
            ObjectiveComplexity complexity
    ) {

        this(
                UUID.randomUUID(),
                name,
                description,
                ObjectiveStatus.ACTIVE,
                Instant.now(),
                null,
                new PomodoroTimer(
                        new PomodoroSession()
                ),
                complexity,
                requireComplexity(
                        complexity
                ).suggestedFocusCycles()
        );
    }

    private Objective(
            UUID id,
            String name,
            String description,
            ObjectiveStatus status,
            Instant createdAt,
            Instant closedAt,
            PomodoroTimer timer,
            ObjectiveComplexity complexity,
            int plannedFocusCycles
    ) {

        this.id =
                Objects.requireNonNull(
                        id,
                        "id"
                );

        this.name =
                validateName(
                        name
                );

        this.description =
                normalizeDescription(
                        description
                );

        this.status =
                Objects.requireNonNull(
                        status,
                        "status"
                );

        this.createdAt =
                Objects.requireNonNull(
                        createdAt,
                        "createdAt"
                );

        this.closedAt =
                closedAt;

        this.timer =
                Objects.requireNonNull(
                        timer,
                        "timer"
                );

        this.complexity =
                requireComplexity(
                        complexity
                );

        if (plannedFocusCycles < 1) {

            throw new IllegalArgumentException(
                    "Planned focus cycles must be at least 1."
            );
        }

        this.plannedFocusCycles =
                plannedFocusCycles;

        validateRestoredState();
    }

    public static Objective restore(
            UUID id,
            String name,
            String description,
            ObjectiveStatus status,
            Instant createdAt,
            Instant closedAt,
            PomodoroTimer timer
    ) {

        return restore(
                id,
                name,
                description,
                status,
                createdAt,
                closedAt,
                ObjectiveComplexity.MEDIUM,
                ObjectiveComplexity.MEDIUM
                        .suggestedFocusCycles(),
                timer
        );
    }

    public static Objective restore(
            UUID id,
            String name,
            String description,
            ObjectiveStatus status,
            Instant createdAt,
            Instant closedAt,
            ObjectiveComplexity complexity,
            int plannedFocusCycles,
            PomodoroTimer timer
    ) {

        return new Objective(
                id,
                name,
                description,
                status,
                createdAt,
                closedAt,
                timer,
                complexity,
                plannedFocusCycles
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ObjectiveStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public PomodoroTimer getTimer() {
        return timer;
    }

    public ObjectiveComplexity getComplexity() {
        return complexity;
    }

    public int getPlannedFocusCycles() {
        return plannedFocusCycles;
    }

    public int getProgressPercentage() {

        int completedFocusCycles =
                timer.snapshot()
                        .completedFocusCycles();

        double progress =
                completedFocusCycles
                        / (double) plannedFocusCycles;

        return (int) Math.min(
                100,
                Math.round(
                        progress * 100
                )
        );
    }

    public boolean isActive() {
        return status == ObjectiveStatus.ACTIVE;
    }

    public void increasePlannedFocusCycles() {

        ensureActive();

        if (
                plannedFocusCycles
                        == Integer.MAX_VALUE
        ) {

            throw new IllegalStateException(
                    "Maximum planned focus cycles reached."
            );
        }

        plannedFocusCycles++;
    }

    public void decreasePlannedFocusCycles() {

        ensureActive();

        int completedFocusCycles =
                timer.snapshot()
                        .completedFocusCycles();

        int minimumAllowed =
                Math.max(
                        1,
                        completedFocusCycles
                );

        if (
                plannedFocusCycles
                        <= minimumAllowed
        ) {

            throw new IllegalStateException(
                    "Planned focus cycles cannot be reduced further."
            );
        }

        plannedFocusCycles--;
    }

    public void complete() {

        closeAs(
                ObjectiveStatus.COMPLETED
        );
    }

    public void markNotFinished() {

        closeAs(
                ObjectiveStatus.NOT_FINISHED
        );
    }

    public void discard() {

        closeAs(
                ObjectiveStatus.DISCARDED
        );
    }

    private void closeAs(
            ObjectiveStatus finalStatus
    ) {

        ensureActive();

        pausePomodoroIfRunning();

        status =
                Objects.requireNonNull(
                        finalStatus
                );

        closedAt =
                Instant.now();
    }

    private void pausePomodoroIfRunning() {

        PomodoroSnapshot snapshot =
                timer.snapshot();

        if (
                snapshot.status()
                        == PomodoroStatus.RUNNING
        ) {

            timer.pause();
        }
    }

    private void ensureActive() {

        if (!isActive()) {

            throw new IllegalStateException(
                    "Objective is already closed."
            );
        }
    }

    private void validateRestoredState() {

        if (
                status == ObjectiveStatus.ACTIVE
                        && closedAt != null
        ) {

            throw new IllegalArgumentException(
                    "Active objective cannot have a closed date."
            );
        }

        if (
                status != ObjectiveStatus.ACTIVE
                        && closedAt == null
        ) {

            throw new IllegalArgumentException(
                    "Closed objective must have a closed date."
            );
        }

        if (
                status != ObjectiveStatus.ACTIVE
                        && timer.snapshot().status()
                        == PomodoroStatus.RUNNING
        ) {

            throw new IllegalArgumentException(
                    "Closed objective cannot have a running Pomodoro."
            );
        }
    }

    private static ObjectiveComplexity requireComplexity(
            ObjectiveComplexity complexity
    ) {

        return Objects.requireNonNull(
                complexity,
                "complexity"
        );
    }

    private static String validateName(
            String name
    ) {

        if (
                name == null
                        || name.isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Objective name is required."
            );
        }

        return name.trim();
    }

    private static String normalizeDescription(
            String description
    ) {

        if (description == null) {
            return "";
        }

        return description.trim();
    }
}
