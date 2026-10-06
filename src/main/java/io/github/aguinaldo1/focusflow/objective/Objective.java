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

    private ObjectiveStatus status;
    private Instant closedAt;

    public Objective(
            String name,
            String description
    ) {

        this.id =
                UUID.randomUUID();

        this.name =
                validateName(name);

        this.description =
                normalizeDescription(description);

        this.createdAt =
                Instant.now();

        this.status =
                ObjectiveStatus.ACTIVE;

        this.timer =
                new PomodoroTimer(
                        new PomodoroSession()
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

    public boolean isActive() {
        return status == ObjectiveStatus.ACTIVE;
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

        if (snapshot.status()
                == PomodoroStatus.RUNNING) {

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

    private static String validateName(
            String name
    ) {

        if (name == null
                || name.isBlank()) {

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
